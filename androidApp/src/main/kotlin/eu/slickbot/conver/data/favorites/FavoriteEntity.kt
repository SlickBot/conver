package eu.slickbot.conver.data.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
  @PrimaryKey val converterId: String,
  val addedAt: Long,
)

@Entity(tableName = "history")
data class HistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val converterId: String,
  val fromUnitId: String,
  val toUnitId: String,
  val input: String,
  val output: String,
  val at: Long,
)
