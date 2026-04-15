package eu.slickbot.conver.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

/** Exposed via [LocalConverTypography] so screens can pull Conver's non-Material styles. */
data class ConverExtraTypography(val numericDisplay: TextStyle)

val LocalConverTypography = staticCompositionLocalOf {
  ConverExtraTypography(numericDisplay = NumericDisplay)
}

/**
 * Theme choice exposed through settings.
 * System = follow device light/dark; TrueBlack = AMOLED-friendly pure black surfaces.
 */
enum class ThemeMode { System, Light, Dark, TrueBlack }

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

  val dynamicAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val colorScheme = when {
    themeMode == ThemeMode.TrueBlack -> TrueBlackColors
    dynamicColor && dynamicAvailable -> {
      val ctx = LocalContext.current
      if (effectiveDark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    }
    effectiveDark -> DarkColors
    else -> LightColors
  }

  CompositionLocalProvider(LocalConverTypography provides ConverExtraTypography(NumericDisplay)) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = AppTypography,
      content = content,
    )
  }
}
