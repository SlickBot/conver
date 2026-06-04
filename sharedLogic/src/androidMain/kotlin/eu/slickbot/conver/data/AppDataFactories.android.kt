package eu.slickbot.conver.data

import android.content.Context
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

actual class PlatformDataContext(val context: Context)

actual fun createDatabaseBuilder(ctx: PlatformDataContext): RoomDatabase.Builder<ConverDatabase> {
  val dbFile = ctx.context.getDatabasePath("conver.db")
  return Room.databaseBuilder<ConverDatabase>(ctx.context, dbFile.absolutePath)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
}

actual fun createPreferencesDataStore(ctx: PlatformDataContext): DataStore<Preferences> =
  PreferenceDataStoreFactory.createWithPath(
    produceFile = { File(ctx.context.filesDir, "conver.preferences_pb").toOkioPath() },
  )
