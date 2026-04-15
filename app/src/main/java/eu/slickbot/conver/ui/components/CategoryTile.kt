package eu.slickbot.conver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.domain.converter.Category

@Composable
fun CategoryTile(
  category: Category,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    modifier = modifier.aspectRatio(1.1f),
    shape = RoundedCornerShape(24.dp),
    color = category.accent.copy(alpha = 0.14f),
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = category.accent,
        contentColor = Color.White,
      ) {
        Icon(
          imageVector = category.icon,
          contentDescription = null,
          modifier = Modifier
            .size(40.dp)
            .padding(8.dp),
        )
      }
      Spacer(Modifier.height(8.dp))
      Text(
        text = category.displayName,
        style = MaterialTheme.typography.titleMedium,
      )
    }
  }
}

/** Small pill-shaped chip used on Home for "recents" / "favorites" rows. */
@Composable
fun AccentChip(
  label: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  accent: Color = MaterialTheme.colorScheme.primaryContainer,
) {
  Surface(
    shape = RoundedCornerShape(100),
    color = accent,
    modifier = modifier
      .clip(RoundedCornerShape(100))
      .clickable(onClick = onClick),
  ) {
    Text(
      text = label,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      style = MaterialTheme.typography.labelLarge,
    )
  }
}
