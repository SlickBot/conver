package eu.slickbot.conver.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterRegistry

/**
 * Maps a nav back-stack entry to a clean browser URL path for `bindToBrowserNavigation`:
 *   Home `/`, Favorites `/favorites`, Settings `/settings`,
 *   a category `/measurement`, a converter `/money/crypto` (category/converter).
 */
fun browserRoute(entry: NavBackStackEntry, registry: ConverterRegistry): String {
  val dest = entry.destination
  return when {
    dest.hasRoute<Destination.Home>() -> ""
    dest.hasRoute<Destination.Favorites>() -> "favorites"
    dest.hasRoute<Destination.Settings>() -> "settings"
    dest.hasRoute<Destination.CategoryDetail>() -> {
      entry.toRoute<Destination.CategoryDetail>().categoryId.lowercase()
    }
    dest.hasRoute<Destination.Converter>() -> {
      val id = entry.toRoute<Destination.Converter>().converterId
      val category = registry[id]?.category?.name?.lowercase()
      if (category != null) "$category/$id" else id
    }
    else -> ""
  }
}

/**
 * Inverse of [browserRoute]: resolves a clean URL path to the [Destination].
 * Returns null for the empty (home) path or anything unrecognized.
 */
fun browserDestination(path: String, registry: ConverterRegistry): Destination? {
  val segments = path.split('/').filter { it.isNotEmpty() }
  return when (segments.size) {
    0 -> null
    1 -> when (val segment = segments[0]) {
      "favorites" -> Destination.Favorites
      "settings" -> Destination.Settings
      // A lone segment is a category; match its lowercased enum name back to the real id.
      else -> {
        Category.entries
          .firstOrNull { it.name.equals(segment, ignoreCase = true) }
          ?.let { Destination.CategoryDetail(it.name) }
      }
    }
    // category/converter: the converter id is the last segment; only restore if it's real.
    else -> {
      segments
        .last()
        .takeIf { registry[it] != null }
        ?.let { Destination.Converter(it) }
    }
  }
}
