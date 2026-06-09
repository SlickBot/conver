package eu.slickbot.conver.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverScaffold(
  title: String,
  modifier: Modifier = Modifier,
  actions: @Composable () -> Unit = {},
  navigationIcon: @Composable () -> Unit = {},
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(title) },
        actions = { actions() },
        navigationIcon = { navigationIcon() },
        // Transparent container so the bar shows the Scaffold/Surface background behind it. The bar
        // background and the page background are the same color (surface == background in every
        // scheme), but Material3's TopAppBar animates its container via animateColorAsState - so on a
        // theme switch the bar would visibly tween while the rest of the screen snaps. Transparent
        // means there's nothing to animate and the bar matches the instant background change.
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.Transparent,
          scrolledContainerColor = Color.Transparent,
        ),
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    content = content,
  )
}

/**
 * Padding for a screen body: applies the Scaffold [insets] (top app bar + nav bar), marks them
 * consumed, then adds [Modifier.imePadding]. Consuming first means imePadding only adds the keyboard
 * height *beyond* the nav bar it already padded - no doubled bottom inset and no gap above the
 * keyboard. Apply before `verticalScroll` (or as a lazy-list modifier) to shrink the viewport.
 */
@Composable
fun Modifier.scaffoldBodyPadding(insets: PaddingValues): Modifier {
  return this
    .padding(insets)
    .consumeWindowInsets(insets)
    .imePadding()
}
