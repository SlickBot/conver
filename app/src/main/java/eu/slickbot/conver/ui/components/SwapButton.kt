package eu.slickbot.conver.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Circular primary-container icon button that spins 180° each press — used for swapping the
 * "from" and "to" units in a conversion. Fires a haptic tick on press (when [haptics] is on).
 */
@Composable
fun SwapButton(
  onSwap: () -> Unit,
  modifier: Modifier = Modifier,
  haptics: Boolean = true,
) {
  var ticks by remember { mutableIntStateOf(0) }
  val rotation by animateFloatAsState(
    targetValue = ticks * 180f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow,
    ),
    label = "swap-rotation",
  )
  val hapticFeedback = LocalHapticFeedback.current

  FilledIconButton(
    onClick = {
      if (haptics) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
      ticks += 1
      onSwap()
    },
    modifier = modifier.size(56.dp),
    colors = IconButtonDefaults.filledIconButtonColors(),
  ) {
    Icon(
      imageVector = Icons.Outlined.SwapVert,
      contentDescription = "Swap units",
      modifier = Modifier.rotate(rotation),
    )
  }
}
