package eu.slickbot.conver.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import eu.slickbot.conver.App
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import kotlinx.browser.document
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
  initKoin { modules(module { single { PlatformDataContext() } }) }
  ComposeViewport(requireNotNull(document.body)) {
    // Bind the Compose nav graph to browser history so Back/Forward + the address bar work.
    App(onNavHostReady = { it.bindToBrowserNavigation() })
  }
}
