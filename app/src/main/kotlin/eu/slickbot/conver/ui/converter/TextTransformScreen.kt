package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.components.scaffoldBodyPadding
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
  val accent = state.converter.category.accent
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
        if (state.isFavorite) Icon(Icons.Outlined.Star, null, tint = accent)
        else Icon(Icons.Outlined.StarOutline, null)
      }
    },
  ) { padding ->
    Column(
      modifier = Modifier
        .scaffoldBodyPadding(padding)
        .verticalScroll(rememberScrollState()),
    ) {
      // Mode chips
      if (state.converter.modes.size > 1) {
        ModeChips(state.converter.modes, state.modeId, onModeChange, accent)
      }

      // Input section - hidden for modes whose output doesn't depend on input
      if (!state.mode.inputless) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp, bottom = 16.dp)) {
          Text(
            "Input",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          )
          Spacer(Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            BasicTextField(
              value = state.input,
              onValueChange = onInputChange,
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = mono,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
              ),
              cursorBrush = SolidColor(accent),
              minLines = 3,
              maxLines = 10,
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              decorationBox = { inner ->
                if (state.input.isEmpty()) {
                  Text(
                    state.converter.placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                    fontFamily = mono,
                  )
                }
                inner()
              },
            )
          }
        }
      }

      Spacer(Modifier.height(8.dp))

      // Result panel
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.12f)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              "Result",
              style = MaterialTheme.typography.labelLarge,
              color = accent.copy(alpha = 0.7f),
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
                  tint = accent.copy(alpha = 0.4f),
                )
              }
            }
          }
          Spacer(Modifier.height(8.dp))
          Text(
            text = state.output.ifEmpty { "\u2026" },
            style = MaterialTheme.typography.bodyLarge.copy(
              fontFamily = mono,
              fontWeight = if (state.output.isNotEmpty()) FontWeight.Medium else FontWeight.Normal,
              lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
            ),
            color = if (state.output.isEmpty()) {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            } else {
              accent
            },
          )
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
  accent: androidx.compose.ui.graphics.Color,
) {
  FlowRow(
    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(0.dp),
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
