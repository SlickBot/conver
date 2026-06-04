package eu.slickbot.conver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room3.RoomDatabase
import eu.slickbot.conver.data.favorites.ConverDatabase

/** Platform handle carrying whatever each target needs to locate app storage (Android: Context). */
expect class PlatformDataContext

/**
 * Builds a [RoomDatabase.Builder] for [ConverDatabase] with the platform SQLite driver and a
 * background query coroutine context already configured. The caller still applies migration policy
 * and calls [RoomDatabase.Builder.build].
 */
expect fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase>

/** Builds the platform-backed preferences [DataStore]. */
expect fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences>
