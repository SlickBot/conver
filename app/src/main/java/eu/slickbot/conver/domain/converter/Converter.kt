package eu.slickbot.conver.domain.converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A converter entry in the catalog. The three [Kind] variants are rendered by three screen
 * templates — adding a new converter is (usually) one entry in the registry, no new screen.
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
