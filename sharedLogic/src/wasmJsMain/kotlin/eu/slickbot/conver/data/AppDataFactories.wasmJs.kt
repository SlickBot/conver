package eu.slickbot.conver.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable

actual class PlatformDataContext

actual fun createSettings(ctx: PlatformDataContext): ObservableSettings {
  return StorageSettings().makeObservable()
}
