package eu.slickbot.conver.domain.search

import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.domain.converter.ConverterRegistry

/**
 * In-memory search over [ConverterRegistry]. Substring match against the converter's display name,
 * category, and aliases. Exact / prefix matches score higher than mid-string substring matches.
 */
class ConverterSearch(private val registry: ConverterRegistry) {

  fun query(q: String, limit: Int = 20): List<Converter> {
    val trimmed = q.trim()
    if (trimmed.isEmpty()) return emptyList()
    val needle = trimmed.lowercase()
    return registry.all
      .mapNotNull { c ->
        val score = score(c, needle) ?: return@mapNotNull null
        c to score
      }
      .sortedByDescending { it.second }
      .take(limit)
      .map { it.first }
  }

  private fun score(c: Converter, needle: String): Int? {
    val haystacks = buildList {
      add(c.name.lowercase())
      add(c.category.displayName.lowercase())
      addAll(c.aliases.map { it.lowercase() })
    }
    var best: Int? = null
    for (h in haystacks) {
      val i = h.indexOf(needle)
      if (i < 0) continue
      val s = when {
        h == needle -> 1000
        i == 0 -> 500 - (h.length - needle.length).coerceAtMost(400)
        else -> 200 - i.coerceAtMost(199)
      }
      if (best == null || s > best) best = s
    }
    return best
  }
}
