package eu.slickbot.conver.domain.converter

/**
 * A single unit within a measurement category (meter, pound, fahrenheit, …).
 *
 * Conversions go via a hidden "base unit" per category. For simple linear units use [linear];
 * for affine units (temperature) provide [toBase] and [fromBase] directly.
 */
data class MeasureUnit(
  val id: String,
  val name: String,
  val symbol: String,
  val toBase: (Double) -> Double,
  val fromBase: (Double) -> Double,
) {
  companion object {
    /** Value in this unit × factor = value in the base unit. */
    fun linear(id: String, name: String, symbol: String, factor: Double): MeasureUnit =
      MeasureUnit(
        id = id,
        name = name,
        symbol = symbol,
        toBase = { it * factor },
        fromBase = { it / factor },
      )
  }
}
