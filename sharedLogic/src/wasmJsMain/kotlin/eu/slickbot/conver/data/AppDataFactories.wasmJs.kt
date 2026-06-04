package eu.slickbot.conver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room3.RoomDatabase
import eu.slickbot.conver.data.favorites.ConverDatabase

// Web persistence (OPFS-backed SQLite via sqlite-web + OPFS-backed DataStore) is deferred to
// Phase 4. These actuals exist only so :sharedLogic:compileKotlinWasmJs stays green; no web app
// runs in Phase 1, so they throw at runtime.

actual class PlatformDataContext

actual fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase> {
  TODO("web persistence wired in Phase 4")
}

actual fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences> {
  TODO("web persistence wired in Phase 4")
}
