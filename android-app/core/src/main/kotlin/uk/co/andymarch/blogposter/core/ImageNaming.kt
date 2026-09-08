package uk.co.andymarch.blogposter.core

/**
 * Builds collision-resistant, Jekyll-friendly filenames for images uploaded to
 * `assets/img/`. The caller supplies [uniqueToken] (e.g. a short random or
 * time-derived string) so the result stays deterministic and testable here.
 */
object ImageNaming {

    fun fileName(
        postSlug: String,
        index: Int,
        uniqueToken: String,
        extension: String,
    ): String {
        val safeSlug = Slugify.slugify(postSlug)
        val safeExtension = extension.lowercase().removePrefix(".").ifBlank { "jpg" }
        val safeToken = Slugify.slugify(uniqueToken, fallback = "img")
        val position = index + 1
        return "$safeSlug-$position-$safeToken.$safeExtension"
    }

    fun extensionForMimeType(mimeType: String?): String = when (mimeType?.lowercase()) {
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/gif" -> "gif"
        else -> "jpg"
    }
}
