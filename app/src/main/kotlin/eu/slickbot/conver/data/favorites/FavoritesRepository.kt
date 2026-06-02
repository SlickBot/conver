package eu.slickbot.conver.data.favorites

import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
  private val favoriteDao: FavoriteDao,
  private val historyDao: HistoryDao,
) {
  fun observeFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.observeAll()
  fun observeIsFavorite(id: String): Flow<Boolean> = favoriteDao.observeIsFavorite(id)
  fun observeRecentHistory(limit: Int = 50): Flow<List<HistoryEntity>> =
    historyDao.observeRecent(limit)

  suspend fun toggleFavorite(id: String, currentlyFavorited: Boolean) {
    if (currentlyFavorited) favoriteDao.delete(id)
    else favoriteDao.upsert(FavoriteEntity(id, System.currentTimeMillis()))
  }

  suspend fun recordConversion(entry: HistoryEntity) {
    historyDao.insert(entry)
  }

  suspend fun clearHistory() = historyDao.clear()
}
