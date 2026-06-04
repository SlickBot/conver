package eu.slickbot.conver.data.favorites

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
  @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
  fun observeAll(): Flow<List<FavoriteEntity>>

  @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE converterId = :id)")
  fun observeIsFavorite(id: String): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(entity: FavoriteEntity)

  @Query("DELETE FROM favorites WHERE converterId = :id")
  suspend fun delete(id: String)
}

@Dao
interface HistoryDao {
  @Query("SELECT * FROM history ORDER BY at DESC LIMIT :limit")
  fun observeRecent(limit: Int = 50): Flow<List<HistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entity: HistoryEntity): Long

  @Query("DELETE FROM history")
  suspend fun clear()
}
