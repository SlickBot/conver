package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Boy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Public
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.TextConverter
import java.util.Locale
import kotlin.math.pow

// ---------- Shoe size --------------------------------------------------------------------------

/**
 * Shoe size formulas (approximate, based on foot length in cm):
 *  - EU: cm × 1.5 + 2
 *  - US Men: (cm / 2.54 × 3) − 22
 *  - US Women: (cm / 2.54 × 3) − 20.5
 *  - UK Men: (cm / 2.54 × 3) − 23
 *  - UK Women: (cm / 2.54 × 3) − 21.5
 *  - JP (Mondopoint): ≈ cm
 *
 * Base unit = cm (foot length).
 */
fun shoeSizeConverter(): MeasurementConverter = MeasurementConverter(
  id = "shoe-size",
  name = "Shoe size",
  category = Category.Everyday,
  icon = Icons.Outlined.Boy,
  aliases = listOf("shoe", "foot", "size"),
  units = listOf(
    MeasureUnit("cm", "Centimetres", "cm",
      toBase = { it }, fromBase = { it }),
    MeasureUnit("eu", "EU", "EU",
      toBase = { (it - 2.0) / 1.5 }, fromBase = { it * 1.5 + 2.0 }),
    MeasureUnit("us-m", "US Men", "US ♂",
      toBase = { (it + 22.0) / 3.0 * 2.54 }, fromBase = { it / 2.54 * 3.0 - 22.0 }),
    MeasureUnit("us-w", "US Women", "US ♀",
      toBase = { (it + 20.5) / 3.0 * 2.54 }, fromBase = { it / 2.54 * 3.0 - 20.5 }),
    MeasureUnit("uk-m", "UK Men", "UK ♂",
      toBase = { (it + 23.0) / 3.0 * 2.54 }, fromBase = { it / 2.54 * 3.0 - 23.0 }),
    MeasureUnit("uk-w", "UK Women", "UK ♀",
      toBase = { (it + 21.5) / 3.0 * 2.54 }, fromBase = { it / 2.54 * 3.0 - 21.5 }),
    MeasureUnit("jp", "Japan (cm)", "JP",
      toBase = { it }, fromBase = { it }),
  ),
  defaultFromId = "eu",
  defaultToId = "us-m",
)

// ---------- Clothing sizes (text lookup) -------------------------------------------------------

fun clothingSizeConverter(): TextConverter = TextConverter(
  id = "clothing-size",
  name = "Clothing sizes",
  category = Category.Everyday,
  icon = Icons.Outlined.Boy,
  aliases = listOf("clothing", "shirt", "dress", "pants", "xs", "xl", "xxl"),
  placeholder = "XS, S, M, L, XL, XXL or EU number",
  modes = listOf(
    TextConverter.Mode("general", "General") { input ->
      val s = input.trim().uppercase()
      val table = mapOf(
        "XS" to "EU 32–34 · US 0–2 · UK 4–6",
        "S" to "EU 36–38 · US 4–6 · UK 8–10",
        "M" to "EU 38–40 · US 8–10 · UK 10–12",
        "L" to "EU 42–44 · US 12–14 · UK 14–16",
        "XL" to "EU 46–48 · US 16–18 · UK 18–20",
        "XXL" to "EU 50–52 · US 20–22 · UK 22–24",
        "XXXL" to "EU 54–56 · US 24–26 · UK 26–28",
      )
      table[s] ?: run {
        val n = s.toIntOrNull() ?: throw IllegalArgumentException("Enter XS–XXXL or an EU number")
        val letter = when {
          n <= 34 -> "XS"
          n <= 38 -> "S"
          n <= 40 -> "M"
          n <= 44 -> "L"
          n <= 48 -> "XL"
          n <= 52 -> "XXL"
          else -> "XXXL"
        }
        "$letter · $s"
      }
    },
  ),
)

// ---------- Paper sizes ------------------------------------------------------------------------

private data class PaperSize(val name: String, val widthMm: Int, val heightMm: Int) {
  val widthIn: Double get() = widthMm / 25.4
  val heightIn: Double get() = heightMm / 25.4
}

private val paperSizes = listOf(
  PaperSize("A0", 841, 1189), PaperSize("A1", 594, 841),
  PaperSize("A2", 420, 594), PaperSize("A3", 297, 420),
  PaperSize("A4", 210, 297), PaperSize("A5", 148, 210),
  PaperSize("A6", 105, 148), PaperSize("A7", 74, 105),
  PaperSize("B0", 1000, 1414), PaperSize("B1", 707, 1000),
  PaperSize("B2", 500, 707), PaperSize("B3", 353, 500),
  PaperSize("B4", 250, 353), PaperSize("B5", 176, 250),
  PaperSize("Letter", 216, 279), PaperSize("Legal", 216, 356),
  PaperSize("Tabloid", 279, 432), PaperSize("Executive", 184, 267),
)

fun paperSizeConverter(): TextConverter = TextConverter(
  id = "paper-size",
  name = "Paper sizes",
  category = Category.Everyday,
  icon = Icons.Outlined.Description,
  aliases = listOf("a4", "letter", "paper", "a3", "a5", "legal"),
  placeholder = "A4, Letter, B5…",
  modes = listOf(
    TextConverter.Mode("lookup", "Look up") { input ->
      val s = input.trim().uppercase()
      val match = paperSizes.firstOrNull { it.name.uppercase() == s }
        ?: throw IllegalArgumentException(
          "Unknown: $s. Try: ${paperSizes.joinToString(", ") { it.name }}"
        )
      "%s × %s mm\n%.1f × %.1f in".format(
        match.widthMm, match.heightMm, match.widthIn, match.heightIn,
      )
    },
    TextConverter.Mode("all", "List all") { _ ->
      paperSizes.joinToString("\n") { p ->
        "%-10s %4d × %4d mm".format(p.name, p.widthMm, p.heightMm)
      }
    },
  ),
)

// ---------- Oven temperature -------------------------------------------------------------------

/**
 * Gas marks are discrete (1–10) but we model them as continuous via the standard formula:
 * °F = (gasmark × 25) + 250. Base unit = Kelvin (reusing the temperature engine).
 */
fun ovenTempConverter(): MeasurementConverter = MeasurementConverter(
  id = "oven-temp",
  name = "Oven temperature",
  category = Category.Everyday,
  icon = Icons.Outlined.LocalFireDepartment,
  aliases = listOf("oven", "gas mark", "baking"),
  units = listOf(
    MeasureUnit(
      id = "c", name = "Celsius", symbol = "°C",
      toBase = { it + 273.15 }, fromBase = { it - 273.15 },
    ),
    MeasureUnit(
      id = "f", name = "Fahrenheit", symbol = "°F",
      toBase = { (it + 459.67) * 5.0 / 9.0 }, fromBase = { it * 9.0 / 5.0 - 459.67 },
    ),
    MeasureUnit(
      id = "gas", name = "Gas Mark", symbol = "Gas",
      toBase = { val f = it * 25.0 + 250.0; (f + 459.67) * 5.0 / 9.0 },
      fromBase = { val f = it * 9.0 / 5.0 - 459.67; (f - 250.0) / 25.0 },
    ),
  ),
  defaultFromId = "c",
  defaultToId = "gas",
)

// ---------- BMI calculator ---------------------------------------------------------------------

fun bmiConverter(): TextConverter = TextConverter(
  id = "bmi",
  name = "BMI calculator",
  category = Category.Everyday,
  icon = Icons.Outlined.FitnessCenter,
  aliases = listOf("bmi", "body mass", "weight", "health"),
  placeholder = "Height(cm), Weight(kg) (e.g. 175, 70)",
  modes = listOf(
    TextConverter.Mode("metric", "Metric (cm, kg)") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 2) { "Enter: height in cm, weight in kg" }
      val heightCm = parts[0]
      val weightKg = parts[1]
      require(heightCm > 0 && weightKg > 0) { "Values must be positive" }
      val heightM = heightCm / 100.0
      val bmi = weightKg / (heightM * heightM)
      val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
      }
      "BMI: %.1f\nCategory: %s".format(bmi, category)
    },
    TextConverter.Mode("imperial", "Imperial (in, lb)") { input ->
      val parts = input.split(",", ";", " ").mapNotNull { it.trim().toDoubleOrNull() }
      require(parts.size >= 2) { "Enter: height in inches, weight in lb" }
      val heightIn = parts[0]
      val weightLb = parts[1]
      require(heightIn > 0 && weightLb > 0) { "Values must be positive" }
      val bmi = weightLb * 703.0 / (heightIn * heightIn)
      val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
      }
      "BMI: %.1f\nCategory: %s".format(bmi, category)
    },
  ),
)

// ---------- Running pace -----------------------------------------------------------------------

/**
 * Running pace: base = min/km (seconds per km internally for precision).
 *
 *  - min/km: identity
 *  - min/mi: min_km * 1.609344
 *  - km/h: 60 / min_km
 *  - mph: 60 / (min_km * 1.609344)
 *
 * Because pace↔speed is reciprocal (not linear), we use custom toBase/fromBase.
 */
fun runningPaceConverter(): MeasurementConverter = MeasurementConverter(
  id = "running-pace",
  name = "Running pace",
  category = Category.Everyday,
  icon = Icons.Outlined.DirectionsRun,
  aliases = listOf("pace", "running", "jogging", "marathon"),
  units = listOf(
    MeasureUnit("min-km", "Minutes per km", "min/km",
      toBase = { it }, fromBase = { it }),
    MeasureUnit("min-mi", "Minutes per mile", "min/mi",
      toBase = { it / 1.609344 }, fromBase = { it * 1.609344 }),
    MeasureUnit("kmh", "Kilometres per hour", "km/h",
      toBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 60.0 / it },
      fromBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 60.0 / it }),
    MeasureUnit("mph", "Miles per hour", "mph",
      toBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 60.0 / (it * 1.609344) },
      fromBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 60.0 / (it * 1.609344) }),
  ),
  defaultFromId = "min-km",
  defaultToId = "min-mi",
)

// ---------- Age on other planets ---------------------------------------------------------------

fun planetAgeConverter(): TextConverter = TextConverter(
  id = "planet-age",
  name = "Age on other planets",
  category = Category.Everyday,
  icon = Icons.Outlined.Public,
  aliases = listOf("planet", "mars", "jupiter", "saturn", "age"),
  placeholder = "Your age in Earth years",
  modes = listOf(
    TextConverter.Mode("calc", "Calculate") { input ->
      val earthYears = input.trim().toDouble()
      require(earthYears > 0) { "Enter a positive age" }
      val planets = listOf(
        "Mercury" to 0.2408467, "Venus" to 0.61519726,
        "Earth" to 1.0, "Mars" to 1.8808158,
        "Jupiter" to 11.862615, "Saturn" to 29.447498,
        "Uranus" to 84.016846, "Neptune" to 164.79132,
      )
      planets.joinToString("\n") { (name, orbitalPeriod) ->
        "%-10s %.2f years".format(Locale.US, name, earthYears / orbitalPeriod)
      }
    },
  ),
)
