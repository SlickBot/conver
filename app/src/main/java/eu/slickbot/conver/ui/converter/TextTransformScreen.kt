package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
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
  TextTransformScreenContent(
    state = state,
    onInputChange = viewModel::onInputChange,
    onModeChange = viewModel::onModeChange,
    onToggleFavorite = viewModel::toggleFavorite,
    onBack = onBack,
  )
}

@Composable
fun TextTransformScreenContent(
  state: TextTransformUiState,
  onInputChange: (String) -> Unit,
  onModeChange: (String) -> Unit,
  onToggleFavorite: () -> Unit,
  onBack: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current
  val clipboard = LocalClipboardManager.current
  val mono = if (state.converter.monospace) FontFamily.Monospace else FontFamily.Default

  ConverScaffold(
    title = state.converter.name,
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
      }
    },
    actions = {
      IconButton(onClick = onToggleFavorite) {
        if (state.isFavorite) Icon(Icons.Outlined.Star, null, tint = MaterialTheme.colorScheme.primary)
        else Icon(Icons.Outlined.StarOutline, null)
      }
    },
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
    ) {
      // Mode chips
      if (state.converter.modes.size > 1) {
        ModeChips(state.converter.modes, state.modeId, onModeChange)
        Spacer(Modifier.height(12.dp))
      }

      // Single card: Input + divider + Output
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          // Input section
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              "Input",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(8.dp))
            BasicTextField(
              value = state.input,
              onValueChange = onInputChange,
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = mono,
              ),
              cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
              minLines = 3,
              maxLines = 8,
              modifier = Modifier.fillMaxWidth(),
              decorationBox = { inner ->
                if (state.input.isEmpty()) {
                  Text(
                    state.converter.placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    fontFamily = mono,
                  )
                }
                inner()
              },
            )
          }

          // Divider
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            modifier = Modifier.padding(horizontal = 20.dp),
          )

          // Output section
          Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                "Result",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f),
              )
              if (state.output.isNotEmpty()) {
                IconButton(
                  onClick = {
                    clipboard.setText(AnnotatedString(state.output))
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  },
                  modifier = Modifier.size(32.dp),
                ) {
                  Icon(
                    Icons.Outlined.ContentCopy, "Copy",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                  )
                }
              }
            }
            Spacer(Modifier.height(4.dp))
            Text(
              text = state.output.ifEmpty { "…" },
              style = MaterialTheme.typography.bodyLarge.copy(fontFamily = mono),
              color = if (state.output.isEmpty()) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
              } else {
                MaterialTheme.colorScheme.primary
              },
              minLines = 2,
            )
          }
        }
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
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
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
