package eu.slickbot.conver.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
  ) {
    // Paint the theme background behind everything - without this, screens that don't sit on a
    // Scaffold (e.g. Home) show the page's default white, which is invisible against dark-theme text.
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      content()
    }
  }
}
