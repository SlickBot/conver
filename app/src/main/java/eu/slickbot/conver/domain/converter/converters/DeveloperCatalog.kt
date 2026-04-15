package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Html
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Tag
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.TextConverter
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

// ---------- Base64 -----------------------------------------------------------------------------

fun base64Converter(): TextConverter = TextConverter(
  id = "base64",
  name = "Base64",
  category = Category.Developer,
  icon = Icons.Outlined.Code,
  aliases = listOf("b64", "encoding", "base-64"),
  monospace = true,
  placeholder = "Paste text or Base64",
  modes = listOf(
    TextConverter.Mode("encode", "Encode") {
      Base64.getEncoder().encodeToString(it.toByteArray(Charsets.UTF_8))
    },
    TextConverter.Mode("decode", "Decode") {
      String(Base64.getDecoder().decode(it.trim()), Charsets.UTF_8)
    },
    TextConverter.Mode("encode-url", "Encode (URL-safe)") {
      Base64.getUrlEncoder().withoutPadding().encodeToString(it.toByteArray(Charsets.UTF_8))
    },
    TextConverter.Mode("decode-url", "Decode (URL-safe)") {
      String(Base64.getUrlDecoder().decode(it.trim()), Charsets.UTF_8)
    },
  ),
)

// ---------- URL encode -------------------------------------------------------------------------

fun urlEncodeConverter(): TextConverter = TextConverter(
  id = "url-encode",
  name = "URL encode",
  category = Category.Developer,
  icon = Icons.Outlined.Link,
  aliases = listOf("percent", "uri", "url"),
  monospace = true,
  modes = listOf(
    TextConverter.Mode("encode", "Encode") { URLEncoder.encode(it, "UTF-8") },
    TextConverter.Mode("decode", "Decode") { URLDecoder.decode(it, "UTF-8") },
  ),
)

// ---------- HTML entity ------------------------------------------------------------------------

private val htmlNamedEntities = mapOf(
  "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'",
  "nbsp" to "\u00A0", "copy" to "©", "reg" to "®", "trade" to "™",
  "hellip" to "…", "mdash" to "—", "ndash" to "–", "euro" to "€",
)

private fun htmlEncode(s: String): String = buildString {
  for (ch in s) {
    when (ch) {
      '&' -> append("&amp;")
      '<' -> append("&lt;")
      '>' -> append("&gt;")
      '"' -> append("&quot;")
      '\'' -> append("&#39;")
      else -> if (ch.code > 127) append("&#").append(ch.code).append(';') else append(ch)
    }
  }
}

private val entityRegex = Regex("&(#x?[0-9A-Fa-f]+|[a-zA-Z]+);")
private fun htmlDecode(s: String): String = entityRegex.replace(s) { match ->
  val body = match.groupValues[1]
  when {
    body.startsWith("#x", ignoreCase = true) -> body.substring(2).toIntOrNull(16)?.toChar()?.toString()
    body.startsWith("#") -> body.substring(1).toIntOrNull()?.toChar()?.toString()
    else -> htmlNamedEntities[body]
  } ?: match.value
}

fun htmlEntityConverter(): TextConverter = TextConverter(
  id = "html-entity",
  name = "HTML entity",
  category = Category.Developer,
  icon = Icons.Outlined.Html,
  aliases = listOf("html", "entity", "encode"),
  monospace = true,
  modes = listOf(
    TextConverter.Mode("encode", "Encode") { htmlEncode(it) },
    TextConverter.Mode("decode", "Decode") { htmlDecode(it) },
  ),
)

// ---------- Hashes -----------------------------------------------------------------------------

private fun hash(algorithm: String, input: String): String {
  val md = MessageDigest.getInstance(algorithm)
  val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
  return bytes.joinToString("") { "%02x".format(it) }
}

fun hashConverter(): TextConverter = TextConverter(
  id = "hash",
  name = "Hash",
  category = Category.Developer,
  icon = Icons.Outlined.Fingerprint,
  aliases = listOf("md5", "sha", "sha256", "sha-256", "digest"),
  monospace = true,
  placeholder = "Text to hash",
  modes = listOf(
    TextConverter.Mode("md5", "MD5") { hash("MD5", it) },
    TextConverter.Mode("sha1", "SHA-1") { hash("SHA-1", it) },
    TextConverter.Mode("sha256", "SHA-256") { hash("SHA-256", it) },
    TextConverter.Mode("sha512", "SHA-512") { hash("SHA-512", it) },
  ),
)

// ---------- UUID generator ---------------------------------------------------------------------

fun uuidConverter(): TextConverter = TextConverter(
  id = "uuid",
  name = "UUID generator",
  category = Category.Developer,
  icon = Icons.Outlined.Tag,
  aliases = listOf("guid", "uuid", "random id"),
  monospace = true,
  placeholder = "How many? (1–100)",
  modes = listOf(
    TextConverter.Mode("v4", "v4 (random)") { input ->
      val n = input.trim().toIntOrNull()?.coerceIn(1, 100) ?: 1
      (1..n).joinToString("\n") { UUID.randomUUID().toString() }
    },
  ),
)

// ---------- JSON formatter ---------------------------------------------------------------------

private fun formatJson(input: String, indent: Int): String {
  val trimmed = input.trim()
  return when (val parsed = JSONTokener(trimmed).nextValue()) {
    is JSONObject -> if (indent > 0) parsed.toString(indent) else parsed.toString()
    is JSONArray -> if (indent > 0) parsed.toString(indent) else parsed.toString()
    else -> parsed.toString()
  }
}

fun jsonConverter(): TextConverter = TextConverter(
  id = "json",
  name = "JSON formatter",
  category = Category.Developer,
  icon = Icons.Outlined.DataObject,
  aliases = listOf("json", "pretty", "format", "minify"),
  monospace = true,
  placeholder = "Paste JSON",
  modes = listOf(
    TextConverter.Mode("pretty", "Pretty (2 spaces)") { formatJson(it, indent = 2) },
    TextConverter.Mode("pretty-4", "Pretty (4 spaces)") { formatJson(it, indent = 4) },
    TextConverter.Mode("minify", "Minify") { formatJson(it, indent = 0) },
  ),
)

