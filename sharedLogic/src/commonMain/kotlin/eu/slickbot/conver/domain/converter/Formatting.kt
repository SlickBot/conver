package eu.slickbot.conver.domain.converter

/** Multiplatform replacements for the JVM `String.format` patterns used across catalogs. */

/** Two-digit uppercase hex (replaces "%02X"). */
fun hex2(value: Int): String = (value and 0xFF).toString(16).uppercase().padStart(2, '0')

/** Two-digit lowercase hex (replaces "%02x"). */
fun hex2Lower(value: Int): String = (value and 0xFF).toString(16).padStart(2, '0')

/** Four-digit uppercase hex (replaces "%04X"). */
fun hex4(value: Int): String = value.toString(16).uppercase().padStart(4, '0')

/** Fixed fractional digits, HALF_UP, locale-independent '.' (replaces "%.Nf" / Locale.US "%.Nf"). */
fun fixed(value: Double, digits: Int): String {
  if (value.isNaN() || value.isInfinite()) return value.toString()
  val negative = value < 0
  val scaled = formatFixedScaled(if (negative) -value else value, digits)
  return if (negative && scaled.any { it != '0' && it != '.' }) "-$scaled" else scaled
}

private fun formatFixedScaled(value: Double, digits: Int): String {
  // Reuse the Format.kt rounding by padding to exactly `digits` fractional places.
  val rounded = formatResult(value, maxDecimals = digits)
  if (digits == 0) return rounded.substringBefore('.')
  val dot = rounded.indexOf('.')
  return if (dot < 0) rounded + "." + "0".repeat(digits)
  else {
    val frac = rounded.substring(dot + 1)
    rounded.substring(0, dot) + "." + frac.padEnd(digits, '0')
  }
}

/** Left-justified, space-padded to [width] (replaces "%-Ns"). */
fun padRight(s: String, width: Int): String = s.padEnd(width)

/** Right-justified, space-padded to [width] (replaces "%Nd" on small ints). */
fun padLeft(value: Int, width: Int): String = value.toString().padStart(width)
