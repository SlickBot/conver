package eu.slickbot.conver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import eu.slickbot.conver.model.ThemeMode
import eu.slickbot.conver.ui.navigation.ConverNavHost
import eu.slickbot.conver.ui.theme.ConverTheme
import org.koin.compose.koinInject

@Composable
fun App() {
  val prefsRepo = koinInject<UserPreferencesRepository>()
  val prefs by prefsRepo.preferences.collectAsStateWithLifecycle(initialValue = null)
  ConverTheme(
    themeMode = prefs?.themeMode ?: ThemeMode.System,
    dynamicColor = prefs?.dynamicColor ?: true,
  ) {
    ConverNavHost()
  }
}
