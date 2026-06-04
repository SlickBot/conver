package eu.slickbot.conver.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import eu.slickbot.conver.model.ThemeMode

@Composable
fun ConverTheme(
  themeMode: ThemeMode = ThemeMode.System,
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val systemDark = isSystemInDarkTheme()
  val effectiveDark = when (themeMode) {
    ThemeMode.System -> systemDark
    ThemeMode.Light -> false
    ThemeMode.Dark, ThemeMode.TrueBlack -> true
  }

  val colorScheme = when {
    themeMode == ThemeMode.TrueBlack -> TrueBlackColors
    dynamicColor -> dynamicColorScheme(effectiveDark) ?: if (effectiveDark) DarkColors else LightColors
    effectiveDark -> DarkColors
    else -> LightColors
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content,
  )
}
