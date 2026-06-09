package eu.slickbot.conver.ui.settings

/**
 * Per-platform availability of settings that only do something on certain targets, so the UI can
 * hide toggles that would be inert. Dynamic (wallpaper) color is Android 12+ only; haptic feedback
 * exists on touch devices (Android, iOS) but is a no-op on desktop and web.
 */
expect val supportsDynamicColor: Boolean

expect val supportsHaptics: Boolean
