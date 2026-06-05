package eu.slickbot.conver.ui.converter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.formatResult
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.icons.accent
import eu.slickbot.conver.ui.components.scaffoldBodyPadding
import eu.slickbot.conver.ui.util.rememberClipboardCopy
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MeasurementScreen(
  converterId: String,
  onBack: (() -> Unit)? = null,
  viewModel: MeasurementViewModel = koinViewModel(key = converterId) { parametersOf(converterId) },
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
  onBack: (() -> Unit)? = null,
) {
  val haptic = LocalHapticFeedback.current
  val copyToClipboard = rememberClipboardCopy()
  val result = state.resultString.ifEmpty { "0" }
  val accent = state.converter.category.accent()

  var swapTicks by remember { mutableIntStateOf(0) }
  val swapRotation by animateFloatAsState(
    targetValue = swapTicks * 180f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow,
    ),
    label = "swap",
  )

  ConverScaffold(
    title = state.converter.name,
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
        }
      }
    },
    actions = {
      IconButton(onClick = onToggleFavorite) {
        if (state.isFavorite) Icon(Icons.Outlined.Star, null, tint = accent)
        else Icon(Icons.Outlined.StarOutline, null)
      }
    },
  ) { padding ->
    LazyVerticalGrid(
      modifier = Modifier.scaffoldBodyPadding(padding),
      columns = GridCells.Fixed(3),
      contentPadding = PaddingValues(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      // --- FROM + Swap + TO ---
      item(span = { GridItemSpan(3) }) {
        val density = LocalDensity.current
        val halfButton = with(density) { 22.dp.roundToPx() }
        var fromHeight by remember { mutableIntStateOf(0) }

        Box(modifier = Modifier.fillMaxWidth()) {
          Column {
            // FROM
            Column(
              modifier = Modifier
                .onSizeChanged { fromHeight = it.height }
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            ) {
              UnitPill(
                unit = state.converter.unit(state.fromUnitId),
                allUnits = state.converter.units,
                onUnitChange = onFromChange,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(Modifier.height(8.dp))
              BasicTextField(
                value = state.input,
                onValueChange = onInputChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.displaySmall.copy(
                  color = MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = (-1).sp,
                ),
                cursorBrush = SolidColor(accent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                decorationBox = { inner ->
                  if (state.input.isEmpty()) {
                    Text(
                      "0",
                      style = MaterialTheme.typography.displaySmall,
                      color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                      fontWeight = FontWeight.Bold,
                      letterSpacing = (-1).sp,
                    )
                  }
                  inner()
                },
              )
            }
            // TO
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      accent.copy(alpha = 0.15f),
                      accent.copy(alpha = 0.04f),
                    ),
                  ),
                )
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 20.dp),
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                UnitPill(
                  unit = state.converter.unit(state.toUnitId),
                  allUnits = state.converter.units,
                  onUnitChange = onToChange,
                  color = accent,
                  modifier = Modifier.weight(1f),
                )
                IconButton(
                  onClick = {
                    copyToClipboard(result)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  },
                  modifier = Modifier.size(36.dp),
                ) {
                  Icon(
                    Icons.Outlined.ContentCopy, "Copy",
                    modifier = Modifier.size(18.dp),
                    tint = accent.copy(alpha = 0.5f),
                  )
                }
              }
              Spacer(Modifier.height(8.dp))
              AnimatedContent(
                targetState = result,
                transitionSpec = {
                  (slideInVertically { it / 4 } + fadeIn(tween(200)))
                    .togetherWith(slideOutVertically { -it / 4 } + fadeOut(tween(100)))
                },
                label = "result",
              ) { shown ->
                Text(
                  text = shown,
                  style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp,
                  ),
                  color = accent,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }

          // Swap button overlaid at boundary
          Surface(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              swapTicks += 1
              onSwap()
            },
            modifier = Modifier
              .size(44.dp)
              .align(Alignment.TopCenter)
              .offset { IntOffset(0, fromHeight - halfButton) }
              .zIndex(1f),
            shape = CircleShape,
            color = accent,
            contentColor = Color.White,
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Outlined.SwapVert, "Swap",
                modifier = Modifier
                  .size(22.dp)
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
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.padding(start = 24.dp, top = 12.dp, bottom = 4.dp),
        )
      }

      // --- Quick units grid ---
      items(state.otherUnits, key = { it.id }) { unit ->
        QuickUnitChip(
          unit = unit,
          value = computeValue(state, unit),
          accent = accent,
          onClick = { onToChange(unit.id) },
          modifier = Modifier.padding(
            start = if (state.otherUnits.indexOf(unit) % 3 == 0) 16.dp else 0.dp,
            end = if (state.otherUnits.indexOf(unit) % 3 == 2) 16.dp else 0.dp,
          ),
        )
      }
    }
  }
}

@Composable
private fun UnitPill(
  unit: MeasureUnit,
  allUnits: List<MeasureUnit>,
  onUnitChange: (String) -> Unit,
  color: Color,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  Box(modifier = modifier) {
    Surface(
      onClick = { expanded = true },
      shape = RoundedCornerShape(20.dp),
      color = Color.Transparent,
      border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          unit.name,
          style = MaterialTheme.typography.labelLarge,
          color = color,
        )
        Spacer(Modifier.width(4.dp))
        Text(
          unit.symbol,
          style = MaterialTheme.typography.labelLarge,
          color = color.copy(alpha = 0.5f),
        )
        Spacer(Modifier.width(2.dp))
        Icon(
          Icons.Outlined.UnfoldMore, null,
          modifier = Modifier.size(14.dp),
          tint = color.copy(alpha = 0.4f),
        )
      }
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
  accent: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(14.dp),
    color = accent.copy(alpha = 0.06f),
    border = BorderStroke(1.dp, accent.copy(alpha = 0.1f)),
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
      Text(
        text = value.ifEmpty { "—" },
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = if (value.isNotEmpty()) accent else MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = unit.symbol,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
      )
    }
  }
}

private fun computeValue(state: MeasurementUiState, unit: MeasureUnit): String {
  val v = state.inputValue ?: return ""
  return formatResult(state.converter.convert(v, state.fromUnitId, unit.id))
}
