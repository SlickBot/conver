package eu.slickbot.conver.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.prefs.UserPreferences
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import eu.slickbot.conver.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
  private val prefsRepo: UserPreferencesRepository,
) : ViewModel() {

  val preferences: StateFlow<UserPreferences> = prefsRepo.preferences
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

  fun setThemeMode(mode: ThemeMode) { viewModelScope.launch { prefsRepo.setThemeMode(mode) } }
  fun setDynamicColor(enabled: Boolean) { viewModelScope.launch { prefsRepo.setDynamicColor(enabled) } }
  fun setHaptics(enabled: Boolean) { viewModelScope.launch { prefsRepo.setHaptics(enabled) } }
  fun setDecimalPrecision(precision: Int) {
    viewModelScope.launch { prefsRepo.setDecimalPrecision(precision) }
  }
}
