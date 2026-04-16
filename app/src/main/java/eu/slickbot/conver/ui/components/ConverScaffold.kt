package eu.slickbot.conver.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

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
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    content = content,
  )
}
