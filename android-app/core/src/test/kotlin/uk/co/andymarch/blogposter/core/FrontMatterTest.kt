package uk.co.andymarch.blogposter.core

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class FrontMatterTest {

    @Test
    fun `renders single tag as plain scalar matching existing posts`() {
        val fm = FrontMatter(
            type = "book-note",
            title = "The Beauty of Everyday Things - Soetsu Yanagi",
            date = LocalDate.of(2025, 1, 9),
            tags = listOf("reading"),
        )
        val expected = """
            ---
            type: book-note
            layout: post
            title: "The Beauty of Everyday Things - Soetsu Yanagi"
            date: "2025-01-09"
            tags: reading
            ---
        """.trimIndent() + "\n"
        assertEquals(expected, fm.render())
    }

    @Test
    fun `omits tags line when there are no tags`() {
        val fm = FrontMatter(type = "photo", title = "Salisbury Cathedral", date = LocalDate.of(2014, 11, 9))
        val expected = """
            ---
            type: photo
            layout: post
            title: "Salisbury Cathedral"
            date: "2014-11-09"
            ---
        """.trimIndent() + "\n"
        assertEquals(expected, fm.render())
    }

    @Test
    fun `renders multiple tags as a yaml list`() {
        val fm = FrontMatter(type = "talk", title = "DevSecCon", date = LocalDate.of(2021, 10, 19), tags = listOf("speaking", "security"))
        val rendered = fm.render()
        assertEquals(
            """
            ---
            type: talk
            layout: post
            title: "DevSecCon"
            date: "2021-10-19"
            tags:
              - speaking
              - security
            ---
            """.trimIndent() + "\n",
            rendered,
        )
    }

    @Test
    fun `escapes double quotes in title`() {
        val fm = FrontMatter(type = "journal", title = """He said "hello"""", date = LocalDate.of(2025, 1, 1))
        assertEquals("""title: "He said \"hello\""""", fm.render().lines()[3])
    }

    @Test
    fun `blank type is omitted so custom types can skip the line entirely`() {
        val fm = FrontMatter(type = "", title = "Untyped", date = LocalDate.of(2025, 1, 1))
        assertEquals(false, fm.render().lines().any { it.startsWith("type:") })
    }
}
