package eu.slickbot.conver.domain.search

import eu.slickbot.conver.domain.converter.ConverterRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConverterSearchTest {

  private val search = ConverterSearch(ConverterRegistry.default())

  @Test fun `exact name match returns converter`() {
    val results = search.query("Length")
    assertEquals("length", results.firstOrNull()?.id)
  }

  @Test fun `alias match is found`() {
    val results = search.query("km")
    assertTrue(results.any { it.id == "length" })
  }

  @Test fun `empty query returns empty list`() {
    assertTrue(search.query("").isEmpty())
    assertTrue(search.query("   ").isEmpty())
  }

  @Test fun `unknown query returns empty list`() {
    assertTrue(search.query("zzzzz").isEmpty())
  }

  @Test fun `whitespace-padded query is trimmed before matching`() {
    val results = search.query("  length  ")
    assertEquals("length", results.firstOrNull()?.id)
  }

  @Test fun `query is case-insensitive`() {
    val upper = search.query("LENGTH").map { it.id }
    val lower = search.query("length").map { it.id }
    assertEquals(lower, upper)
  }

  @Test fun `limit caps the number of results`() {
    val results = search.query("e", limit = 3)
    assertEquals(3, results.size)
  }

  @Test fun `exact alias match outranks prefix alias match`() {
    // "url-encode" has alias "url" (exact, score 1000); "slug" has alias "url slug" (prefix, score ~496).
    val ids = search.query("url").map { it.id }
    assertTrue(ids.indexOf("url-encode") < ids.indexOf("slug"))
  }
}
