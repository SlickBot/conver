package eu.slickbot.conver.ios

import androidx.compose.ui.window.ComposeUIViewController
import eu.slickbot.conver.ui.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
