package uk.co.andymarch.blogposter.core

/**
 * The `type:` front-matter values already in use across the andymarch.github.io
 * `_posts` archive. Kept as an enum so the editor can offer a picker, but [CUSTOM]
 * lets a user type any other value without the app getting in the way.
 */
enum class PostType(val frontMatterValue: String, val label: String) {
    JOURNAL("journal", "Journal"),
    DEV("dev", "Dev"),
    PHOTO("photo", "Photo"),
    TALK("talk", "Talk"),
    BOOK_NOTE("book-note", "Book note"),
    TWEET("tweet", "Tweet"),
    CUSTOM("", "Custom");

    companion object {
        fun fromFrontMatterValue(value: String): PostType =
            entries.firstOrNull { it.frontMatterValue == value && it != CUSTOM } ?: CUSTOM
    }
}
