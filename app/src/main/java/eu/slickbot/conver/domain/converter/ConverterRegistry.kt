package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.lengthConverter

/**
 * Central catalog of every Converter in the app. Browse, search, and deep-links all read from here.
 * Adding a new converter = append one entry to [default].
 */
class ConverterRegistry(val all: List<Converter>) {

  private val byId = all.associateBy { it.id }

  operator fun get(id: String): Converter? = byId[id]

  fun byCategory(category: Category): List<Converter> = all.filter { it.category == category }

  companion object {
    fun default(): ConverterRegistry = ConverterRegistry(
      listOf(
        lengthConverter(),
      )
    )
  }
}
