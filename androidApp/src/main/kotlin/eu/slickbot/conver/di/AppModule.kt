package eu.slickbot.conver.di

import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
  includes(dataModule, domainModule, viewModelModule)
}
