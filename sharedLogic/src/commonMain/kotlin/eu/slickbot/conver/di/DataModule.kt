package eu.slickbot.conver.di

import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.data.createSettings
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.ioDispatcher
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module = module {
  single { Json { ignoreUnknownKeys = true } }
  single<CoroutineDispatcher> { ioDispatcher }
  single { createSettings(get<PlatformDataContext>()) }
  single { FavoritesRepository(get(), get(), get()) }
  single { UserPreferencesRepository(get(), get()) }
}
