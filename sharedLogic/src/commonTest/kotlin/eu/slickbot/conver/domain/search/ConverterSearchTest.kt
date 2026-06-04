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
}
