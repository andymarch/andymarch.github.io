package uk.co.andymarch.blogposter.core

import java.time.LocalDate

/** A post's front matter plus its markdown body, ready to write to `_posts/`. */
data class PostDocument(
    val frontMatter: FrontMatter,
    val body: String,
) {
    fun render(): String = frontMatter.render() + "\n" + body.trimEnd() + "\n"

    companion object {
        private val FRONT_MATTER_DELIMITER = "---"

        /**
         * Best-effort parse of an existing `_posts` markdown file back into a
         * [PostDocument], for the "edit an existing post" flow. Only understands
         * the flat `key: value` / simple tag-list shape this app itself writes;
         * returns null for anything more exotic so callers can fall back to a
         * raw-text editor instead of corrupting the file.
         */
        fun parse(raw: String): PostDocument? {
            val lines = raw.replace("\r\n", "\n").split("\n")
            if (lines.isEmpty() || lines[0].trim() != FRONT_MATTER_DELIMITER) return null

            val closingIndex = lines.drop(1).indexOfFirst { it.trim() == FRONT_MATTER_DELIMITER } + 1
            if (closingIndex <= 0) return null

            var type = ""
            var layout = "post"
            var title: String? = null
            var date: LocalDate? = null
            val tags = mutableListOf<String>()
            var i = 1
            while (i < closingIndex) {
                val line = lines[i]
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("type:") -> type = unquote(trimmed.removePrefix("type:").trim())
                    trimmed.startsWith("layout:") -> layout = unquote(trimmed.removePrefix("layout:").trim())
                    trimmed.startsWith("title:") -> title = unquote(trimmed.removePrefix("title:").trim())
                    trimmed.startsWith("date:") -> date = runCatching {
                        LocalDate.parse(unquote(trimmed.removePrefix("date:").trim()).take(10))
                    }.getOrNull()
                    trimmed == "tags:" -> {
                        var j = i + 1
                        while (j < closingIndex && lines[j].trim().startsWith("- ")) {
                            tags += unquote(lines[j].trim().removePrefix("- ").trim())
                            j++
                        }
                        i = j - 1
                    }
                    trimmed.startsWith("tags:") -> {
                        unquote(trimmed.removePrefix("tags:").trim())
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .let(tags::addAll)
                    }
                }
                i++
            }

            val safeTitle = title ?: return null
            val safeDate = date ?: return null
            val body = lines.drop(closingIndex + 1).joinToString("\n").trim('\n')

            return PostDocument(
                frontMatter = FrontMatter(type = type, title = safeTitle, date = safeDate, tags = tags, layout = layout),
                body = body,
            )
        }

        private fun unquote(value: String): String {
            val trimmed = value.trim()
            return if (trimmed.length >= 2 && trimmed.first() == '"' && trimmed.last() == '"') {
                trimmed.substring(1, trimmed.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
            } else {
                trimmed
            }
        }
    }
}
