package eu.slickbot.conver.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.search.ConverterSearch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
  val query: String = "",
  val searching: Boolean = false,
  val results: List<Converter> = emptyList(),
  val recents: List<Converter> = emptyList(),
  val favorites: List<Converter> = emptyList(),
)

class HomeViewModel(
  private val registry: ConverterRegistry,
  private val search: ConverterSearch,
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  private val query = MutableStateFlow("")
  private val searching = MutableStateFlow(false)

  val uiState: StateFlow<HomeUiState> = combine(
    query,
    searching,
    favoritesRepo.observeFavorites(),
    favoritesRepo.observeRecentHistory(limit = 10),
  ) { q, isSearching, favs, history ->
    val favorites = favs.mapNotNull { registry[it.converterId] }
    val recents = history.map { it.converterId }.distinct().mapNotNull { registry[it] }
    HomeUiState(
      query = q,
      searching = isSearching,
      results = if (q.isBlank()) emptyList() else search.query(q),
      recents = recents,
      favorites = favorites,
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

  fun onQueryChange(q: String) { query.value = q }
  fun onSearchingChange(active: Boolean) { searching.value = active }
}
