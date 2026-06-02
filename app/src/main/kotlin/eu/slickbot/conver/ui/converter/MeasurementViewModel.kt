package eu.slickbot.conver.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.favorites.HistoryEntity
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.formatResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MeasurementUiState(
  val converter: MeasurementConverter,
  val input: String,
  val fromUnitId: String,
  val toUnitId: String,
  val isFavorite: Boolean,
) {
  val inputValue: Double? get() = input.toDoubleOrNull()
  val result: Double? get() = inputValue?.let { converter.convert(it, fromUnitId, toUnitId) }
  val resultString: String get() = result?.let { formatResult(it) } ?: ""
  val otherUnits get() = converter.units.filter { it.id != toUnitId }
}

class MeasurementViewModel(
  converterId: String,
  private val registry: ConverterRegistry,
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  private val converter: MeasurementConverter =
    (registry[converterId] as? MeasurementConverter)
      ?: error("Unknown or non-measurement converter: $converterId")

  private val input = MutableStateFlow("1")
  private val fromId = MutableStateFlow(converter.defaultFromId)
  private val toId = MutableStateFlow(converter.defaultToId)

  val uiState: StateFlow<MeasurementUiState> = combine(
    input,
    fromId,
    toId,
    favoritesRepo.observeIsFavorite(converter.id),
  ) { i, f, t, fav ->
    MeasurementUiState(
      converter = converter,
      input = i,
      fromUnitId = f,
      toUnitId = t,
      isFavorite = fav,
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    MeasurementUiState(converter, input.value, fromId.value, toId.value, isFavorite = false),
  )

  fun onInputChange(value: String) {
    if (value.isEmpty() || value.matches(ALLOWED)) input.value = value
  }

  fun onFromChange(id: String) { fromId.value = id }
  fun onToChange(id: String) { toId.value = id }

  fun swap() {
    val oldFrom = fromId.value
    fromId.value = toId.value
    toId.value = oldFrom
  }

  fun toggleFavorite() {
    viewModelScope.launch {
      favoritesRepo.toggleFavorite(converter.id, uiState.value.isFavorite)
    }
  }

  fun recordInHistory() {
    val state = uiState.value
    val result = state.result ?: return
    viewModelScope.launch {
      favoritesRepo.recordConversion(
        HistoryEntity(
          converterId = converter.id,
          fromUnitId = state.fromUnitId,
          toUnitId = state.toUnitId,
          input = state.input,
          output = formatResult(result),
          at = System.currentTimeMillis(),
        ),
      )
    }
  }

  private companion object {
    val ALLOWED = Regex("^-?\\d*(\\.\\d*)?$")
  }
}
