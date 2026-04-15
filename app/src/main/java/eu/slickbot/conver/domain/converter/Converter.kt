package eu.slickbot.conver.domain.converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A converter entry in the catalog. Two kinds today:
 *  - [MeasurementConverter] — numeric input with unit pickers (rendered by `MeasurementScreen`).
 *  - [TextConverter] — text-in / text-out transforms with optional modes (rendered by
 *    `TextTransformScreen`).
 *
 * Adding a new converter is (usually) one entry in [ConverterRegistry.default]; no new screen.
 */
sealed interface Converter {
  val id: String
  val name: String
  val category: Category
  val icon: ImageVector
  val aliases: List<String>
}

data class MeasurementConverter(
  override val id: String,
  override val name: String,
  override val category: Category,
  override val icon: ImageVector = Icons.Outlined.SwapHoriz,
  override val aliases: List<String> = emptyList(),
  val units: List<MeasureUnit>,
  val defaultFromId: String,
  val defaultToId: String,
) : Converter {

  init {
    require(units.isNotEmpty()) { "Converter $id has no units" }
    require(units.any { it.id == defaultFromId }) { "defaultFromId $defaultFromId not in units for $id" }
    require(units.any { it.id == defaultToId }) { "defaultToId $defaultToId not in units for $id" }
  }

  fun unit(id: String): MeasureUnit =
    units.firstOrNull { it.id == id }
      ?: error("Unknown unit $id in converter ${this.id}")

  fun convert(value: Double, fromId: String, toId: String): Double {
    if (fromId == toId) return value
    return unit(toId).fromBase(unit(fromId).toBase(value))
  }
}

/**
 * Text-in / text-out transformer. A converter with multiple [modes] renders FilterChips to switch
 * between them (e.g. Base64 encode vs decode; MD5 vs SHA-256; snake_case vs camelCase).
 * A single-mode converter hides the chip row.
 *
 * The [transform] is expected to be pure and fast; failures should surface as the error message
 * (wrapped by [run]) rather than throwing.
 */
data class TextConverter(
  override val id: String,
  override val name: String,
  override val category: Category,
  override val icon: ImageVector = Icons.Outlined.TextFields,
  override val aliases: List<String> = emptyList(),
  val modes: List<Mode>,
  val placeholder: String = "Enter text",
  val monospace: Boolean = false,
) : Converter {

  init {
    require(modes.isNotEmpty()) { "Converter $id has no modes" }
  }

  data class Mode(val id: String, val label: String, val transform: (String) -> String)

  fun mode(id: String): Mode = modes.firstOrNull { it.id == id } ?: modes.first()

  fun run(modeId: String, input: String): String {
    if (input.isEmpty()) return ""
    return runCatching { mode(modeId).transform(input) }.getOrElse { it.message ?: "Error" }
  }
}
