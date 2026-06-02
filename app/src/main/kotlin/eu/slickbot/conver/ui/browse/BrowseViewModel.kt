package eu.slickbot.conver.ui.browse

import androidx.lifecycle.ViewModel
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.domain.converter.ConverterRegistry

data class BrowseUiState(
  val sections: List<Pair<Category, List<Converter>>>,
)

class BrowseViewModel(
  registry: ConverterRegistry,
) : ViewModel() {
  val uiState: BrowseUiState = BrowseUiState(
    sections = Category.entries
      .map { it to registry.byCategory(it) }
      .filter { it.second.isNotEmpty() },
  )
}
