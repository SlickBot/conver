package eu.slickbot.conver.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import eu.slickbot.conver.App
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import org.koin.dsl.module

fun main() {
  initKoin { modules(module { single { PlatformDataContext() } }) }
  application {
    Window(onCloseRequest = ::exitApplication, title = "Conver") { App() }
  }
}
