package uk.co.andymarch.blogposter.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.andymarch.blogposter.core.PostDocument
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.draft.Draft
import uk.co.andymarch.blogposter.data.draft.DraftStatus
import uk.co.andymarch.blogposter.data.draft.DraftStore
import uk.co.andymarch.blogposter.data.github.GitHubRepository
import uk.co.andymarch.blogposter.data.github.PostSummary

data class RecentPostsUiState(
    val isLoading: Boolean = false,
    val posts: List<PostSummary> = emptyList(),
    val errorMessage: String? = null,
    val unsupportedMessage: String? = null,
)

class RecentPostsViewModel(
    private val githubRepository: GitHubRepository,
    private val settingsStore: SettingsStore,
    private val draftStore: DraftStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentPostsUiState())
    val uiState: StateFlow<RecentPostsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val config = settingsStore.current().toRepoConfig()
                    ?: error("Set up the repository in Settings first.")
                val posts = githubRepository.listPosts(config)
                _uiState.value = _uiState.value.copy(isLoading = false, posts = posts)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message ?: "Could not load posts.")
            }
        }
    }

    /** Loads [post], turning it into an editable local draft; invokes [onReady] with its new draft id. */
    fun openForEditing(post: PostSummary, onReady: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(unsupportedMessage = null)
            try {
                val config = settingsStore.current().toRepoConfig() ?: error("Repository isn't configured.")
                val raw = githubRepository.readFile(config, post.path) ?: error("That post couldn't be found.")
                val document = PostDocument.parse(raw)
                if (document == null) {
                    _uiState.value = _uiState.value.copy(
                        unsupportedMessage = "\"${post.name}\" uses a layout this app can't safely edit yet.",
                    )
                    return@launch
                }
                val now = System.currentTimeMillis()
                val draft = Draft(
                    id = UUID.randomUUID().toString(),
                    title = document.frontMatter.title,
                    typeValue = document.frontMatter.type,
                    tags = document.frontMatter.tags,
                    dateIso = document.frontMatter.date.toString(),
                    body = document.body,
                    status = DraftStatus.PUBLISHED,
                    createdAtEpochMillis = now,
                    updatedAtEpochMillis = now,
                    publishedPostPath = post.path,
                )
                draftStore.upsert(draft)
                onReady(draft.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(unsupportedMessage = e.message ?: "Could not open that post.")
            }
        }
    }
}
