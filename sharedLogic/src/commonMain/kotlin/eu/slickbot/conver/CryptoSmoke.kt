package eu.slickbot.conver

import org.kotlincrypto.hash.md.MD5
import org.kotlincrypto.hash.sha2.SHA256

/** Phase-0 smoke proof that synchronous hashing works in common code. Removed in Phase 1. */
fun smokeHashHex(algorithm: String, input: String): String {
  val digest = when (algorithm) {
    "md5" -> MD5()
    "sha256" -> SHA256()
    else -> error("unknown algorithm $algorithm")
  }
  val bytes = digest.digest(input.encodeToByteArray())
  return bytes.joinToString("") { b ->
    val v = b.toInt() and 0xFF
    v.toString(16).padStart(2, '0')
  }
}
