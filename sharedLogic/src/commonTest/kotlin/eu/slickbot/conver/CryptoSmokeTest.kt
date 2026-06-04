package eu.slickbot.conver

import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoSmokeTest {
  @Test fun md5_of_abc() {
    assertEquals("900150983cd24fb0d6963f7d28e17f72", smokeHashHex("md5", "abc"))
  }

  @Test fun sha256_of_abc() {
    assertEquals(
      "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
      smokeHashHex("sha256", "abc"),
    )
  }
}
