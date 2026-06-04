package eu.slickbot.conver.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import eu.slickbot.conver.ui.App

fun main() = application {
  Window(onCloseRequest = ::exitApplication, title = "Conver") {
    App()
  }
}
