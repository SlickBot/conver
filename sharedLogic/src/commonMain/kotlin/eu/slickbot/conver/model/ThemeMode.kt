package eu.slickbot.conver.model

/**
 * Theme choice exposed through settings.
 * System = follow device light/dark; TrueBlack = AMOLED-friendly pure black surfaces.
 */
enum class ThemeMode {
  System, Light, Dark, TrueBlack;

  companion object {
    fun fromName(name: String): ThemeMode {
      return runCatching { ThemeMode.valueOf(name) }.getOrNull() ?: System
    }
  }
}
