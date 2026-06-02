package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Translate
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.TextConverter

// ---------- Base converter ---------------------------------------------------------------------

/**
 * Parse an integer from binary (0b…), hex (0x…), or decimal. Throws on malformed input.
 */
private fun parseAnyBase(raw: String): Long {
  val s = raw.trim()
  return when {
    s.startsWith("0x", ignoreCase = true) -> s.substring(2).toLong(16)
    s.startsWith("0b", ignoreCase = true) -> s.substring(2).toLong(2)
    else -> s.toLong()
  }
}

fun baseConverter(): TextConverter = TextConverter(
  id = "number-base",
  name = "Number base",
  category = Category.Numbers,
  icon = Icons.Outlined.Numbers,
  aliases = listOf("binary", "hex", "hexadecimal", "octal", "decimal"),
  placeholder = "42, 0xff, 0b1010",
  monospace = true,
  modes = listOf(
    TextConverter.Mode("bin", "Binary") { "0b" + parseAnyBase(it).toString(2) },
    TextConverter.Mode("oct", "Octal") { "0o" + parseAnyBase(it).toString(8) },
    TextConverter.Mode("dec", "Decimal") { parseAnyBase(it).toString(10) },
    TextConverter.Mode("hex", "Hex") { "0x" + parseAnyBase(it).toString(16).uppercase() },
  ),
)

// ---------- Roman numerals ---------------------------------------------------------------------

private val romanTable = listOf(
  1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
  100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
  10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I",
)

private fun toRoman(n: Int): String {
  require(n in 1..3999) { "Roman numerals support 1..3999 only" }
  val out = StringBuilder()
  var remaining = n
  for ((value, symbol) in romanTable) {
    while (remaining >= value) { out.append(symbol); remaining -= value }
  }
  return out.toString()
}

private fun fromRoman(raw: String): Int {
  val s = raw.trim().uppercase()
  require(s.all { it in "IVXLCDM" }) { "Not a Roman numeral" }
  val values = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
  var total = 0
  var prev = 0
  for (ch in s.reversed()) {
    val v = values.getValue(ch)
    total += if (v < prev) -v else v
    prev = v
  }
  require(total in 1..3999) { "Out of range" }
  // Round-trip to reject non-canonical forms like "IIII".
  require(toRoman(total) == s) { "Non-canonical Roman numeral" }
  return total
}

fun romanConverter(): TextConverter = TextConverter(
  id = "roman",
  name = "Roman numerals",
  category = Category.Numbers,
  icon = Icons.Outlined.Abc,
  aliases = listOf("roman", "numerals", "mcm"),
  modes = listOf(
    TextConverter.Mode("to-roman", "Number → Roman") { toRoman(it.trim().toInt()) },
    TextConverter.Mode("from-roman", "Roman → Number") { fromRoman(it).toString() },
  ),
)

// ---------- Unicode / ASCII --------------------------------------------------------------------

fun unicodeConverter(): TextConverter = TextConverter(
  id = "unicode",
  name = "Unicode code point",
  category = Category.Numbers,
  icon = Icons.Outlined.Translate,
  aliases = listOf("ascii", "codepoint", "unicode", "utf"),
  placeholder = "Enter a character or U+XXXX",
  modes = listOf(
    TextConverter.Mode("char-to-cp", "Char → Code point") { input ->
      val s = input.trim()
      require(s.isNotEmpty()) { "Empty input" }
      val cp = s.codePointAt(0)
      "U+%04X · decimal %d".format(cp, cp)
    },
    TextConverter.Mode("cp-to-char", "Code point → Char") { input ->
      val s = input.trim().removePrefix("U+").removePrefix("u+").removePrefix("0x")
      val cp = s.toInt(16)
      String(Character.toChars(cp))
    },
  ),
)

// ---------- Number to words --------------------------------------------------------------------

private val unitsWords = arrayOf(
  "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
  "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
  "seventeen", "eighteen", "nineteen",
)
private val tensWords = arrayOf(
  "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
)
private val scaleWords = arrayOf("", "thousand", "million", "billion", "trillion")

private fun belowThousand(n: Int): String {
  require(n in 0..999)
  val hundreds = n / 100
  val remainder = n % 100
  val sb = StringBuilder()
  if (hundreds > 0) sb.append(unitsWords[hundreds]).append(" hundred")
  if (remainder > 0) {
    if (sb.isNotEmpty()) sb.append(' ')
    if (remainder < 20) {
      sb.append(unitsWords[remainder])
    } else {
      sb.append(tensWords[remainder / 10])
      if (remainder % 10 != 0) sb.append('-').append(unitsWords[remainder % 10])
    }
  }
  return sb.toString()
}

fun numberToWords(n: Long): String {
  if (n == 0L) return "zero"
  if (n < 0) return "negative ${numberToWords(-n)}"
  val parts = mutableListOf<String>()
  var remaining = n
  var scale = 0
  while (remaining > 0) {
    val chunk = (remaining % 1000).toInt()
    if (chunk > 0) {
      val piece = belowThousand(chunk) +
        if (scale > 0) " ${scaleWords[scale]}" else ""
      parts.add(0, piece)
    }
    remaining /= 1000
    scale += 1
  }
  return parts.joinToString(" ")
}

fun numberToWordsConverter(): TextConverter = TextConverter(
  id = "number-to-words",
  name = "Number to words",
  category = Category.Numbers,
  icon = Icons.Outlined.Abc,
  aliases = listOf("spell", "words", "wording"),
  placeholder = "Enter a number",
  modes = listOf(
    TextConverter.Mode("en", "English") { numberToWords(it.trim().toLong()) },
  ),
)
