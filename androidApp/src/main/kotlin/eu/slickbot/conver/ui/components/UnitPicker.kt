package eu.slickbot.conver.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.domain.converter.MeasureUnit

@Composable
fun UnitPicker(
  label: String,
  units: List<MeasureUnit>,
  selectedId: String,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  val selected = units.firstOrNull { it.id == selectedId } ?: units.first()

  Column(modifier = modifier) {
    Text(
      text = label.uppercase(),
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
    )
    OutlinedButton(
      onClick = { expanded = true },
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column {
          Text(selected.name, style = MaterialTheme.typography.bodyLarge)
          Text(
            selected.symbol,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Icon(Icons.Outlined.ExpandMore, contentDescription = null)
      }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      units.forEach { unit ->
        DropdownMenuItem(
          text = { Text("${unit.name} (${unit.symbol})") },
          onClick = {
            onSelect(unit.id)
            expanded = false
          },
        )
      }
    }
  }
}
