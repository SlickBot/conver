package eu.slickbot.conver.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import java.awt.datatransfer.StringSelection
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberClipboardCopy(): (String) -> Unit {
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()
  return remember(clipboard, scope) {
    { text ->
      scope.launch {
        clipboard.setClipEntry(ClipEntry(StringSelection(text)))
      }
    }
  }
}
