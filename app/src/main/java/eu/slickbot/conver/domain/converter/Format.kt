package eu.slickbot.conver.domain.converter

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Formats a [Double] result to at most [maxDecimals] fractional digits, trimming trailing zeros
 * and the trailing decimal point. Always returns a stable string (no "1.0E-5" scientific form for
 * reasonable-magnitude numbers). `NaN` and infinities render as "—".
 */
fun formatResult(value: Double, maxDecimals: Int = 6): String {
  if (value.isNaN() || value.isInfinite()) return "—"
  val precision = (maxDecimals + 6).coerceAtLeast(10)
  val bd = BigDecimal(value, MathContext(precision))
    .setScale(maxDecimals.coerceAtLeast(0), RoundingMode.HALF_UP)
  var s = bd.toPlainString()
  if (s.contains('.')) {
    s = s.trimEnd('0').trimEnd('.')
  }
  return s.ifEmpty { "0" }
}
