package eu.slickbot.conver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.core.okio.WebLocalStorage
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import eu.slickbot.conver.data.favorites.ConverDatabase
import kotlinx.coroutines.Dispatchers
import org.w3c.dom.Worker

// Web persistence: Room on OPFS-backed SQLite via a Web Worker (sqlite-web), DataStore on
// localStorage. The worker module is served as the "sqlite-wasm-worker" local NPM package.
actual class PlatformDataContext

// The full `new Worker(new URL(…, import.meta.url), {type:"module"})` must be a single js() expression
@OptIn(ExperimentalWasmJsInterop::class)
private fun createDbWorker(): Worker =
  js("""new Worker(new URL("sqlite-wasm-worker/worker.js", import.meta.url), { type: "module" })""")

actual fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase> {
  return Room.databaseBuilder<ConverDatabase>("conver.db")
    .setDriver(WebWorkerSQLiteDriver(createDbWorker()))
    .setQueryCoroutineContext(Dispatchers.Default)  // wasm has no Dispatchers.IO
}

actual fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences> {
  return PreferenceDataStoreFactory.create(
    storage = WebLocalStorage(serializer = PreferencesSerializer, name = "conver.preferences_pb"),
  )
}
