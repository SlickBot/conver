package eu.slickbot.conver.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import eu.slickbot.conver.App
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import kotlinx.browser.document
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  initKoin { modules(module { single { PlatformDataContext() } }) }
  ComposeViewport(requireNotNull(document.body)) {
    App()
  }
}
