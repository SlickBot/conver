package eu.slickbot.conver.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Material 3 SearchBar that expands in place. Caller owns the query state and renders the
 * result list in [content]. When the bar is collapsed, content is not drawn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHeader(
  query: String,
  onQueryChange: (String) -> Unit,
  active: Boolean,
  onActiveChange: (Boolean) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  SearchBar(
    query = query,
    onQueryChange = onQueryChange,
    onSearch = { onActiveChange(false) },
    active = active,
    onActiveChange = onActiveChange,
    placeholder = { Text(placeholder) },
    leadingIcon = {
      Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = if (active) 0.dp else 16.dp),
  ) {
    Column {
      Spacer(Modifier.height(8.dp))
      content()
    }
  }
}
