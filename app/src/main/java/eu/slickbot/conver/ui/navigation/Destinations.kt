package eu.slickbot.conver.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface Destination {
  @Serializable data object Home : Destination
  @Serializable data object Browse : Destination
  @Serializable data object Favorites : Destination
  @Serializable data object Settings : Destination

  @Serializable data class Measurement(val converterId: String) : Destination
}

enum class TopLevelDestination(
  val destination: Destination,
  val label: String,
  val icon: ImageVector,
) {
  Home(Destination.Home, "Home", Icons.Outlined.Home),
  Browse(Destination.Browse, "Browse", Icons.Outlined.Apps),
  Favorites(Destination.Favorites, "Favorites", Icons.Outlined.Star),
  Settings(Destination.Settings, "Settings", Icons.Outlined.Settings),
}
