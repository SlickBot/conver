package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.slugify
import kotlin.test.Test
import kotlin.test.assertEquals

class SlugifyConsistencyTest {

  @Test fun `strips accents from latin letters`() {
    assertEquals("cafe", slugify("Café"))
    assertEquals("manana", slugify("mañana"))
    assertEquals("uber", slugify("über"))
    assertEquals("francois", slugify("François"))
  }

  @Test fun `collapses whitespace and symbols into single hyphens`() {
    assertEquals("my-blog-post-2025", slugify("My Blog Post — 2025!"))
    assertEquals("a-b", slugify("  a   b  "))
  }

  @Test fun `trims leading and trailing separators`() {
    assertEquals("hello", slugify("--hello--"))
    assertEquals("hello-world", slugify("!!!hello world!!!"))
  }

  // White-box: exercises the expect/actual normalizeNfd contract directly on each platform target.
  @Test fun `normalizeNfd decomposes a precomposed character`() {
    val decomposed = normalizeNfd("é")
    assertEquals(2, decomposed.length)
    assertEquals('e', decomposed[0])
    assertEquals('́', decomposed[1])
  }
}
