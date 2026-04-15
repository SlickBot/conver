package eu.slickbot.conver.domain.converter

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTest {

  @Test fun `integers drop trailing zeros`() {
    assertEquals("100", formatResult(100.0))
  }

  @Test fun `short decimals keep their digits`() {
    assertEquals("3.14", formatResult(3.14))
  }

  @Test fun `long decimals are rounded to max fractional digits`() {
    assertEquals("0.333333", formatResult(1.0 / 3.0, maxDecimals = 6))
  }

  @Test fun `NaN and infinity render as em-dash`() {
    assertEquals("—", formatResult(Double.NaN))
    assertEquals("—", formatResult(Double.POSITIVE_INFINITY))
  }

  @Test fun `trailing dot is stripped`() {
    assertEquals("2", formatResult(2.0000001, maxDecimals = 5))
  }
}
