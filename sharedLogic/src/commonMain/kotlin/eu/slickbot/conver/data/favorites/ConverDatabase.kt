package eu.slickbot.conver.data.favorites

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object ConverDatabaseConstructor : RoomDatabaseConstructor<ConverDatabase> {
  override fun initialize(): ConverDatabase
}

@Database(
  entities = [FavoriteEntity::class, HistoryEntity::class],
  version = 1,
  exportSchema = false,
)
@ConstructedBy(ConverDatabaseConstructor::class)
abstract class ConverDatabase : RoomDatabase() {
  abstract fun favoriteDao(): FavoriteDao
  abstract fun historyDao(): HistoryDao
}
