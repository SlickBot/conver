package eu.slickbot.conver.data.prefs

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getIntFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import eu.slickbot.conver.model.ThemeMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

data class UserPreferences(
  val themeMode: ThemeMode = ThemeMode.System,
  val dynamicColor: Boolean = true,
  val haptics: Boolean = true,
  val decimalPrecision: Int = 6,
)

class UserPreferencesRepository(
  private val settings: ObservableSettings,
  private val dispatcher: CoroutineDispatcher,
) {

  companion object {
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_DYNAMIC_COLOR = "dynamic_color"
    private const val KEY_HAPTICS = "haptics"
    private const val KEY_DECIMAL_PRECISION = "decimal_precision"
  }

  val preferences: Flow<UserPreferences> = combine(
    settings.getStringOrNullFlow(KEY_THEME_MODE),
    settings.getBooleanFlow(KEY_DYNAMIC_COLOR, true),
    settings.getBooleanFlow(KEY_HAPTICS, true),
    settings.getIntFlow(KEY_DECIMAL_PRECISION, 6),
  ) { themeName, dynamicColor, haptics, precision ->
    UserPreferences(
      themeMode = themeName?.let { ThemeMode.fromName(it) } ?: ThemeMode.System,
      dynamicColor = dynamicColor,
      haptics = haptics,
      decimalPrecision = precision,
    )
  }

  suspend fun setThemeMode(mode: ThemeMode) {
    withContext(dispatcher) { settings.putString(KEY_THEME_MODE, mode.name) }
  }

  suspend fun setDynamicColor(enabled: Boolean) {
    withContext(dispatcher) { settings.putBoolean(KEY_DYNAMIC_COLOR, enabled) }
  }

  suspend fun setHaptics(enabled: Boolean) {
    withContext(dispatcher) { settings.putBoolean(KEY_HAPTICS, enabled) }
  }

  suspend fun setDecimalPrecision(precision: Int) {
    withContext(dispatcher) { settings.putInt(KEY_DECIMAL_PRECISION, precision.coerceIn(0, 12)) }
  }
}
