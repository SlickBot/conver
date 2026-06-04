package eu.slickbot.conver.data.favorites

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [FavoriteEntity::class, HistoryEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class ConverDatabase : RoomDatabase() {
  abstract fun favoriteDao(): FavoriteDao
  abstract fun historyDao(): HistoryDao
}
