package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.caesarConverter
import eu.slickbot.conver.domain.converter.converters.colorConverter
import eu.slickbot.conver.domain.converter.converters.durationFormatConverter
import eu.slickbot.conver.domain.converter.converters.morseConverter
import eu.slickbot.conver.domain.converter.converters.wordCountConverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextCatalogTest {

  @Test fun `morse encodes SOS`() {
    val m = morseConverter()
    assertEquals("... --- ...", m.run("to-morse", "SOS"))
  }

  @Test fun `morse round trip`() {
    val m = morseConverter()
    val encoded = m.run("to-morse", "HELLO WORLD")
    assertEquals("HELLO WORLD", m.run("from-morse", encoded))
  }

  @Test fun `caesar rot13 self inverse`() {
    val c = caesarConverter()
    val input = "Hello, World!"
    val rotated = c.run("rot13", input)
    assertEquals(input, c.run("rot13", rotated))
  }

  @Test fun `word count counts words and characters`() {
    val w = wordCountConverter()
    val result = w.run("all", "Hello, World!")
    assertTrue(result.contains("Words: 2"))
    assertTrue(result.contains("Characters: 13"))
  }

  @Test fun `color hex in renders all three notations`() {
    val c = colorConverter()
    val out = c.run("hex-in", "#FF3366")
    assertTrue(out.contains("HEX: #FF3366"))
    assertTrue(out.contains("RGB: rgb(255, 51, 102)"))
    assertTrue(out.contains("HSL: hsl("))
  }

  @Test fun `color rgb in accepts triple formats`() {
    val c = colorConverter()
    val viaComma = c.run("rgb-in", "255, 51, 102")
    val viaRgbWrapper = c.run("rgb-in", "rgb(255, 51, 102)")
    assertEquals(viaComma, viaRgbWrapper)
  }

  @Test fun `duration format seconds to hms`() {
    val d = durationFormatConverter()
    assertEquals("01:02:05", d.run("seconds-to-hms", "3725"))
    assertEquals("00:00:00", d.run("seconds-to-hms", "0"))
    assertEquals("-01:00:00", d.run("seconds-to-hms", "-3600"))
  }

  @Test fun `duration format hms to seconds`() {
    val d = durationFormatConverter()
    assertEquals("3725", d.run("hms-to-seconds", "01:02:05"))
    assertEquals("90", d.run("hms-to-seconds", "01:30"))
    assertEquals("42", d.run("hms-to-seconds", "42"))
  }
}
