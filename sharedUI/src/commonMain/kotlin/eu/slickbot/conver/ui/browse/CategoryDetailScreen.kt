package eu.slickbot.conver.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.icons.imageVector
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryDetailScreen(
  categoryId: String,
  onBack: () -> Unit,
  onConverterClick: (String) -> Unit,
  viewModel: CategoryDetailViewModel = koinViewModel { parametersOf(categoryId) },
) {
  val state = viewModel.uiState

  ConverScaffold(
    title = state.category?.displayName ?: "Category",
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
      }
    },
  ) { padding ->
    if (state.converters.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text("Nothing here yet", style = MaterialTheme.typography.titleMedium)
          Text(
            "Converters in this category are coming soon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(
          top = padding.calculateTopPadding(),
          bottom = padding.calculateBottomPadding() + 24.dp,
          start = 16.dp,
          end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(state.converters, key = { it.id }) { converter ->
          Card(
            onClick = { onConverterClick(converter.id) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            modifier = Modifier.fillMaxWidth(),
          ) {
            ListItem(
              headlineContent = { Text(converter.name) },
              leadingContent = { Icon(converter.icon.imageVector(), contentDescription = null) },
              colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
          }
        }
      }
    }
  }
}
