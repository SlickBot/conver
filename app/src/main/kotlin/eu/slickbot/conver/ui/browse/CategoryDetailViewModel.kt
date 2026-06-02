package eu.slickbot.conver.ui.browse

import androidx.lifecycle.ViewModel
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.Converter
import eu.slickbot.conver.domain.converter.ConverterRegistry

data class CategoryDetailUiState(
  val category: Category?,
  val converters: List<Converter>,
)

class CategoryDetailViewModel(
  categoryId: String,
  registry: ConverterRegistry,
) : ViewModel() {
  val uiState: CategoryDetailUiState = run {
    val category = Category.entries.firstOrNull { it.name == categoryId }
    CategoryDetailUiState(
      category = category,
      converters = category?.let { registry.byCategory(it) } ?: emptyList(),
    )
  }
}
