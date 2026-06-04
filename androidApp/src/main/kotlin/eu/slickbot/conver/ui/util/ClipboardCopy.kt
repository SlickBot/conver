package eu.slickbot.conver.ui.util

import android.content.ClipData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import eu.slickbot.conver.R
import kotlinx.coroutines.launch

@Composable
fun rememberClipboardCopy(): (String) -> Unit {
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()
  val label = stringResource(R.string.app_name)
  return remember(clipboard, scope, label) {
    { text ->
      scope.launch {
        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(label, text)))
      }
    }
  }
}
