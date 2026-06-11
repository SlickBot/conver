package eu.slickbot.conver.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual class PlatformDataContext

actual fun createSettings(ctx: PlatformDataContext): ObservableSettings {
  return PreferencesSettings(Preferences.userRoot().node("eu/slickbot/conver"))
}
