package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.lengthConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class LengthConverterTest {

  private val length = lengthConverter()

  @Test fun `1 mile equals 1 609_344 metres exactly`() {
    assertEquals(1609.344, length.convert(1.0, "mi", "m"), 1e-9)
  }

  @Test fun `1 inch equals 2_54 cm exactly`() {
    assertEquals(2.54, length.convert(1.0, "in", "cm"), 1e-12)
  }

  @Test fun `1 foot equals 0_3048 metres`() {
    assertEquals(0.3048, length.convert(1.0, "ft", "m"), 1e-12)
  }

  @Test fun `1 yard equals 3 feet`() {
    assertEquals(3.0, length.convert(1.0, "yd", "ft"), 1e-12)
  }

  @Test fun `1 km equals 1000 m`() {
    assertEquals(1000.0, length.convert(1.0, "km", "m"), 1e-12)
  }

  @Test fun `1 nautical mile equals 1852 m`() {
    assertEquals(1852.0, length.convert(1.0, "nmi", "m"), 1e-12)
  }

  @Test fun `same unit returns identity`() {
    assertEquals(42.5, length.convert(42.5, "m", "m"), 0.0)
  }

  @Test fun `round-trip conversion returns original value`() {
    val v = 123.456
    val km = length.convert(v, "m", "km")
    val back = length.convert(km, "km", "m")
    assertEquals(v, back, 1e-9)
  }
}
