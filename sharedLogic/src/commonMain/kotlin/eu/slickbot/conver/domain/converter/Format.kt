package eu.slickbot.conver.domain.converter

import kotlin.math.absoluteValue

/**
 * Formats a [Double] to at most [maxDecimals] fractional digits, HALF_UP rounded, trailing zeros
 * and trailing decimal point trimmed, never scientific notation. NaN/infinity render as "—".
 *
 * Operates on the Double's shortest round-trip decimal string (no arbitrary-precision arithmetic
 * is needed — the value is already a Double), so results are identical on every platform.
 */
fun formatResult(value: Double, maxDecimals: Int = 6): String {
  if (value.isNaN() || value.isInfinite()) return "—"
  if (value == 0.0) return "0"
  val negative = value < 0
  val plain = toPlainDecimalString(value.absoluteValue)
  val rounded = roundHalfUp(plain, maxDecimals.coerceAtLeast(0))
  val trimmed = trimDecimal(rounded)
  val result = if (trimmed == "0" || trimmed.isEmpty()) "0" else trimmed
  return if (negative && result != "0") "-$result" else result
}

/** Expands a non-negative Double to a plain decimal string, removing any exponent. */
private fun toPlainDecimalString(value: Double): String {
  val s = value.toString()
  val eIndex = s.indexOfFirst { it == 'e' || it == 'E' }
  if (eIndex < 0) return s
  val mantissa = s.substring(0, eIndex)
  val exp = s.substring(eIndex + 1).toInt()
  val dot = mantissa.indexOf('.')
  val digits = if (dot < 0) mantissa else mantissa.substring(0, dot) + mantissa.substring(dot + 1)
  val pointPos = (if (dot < 0) mantissa.length else dot) + exp
  return when {
    pointPos <= 0 -> "0." + "0".repeat(-pointPos) + digits
    pointPos >= digits.length -> digits + "0".repeat(pointPos - digits.length)
    else -> digits.substring(0, pointPos) + "." + digits.substring(pointPos)
  }
}

/** HALF_UP rounds a plain non-negative decimal string to [scale] fractional digits. */
private fun roundHalfUp(plain: String, scale: Int): String {
  val dot = plain.indexOf('.')
  if (dot < 0) return plain
  val intPart = plain.substring(0, dot)
  val frac = plain.substring(dot + 1)
  if (frac.length <= scale) return intPart + "." + frac
  val keep = frac.substring(0, scale)
  val roundUp = frac[scale] >= '5'
  if (!roundUp) return if (scale == 0) intPart else "$intPart.$keep"
  // propagate carry through keep then intPart
  val digits = (intPart + keep).toMutableList()
  var i = digits.lastIndex
  while (i >= 0) {
    if (digits[i] == '9') { digits[i] = '0'; i-- } else { digits[i] = digits[i] + 1; break }
  }
  val carried = if (i < 0) "1" + digits.joinToString("") else digits.joinToString("")
  val newIntLen = carried.length - scale
  return if (scale == 0) carried else carried.substring(0, newIntLen) + "." + carried.substring(newIntLen)
}

private fun trimDecimal(s: String): String {
  if (!s.contains('.')) return s
  return s.trimEnd('0').trimEnd('.')
}
