package eu.slickbot.conver.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Seed color for Conver's brand palette (used when dynamic color is unavailable).
private val Seed = Color(0xFF5B7FFF)

internal val LightColors: ColorScheme = lightColorScheme(
  primary = Color(0xFF2A5BDD),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDCE1FF),
  onPrimaryContainer = Color(0xFF001550),
  secondary = Color(0xFF595D72),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDEE1F9),
  onSecondaryContainer = Color(0xFF161B2C),
  tertiary = Color(0xFF745471),
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFFFD7F8),
  onTertiaryContainer = Color(0xFF2B122B),
  background = Color(0xFFFBF8FF),
  onBackground = Color(0xFF1B1B21),
  surface = Color(0xFFFBF8FF),
  onSurface = Color(0xFF1B1B21),
  surfaceVariant = Color(0xFFE2E1EC),
  onSurfaceVariant = Color(0xFF45464F),
  outline = Color(0xFF757680),
  outlineVariant = Color(0xFFC5C6D0),
)

internal val DarkColors: ColorScheme = darkColorScheme(
  primary = Color(0xFFB6C4FF),
  onPrimary = Color(0xFF002780),
  primaryContainer = Color(0xFF0A3EB3),
  onPrimaryContainer = Color(0xFFDCE1FF),
  secondary = Color(0xFFC2C5DD),
  onSecondary = Color(0xFF2B3042),
  secondaryContainer = Color(0xFF414659),
  onSecondaryContainer = Color(0xFFDEE1F9),
  tertiary = Color(0xFFE2BBDC),
  onTertiary = Color(0xFF432741),
  tertiaryContainer = Color(0xFF5B3D58),
  onTertiaryContainer = Color(0xFFFFD7F8),
  background = Color(0xFF131318),
  onBackground = Color(0xFFE4E1E9),
  surface = Color(0xFF131318),
  onSurface = Color(0xFFE4E1E9),
  surfaceVariant = Color(0xFF45464F),
  onSurfaceVariant = Color(0xFFC5C6D0),
  outline = Color(0xFF8F9099),
  outlineVariant = Color(0xFF45464F),
)

/** AMOLED / true-black dark variant — swaps background & surface to pure black. */
internal val TrueBlackColors: ColorScheme = DarkColors.copy(
  background = Color.Black,
  surface = Color.Black,
  surfaceContainerLowest = Color.Black,
  surfaceContainerLow = Color(0xFF0A0A0C),
  surfaceContainer = Color(0xFF111114),
  surfaceContainerHigh = Color(0xFF17171B),
  surfaceContainerHighest = Color(0xFF1D1D21),
)

/** Category accent tints — used for tiles, headers, and chips in a category. */
internal object CategoryAccents {
  val Measurement = Color(0xFF5B7FFF)
  val Money = Color(0xFF2BB673)
  val Time = Color(0xFFEA7C3F)
  val Numbers = Color(0xFF8F5BE8)
  val Developer = Color(0xFF00A8A8)
  val Color = Color(0xFFE23474)
  val Files = Color(0xFF0F7BD8)
  val Everyday = Color(0xFFC69A1F)
}
