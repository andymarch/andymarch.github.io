package uk.co.andymarch.blogposter.ui.editor

import java.time.LocalDate
import uk.co.andymarch.blogposter.core.PostType
import uk.co.andymarch.blogposter.data.draft.Draft
import uk.co.andymarch.blogposter.data.draft.DraftImage
import uk.co.andymarch.blogposter.data.draft.DraftStatus
import uk.co.andymarch.blogposter.data.draft.ImageUploadStatus

data class EditorUiState(
    val draftId: String,
    val title: String = "",
    val postType: PostType = PostType.JOURNAL,
    val customType: String = "",
    val tags: List<String> = emptyList(),
    val dateIso: String = LocalDate.now().toString(),
    val body: String = "",
    val images: List<DraftImage> = emptyList(),
    val status: DraftStatus = DraftStatus.DRAFT,
    val lastError: String? = null,
    val isRepoConfigured: Boolean = true,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val publishedPostPath: String? = null,
) {
    val effectiveTypeValue: String get() = if (postType == PostType.CUSTOM) customType.trim() else postType.frontMatterValue

    val hasImagesInFlight: Boolean get() =
        images.any { it.status == ImageUploadStatus.PENDING || it.status == ImageUploadStatus.UPLOADING }

    val hasFailedImages: Boolean get() = images.any { it.status == ImageUploadStatus.FAILED }

    val canPublish: Boolean get() =
        title.isNotBlank() && isRepoConfigured && status != DraftStatus.PUBLISHING && !hasImagesInFlight && !hasFailedImages

    fun toDraft(updatedAtEpochMillis: Long = System.currentTimeMillis()): Draft = Draft(
        id = draftId,
        title = title,
        typeValue = effectiveTypeValue,
        tags = tags,
        dateIso = dateIso,
        body = body,
        images = images,
        status = status,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        lastError = lastError,
        publishedPostPath = publishedPostPath,
    )

    val isBlank: Boolean get() = title.isBlank() && body.isBlank() && images.isEmpty()

    companion object {
        fun fromDraft(draft: Draft, isRepoConfigured: Boolean): EditorUiState {
            val postType = PostType.fromFrontMatterValue(draft.typeValue)
            return EditorUiState(
                draftId = draft.id,
                title = draft.title,
                postType = postType,
                customType = if (postType == PostType.CUSTOM) draft.typeValue else "",
                tags = draft.tags,
                dateIso = draft.dateIso,
                body = draft.body,
                images = draft.images,
                status = draft.status,
                lastError = draft.lastError,
                isRepoConfigured = isRepoConfigured,
                createdAtEpochMillis = draft.createdAtEpochMillis,
                publishedPostPath = draft.publishedPostPath,
            )
        }
    }
}
