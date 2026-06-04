package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.CalculatorConverter
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.components.scaffoldBodyPadding
import eu.slickbot.conver.ui.icons.accent
import eu.slickbot.conver.ui.util.rememberClipboardCopy
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CalculatorScreen(
  converterId: String,
  onBack: () -> Unit,
  viewModel: CalculatorViewModel = koinViewModel { parametersOf(converterId) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  LaunchedEffect(state.inputs, state.modeId) {
    if (state.inputs.values.any { it.isNotEmpty() }) viewModel.recordInHistory()
  }
  CalculatorScreenContent(
    state = state,
    onFieldChange = viewModel::onFieldChange,
    onModeChange = viewModel::onModeChange,
    onToggleFavorite = viewModel::toggleFavorite,
    onBack = onBack,
  )
}

@Composable
fun CalculatorScreenContent(
  state: CalculatorUiState,
  onFieldChange: (String, String) -> Unit,
  onModeChange: (String) -> Unit,
  onToggleFavorite: () -> Unit,
  onBack: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current
  val copyToClipboard = rememberClipboardCopy()
  val accent = state.converter.category.accent()
  val results = state.results
  val resultText = results.joinToString("\n") { "${it.label} ${it.value}" }

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
        ModeChipRow(state.converter.modes, state.modeId, onModeChange, accent)
      }

      // Input fields
      state.converter.fields.forEach { field ->
        FieldRow(
          field = field,
          value = state.inputs[field.id] ?: "",
          onValueChange = { onFieldChange(field.id, it) },
          accent = accent,
        )
      }

      Spacer(Modifier.height(16.dp))

      // Results panel
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
            if (results.isNotEmpty()) {
              IconButton(
                onClick = {
                  copyToClipboard(resultText)
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
          if (results.isEmpty()) {
            Text(
              "…",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            )
          } else {
            results.forEach { result ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  result.label,
                  style = MaterialTheme.typography.bodyLarge,
                  color = accent.copy(alpha = 0.7f),
                )
                Text(
                  result.value,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = accent,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FieldRow(
  field: CalculatorConverter.Field,
  value: String,
  onValueChange: (String) -> Unit,
  accent: androidx.compose.ui.graphics.Color,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp),
  ) {
    Text(
      field.label,
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(4.dp))
    Row(verticalAlignment = Alignment.Bottom) {
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.Medium,
        ),
        cursorBrush = SolidColor(accent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.weight(1f),
        decorationBox = { inner ->
          Box {
            if (value.isEmpty()) {
              Text(
                field.default?.let {
                  if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString()
                } ?: "0",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                fontWeight = FontWeight.Medium,
              )
            }
            inner()
          }
        },
      )
      if (field.suffix.isNotEmpty()) {
        Spacer(Modifier.width(8.dp))
        Text(
          field.suffix,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.padding(bottom = 2.dp),
        )
      }
    }
    Spacer(Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)),
    )
  }
}

@Composable
private fun ModeChipRow(
  modes: List<CalculatorConverter.Mode>,
  selectedId: String?,
  onSelect: (String) -> Unit,
  accent: androidx.compose.ui.graphics.Color,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(accent.copy(alpha = 0.06f))
      .padding(vertical = 8.dp),
  ) {
    FlowRow(
      modifier = Modifier.padding(horizontal = 24.dp),
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
}
