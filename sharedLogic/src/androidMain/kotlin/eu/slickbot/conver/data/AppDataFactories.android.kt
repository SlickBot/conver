package eu.slickbot.conver.data

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings

actual class PlatformDataContext(val context: Context)

actual fun createSettings(ctx: PlatformDataContext): ObservableSettings {
  val prefs = ctx.context.getSharedPreferences("conver_settings", Context.MODE_PRIVATE)
  return SharedPreferencesSettings(prefs)
}
