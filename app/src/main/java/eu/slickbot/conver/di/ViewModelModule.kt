package eu.slickbot.conver.di

import eu.slickbot.conver.ui.browse.BrowseViewModel
import eu.slickbot.conver.ui.converter.MeasurementViewModel
import eu.slickbot.conver.ui.converter.TextTransformViewModel
import eu.slickbot.conver.ui.favorites.FavoritesViewModel
import eu.slickbot.conver.ui.home.HomeViewModel
import eu.slickbot.conver.ui.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule: Module = module {
  viewModel { HomeViewModel(get(), get(), get()) }
  viewModel { BrowseViewModel(get()) }
  viewModel { FavoritesViewModel(get(), get()) }
  viewModel { SettingsViewModel(get()) }
  viewModel { (converterId: String) -> MeasurementViewModel(converterId, get(), get()) }
  viewModel { (converterId: String) -> TextTransformViewModel(converterId, get(), get()) }
}
