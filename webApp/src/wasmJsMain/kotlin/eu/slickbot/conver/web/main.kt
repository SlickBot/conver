package eu.slickbot.conver.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import eu.slickbot.conver.App
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.ui.navigation.browserRoute
import kotlinx.browser.document
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
  initKoin { modules(module { single { PlatformDataContext() } }) }
  val registry = KoinPlatform.getKoin().get<ConverterRegistry>()
  ComposeViewport(requireNotNull(document.body)) {
    App(
      onNavHostReady = { navController ->
        // Bind the nav graph to browser history (Back/Forward + the address bar) with clean URL paths.
        navController.bindToBrowserNavigation { entry ->
          browserRoute(entry, registry)
        }
      }
    )
  }
}
