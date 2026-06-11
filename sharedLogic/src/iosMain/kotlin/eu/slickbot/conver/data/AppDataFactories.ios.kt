package eu.slickbot.conver.data

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

actual class PlatformDataContext

actual fun createSettings(ctx: PlatformDataContext): ObservableSettings {
  return NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
