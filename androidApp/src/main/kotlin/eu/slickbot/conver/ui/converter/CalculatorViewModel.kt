package eu.slickbot.conver.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.favorites.HistoryEntity
import eu.slickbot.conver.domain.converter.CalculatorConverter
import eu.slickbot.conver.domain.converter.ConverterRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalculatorUiState(
  val converter: CalculatorConverter,
  val inputs: Map<String, String>,
  val modeId: String?,
  val isFavorite: Boolean,
) {
  val results: List<CalculatorConverter.Result>
    get() {
      val values = mutableMapOf<String, Double>()
      for (field in converter.fields) {
        val raw = inputs[field.id]
        val v = raw?.toDoubleOrNull() ?: field.default ?: return emptyList()
        values[field.id] = v
      }
      return runCatching { converter.calculate(modeId, values) }.getOrElse { emptyList() }
    }
}

class CalculatorViewModel(
  converterId: String,
  private val registry: ConverterRegistry,
  private val favoritesRepo: FavoritesRepository,
) : ViewModel() {

  companion object {
    private val ALLOWED = Regex("^-?\\d*(\\.\\d*)?$")
  }

  private val converter: CalculatorConverter =
    (registry[converterId] as? CalculatorConverter)
      ?: error("Unknown or non-calculator converter: $converterId")

  private val inputs = MutableStateFlow(
    converter.fields.associate { f ->
      f.id to (f.default?.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() } ?: "")
    },
  )
  private val modeId = MutableStateFlow(converter.defaultModeId())

  val uiState: StateFlow<CalculatorUiState> = combine(
    inputs,
    modeId,
    favoritesRepo.observeIsFavorite(converter.id),
  ) { i, m, fav ->
    CalculatorUiState(converter = converter, inputs = i, modeId = m, isFavorite = fav)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    CalculatorUiState(converter, inputs.value, modeId.value, isFavorite = false),
  )

  fun onFieldChange(fieldId: String, value: String) {
    if (value.isEmpty() || value.matches(ALLOWED)) {
      inputs.value = inputs.value.toMutableMap().apply { this[fieldId] = value }
    }
  }

  fun onModeChange(id: String) {
    modeId.value = id
  }

  fun toggleFavorite() {
    viewModelScope.launch {
      favoritesRepo.toggleFavorite(converter.id, uiState.value.isFavorite)
    }
  }

  fun recordInHistory() {
    val state = uiState.value
    val results = state.results
    if (results.isEmpty()) return
    viewModelScope.launch {
      favoritesRepo.recordConversion(
        HistoryEntity(
          converterId = converter.id,
          fromUnitId = state.modeId ?: "",
          toUnitId = "",
          input = state.inputs.values.joinToString(", "),
          output = results.joinToString(", ") { "${it.label} ${it.value}" },
          at = System.currentTimeMillis(),
        ),
      )
    }
  }
}
