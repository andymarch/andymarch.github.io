package uk.co.andymarch.blogposter.ui.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.andymarch.blogposter.core.ImageNaming
import uk.co.andymarch.blogposter.core.MarkdownSnippets
import uk.co.andymarch.blogposter.core.PostPath
import uk.co.andymarch.blogposter.core.PostType
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.draft.DraftImage
import uk.co.andymarch.blogposter.data.draft.DraftStatus
import uk.co.andymarch.blogposter.data.draft.DraftStore
import uk.co.andymarch.blogposter.data.draft.ImageUploadStatus
import uk.co.andymarch.blogposter.data.github.GitHubRepository
import uk.co.andymarch.blogposter.image.ImageProcessor
import uk.co.andymarch.blogposter.publish.PublishWorker

/**
 * Backs the post composer. A post's images are uploaded to `assets/img/` as
 * soon as they're picked (so a slow upload doesn't block finishing the write-up),
 * while the final "Publish" step — writing the `_posts/*.md` file — runs as a
 * WorkManager job so it survives the app being backgrounded mid-commit.
 */
class EditorViewModel(
    val id: String,
    private val draftStore: DraftStore,
    private val settingsStore: SettingsStore,
    private val githubRepository: GitHubRepository,
    private val imageProcessor: ImageProcessor,
    private val appContext: Context,
) : ViewModel() {

    // DraftStore loads its backing file synchronously at construction, so this is safe to read here.
    private val existingDraft = draftStore.get(id)

    private val _uiState = MutableStateFlow(
        existingDraft?.let { EditorUiState.fromDraft(it, isRepoConfigured = true) } ?: EditorUiState(draftId = id),
    )
    val uiState: StateFlow<EditorUiState> = _uiState

    init {
        viewModelScope.launch {
            settingsStore.settings.collect { settings ->
                _uiState.value = _uiState.value.copy(isRepoConfigured = settings.isRepoConfigured)
            }
        }
    }

    fun updateTitle(value: String) = mutate { it.copy(title = value) }
    fun selectPostType(type: PostType) = mutate { it.copy(postType = type) }
    fun updateCustomType(value: String) = mutate { it.copy(customType = value) }
    fun updateTags(tags: List<String>) = mutate { it.copy(tags = tags) }
    fun updateDate(date: LocalDate) = mutate { it.copy(dateIso = date.toString()) }
    fun updateBody(value: String) = mutate { it.copy(body = value) }

    fun pickImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val newImages = uris.map { DraftImage(id = UUID.randomUUID().toString(), localUri = it.toString()) }
        mutate { it.copy(images = it.images + newImages) }
        newImages.forEach { uploadImage(it.id) }
    }

    fun retryUpload(imageId: String) = uploadImage(imageId)

    fun removeImage(imageId: String) {
        val image = _uiState.value.images.firstOrNull { it.id == imageId } ?: return
        mutate { state ->
            val strippedBody = image.remoteFileName?.let { fileName ->
                removeImageMarkdown(state.body, fileName)
            } ?: state.body
            state.copy(images = state.images.filterNot { it.id == imageId }, body = strippedBody)
        }
    }

    fun publish() {
        val state = _uiState.value
        if (!state.canPublish) return
        mutate { it.copy(status = DraftStatus.PUBLISHING, lastError = null) }
        viewModelScope.launch {
            persistNow()
            val request = OneTimeWorkRequestBuilder<PublishWorker>()
                .setInputData(workDataOf(PublishWorker.KEY_DRAFT_ID to id))
                .build()
            WorkManager.getInstance(appContext)
                .enqueueUniqueWork(PublishWorker.uniqueWorkName(id), ExistingWorkPolicy.REPLACE, request)
        }
    }

    /** Called by the screen when it leaves composition for good (back navigation, not a config change). */
    fun discardIfBlank() {
        val state = _uiState.value
        if (state.isBlank) {
            persistJob?.cancel()
            viewModelScope.launch { draftStore.delete(id) }
        }
    }

    private fun uploadImage(imageId: String) {
        viewModelScope.launch {
            updateImage(imageId) { it.copy(status = ImageUploadStatus.UPLOADING, errorMessage = null) }
            try {
                val settings = settingsStore.current()
                val repoConfig = settings.toRepoConfig() ?: error("Set up the repository in Settings before adding images.")
                val currentState = _uiState.value
                val index = currentState.images.indexOfFirst { it.id == imageId }
                val image = currentState.images[index]

                val processed = imageProcessor.process(
                    uri = Uri.parse(image.localUri),
                    maxDimensionPx = settings.imageMaxDimensionPx,
                    jpegQuality = settings.imageJpegQuality,
                )
                val fileName = ImageNaming.fileName(
                    postSlug = currentState.title.ifBlank { "post" },
                    index = index,
                    uniqueToken = imageId,
                    extension = ImageNaming.extensionForMimeType(processed.mimeType),
                )

                githubRepository.createOrUpdateFile(
                    config = repoConfig,
                    path = PostPath.imageFilePath(fileName),
                    bytes = processed.bytes,
                    commitMessage = "Add image for \"${currentState.title.ifBlank { "untitled post" }}\"",
                    author = settings.toCommitAuthor(),
                )

                updateImage(imageId) { it.copy(status = ImageUploadStatus.UPLOADED, remoteFileName = fileName) }
                mutate { it.copy(body = appendImageMarkdown(it.body, fileName)) }
            } catch (e: Exception) {
                updateImage(imageId) { it.copy(status = ImageUploadStatus.FAILED, errorMessage = e.message ?: "Upload failed") }
            }
            persistNow()
        }
    }

    private fun updateImage(imageId: String, transform: (DraftImage) -> DraftImage) {
        mutate { state ->
            state.copy(images = state.images.map { if (it.id == imageId) transform(it) else it })
        }
    }

    private fun appendImageMarkdown(body: String, fileName: String): String {
        val snippet = MarkdownSnippets.image(PostPath.imageUrl(fileName))
        return if (body.isBlank()) snippet else body.trimEnd() + "\n\n" + snippet
    }

    private fun removeImageMarkdown(body: String, fileName: String): String {
        val snippet = MarkdownSnippets.image(PostPath.imageUrl(fileName))
        return body.replace("\n\n$snippet", "").replace(snippet, "").trimEnd()
    }

    private var persistJob: Job? = null

    private fun mutate(reducer: (EditorUiState) -> EditorUiState) {
        _uiState.value = reducer(_uiState.value)
        persistDebounced()
    }

    /** Coalesces rapid edits (e.g. every keystroke) into a single disk write. */
    private fun persistDebounced() {
        persistJob?.cancel()
        persistJob = viewModelScope.launch {
            delay(PERSIST_DEBOUNCE_MS)
            persistNow()
        }
    }

    private suspend fun persistNow() {
        draftStore.upsert(_uiState.value.toDraft())
    }

    companion object {
        private const val PERSIST_DEBOUNCE_MS = 400L
    }
}
