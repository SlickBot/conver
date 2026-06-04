package eu.slickbot.conver.ios

import androidx.compose.ui.window.ComposeUIViewController
import eu.slickbot.conver.App
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

private var koinStarted = false

fun initKoinIos() {
  if (koinStarted) return
  koinStarted = true
  initKoin { modules(module { single { PlatformDataContext() } }) }
}

fun MainViewController(): UIViewController {
  initKoinIos()
  return ComposeUIViewController { App() }
}
