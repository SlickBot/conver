package eu.slickbot.conver.data

import com.russhwolf.settings.ObservableSettings

/** Platform handle carrying whatever each target needs to locate app storage (Android: Context). */
expect class PlatformDataContext

/** Builds the platform-backed observable key-value store used for all app persistence. */
expect fun createSettings(ctx: PlatformDataContext): ObservableSettings
