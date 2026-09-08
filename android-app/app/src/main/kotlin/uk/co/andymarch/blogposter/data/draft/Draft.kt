package uk.co.andymarch.blogposter.data.draft

import kotlinx.serialization.Serializable

enum class DraftStatus { DRAFT, PUBLISHING, PUBLISHED, FAILED }

enum class ImageUploadStatus { PENDING, UPLOADING, UPLOADED, FAILED }

@Serializable
data class DraftImage(
    val id: String,
    /** content:// or file:// URI, as a string, of the original picked/captured image. */
    val localUri: String,
    val altText: String = "",
    val status: ImageUploadStatus = ImageUploadStatus.PENDING,
    /** Set once the image has been uploaded to assets/img/. */
    val remoteFileName: String? = null,
    val errorMessage: String? = null,
)

/**
 * Everything needed to render and publish one post, persisted locally so
 * composing survives app restarts and publishing survives process death
 * (the actual upload runs in a WorkManager worker, not tied to the UI).
 */
@Serializable
data class Draft(
    val id: String,
    val title: String = "",
    val typeValue: String = "",
    val tags: List<String> = emptyList(),
    /** ISO-8601, e.g. "2025-01-09". */
    val dateIso: String,
    val body: String = "",
    val images: List<DraftImage> = emptyList(),
    val status: DraftStatus = DraftStatus.DRAFT,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val lastError: String? = null,
    val publishedPostPath: String? = null,
) {
    val isPublished: Boolean get() = status == DraftStatus.PUBLISHED

    val allImagesUploaded: Boolean get() = images.all { it.status == ImageUploadStatus.UPLOADED }
    val hasFailedImages: Boolean get() = images.any { it.status == ImageUploadStatus.FAILED }
    val hasImagesInFlight: Boolean get() =
        images.any { it.status == ImageUploadStatus.PENDING || it.status == ImageUploadStatus.UPLOADING }

    /** Ready to write the post file: has a title, body, and nothing still uploading or broken. */
    val canPublish: Boolean get() =
        title.isNotBlank() && status != DraftStatus.PUBLISHING && !hasImagesInFlight && !hasFailedImages
}
