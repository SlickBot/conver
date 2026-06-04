package eu.slickbot.conver.domain.converter

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping

// Kotlin String and NSString are bridged at runtime, so this cast succeeds even
// though the compiler cannot prove it - NSString has no String-accepting factory.
@Suppress("CAST_NEVER_SUCCEEDS")
actual fun normalizeNfd(input: String): String =
  (input as NSString).decomposedStringWithCanonicalMapping
