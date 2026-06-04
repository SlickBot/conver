package eu.slickbot.conver.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import eu.slickbot.conver.data.favorites.ConverDatabase
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toOkioPath
import java.io.File

actual class PlatformDataContext {
  val appDir: File = File(System.getProperty("user.home") ?: ".", ".conver").also { it.mkdirs() }
}

actual fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase> {
  val dbFile = File(ctx.appDir, "conver.db")
  return Room.databaseBuilder<ConverDatabase>(dbFile.absolutePath)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
}

actual fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences> =
  PreferenceDataStoreFactory.createWithPath(
    produceFile = { File(ctx.appDir, "conver.preferences_pb").toOkioPath() },
  )
