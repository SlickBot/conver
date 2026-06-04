package eu.slickbot.conver.domain.converter

/** Unicode NFD (canonical decomposition) - used to strip diacritics in slugify. */
expect fun normalizeNfd(input: String): String
