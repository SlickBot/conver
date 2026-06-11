package eu.slickbot.conver.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import eu.slickbot.conver.model.ThemeMode

private const val THEME_TRANSITION_MILLIS = 350

@Composable
fun ConverTheme(
  modifier: Modifier = Modifier,
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

  val target = when {
    themeMode == ThemeMode.TrueBlack -> TrueBlackColors
    dynamicColor -> dynamicColorScheme(effectiveDark) ?: if (effectiveDark) DarkColors else LightColors
    effectiveDark -> DarkColors
    else -> LightColors
  }

  val colorScheme = animateColorScheme(target)

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
  ) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      content()
    }
  }
}

/**
 * Returns [target] with every rendered color driven through [animateColorAsState], so a theme change
 * tweens uniformly instead of snapping. Unlisted fixed-accent colors aren't used by the app, so they
 * keep [target]'s value. animateColorAsState seeds itself with the first value, so there's no
 * spurious animation on the first composition.
 */
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
  val spec: AnimationSpec<Color> = tween(durationMillis = THEME_TRANSITION_MILLIS)

  @Composable
  fun anim(value: Color): Color {
    return animateColorAsState(value, spec).value
  }

  return target.copy(
    primary = anim(target.primary),
    onPrimary = anim(target.onPrimary),
    primaryContainer = anim(target.primaryContainer),
    onPrimaryContainer = anim(target.onPrimaryContainer),
    inversePrimary = anim(target.inversePrimary),
    secondary = anim(target.secondary),
    onSecondary = anim(target.onSecondary),
    secondaryContainer = anim(target.secondaryContainer),
    onSecondaryContainer = anim(target.onSecondaryContainer),
    tertiary = anim(target.tertiary),
    onTertiary = anim(target.onTertiary),
    tertiaryContainer = anim(target.tertiaryContainer),
    onTertiaryContainer = anim(target.onTertiaryContainer),
    background = anim(target.background),
    onBackground = anim(target.onBackground),
    surface = anim(target.surface),
    onSurface = anim(target.onSurface),
    surfaceVariant = anim(target.surfaceVariant),
    onSurfaceVariant = anim(target.onSurfaceVariant),
    surfaceTint = anim(target.surfaceTint),
    inverseSurface = anim(target.inverseSurface),
    inverseOnSurface = anim(target.inverseOnSurface),
    error = anim(target.error),
    onError = anim(target.onError),
    errorContainer = anim(target.errorContainer),
    onErrorContainer = anim(target.onErrorContainer),
    outline = anim(target.outline),
    outlineVariant = anim(target.outlineVariant),
    scrim = anim(target.scrim),
    surfaceBright = anim(target.surfaceBright),
    surfaceDim = anim(target.surfaceDim),
    surfaceContainer = anim(target.surfaceContainer),
    surfaceContainerHigh = anim(target.surfaceContainerHigh),
    surfaceContainerHighest = anim(target.surfaceContainerHighest),
    surfaceContainerLow = anim(target.surfaceContainerLow),
    surfaceContainerLowest = anim(target.surfaceContainerLowest),
  )
}
