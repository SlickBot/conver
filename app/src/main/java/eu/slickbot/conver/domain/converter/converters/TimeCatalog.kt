package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Schedule
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.TextConverter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// ---------- Unix timestamp ---------------------------------------------------------------------

fun timestampConverter(): TextConverter = TextConverter(
  id = "timestamp",
  name = "Unix timestamp",
  category = Category.Time,
  icon = Icons.Outlined.Schedule,
  aliases = listOf("epoch", "unix", "timestamp", "date"),
  monospace = true,
  placeholder = "1733923200 or 2025-01-01T00:00:00Z",
  modes = listOf(
    TextConverter.Mode("to-date", "Timestamp → Date") { input ->
      val n = input.trim().toLong()
      // Accept seconds or milliseconds.
      val instant = if (abs(n) > 9_999_999_999L) {
        Instant.fromEpochMilliseconds(n)
      } else {
        Instant.fromEpochSeconds(n)
      }
      val utc = instant.toLocalDateTime(TimeZone.UTC)
      val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
      buildString {
        append("UTC:   ").append(utc).append('Z').append('\n')
        append("Local: ").append(local)
      }
    },
    TextConverter.Mode("to-seconds", "Date → Seconds") { input ->
      Instant.parse(input.trim()).epochSeconds.toString()
    },
    TextConverter.Mode("to-millis", "Date → Millis") { input ->
      Instant.parse(input.trim()).toEpochMilliseconds().toString()
    },
    TextConverter.Mode("now", "Now") { _ ->
      val millis = java.lang.System.currentTimeMillis()
      val now = Instant.fromEpochMilliseconds(millis)
      buildString {
        append("Seconds: ").append(now.epochSeconds).append('\n')
        append("Millis:  ").append(millis).append('\n')
        append("UTC:     ").append(now.toLocalDateTime(TimeZone.UTC)).append('Z')
      }
    },
  ),
)

// ---------- Duration HH:MM:SS ------------------------------------------------------------------

fun durationFormatConverter(): TextConverter = TextConverter(
  id = "duration-format",
  name = "Duration format",
  category = Category.Time,
  icon = Icons.Outlined.HourglassTop,
  aliases = listOf("hms", "duration", "seconds to hours"),
  monospace = true,
  placeholder = "3725 or 01:02:05",
  modes = listOf(
    TextConverter.Mode("seconds-to-hms", "Seconds → HH:MM:SS") { input ->
      val total = input.trim().toLong()
      val abs = kotlin.math.abs(total)
      val h = abs / 3600
      val m = (abs % 3600) / 60
      val s = abs % 60
      val sign = if (total < 0) "-" else ""
      "%s%02d:%02d:%02d".format(sign, h, m, s)
    },
    TextConverter.Mode("hms-to-seconds", "HH:MM:SS → Seconds") { input ->
      val s = input.trim().removePrefix("-")
      val parts = s.split(':').map { it.toLong() }
      val total = when (parts.size) {
        3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
        2 -> parts[0] * 60 + parts[1]
        1 -> parts[0]
        else -> throw IllegalArgumentException("Expected HH:MM:SS, MM:SS, or SS")
      }
      val signed = if (input.trim().startsWith('-')) -total else total
      signed.toString()
    },
  ),
)

private fun abs(n: Long): Long = if (n < 0) -n else n
