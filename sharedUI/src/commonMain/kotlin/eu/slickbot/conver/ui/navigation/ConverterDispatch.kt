package eu.slickbot.conver.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.slickbot.conver.domain.converter.CalculatorConverter
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.StandaloneConverter
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.ui.converter.CalculatorScreen
import eu.slickbot.conver.ui.converter.MeasurementScreen
import eu.slickbot.conver.ui.converter.TextTransformScreen
import eu.slickbot.conver.ui.receiptsplit.ReceiptSplitScreen
import org.koin.compose.koinInject

/**
 * Renders the screen for [converterId] based on its converter type. [onBack] is null when the
 * converter is embedded in a detail pane (no back button); non-null when shown full-screen.
 */
@Composable
fun ConverterDispatch(converterId: String, onBack: (() -> Unit)? = null) {
  val registry: ConverterRegistry = koinInject()
  when (val converter = registry[converterId]) {
    is MeasurementConverter -> MeasurementScreen(converterId = converterId, onBack = onBack)
    is TextConverter -> TextTransformScreen(converterId = converterId, onBack = onBack)
    is CalculatorConverter -> CalculatorScreen(converterId = converterId, onBack = onBack)
    is StandaloneConverter -> if (converter.screenId == "receipt-split") {
      ReceiptSplitScreen(onBack = onBack)
    } else {
      NotAvailable("Screen not implemented: $converterId", converterId, onBack)
    }
    null -> NotAvailable("Unknown converter: $converterId", converterId, onBack)
  }
}

@Composable
private fun NotAvailable(message: String, converterId: String, onBack: (() -> Unit)?) {
  Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
    Text(message, style = MaterialTheme.typography.titleMedium)
  }
  LaunchedEffect(converterId) { onBack?.invoke() }
}
