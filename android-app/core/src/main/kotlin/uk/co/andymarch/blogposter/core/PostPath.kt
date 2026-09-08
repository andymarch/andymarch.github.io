package uk.co.andymarch.blogposter.core

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Builds the repository-relative paths Jekyll expects for posts and images. */
object PostPath {

    private val FILE_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** e.g. `_posts/2025-01-09-the-beauty-of-everyday-things.md` */
    fun postFilePath(date: LocalDate, slug: String): String {
        val safeSlug = Slugify.slugify(slug)
        return "_posts/${date.format(FILE_DATE_FORMAT)}-$safeSlug.md"
    }

    /** e.g. `assets/img/the-beauty-of-everyday-things-a1b2c3.jpg` */
    fun imageFilePath(fileName: String): String = "assets/img/$fileName"

    /** The `/assets/img/...` URL Jekyll serves the image at, for use in markdown. */
    fun imageUrl(fileName: String): String = "/assets/img/$fileName"
}
