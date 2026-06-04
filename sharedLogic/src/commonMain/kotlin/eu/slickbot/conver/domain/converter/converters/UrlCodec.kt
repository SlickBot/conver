package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.hex2

/**
 * Multiplatform application/x-www-form-urlencoded encode/decode (RFC 3986 unreserved kept;
 * space→`+` on encode, `+`→space and `%XX` on decode, UTF-8). Replaces java.net.URLEncoder/Decoder.
 */
private const val UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~"

fun urlEncode(s: String): String = buildString {
  for (b in s.encodeToByteArray()) {
    val c = b.toInt().toChar()
    when {
      c in UNRESERVED -> append(c)
      c == ' ' -> append('+')
      else -> append('%').append(hex2(b.toInt()))
    }
  }
}

fun urlDecode(s: String): String {
  val out = ArrayList<Byte>(s.length)
  var i = 0
  while (i < s.length) {
    when (val c = s[i]) {
      '+' -> { out.add(' '.code.toByte()); i++ }
      '%' -> { out.add(s.substring(i + 1, i + 3).toInt(16).toByte()); i += 3 }
      else -> { out.add(c.code.toByte()); i++ }
    }
  }
  return out.toByteArray().decodeToString()
}
