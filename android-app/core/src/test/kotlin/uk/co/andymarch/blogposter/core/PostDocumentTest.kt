package uk.co.andymarch.blogposter.core

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostDocumentTest {

    @Test
    fun `render then parse round-trips`() {
        val doc = PostDocument(
            frontMatter = FrontMatter(
                type = "journal",
                title = "A day out",
                date = LocalDate.of(2025, 3, 4),
                tags = listOf("family", "walking"),
            ),
            body = "We went for a long walk.\n\n![](/assets/img/a-day-out-1-ab12cd.jpg)",
        )

        val parsed = PostDocument.parse(doc.render())

        assertEquals(doc.frontMatter, parsed?.frontMatter)
        assertEquals(doc.body, parsed?.body)
    }

    @Test
    fun `parses a real existing post with a single plain tag`() {
        val raw = """
            ---
            type: book-note
            layout: post
            title: "The Beauty of Everyday Things - Soetsu Yanagi"
            date: "2025-01-09"
            tags: reading
            ---

            Some body text here.
        """.trimIndent()

        val parsed = PostDocument.parse(raw)

        assertEquals("book-note", parsed?.frontMatter?.type)
        assertEquals("The Beauty of Everyday Things - Soetsu Yanagi", parsed?.frontMatter?.title)
        assertEquals(LocalDate.of(2025, 1, 9), parsed?.frontMatter?.date)
        assertEquals(listOf("reading"), parsed?.frontMatter?.tags)
        assertEquals("Some body text here.", parsed?.body)
    }

    @Test
    fun `returns null when there is no front matter`() {
        assertNull(PostDocument.parse("Just some text, no front matter."))
    }

    @Test
    fun `returns null when title is missing`() {
        val raw = """
            ---
            type: journal
            date: "2025-01-01"
            ---
            body
        """.trimIndent()
        assertNull(PostDocument.parse(raw))
    }
}
