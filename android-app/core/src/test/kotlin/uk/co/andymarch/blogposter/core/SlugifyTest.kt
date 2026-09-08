package uk.co.andymarch.blogposter.core

import org.junit.Assert.assertEquals
import org.junit.Test

class SlugifyTest {

    @Test
    fun `lowercases and hyphenates`() {
        assertEquals("the-beauty-of-everyday-things", Slugify.slugify("The Beauty of Everyday Things"))
    }

    @Test
    fun `strips punctuation`() {
        assertEquals("well-earned-brewdog-after-shifting-1-7-tonnes", Slugify.slugify("Well earned brewdog after shifting 1.7 tonnes"))
    }

    @Test
    fun `collapses repeated separators and trims edges`() {
        assertEquals("hello-world", Slugify.slugify("  Hello -- World!! "))
    }

    @Test
    fun `strips accents`() {
        assertEquals("cafe-au-lait", Slugify.slugify("Café au lait"))
    }

    @Test
    fun `falls back when nothing alphanumeric survives`() {
        assertEquals("post", Slugify.slugify("!!!"))
    }

    @Test
    fun `custom fallback is honoured`() {
        assertEquals("img", Slugify.slugify("???", fallback = "img"))
    }
}
