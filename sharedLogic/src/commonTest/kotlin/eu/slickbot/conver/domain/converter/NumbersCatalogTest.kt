package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.baseConverter
import eu.slickbot.conver.domain.converter.converters.numberToWords
import eu.slickbot.conver.domain.converter.converters.romanConverter
import eu.slickbot.conver.domain.converter.converters.unicodeConverter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumbersCatalogTest {

  @Test fun `base converter decimal to hex`() {
    val b = baseConverter()
    assertEquals("0xFF", b.run("hex", "255"))
    assertEquals("0x2A", b.run("hex", "42"))
  }

  @Test fun `base converter decimal to binary`() {
    val b = baseConverter()
    assertEquals("0b1010", b.run("bin", "10"))
    assertEquals("0b11111111", b.run("bin", "255"))
  }

  @Test fun `base converter hex input to decimal`() {
    val b = baseConverter()
    assertEquals("255", b.run("dec", "0xff"))
    assertEquals("255", b.run("dec", "0xFF"))
  }

  @Test fun `base converter binary input`() {
    val b = baseConverter()
    assertEquals("10", b.run("dec", "0b1010"))
  }

  @Test fun `roman numerals basic`() {
    val r = romanConverter()
    assertEquals("IV", r.run("to-roman", "4"))
    assertEquals("IX", r.run("to-roman", "9"))
    assertEquals("XL", r.run("to-roman", "40"))
    assertEquals("MCMXCIX", r.run("to-roman", "1999"))
    assertEquals("MMXXV", r.run("to-roman", "2025"))
  }

  @Test fun `roman numerals reverse`() {
    val r = romanConverter()
    assertEquals("1994", r.run("from-roman", "MCMXCIV"))
    assertEquals("2025", r.run("from-roman", "MMXXV"))
  }

  @Test fun `roman numerals rejects non-canonical`() {
    val r = romanConverter()
    // "IIII" is historical but not canonical; should surface an error message (not throw).
    val result = r.run("from-roman", "IIII")
    assertTrue(result.contains("canonical", ignoreCase = true), "non-canonical should surface an error, got: $result")
  }

  @Test fun `unicode char to code point`() {
    val u = unicodeConverter()
    val result = u.run("char-to-cp", "A")
    assertTrue(result.contains("U+0041"))
    assertTrue(result.contains("65"))
  }

  @Test fun `unicode code point to char`() {
    val u = unicodeConverter()
    assertEquals("A", u.run("cp-to-char", "U+0041"))
    assertEquals("A", u.run("cp-to-char", "0x41"))
    assertEquals("A", u.run("cp-to-char", "41"))
  }

  @Test fun `number to words covers boundaries`() {
    assertEquals("zero", numberToWords(0))
    assertEquals("one", numberToWords(1))
    assertEquals("twenty-one", numberToWords(21))
    assertEquals("one hundred", numberToWords(100))
    assertEquals("one hundred one", numberToWords(101))
    assertEquals("one thousand", numberToWords(1000))
    assertEquals(
      "one million two hundred thirty-four thousand five hundred sixty-seven",
      numberToWords(1_234_567),
    )
  }
}
