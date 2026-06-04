package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.domain.converter.hex2
import kotlin.math.roundToInt

private data class Rgb(val r: Int, val g: Int, val b: Int) {
  init {
    require(r in 0..255 && g in 0..255 && b in 0..255) { "RGB component out of range" }
  }
}

private fun parseHex(raw: String): Rgb {
  val s = raw.trim().removePrefix("#")
  val normalized = when (s.length) {
    3 -> s.map { "$it$it" }.joinToString("") // expand shorthand
    6 -> s
    8 -> s.takeLast(6) // drop alpha
    else -> throw IllegalArgumentException("Expected 3, 6, or 8 hex digits")
  }
  val value = normalized.toInt(16)
  return Rgb((value shr 16) and 0xFF, (value shr 8) and 0xFF, value and 0xFF)
}

private fun parseRgbTriple(raw: String): Rgb {
  val nums = Regex("\\d+").findAll(raw).map { it.value.toInt() }.toList()
  require(nums.size >= 3) { "Need three numbers (R, G, B)" }
  return Rgb(nums[0], nums[1], nums[2])
}

private fun rgbToHex(c: Rgb): String = "#" + hex2(c.r) + hex2(c.g) + hex2(c.b)

/** Returns h in [0, 360), s and l in [0, 1]. */
private fun rgbToHsl(c: Rgb): Triple<Double, Double, Double> {
  val r = c.r / 255.0
  val g = c.g / 255.0
  val b = c.b / 255.0
  val max = maxOf(r, g, b)
  val min = minOf(r, g, b)
  val l = (max + min) / 2.0
  if (max == min) return Triple(0.0, 0.0, l)
  val d = max - min
  val s = if (l > 0.5) d / (2.0 - max - min) else d / (max + min)
  val h = when (max) {
    r -> ((g - b) / d + if (g < b) 6.0 else 0.0)
    g -> ((b - r) / d + 2.0)
    else -> ((r - g) / d + 4.0)
  } * 60.0
  return Triple(h, s, l)
}

private fun hslToRgb(h: Double, s: Double, l: Double): Rgb {
  if (s == 0.0) {
    val v = (l * 255).roundToInt()
    return Rgb(v, v, v)
  }
  val q = if (l < 0.5) l * (1 + s) else l + s - l * s
  val p = 2 * l - q
  fun hue2rgb(t: Double): Double {
    var tt = t
    if (tt < 0) tt += 1
    if (tt > 1) tt -= 1
    return when {
      tt < 1.0 / 6 -> p + (q - p) * 6 * tt
      tt < 1.0 / 2 -> q
      tt < 2.0 / 3 -> p + (q - p) * (2.0 / 3 - tt) * 6
      else -> p
    }
  }
  val hh = h / 360.0
  val r = hue2rgb(hh + 1.0 / 3)
  val g = hue2rgb(hh)
  val b = hue2rgb(hh - 1.0 / 3)
  return Rgb((r * 255).roundToInt(), (g * 255).roundToInt(), (b * 255).roundToInt())
}

private fun parseHsl(raw: String): Triple<Double, Double, Double> {
  val nums = Regex("-?\\d+(?:\\.\\d+)?").findAll(raw).map { it.value.toDouble() }.toList()
  require(nums.size >= 3) { "Need H, S, L values" }
  val h = nums[0].mod(360.0).let { if (it < 0) it + 360 else it }
  val s = (nums[1] / if (raw.contains('%')) 100.0 else 1.0).coerceIn(0.0, 1.0)
  val l = (nums[2] / if (raw.contains('%')) 100.0 else 1.0).coerceIn(0.0, 1.0)
  return Triple(h, s, l)
}

private fun renderColor(c: Rgb): String {
  val (h, s, l) = rgbToHsl(c)
  return buildString {
    append("HEX: ").append(rgbToHex(c)).append('\n')
    append("RGB: rgb(").append(c.r).append(", ").append(c.g).append(", ").append(c.b).append(')').append('\n')
    append("HSL: hsl(")
      .append(h.roundToInt()).append(", ")
      .append((s * 100).roundToInt()).append("%, ")
      .append((l * 100).roundToInt()).append("%)")
  }
}

fun colorConverter(): TextConverter = TextConverter(
  id = "color",
  name = "Color converter",
  category = Category.Color,
  icon = ConverterIcon.Palette,
  aliases = listOf("color", "colour", "hex", "rgb", "hsl"),
  monospace = true,
  placeholder = "#ff3366 · rgb(255,51,102) · hsl(346,100%,60%)",
  modes = listOf(
    TextConverter.Mode("hex-in", "HEX in") { renderColor(parseHex(it)) },
    TextConverter.Mode("rgb-in", "RGB in") { renderColor(parseRgbTriple(it)) },
    TextConverter.Mode("hsl-in", "HSL in") {
      val (h, s, l) = parseHsl(it)
      renderColor(hslToRgb(h, s, l))
    },
  ),
)
