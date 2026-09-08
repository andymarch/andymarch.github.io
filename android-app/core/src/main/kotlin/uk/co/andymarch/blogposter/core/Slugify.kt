package uk.co.andymarch.blogposter.core

import java.text.Normalizer

/**
 * Turns free-form text into a URL/filename-safe slug, matching the style already
 * used by the existing `_posts` filenames (lowercase, hyphen-separated).
 */
object Slugify {

    private val nonAlphanumeric = Regex("[^a-z0-9]+")
    private val edgeHyphens = Regex("(^-+)|(-+$)")

    fun slugify(input: String, fallback: String = "post"): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFKD)
            .replace(Regex("\\p{M}"), "") // strip accents
            .lowercase()

        val slug = nonAlphanumeric.replace(normalized, "-")
            .let { edgeHyphens.replace(it, "") }

        return slug.ifBlank { fallback }
    }
}
