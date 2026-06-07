package eu.slickbot.conver.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.model.ThemeMode
import eu.slickbot.conver.ui.components.ConverScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
  onBack: () -> Unit,
  viewModel: SettingsViewModel = koinViewModel(),
) {
  val prefs by viewModel.preferences.collectAsStateWithLifecycle()

  ConverScaffold(
    title = "Settings",
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
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
      item { SectionHeader("Appearance") }
      item {
        ThemeCard(selected = prefs.themeMode, onSelect = viewModel::setThemeMode)
      }
      item {
        SwitchRow(
          title = "Dynamic color",
          subtitle = "Use the wallpaper palette (Android 12+)",
          checked = prefs.dynamicColor,
          onChange = viewModel::setDynamicColor,
        )
      }
      item { SectionHeader("Behavior") }
      item {
        SwitchRow(
          title = "Haptics",
          subtitle = "Vibrate on swap and copy",
          checked = prefs.haptics,
          onChange = viewModel::setHaptics,
        )
      }
      item {
        PrecisionCard(precision = prefs.decimalPrecision, onChange = viewModel::setDecimalPrecision)
      }
    }
  }
}

@Composable
private fun ThemeCard(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .selectableGroup(),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text("Theme", style = MaterialTheme.typography.titleSmall)
      ChipRow(
        items = ThemeMode.entries,
        isSelected = { it == selected },
        onSelect = onSelect,
        label = { it.display() },
      )
    }
  }
}

@Composable
private fun PrecisionCard(precision: Int, onChange: (Int) -> Unit) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text("Decimal precision", style = MaterialTheme.typography.titleSmall)
      Text(
        "$precision digits",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Slider(
        value = precision.toFloat(),
        onValueChange = { onChange(it.toInt()) },
        valueRange = 0f..12f,
        steps = 11,
      )
    }
  }
}

@Composable
private fun SectionHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )
}

@Composable
private fun SwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onChange: (Boolean) -> Unit,
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ),
    modifier = Modifier.fillMaxWidth(),
  ) {
    ListItem(
      headlineContent = { Text(title) },
      supportingContent = { Text(subtitle) },
      trailingContent = {
        Switch(checked = checked, onCheckedChange = onChange)
      },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
  }
}

@Composable
private fun <T> ChipRow(
  items: List<T>,
  isSelected: (T) -> Boolean,
  onSelect: (T) -> Unit,
  label: (T) -> String,
) {
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items.forEach { item ->
      FilterChip(
        selected = isSelected(item),
        onClick = { onSelect(item) },
        label = { Text(label(item)) },
        colors = FilterChipDefaults.filterChipColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
          selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        border = null,
      )
    }
  }
}

private fun ThemeMode.display(): String = when (this) {
  ThemeMode.System -> "System"
  ThemeMode.Light -> "Light"
  ThemeMode.Dark -> "Dark"
  ThemeMode.TrueBlack -> "True black"
}
