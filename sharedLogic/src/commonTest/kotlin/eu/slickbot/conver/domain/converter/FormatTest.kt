package eu.slickbot.conver.domain.converter

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatTest {
  @Test fun integers_drop_trailing_zeros() { assertEquals("100", formatResult(100.0)) }
  @Test fun short_decimals_keep_digits() { assertEquals("3.14", formatResult(3.14)) }
  @Test fun long_decimals_rounded() { assertEquals("0.333333", formatResult(1.0 / 3.0, maxDecimals = 6)) }
  @Test fun nan_and_infinity() {
    assertEquals("—", formatResult(Double.NaN))
    assertEquals("—", formatResult(Double.POSITIVE_INFINITY))
  }
  @Test fun trailing_dot_stripped() { assertEquals("2", formatResult(2.0000001, maxDecimals = 5)) }
  @Test fun negative_values() { assertEquals("-3.5", formatResult(-3.5)) }
  @Test fun half_up_rounding() { assertEquals("2.46", formatResult(2.455, maxDecimals = 2)) }
  @Test fun large_magnitude_no_scientific() { assertEquals("1000000000000", formatResult(1.0e12)) }
  @Test fun tiny_magnitude_no_scientific() { assertEquals("0.0000001", formatResult(1.0e-7, maxDecimals = 7)) }
}
