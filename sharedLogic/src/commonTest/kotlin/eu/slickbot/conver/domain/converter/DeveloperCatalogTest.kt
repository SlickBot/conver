package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.base64Converter
import eu.slickbot.conver.domain.converter.converters.caseConverter
import eu.slickbot.conver.domain.converter.converters.hashConverter
import eu.slickbot.conver.domain.converter.converters.htmlEntityConverter
import eu.slickbot.conver.domain.converter.converters.jsonConverter
import eu.slickbot.conver.domain.converter.converters.slugify
import eu.slickbot.conver.domain.converter.converters.urlEncodeConverter
import eu.slickbot.conver.domain.converter.converters.uuidConverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeveloperCatalogTest {

  @Test fun `base64 round trip`() {
    val b = base64Converter()
    val encoded = b.run("encode", "Hello, World!")
    assertEquals("SGVsbG8sIFdvcmxkIQ==", encoded)
    assertEquals("Hello, World!", b.run("decode", encoded))
  }

  @Test fun `url encode and decode`() {
    val u = urlEncodeConverter()
    val encoded = u.run("encode", "hello world & friends")
    assertTrue(encoded.contains("%20") || encoded.contains("+"))
    assertEquals("hello world & friends", u.run("decode", encoded))
  }

  @Test fun `html entity encoding`() {
    val h = htmlEntityConverter()
    assertEquals("&lt;b&gt;bold&lt;/b&gt;", h.run("encode", "<b>bold</b>"))
    assertEquals("<b>bold</b>", h.run("decode", "&lt;b&gt;bold&lt;/b&gt;"))
    assertEquals("AT&T", h.run("decode", "AT&amp;T"))
  }

  @Test fun `hash known values`() {
    val h = hashConverter()
    // MD5 of "abc" = 900150983cd24fb0d6963f7d28e17f72
    assertEquals("900150983cd24fb0d6963f7d28e17f72", h.run("md5", "abc"))
    // SHA-256 of "abc" = ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
    assertEquals(
      "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
      h.run("sha256", "abc"),
    )
  }

  @Test fun `uuid generates correct count`() {
    val u = uuidConverter()
    val result = u.run("v4", "3")
    assertEquals(3, result.lines().size)
    // loose shape check: each line is 36 chars (8-4-4-4-12)
    result.lines().forEach { assertEquals(36, it.length) }
  }

  @Test fun `json pretty formats a blob`() {
    val j = jsonConverter()
    val input = """{"a":1,"b":[1,2]}"""
    val pretty = j.run("pretty", input)
    assertTrue(pretty.contains("\n"))
    val minified = j.run("minify", pretty)
    assertEquals(input, minified)
  }

  @Test fun `case converter variants`() {
    val c = caseConverter()
    val input = "my cool string"
    assertEquals("MY COOL STRING", c.run("upper", input))
    assertEquals("my cool string", c.run("lower", input))
    assertEquals("My Cool String", c.run("title", input))
    assertEquals("my_cool_string", c.run("snake", input))
    assertEquals("my-cool-string", c.run("kebab", input))
    assertEquals("myCoolString", c.run("camel", input))
    assertEquals("MyCoolString", c.run("pascal", input))
    assertEquals("MY_COOL_STRING", c.run("constant", input))
  }

  @Test fun `slugify removes diacritics and punctuation`() {
    assertEquals("cafe-au-lait", slugify("Café au Lait!"))
    assertEquals("my-post-2025", slugify("My  Post — 2025"))
  }
}
