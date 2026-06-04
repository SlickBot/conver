package eu.slickbot.conver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import eu.slickbot.conver.data.favorites.ConverDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class PlatformDataContext

@OptIn(ExperimentalForeignApi::class)
private fun documentsDir(): String {
  val url: NSURL = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null,
  ) ?: error("could not resolve NSDocumentDirectory")
  return requireNotNull(url.path) { "NSDocumentDirectory URL had no path" }
}

actual fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase> {
  val dbPath = documentsDir() + "/conver.db"
  return Room.databaseBuilder<ConverDatabase>(dbPath)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
}

actual fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences> =
  PreferenceDataStoreFactory.createWithPath(
    produceFile = { (documentsDir() + "/conver.preferences_pb").toPath() },
  )
