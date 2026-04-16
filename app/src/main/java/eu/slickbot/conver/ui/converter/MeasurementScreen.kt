package eu.slickbot.conver.ui.converter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
  val fromUnit = state.converter.unit(state.fromUnitId)
  val toUnit = state.converter.unit(state.toUnitId)

  // Swap button rotation
  var swapTicks by remember { mutableIntStateOf(0) }
  val swapRotation by animateFloatAsState(
    targetValue = swapTicks * 180f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    label = "swap",
  )

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
      // --- Conversion card (full span) ---
      item(span = { GridItemSpan(3) }) {
        Surface(
          shape = RoundedCornerShape(28.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Box {
            Column {
              // FROM section
              ConversionHalf(
                value = state.input,
                onValueChange = onInputChange,
                unit = fromUnit,
                allUnits = state.converter.units,
                onUnitChange = onFromChange,
                editable = true,
                valueColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
              )

              // Divider
              HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 20.dp),
              )

              // TO section
              Row(
                modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 20.dp),
                verticalAlignment = Alignment.Top,
              ) {
                ConversionHalf(
                  value = result,
                  onValueChange = {},
                  unit = toUnit,
                  allUnits = state.converter.units,
                  onUnitChange = onToChange,
                  editable = false,
                  valueColor = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.weight(1f),
                )
                IconButton(onClick = {
                  clipboard.setText(AnnotatedString(result))
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }) {
                  Icon(
                    Icons.Outlined.ContentCopy, "Copy",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                  )
                }
              }
            }

            // Floating swap FAB on the divider
            SmallFloatingActionButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                swapTicks += 1
                onSwap()
              },
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .zIndex(1f),
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
              Icon(
                Icons.Outlined.SwapVert, "Swap",
                modifier = Modifier
                  .size(20.dp)
                  .rotate(swapRotation),
              )
            }
          }
        }
      }

      // --- Quick units header ---
      item(span = { GridItemSpan(3) }) {
        Text(
          "All units",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
        )
      }

      // --- Quick units grid ---
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
private fun ConversionHalf(
  value: String,
  onValueChange: (String) -> Unit,
  unit: MeasureUnit,
  allUnits: List<MeasureUnit>,
  onUnitChange: (String) -> Unit,
  editable: Boolean,
  valueColor: Color,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Column(modifier = modifier) {
    // Big number
    if (editable) {
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineLarge.copy(
          color = valueColor,
          fontWeight = FontWeight.Bold,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        decorationBox = { inner ->
          if (value.isEmpty()) {
            Text(
              "0",
              style = MaterialTheme.typography.headlineLarge,
              color = valueColor.copy(alpha = 0.3f),
              fontWeight = FontWeight.Bold,
            )
          }
          inner()
        },
      )
    } else {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
        color = valueColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    Spacer(Modifier.height(4.dp))

    // Unit pill
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .clickable { expanded = true }
        .padding(vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        unit.name,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(Modifier.width(2.dp))
      Text(
        "(${unit.symbol})",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
      )
      Spacer(Modifier.width(4.dp))
      Icon(
        Icons.Outlined.UnfoldMore, null,
        modifier = Modifier.size(16.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
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
