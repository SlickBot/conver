package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.lengthConverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConverterEdgeCasesTest {

  private val length = lengthConverter()

  @Test fun `zero converts to zero`() {
    assertEquals(0.0, length.convert(0.0, "km", "m"), 0.0)
  }

  @Test fun `negative values convert linearly`() {
    assertEquals(-1000.0, length.convert(-1.0, "km", "m"), 1e-9)
  }

  @Test fun `very large value does not overflow to infinity`() {
    val r = length.convert(1e12, "km", "m")
    assertTrue(r.isFinite())
    assertEquals(1e15, r, 1.0)
  }

  @Test fun `very small value keeps precision`() {
    assertEquals(1e-6, length.convert(1e-3, "mm", "m"), 1e-18)
  }
}
