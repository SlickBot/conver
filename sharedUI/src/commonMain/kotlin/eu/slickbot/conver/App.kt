package eu.slickbot.conver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import eu.slickbot.conver.model.ThemeMode
import eu.slickbot.conver.ui.navigation.ConverNavHost
import eu.slickbot.conver.ui.theme.ConverTheme
import org.koin.compose.koinInject

// [onNavHostReady] is invoked once with the NavController; the web client uses it to bind the
// nav graph to browser history (Back/Forward + address bar). Other platforms leave it a no-op.
@Composable
fun App(onNavHostReady: suspend (NavHostController) -> Unit = {}) {
  val prefsRepo = koinInject<UserPreferencesRepository>()
  val prefs by prefsRepo.preferences.collectAsStateWithLifecycle(initialValue = null)
  ConverTheme(
    themeMode = prefs?.themeMode ?: ThemeMode.System,
    dynamicColor = prefs?.dynamicColor ?: true,
  ) {
    ConverNavHost(onNavHostReady = onNavHostReady)
  }
}
