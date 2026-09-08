package uk.co.andymarch.blogposter.core

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PostPathTest {

    @Test
    fun `builds the _posts filename jekyll expects`() {
        assertEquals(
            "_posts/2025-01-09-the-beauty-of-everyday-things.md",
            PostPath.postFilePath(LocalDate.of(2025, 1, 9), "The Beauty of Everyday Things"),
        )
    }

    @Test
    fun `image paths live under assets slash img`() {
        assertEquals("assets/img/sunset-1-ab12cd.jpg", PostPath.imageFilePath("sunset-1-ab12cd.jpg"))
        assertEquals("/assets/img/sunset-1-ab12cd.jpg", PostPath.imageUrl("sunset-1-ab12cd.jpg"))
    }
}
