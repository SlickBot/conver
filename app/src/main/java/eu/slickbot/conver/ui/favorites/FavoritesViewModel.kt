package eu.slickbot.conver.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.domain.converter.ConverterRegistry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class FavoritesUiState(val favorites: List<Converter> = emptyList())

class FavoritesViewModel(
  private val registry: ConverterRegistry,
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  val uiState: StateFlow<FavoritesUiState> = favoritesRepo.observeFavorites()
    .map { rows -> FavoritesUiState(rows.mapNotNull { registry[it.converterId] }) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())
}
