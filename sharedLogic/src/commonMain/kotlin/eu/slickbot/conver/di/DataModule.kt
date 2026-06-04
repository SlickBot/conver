package eu.slickbot.conver.di

import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.data.createDatabaseBuilder
import eu.slickbot.conver.data.createPreferencesDataStore
import eu.slickbot.conver.data.favorites.ConverDatabase
import eu.slickbot.conver.data.favorites.FavoritesRepository
import eu.slickbot.conver.data.prefs.UserPreferencesRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module = module {
  single {
    createDatabaseBuilder(get<PlatformDataContext>())
      // TODO before next release: remove destructive fallback and write a proper migration.
      // TODO Also set exportSchema = true.
      .fallbackToDestructiveMigration(dropAllTables = true)
      .build()
  }
  single { get<ConverDatabase>().favoriteDao() }
  single { get<ConverDatabase>().historyDao() }
  single { createPreferencesDataStore(get<PlatformDataContext>()) }
  single { FavoritesRepository(get(), get()) }
  single { UserPreferencesRepository(get()) }
}
