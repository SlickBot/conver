package eu.slickbot.conver.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import eu.slickbot.conver.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "conver_prefs")

data class UserPreferences(
  val themeMode: ThemeMode = ThemeMode.System,
  val dynamicColor: Boolean = true,
  val haptics: Boolean = true,
  val decimalPrecision: Int = 6,
)

class UserPreferencesRepository(context: Context) {

  private val store: DataStore<Preferences> = context.applicationContext.dataStore

  val preferences: Flow<UserPreferences> = store.data.map { prefs ->
    UserPreferences(
      themeMode = prefs[Keys.ThemeMode]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
        ?: ThemeMode.System,
      dynamicColor = prefs[Keys.DynamicColor] ?: true,
      haptics = prefs[Keys.Haptics] ?: true,
      decimalPrecision = prefs[Keys.DecimalPrecision] ?: 6,
    )
  }

  suspend fun setThemeMode(mode: ThemeMode) {
    store.edit { it[Keys.ThemeMode] = mode.name }
  }

  suspend fun setDynamicColor(enabled: Boolean) {
    store.edit { it[Keys.DynamicColor] = enabled }
  }

  suspend fun setHaptics(enabled: Boolean) {
    store.edit { it[Keys.Haptics] = enabled }
  }

  suspend fun setDecimalPrecision(precision: Int) {
    store.edit { it[Keys.DecimalPrecision] = precision.coerceIn(0, 12) }
  }

  private object Keys {
    val ThemeMode = stringPreferencesKey("theme_mode")
    val DynamicColor = booleanPreferencesKey("dynamic_color")
    val Haptics = booleanPreferencesKey("haptics")
    val DecimalPrecision = intPreferencesKey("decimal_precision")
  }
}
