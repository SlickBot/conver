package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.ui.components.ConverScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TextTransformScreen(
  converterId: String,
  onBack: () -> Unit,
  viewModel: TextTransformViewModel = koinViewModel { parametersOf(converterId) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(state.input, state.modeId) {
    if (state.input.isNotEmpty()) viewModel.recordInHistory()
  }

  ConverScaffold(
    title = state.converter.name,
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(onClick = viewModel::toggleFavorite) {
        if (state.isFavorite) {
          Icon(
            Icons.Outlined.Star,
            contentDescription = "Unfavorite",
            tint = MaterialTheme.colorScheme.primary,
          )
        } else {
          Icon(Icons.Outlined.StarOutline, contentDescription = "Favorite")
        }
      }
    },
  ) { padding ->
    Column(
      modifier = Modifier.padding(
        top = padding.calculateTopPadding(),
        bottom = padding.calculateBottomPadding(),
      ),
    ) {
      if (state.converter.modes.size > 1) {
        ModeChips(
          modes = state.converter.modes,
          selectedId = state.modeId,
          onSelect = viewModel::onModeChange,
        )
      }

      Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedTextField(
          value = state.input,
          onValueChange = viewModel::onInputChange,
          label = { Text("Input") },
          placeholder = { Text(state.converter.placeholder) },
          minLines = 3,
          maxLines = 8,
          shape = RoundedCornerShape(20.dp),
          textStyle = if (state.converter.monospace) {
            TextStyle(fontFamily = FontFamily.Monospace)
          } else {
            TextStyle.Default
          },
          modifier = Modifier.fillMaxWidth(),
        )

        OutputBox(
          output = state.output,
          monospace = state.converter.monospace,
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModeChips(
  modes: List<eu.slickbot.conver.domain.converter.TextConverter.Mode>,
  selectedId: String,
  onSelect: (String) -> Unit,
) {
  FlowRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    modes.forEach { mode ->
      FilterChip(
        selected = mode.id == selectedId,
        onClick = { onSelect(mode.id) },
        label = { Text(mode.label) },
      )
    }
  }
}

@Composable
private fun OutputBox(output: String, monospace: Boolean) {
  val clipboard = LocalClipboardManager.current
  val haptic = LocalHapticFeedback.current

  OutlinedTextField(
    value = output,
    onValueChange = {},
    readOnly = true,
    label = { Text("Output") },
    minLines = 3,
    maxLines = 12,
    shape = RoundedCornerShape(20.dp),
    textStyle = if (monospace) TextStyle(fontFamily = FontFamily.Monospace) else TextStyle.Default,
    trailingIcon = {
      IconButton(
        onClick = {
          if (output.isNotEmpty()) {
            clipboard.setText(AnnotatedString(output))
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
        },
        modifier = Modifier.clickable {},
      ) {
        Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy output")
      }
    },
    colors = TextFieldDefaults.colors(),
    modifier = Modifier.fillMaxWidth(),
  )
  Spacer(Modifier.height(4.dp))
}
