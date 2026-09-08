package uk.co.andymarch.blogposter.core

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Everything needed to render the YAML front matter block that sits at the top
 * of every post in `_posts/`, e.g.:
 *
 * ```
 * ---
 * type: book-note
 * layout: post
 * title: "The Beauty of Everyday Things - Soetsu Yanagi"
 * date: "2025-01-09"
 * tags: reading
 * ---
 * ```
 */
data class FrontMatter(
    val type: String,
    val title: String,
    val date: LocalDate,
    val tags: List<String> = emptyList(),
    val layout: String = "post",
) {
    fun render(): String = buildString {
        appendLine("---")
        if (type.isNotBlank()) appendLine("type: ${yamlPlain(type)}")
        appendLine("layout: ${yamlPlain(layout)}")
        appendLine("title: ${yamlQuoted(title)}")
        appendLine("date: ${yamlQuoted(date.format(DATE_FORMAT))}")
        when (tags.size) {
            0 -> Unit
            1 -> appendLine("tags: ${yamlPlain(tags.first())}")
            else -> {
                appendLine("tags:")
                tags.forEach { appendLine("  - ${yamlPlain(it)}") }
            }
        }
        appendLine("---")
    }

    companion object {
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        /** Escapes a value for use inside a double-quoted YAML scalar. */
        private fun yamlQuoted(value: String): String =
            "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

        /**
         * Emits a bare YAML scalar, falling back to a quoted one if the value
         * contains characters that would otherwise change its meaning.
         */
        private fun yamlPlain(value: String): String {
            val needsQuoting = value.isEmpty() ||
                value.any { it in ":#{}[],&*!|>'\"%@`" } ||
                value.first().isWhitespace() ||
                value.last().isWhitespace()
            return if (needsQuoting) yamlQuoted(value) else value
        }
    }
}
