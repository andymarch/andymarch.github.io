package uk.co.andymarch.blogposter.core

/** Small helpers for inserting markdown into the post body editor. */
object MarkdownSnippets {

    fun image(imageUrl: String, altText: String = ""): String = "![$altText]($imageUrl)"

    fun bold(text: String): String = "**$text**"

    fun italic(text: String): String = "*$text*"

    fun link(text: String, url: String): String = "[$text]($url)"

    fun quote(text: String): String = text.lineSequence().joinToString("\n") { "> $it" }
}
