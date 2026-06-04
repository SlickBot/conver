package eu.slickbot.conver.di

import org.koin.dsl.module

val appModule = module {
  includes(dataModule, domainModule, viewModelModule)
}
