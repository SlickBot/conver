package eu.slickbot.conver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import eu.slickbot.conver.ui.navigation.ConverNavHost
import eu.slickbot.conver.ui.theme.ConverTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { ConverRoot() }
  }
}

@Composable
fun ConverRoot() {
  val prefsRepo: UserPreferencesRepository = koinInject()
  val prefs by prefsRepo.preferences.collectAsStateWithLifecycle(initialValue = null)

  val settings = prefs
  ConverTheme(
    themeMode = settings?.themeMode ?: eu.slickbot.conver.ui.theme.ThemeMode.System,
    dynamicColor = settings?.dynamicColor ?: true,
  ) {
    ConverNavHost()
  }
}
