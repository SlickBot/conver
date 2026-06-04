package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.domain.converter.normalizeNfd
import kotlin.random.Random

// ---------- Case converter ---------------------------------------------------------------------

private fun splitWords(input: String): List<String> =
  input.trim()
    .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    .split(Regex("[\\s_\\-.]+"))
    .filter { it.isNotEmpty() }

fun caseConverter(): TextConverter = TextConverter(
  id = "case",
  name = "Case converter",
  category = Category.Developer,
  icon = ConverterIcon.TextFields,
  aliases = listOf("camel", "snake", "kebab", "pascal", "case", "capitalize"),
  placeholder = "Some text to re-case",
  modes = listOf(
    TextConverter.Mode("upper", "UPPER") { it.uppercase() },
    TextConverter.Mode("lower", "lower") { it.lowercase() },
    TextConverter.Mode("title", "Title Case") {
      it.lowercase().split(" ").joinToString(" ") { w ->
        w.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
      }
    },
    TextConverter.Mode("sentence", "Sentence case") {
      val lower = it.lowercase()
      lower.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
    },
    TextConverter.Mode("snake", "snake_case") {
      splitWords(it).joinToString("_") { w -> w.lowercase() }
    },
    TextConverter.Mode("kebab", "kebab-case") {
      splitWords(it).joinToString("-") { w -> w.lowercase() }
    },
    TextConverter.Mode("camel", "camelCase") {
      splitWords(it).mapIndexed { i, w ->
        if (i == 0) w.lowercase()
        else w.lowercase().replaceFirstChar { c -> c.titlecase() }
      }.joinToString("")
    },
    TextConverter.Mode("pascal", "PascalCase") {
      splitWords(it).joinToString("") { w ->
        w.lowercase().replaceFirstChar { c -> c.titlecase() }
      }
    },
    TextConverter.Mode("constant", "CONSTANT_CASE") {
      splitWords(it).joinToString("_") { w -> w.uppercase() }
    },
  ),
)

// ---------- Slugify ----------------------------------------------------------------------------

fun slugify(input: String): String {
  val normalized = normalizeNfd(input.trim().lowercase())
    .replace(Regex("\\p{Mn}+"), "")
  return normalized
    .replace(Regex("[^a-z0-9\\s-]"), "")
    .replace(Regex("[\\s-]+"), "-")
    .trim('-')
}

fun slugConverter(): TextConverter = TextConverter(
  id = "slug",
  name = "Slugify",
  category = Category.Developer,
  icon = ConverterIcon.Spellcheck,
  aliases = listOf("url slug", "kebab", "seo"),
  placeholder = "My Blog Post — 2025!",
  modes = listOf(
    TextConverter.Mode("slug", "URL slug") { slugify(it) },
  ),
)

// ---------- Word / char count ------------------------------------------------------------------

fun wordCountConverter(): TextConverter = TextConverter(
  id = "word-count",
  name = "Word count",
  category = Category.Developer,
  icon = ConverterIcon.FormatQuote,
  aliases = listOf("count", "chars", "characters", "words"),
  placeholder = "Paste text",
  modes = listOf(
    TextConverter.Mode("all", "Stats") { input ->
      val text = input
      val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
      val chars = text.length
      val charsNoSpace = text.count { !it.isWhitespace() }
      val lines = if (text.isEmpty()) 0 else text.split('\n').size
      val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }.size
      buildString {
        append("Words: ").append(words).append('\n')
        append("Characters: ").append(chars).append('\n')
        append("Characters (no spaces): ").append(charsNoSpace).append('\n')
        append("Lines: ").append(lines).append('\n')
        append("Sentences: ").append(sentences)
      }
    },
  ),
)

// ---------- Morse code -------------------------------------------------------------------------

private val morseTable = mapOf(
  'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
  'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
  'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
  'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
  'Y' to "-.--", 'Z' to "--..",
  '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
  '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
  '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '\'' to ".----.", '!' to "-.-.--",
  '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-", '&' to ".-...", ':' to "---...",
  ';' to "-.-.-.", '=' to "-...-", '+' to ".-.-.", '-' to "-....-", '_' to "..--.-",
  '"' to ".-..-.", '@' to ".--.-.",
)
private val morseReverse = morseTable.entries.associate { (k, v) -> v to k }

fun morseConverter(): TextConverter = TextConverter(
  id = "morse",
  name = "Morse code",
  category = Category.Developer,
  icon = ConverterIcon.GraphicEq,
  aliases = listOf("morse", "sos", "dot dash"),
  monospace = true,
  modes = listOf(
    TextConverter.Mode("to-morse", "Text → Morse") { input ->
      input.uppercase().map { ch ->
        when {
          ch == ' ' -> "/"
          morseTable[ch] != null -> morseTable.getValue(ch)
          else -> ""
        }
      }.filter { it.isNotEmpty() }.joinToString(" ")
    },
    TextConverter.Mode("from-morse", "Morse → Text") { input ->
      input.split(Regex("\\s+"))
        .joinToString("") { token ->
          when {
            token == "/" -> " "
            token.isEmpty() -> ""
            else -> morseReverse[token]?.toString() ?: "?"
          }
        }
    },
  ),
)

// ---------- Caesar / ROT13 ---------------------------------------------------------------------

private fun caesar(input: String, shift: Int): String = input.map { ch ->
  when {
    ch in 'A'..'Z' -> ((ch.code - 'A'.code + shift).mod(26) + 'A'.code).toChar()
    ch in 'a'..'z' -> ((ch.code - 'a'.code + shift).mod(26) + 'a'.code).toChar()
    else -> ch
  }
}.joinToString("")

fun caesarConverter(): TextConverter = TextConverter(
  id = "caesar",
  name = "Caesar cipher",
  category = Category.Developer,
  icon = ConverterIcon.Lock,
  aliases = listOf("rot13", "cipher", "shift", "caesar"),
  modes = listOf(
    TextConverter.Mode("rot13", "ROT13") { caesar(it, 13) },
    TextConverter.Mode("rot5", "ROT5") { caesar(it, 5) },
    TextConverter.Mode("rot1", "ROT1") { caesar(it, 1) },
    TextConverter.Mode("rot25-rev", "ROT25 (reverse)") { caesar(it, 25) },
  ),
)

// ---------- Lorem ipsum ------------------------------------------------------------------------

private val loremWords = (
  "lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt " +
    "ut labore et dolore magna aliqua enim ad minim veniam quis nostrud exercitation ullamco " +
    "laboris nisi aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit " +
    "voluptate velit esse cillum fugiat nulla pariatur excepteur sint occaecat cupidatat non " +
    "proident sunt culpa qui officia deserunt mollit anim id est laborum"
  ).split(' ')

fun loremIpsum(paragraphCount: Int, random: Random = Random): String {
  val count = paragraphCount.coerceIn(1, 20)
  val sb = StringBuilder()
  repeat(count) { pIdx ->
    val sentences = random.nextInt(3, 6)
    val paragraph = StringBuilder()
    repeat(sentences) { sIdx ->
      val wordsCount = random.nextInt(6, 14)
      val words = List(wordsCount) { loremWords[random.nextInt(loremWords.size)] }
      val sentence = words.joinToString(" ")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } + "."
      if (sIdx > 0) paragraph.append(' ')
      paragraph.append(sentence)
    }
    if (pIdx > 0) sb.append("\n\n")
    sb.append(paragraph)
  }
  return sb.toString()
}

fun loremConverter(): TextConverter = TextConverter(
  id = "lorem",
  name = "Lorem ipsum",
  category = Category.Developer,
  icon = ConverterIcon.Subtitles,
  aliases = listOf("lorem", "placeholder", "dummy text"),
  placeholder = "How many paragraphs? (1–20)",
  modes = listOf(
    TextConverter.Mode("paragraphs", "Paragraphs") { input ->
      val n = input.trim().toIntOrNull() ?: 3
      loremIpsum(n)
    },
  ),
)
