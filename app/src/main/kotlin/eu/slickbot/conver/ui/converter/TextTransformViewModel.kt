package eu.slickbot.conver.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.favorites.HistoryEntity
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.converter.TextConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TextTransformUiState(
  val converter: TextConverter,
  val input: String,
  val modeId: String,
  val isFavorite: Boolean,
) {
  val mode: TextConverter.Mode = converter.mode(modeId)
  val output: String = converter.run(modeId, input)
}

class TextTransformViewModel(
  converterId: String,
  private val registry: ConverterRegistry,
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  private val converter: TextConverter =
    (registry[converterId] as? TextConverter)
      ?: error("Unknown or non-text converter: $converterId")

  private val input = MutableStateFlow("")
  private val modeId = MutableStateFlow(converter.modes.first().id)

  val uiState: StateFlow<TextTransformUiState> = combine(
    input,
    modeId,
    favoritesRepo.observeIsFavorite(converter.id),
  ) { i, m, fav ->
    TextTransformUiState(converter, i, m, fav)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    TextTransformUiState(converter, "", modeId.value, isFavorite = false),
  )

  fun onInputChange(value: String) { input.value = value }
  fun onModeChange(id: String) { modeId.value = id }

  fun toggleFavorite() {
    viewModelScope.launch {
      favoritesRepo.toggleFavorite(converter.id, uiState.value.isFavorite)
    }
  }

  fun recordInHistory() {
    val state = uiState.value
    if (state.input.isEmpty() || state.output.isEmpty()) return
    viewModelScope.launch {
      favoritesRepo.recordConversion(
        HistoryEntity(
          converterId = converter.id,
          fromUnitId = state.modeId,
          toUnitId = state.modeId,
          input = state.input.take(200),
          output = state.output.take(200),
          at = System.currentTimeMillis(),
        ),
      )
    }
  }
}
