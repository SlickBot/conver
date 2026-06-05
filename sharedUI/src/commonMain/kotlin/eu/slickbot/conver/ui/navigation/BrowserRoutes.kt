package eu.slickbot.conver.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
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
