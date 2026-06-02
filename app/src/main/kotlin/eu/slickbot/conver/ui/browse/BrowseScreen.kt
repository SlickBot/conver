package eu.slickbot.conver.ui.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.ui.components.ConverScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrowseScreen(
  onConverterClick: (String) -> Unit,
  viewModel: BrowseViewModel = koinViewModel(),
) {
  ConverScaffold(title = "Browse") { padding ->
    LazyColumn(
      contentPadding = PaddingValues(
        top = padding.calculateTopPadding(),
        bottom = 24.dp,
      ),
    ) {
      viewModel.uiState.sections.forEach { (category, converters) ->
        item(key = "h-${category.name}") {
          Text(
            text = category.displayName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }
        items(converters, key = { it.id }) { c ->
          ListItem(
            headlineContent = { Text(c.name) },
            supportingContent = { Text(c.category.displayName) },
            leadingContent = { Icon(c.icon, contentDescription = null) },
            modifier = Modifier
              .clickable { onConverterClick(c.id) }
              .padding(horizontal = 8.dp),
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
          )
        }
      }
    }
  }
}
