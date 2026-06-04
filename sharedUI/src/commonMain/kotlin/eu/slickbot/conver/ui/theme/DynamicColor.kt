package eu.slickbot.conver.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/** Platform dynamic (wallpaper-based) color scheme, or null if unavailable. */
@Composable
expect fun dynamicColorScheme(dark: Boolean): ColorScheme?
