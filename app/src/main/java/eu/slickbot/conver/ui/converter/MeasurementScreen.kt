package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.formatResult
import eu.slickbot.conver.ui.components.ConverScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MeasurementScreen(
  converterId: String,
  onBack: () -> Unit,
  viewModel: MeasurementViewModel = koinViewModel { parametersOf(converterId) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  LaunchedEffect(state.input, state.fromUnitId, state.toUnitId) {
    if (state.inputValue != null) viewModel.recordInHistory()
  }
  MeasurementScreenContent(
    state = state,
    onInputChange = viewModel::onInputChange,
    onFromChange = viewModel::onFromChange,
    onToChange = viewModel::onToChange,
    onSwap = viewModel::swap,
    onToggleFavorite = viewModel::toggleFavorite,
    onBack = onBack,
  )
}

@Composable
fun MeasurementScreenContent(
  state: MeasurementUiState,
  onInputChange: (String) -> Unit,
  onFromChange: (String) -> Unit,
  onToChange: (String) -> Unit,
  onSwap: () -> Unit,
  onToggleFavorite: () -> Unit,
  onBack: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current
  val clipboard = LocalClipboardManager.current
  val result = state.resultString.ifEmpty { "0" }

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
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      contentPadding = PaddingValues(
        top = padding.calculateTopPadding() + 8.dp,
        bottom = padding.calculateBottomPadding() + 24.dp,
        start = 16.dp, end = 16.dp,
      ),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      // --- Conversion pair (full span) ---
      item(span = { GridItemSpan(3) }) {
        Box {
          Column {
            // FROM field
            ConversionField(
              value = state.input,
              onValueChange = onInputChange,
              unit = state.converter.unit(state.fromUnitId),
              allUnits = state.converter.units,
              onUnitChange = onFromChange,
              editable = true,
              shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
              containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
              contentColor = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(3.dp))
            // TO field
            ConversionField(
              value = result,
              onValueChange = {},
              unit = state.converter.unit(state.toUnitId),
              allUnits = state.converter.units,
              onUnitChange = onToChange,
              editable = false,
              shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
              trailingIcon = {
                IconButton(onClick = {
                  clipboard.setText(AnnotatedString(result))
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }) {
                  Icon(
                    Icons.Outlined.ContentCopy, "Copy",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp),
                  )
                }
              },
            )
          }
          // Floating swap button
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .zIndex(1f),
          ) {
            FilledIconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSwap()
              },
              modifier = Modifier.size(44.dp),
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
              ),
            ) {
              Icon(Icons.Outlined.SwapVert, "Swap", modifier = Modifier.size(22.dp))
            }
          }
        }
      }

      // --- Quick units grid ---
      item(span = { GridItemSpan(3) }) {
        Spacer(Modifier.height(4.dp))
      }
      items(state.otherUnits, key = { it.id }) { unit ->
        QuickUnitChip(
          unit = unit,
          value = computeValue(state, unit),
          onClick = { onToChange(unit.id) },
        )
      }
    }
  }
}

@Composable
private fun ConversionField(
  value: String,
  onValueChange: (String) -> Unit,
  unit: MeasureUnit,
  allUnits: List<MeasureUnit>,
  onUnitChange: (String) -> Unit,
  editable: Boolean,
  shape: RoundedCornerShape,
  containerColor: androidx.compose.ui.graphics.Color,
  contentColor: androidx.compose.ui.graphics.Color,
  trailingIcon: @Composable (() -> Unit)? = null,
) {
  var expanded by remember { mutableStateOf(false) }

  Surface(
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        if (editable) {
          BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
              color = contentColor,
              fontWeight = FontWeight.SemiBold,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            decorationBox = { innerTextField ->
              if (value.isEmpty()) {
                Text(
                  "0",
                  style = MaterialTheme.typography.headlineMedium,
                  color = contentColor.copy(alpha = 0.4f),
                  fontWeight = FontWeight.SemiBold,
                )
              }
              innerTextField()
            },
          )
        } else {
          Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Spacer(Modifier.height(2.dp))
        // Unit selector
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(vertical = 2.dp, horizontal = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "${unit.name} (${unit.symbol})",
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor.copy(alpha = 0.7f),
          )
          Spacer(Modifier.width(4.dp))
          Icon(
            Icons.Outlined.UnfoldMore, null,
            modifier = Modifier.size(16.dp),
            tint = contentColor.copy(alpha = 0.5f),
          )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          allUnits.forEach { u ->
            DropdownMenuItem(
              text = { Text("${u.name} (${u.symbol})") },
              onClick = { onUnitChange(u.id); expanded = false },
            )
          }
        }
      }
      trailingIcon?.invoke()
    }
  }
}

@Composable
private fun QuickUnitChip(
  unit: MeasureUnit,
  value: String,
  onClick: () -> Unit,
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
      Text(
        text = value.ifEmpty { "—" },
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = unit.symbol,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

private fun computeValue(state: MeasurementUiState, unit: MeasureUnit): String {
  val v = state.inputValue ?: return ""
  return formatResult(state.converter.convert(v, state.fromUnitId, unit.id))
}
