package eu.slickbot.conver.data.favorites

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteEntity(
  val converterId: String,
  val addedAt: Long,
)

@Serializable
data class HistoryEntity(
  val converterId: String,
  val fromUnitId: String,
  val toUnitId: String,
  val input: String,
  val output: String,
  val at: Long,
)
