package eu.slickbot.conver.data.favorites

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class FavoritesRepository(
  private val settings: ObservableSettings,
  private val json: Json,
  private val dispatcher: CoroutineDispatcher,
) {

  companion object {
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_HISTORY = "history"
    private const val MAX_HISTORY = 50
  }

  private val writeMutex = Mutex()

  fun observeFavorites(): Flow<List<FavoriteEntity>> {
    return settings.getStringOrNullFlow(KEY_FAVORITES).map { decodeFavorites(it) }
  }

  fun observeIsFavorite(id: String): Flow<Boolean> {
    return settings.getStringOrNullFlow(KEY_FAVORITES)
      .map { raw -> decodeFavorites(raw).any { it.converterId == id } }
      .distinctUntilChanged()
  }

  fun observeRecentHistory(limit: Int = 50): Flow<List<HistoryEntity>> {
    return settings.getStringOrNullFlow(KEY_HISTORY).map { decodeHistory(it).take(limit) }
  }

  suspend fun toggleFavorite(id: String, currentlyFavorited: Boolean) {
    edit {
      val remaining = decodeFavorites(settings.getStringOrNull(KEY_FAVORITES)).filterNot { it.converterId == id }
      val updated = if (currentlyFavorited) {
        remaining
      } else {
        remaining + FavoriteEntity(id, Clock.System.now().toEpochMilliseconds())
      }
      settings.putString(KEY_FAVORITES, json.encodeToString(updated))
    }
  }

  suspend fun recordConversion(entry: HistoryEntity) {
    edit {
      val updated = (listOf(entry) + decodeHistory(settings.getStringOrNull(KEY_HISTORY))).take(MAX_HISTORY)
      settings.putString(KEY_HISTORY, json.encodeToString(updated))
    }
  }

  private suspend fun edit(block: () -> Unit) {
    withContext(dispatcher) {
      writeMutex.withLock { block() }
    }
  }

  private fun decodeFavorites(raw: String?): List<FavoriteEntity> {
    if (raw == null) return emptyList()
    return json.decodeFromString<List<FavoriteEntity>>(raw).sortedByDescending { it.addedAt }
  }

  private fun decodeHistory(raw: String?): List<HistoryEntity> {
    if (raw == null) return emptyList()
    return json.decodeFromString<List<HistoryEntity>>(raw).sortedByDescending { it.at }
  }
}
