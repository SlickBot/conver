package eu.slickbot.conver.ui.util

import androidx.compose.runtime.Composable

/** Returns a callback that copies [text] to the system clipboard. */
@Composable
expect fun rememberClipboardCopy(): (String) -> Unit
