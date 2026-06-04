package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.domain.converter.hex2Lower
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.kotlincrypto.hash.md.MD5
import org.kotlincrypto.hash.sha1.SHA1
import org.kotlincrypto.hash.sha2.SHA256
import org.kotlincrypto.hash.sha2.SHA512

// ---------- Base64 -----------------------------------------------------------------------------

@OptIn(ExperimentalEncodingApi::class)
fun base64Converter(): TextConverter = TextConverter(
  id = "base64",
  name = "Base64",
  category = Category.Developer,
  icon = ConverterIcon.Code,
  aliases = listOf("b64", "encoding", "base-64"),
  monospace = true,
  placeholder = "Paste text or Base64",
  modes = listOf(
    TextConverter.Mode("encode", "Encode") {
      Base64.encode(it.encodeToByteArray())
    },
    TextConverter.Mode("decode", "Decode") {
      Base64.decode(it.trim()).decodeToString()
    },
    TextConverter.Mode("encode-url", "Encode (URL-safe)") {
      Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(it.encodeToByteArray())
    },
    TextConverter.Mode("decode-url", "Decode (URL-safe)") {
      Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL).decode(it.trim()).decodeToString()
    },
  ),
)

// ---------- URL encode -------------------------------------------------------------------------

fun urlEncodeConverter(): TextConverter = TextConverter(
  id = "url-encode",
  name = "URL encode",
  category = Category.Developer,
  icon = ConverterIcon.Link,
  aliases = listOf("percent", "uri", "url"),
  monospace = true,
  modes = listOf(
    TextConverter.Mode("encode", "Encode") { urlEncode(it) },
    TextConverter.Mode("decode", "Decode") { urlDecode(it) },
  ),
)

// ---------- HTML entity ------------------------------------------------------------------------

private val htmlNamedEntities = mapOf(
  "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'",
  "nbsp" to " ", "copy" to "©", "reg" to "®", "trade" to "™",
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
  icon = ConverterIcon.Html,
  aliases = listOf("html", "entity", "encode"),
  monospace = true,
  modes = listOf(
    TextConverter.Mode("encode", "Encode") { htmlEncode(it) },
    TextConverter.Mode("decode", "Decode") { htmlDecode(it) },
  ),
)

// ---------- Hashes -----------------------------------------------------------------------------

private fun hash(algorithm: String, input: String): String {
  val digest = when (algorithm) {
    "md5" -> MD5()
    "sha1" -> SHA1()
    "sha256" -> SHA256()
    "sha512" -> SHA512()
    else -> error("unknown $algorithm")
  }
  return digest.digest(input.encodeToByteArray()).joinToString("") { hex2Lower(it.toInt()) }
}

fun hashConverter(): TextConverter = TextConverter(
  id = "hash",
  name = "Hash",
  category = Category.Developer,
  icon = ConverterIcon.Fingerprint,
  aliases = listOf("md5", "sha", "sha256", "sha-256", "digest"),
  monospace = true,
  placeholder = "Text to hash",
  modes = listOf(
    TextConverter.Mode("md5", "MD5") { hash("md5", it) },
    TextConverter.Mode("sha1", "SHA-1") { hash("sha1", it) },
    TextConverter.Mode("sha256", "SHA-256") { hash("sha256", it) },
    TextConverter.Mode("sha512", "SHA-512") { hash("sha512", it) },
  ),
)

// ---------- UUID generator ---------------------------------------------------------------------

@OptIn(ExperimentalUuidApi::class)
fun uuidConverter(): TextConverter = TextConverter(
  id = "uuid",
  name = "UUID generator",
  category = Category.Developer,
  icon = ConverterIcon.Tag,
  aliases = listOf("guid", "uuid", "random id"),
  monospace = true,
  placeholder = "How many? (1–100)",
  modes = listOf(
    TextConverter.Mode("v4", "v4 (random)") { input ->
      val n = input.trim().toIntOrNull()?.coerceIn(1, 100) ?: 1
      (1..n).joinToString("\n") { Uuid.random().toString() }
    },
  ),
)

// ---------- JSON formatter ---------------------------------------------------------------------

private val prettyJson = Json { prettyPrint = true; prettyPrintIndent = "  " }
private val prettyJson4 = Json { prettyPrint = true; prettyPrintIndent = "    " }
private val minifyJson = Json { prettyPrint = false }

private fun formatJson(input: String, json: Json): String {
  val element = Json.parseToJsonElement(input.trim())
  return json.encodeToString(JsonElement.serializer(), element)
}

fun jsonConverter(): TextConverter = TextConverter(
  id = "json",
  name = "JSON formatter",
  category = Category.Developer,
  icon = ConverterIcon.DataObject,
  aliases = listOf("json", "pretty", "format", "minify"),
  monospace = true,
  placeholder = "Paste JSON",
  modes = listOf(
    TextConverter.Mode("pretty", "Pretty (2 spaces)") { formatJson(it, prettyJson) },
    TextConverter.Mode("pretty-4", "Pretty (4 spaces)") { formatJson(it, prettyJson4) },
    TextConverter.Mode("minify", "Minify") { formatJson(it, minifyJson) },
  ),
)
