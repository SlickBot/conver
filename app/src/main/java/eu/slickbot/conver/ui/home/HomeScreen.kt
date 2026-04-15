package eu.slickbot.conver.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.ui.components.AccentChip
import eu.slickbot.conver.ui.components.CategoryTile
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.grid.items as gridItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onConverterClick: (String) -> Unit,
  onBrowseAllClick: () -> Unit,
  viewModel: HomeViewModel = koinViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val horizontalPadding by animateDpAsState(
    targetValue = if (state.searching) 0.dp else 16.dp,
    label = "search_horizontal_padding",
  )
  val verticalPadding by animateDpAsState(
    targetValue = if (state.searching) 0.dp else 8.dp,
    label = "search_vertical_padding",
  )

  Column(Modifier.fillMaxSize()) {

    val searchBarColors = SearchBarDefaults.colors()
    SearchBar(
      inputField = {
        SearchBarDefaults.InputField(
          query = state.query,
          onQueryChange = viewModel::onQueryChange,
          onSearch = { viewModel.onSearchingChange(false) },
          expanded = state.searching,
          onExpandedChange = viewModel::onSearchingChange,
          enabled = true,
          placeholder = { Text("Search any converter…") },
          leadingIcon = null,
          trailingIcon = null,
          colors = searchBarColors.inputFieldColors,
          interactionSource = null,
        )
      },
      expanded = state.searching,
      onExpandedChange = viewModel::onSearchingChange,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
      shape = SearchBarDefaults.inputFieldShape,
      colors = searchBarColors,
      tonalElevation = SearchBarDefaults.TonalElevation,
      shadowElevation = SearchBarDefaults.ShadowElevation,
      windowInsets = SearchBarDefaults.windowInsets,
      content = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
        ) {
          state.results.forEach { converter ->
            ListItem(
              headlineContent = { Text(converter.name) },
              supportingContent = { Text(converter.category.displayName) },
              leadingContent = { Icon(converter.icon, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onConverterClick(converter.id) },
            )
          }
        }
      },
    )

    LazyVerticalGrid(
      modifier = Modifier.fillMaxSize(),
      columns = GridCells.Fixed(2),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(16.dp),
    ) {
      if (state.favorites.isNotEmpty()) {
        maxSpanItem {
          SectionBlock(title = "Favorites") {
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              items(state.favorites, key = { it.id }) { converter ->
                AccentChip(
                  label = converter.name,
                  onClick = { onConverterClick(converter.id) },
                )
              }
            }
          }
        }
      }

      if (state.recents.isNotEmpty()) {
        maxSpanItem {
          SectionBlock(title = "Recent") {
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              items(state.recents, key = { it.id }) { converter ->
                AccentChip(
                  label = converter.name,
                  onClick = { onConverterClick(converter.id) },
                  accent = MaterialTheme.colorScheme.secondaryContainer,
                )
              }
            }
          }
        }
      }

      maxSpanItem {
        SectionTitle(text = "Browse")
      }

      gridItems(
        items = Category.entries.toList(),
        key = { it.name },
      ) { category ->
        CategoryTile(
          category = category,
          onClick = onBrowseAllClick,
        )
      }
    }
  }
}

@Composable
private fun SectionBlock(
  title: String,
  content: @Composable () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    SectionTitle(title)
    Spacer(Modifier.height(8.dp))
    content()
  }
}

@Composable
private fun SectionTitle(
  text: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    modifier = modifier,
    style = MaterialTheme.typography.titleMedium.copy(
      fontWeight = FontWeight.SemiBold,
    ),
    color = MaterialTheme.colorScheme.onSurface,
  )
}

private fun LazyGridScope.maxSpanItem(
  key: Any? = null,
  contentType: Any? = null,
  content: @Composable LazyGridItemScope.() -> Unit,
) {
  item(
    key = key,
    span = { GridItemSpan(maxLineSpan) },
    contentType = contentType,
    content = content,
  )
}
