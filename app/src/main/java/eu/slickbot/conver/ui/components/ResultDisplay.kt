package eu.slickbot.conver.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.ui.theme.LocalConverTypography

/**
 * Big tabular-figure result display. Tapping copies [textToCopy] to the clipboard.
 * Value transitions are animated with a vertical slide.
 */
@Composable
fun ResultDisplay(
  value: String,
  unitSymbol: String,
  modifier: Modifier = Modifier,
  textToCopy: String = value,
  haptics: Boolean = true,
  onCopied: (String) -> Unit = {},
) {
  val clipboard = LocalClipboardManager.current
  val haptic = LocalHapticFeedback.current
  val numeric = LocalConverTypography.current.numericDisplay

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        clipboard.setText(AnnotatedString(textToCopy))
        if (haptics) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onCopied(textToCopy)
      },
    shape = RoundedCornerShape(24.dp),
    tonalElevation = 2.dp,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
  ) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
      AnimatedContent(
        targetState = value,
        transitionSpec = {
          (slideInVertically { it / 3 } + fadeIn(tween(200)))
            .togetherWith(slideOutVertically { -it / 3 } + fadeOut(tween(120)))
        },
        label = "result-value",
      ) { shown ->
        Text(text = shown, style = numeric, color = MaterialTheme.colorScheme.onSurface)
      }
      Text(
        text = unitSymbol,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
