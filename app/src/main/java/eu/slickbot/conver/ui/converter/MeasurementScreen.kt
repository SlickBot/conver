package eu.slickbot.conver.ui.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.formatResult
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.components.ResultDisplay
import eu.slickbot.conver.ui.components.SwapButton
import eu.slickbot.conver.ui.components.UnitPicker
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MeasurementScreen(
  converterId: String,
  onBack: () -> Unit,
  viewModel: MeasurementViewModel = koinViewModel { parametersOf(converterId) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  // Persist a history row whenever the user types a new valid value.
  LaunchedEffect(state.input, state.fromUnitId, state.toUnitId) {
    if (state.inputValue != null) viewModel.recordInHistory()
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
          Icon(Icons.Outlined.Star, contentDescription = "Unfavorite", tint = MaterialTheme.colorScheme.primary)
        } else {
          Icon(Icons.Outlined.StarOutline, contentDescription = "Favorite")
        }
      }
    },
  ) { padding ->
    LazyColumn(
      contentPadding = PaddingValues(
        top = padding.calculateTopPadding(),
        bottom = padding.calculateBottomPadding() + 24.dp,
        start = 16.dp,
        end = 16.dp,
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        OutlinedTextField(
          value = state.input,
          onValueChange = viewModel::onInputChange,
          singleLine = true,
          label = { Text("Value") },
          placeholder = { Text("Enter a number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          shape = RoundedCornerShape(20.dp),
          textStyle = MaterialTheme.typography.headlineSmall,
          modifier = Modifier.fillMaxWidth(),
        )
      }
      item {
        androidx.compose.foundation.layout.Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          UnitPicker(
            label = "From",
            units = state.converter.units,
            selectedId = state.fromUnitId,
            onSelect = viewModel::onFromChange,
            modifier = Modifier.weight(1f),
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.size(20.dp))
            SwapButton(onSwap = viewModel::swap)
          }
          UnitPicker(
            label = "To",
            units = state.converter.units,
            selectedId = state.toUnitId,
            onSelect = viewModel::onToChange,
            modifier = Modifier.weight(1f),
          )
        }
      }
      item {
        ResultDisplay(
          value = state.resultString.ifEmpty { "0" },
          unitSymbol = state.converter.unit(state.toUnitId).let { "${it.name} (${it.symbol})" },
          textToCopy = state.resultString,
        )
      }
      item {
        SectionTitle("All units")
      }
      items(state.otherUnits, key = { it.id }) { unit ->
        AllUnitRow(
          unit = unit,
          valueDisplay = computeValue(state, unit),
          onPick = { viewModel.onToChange(unit.id) },
        )
      }
      item {
        Spacer(Modifier.width(0.dp))
      }
    }
  }
}

private fun computeValue(state: MeasurementUiState, unit: MeasureUnit): String {
  val v = state.inputValue ?: return ""
  val converted = state.converter.convert(v, state.fromUnitId, unit.id)
  return formatResult(converted)
}

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )
}

@Composable
private fun AllUnitRow(
  unit: MeasureUnit,
  valueDisplay: String,
  onPick: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onPick),
  ) {
    ListItem(
      headlineContent = { Text(valueDisplay.ifEmpty { "—" }) },
      supportingContent = { Text("${unit.name} (${unit.symbol})") },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
  }
}
