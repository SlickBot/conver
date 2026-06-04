package eu.slickbot.conver.domain.converter

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun jsNormalizeNfd(input: String): String =
  js("input.normalize('NFD')")

actual fun normalizeNfd(input: String): String = jsNormalizeNfd(input)
