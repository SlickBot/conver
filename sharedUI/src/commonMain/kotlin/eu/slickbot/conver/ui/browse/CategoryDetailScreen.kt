package eu.slickbot.conver.ui.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.ui.components.ConverScaffold
import eu.slickbot.conver.ui.icons.imageVector
import eu.slickbot.conver.ui.navigation.ConverterDispatch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Below this content width, the category is a single pane (phones); above it, list + detail. */
private val TwoPaneBreakpoint = 720.dp

/**
 * Browse a category's converters. On wide screens this is a list + detail two-pane; selecting a
 * converter calls [onSelectConverter] (which navigates, so the URL updates to /category/converter).
 * On phones it shows the list, or - when [selectedConverterId] is set - the converter full-screen.
 */
@Composable
fun CategoryDetailScreen(
  categoryId: String,
  selectedConverterId: String?,
  onBack: () -> Unit,
  onSelectConverter: (String) -> Unit,
  viewModel: CategoryDetailViewModel = koinViewModel(key = categoryId) { parametersOf(categoryId) },
) {
  val state = viewModel.uiState
  val title = state.category?.displayName ?: "Category"
  BoxWithConstraints(Modifier.fillMaxSize()) {
    when {
      maxWidth >= TwoPaneBreakpoint && state.converters.isNotEmpty() -> {
        val initialSelected = selectedConverterId ?: state.converters.first().id
        TwoPane(title, state.converters, initialSelected, onBack)
      }
      selectedConverterId != null -> ConverterDispatch(converterId = selectedConverterId, onBack = onBack)
      else -> SinglePaneList(title, state.converters, onBack, onSelectConverter)
    }
  }
}

@Composable
private fun TwoPane(
  title: String,
  converters: List<Converter>,
  initialSelectedId: String,
  onBack: () -> Unit,
) {
  var selectedId by rememberSaveable(initialSelectedId) { mutableStateOf(initialSelectedId) }
  Row(Modifier.fillMaxSize()) {
    Surface(
      modifier = Modifier.width(300.dp).fillMaxHeight(),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Column(Modifier.fillMaxSize()) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
          }
          Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        LazyColumn(Modifier.fillMaxSize()) {
          items(converters, key = { it.id }) { converter ->
            val selected = converter.id == selectedId
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 2.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { selectedId = converter.id }
                .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(converter.icon.imageVector(), contentDescription = null, modifier = Modifier.size(22.dp))
              Spacer(Modifier.width(14.dp))
              Text(
                converter.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
              )
            }
          }
        }
      }
    }
    Box(Modifier.weight(1f).fillMaxHeight()) {
      ConverterDispatch(converterId = selectedId, onBack = null)
    }
  }
}

@Composable
private fun SinglePaneList(
  title: String,
  converters: List<Converter>,
  onBack: () -> Unit,
  onSelectConverter: (String) -> Unit,
) {
  ConverScaffold(
    title = title,
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
      }
    },
  ) { padding ->
    if (converters.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
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
        items(converters, key = { it.id }) { converter ->
          Card(
            onClick = { onSelectConverter(converter.id) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
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
