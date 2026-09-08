package uk.co.andymarch.blogposter.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageNamingTest {

    @Test
    fun `builds a slug-index-token filename`() {
        assertEquals(
            "sunset-walk-1-ab12cd.jpg",
            ImageNaming.fileName(postSlug = "Sunset Walk", index = 0, uniqueToken = "ab12cd", extension = "JPG"),
        )
    }

    @Test
    fun `index is 1-based in the filename`() {
        assertEquals(
            "sunset-walk-2-ab12cd.png",
            ImageNaming.fileName(postSlug = "Sunset Walk", index = 1, uniqueToken = "ab12cd", extension = ".PNG"),
        )
    }

    @Test
    fun `maps mime types to extensions`() {
        assertEquals("png", ImageNaming.extensionForMimeType("image/png"))
        assertEquals("webp", ImageNaming.extensionForMimeType("image/webp"))
        assertEquals("jpg", ImageNaming.extensionForMimeType("image/heic"))
        assertEquals("jpg", ImageNaming.extensionForMimeType(null))
    }
}
