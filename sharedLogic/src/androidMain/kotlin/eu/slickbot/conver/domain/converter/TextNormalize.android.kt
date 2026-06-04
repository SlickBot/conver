package eu.slickbot.conver.domain.converter

import java.text.Normalizer

actual fun normalizeNfd(input: String): String =
  Normalizer.normalize(input, Normalizer.Form.NFD)
