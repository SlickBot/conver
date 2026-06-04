package eu.slickbot.conver.ui.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.icons.imageVector
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
  onConverterClick: (String) -> Unit,
  viewModel: FavoritesViewModel = koinViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  ConverScaffold(title = "Favorites") { padding ->
    if (state.favorites.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Icon(
            Icons.Outlined.StarOutline,
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            "No favorites yet",
            style = MaterialTheme.typography.titleMedium,
          )
          Text(
            "Star a converter to pin it here for one-tap access.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    } else {
      LazyColumn(contentPadding = padding) {
        items(state.favorites, key = { it.id }) { c ->
          ListItem(
            headlineContent = { Text(c.name) },
            supportingContent = { Text(c.category.displayName) },
            leadingContent = { Icon(c.icon.imageVector(), contentDescription = null) },
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
