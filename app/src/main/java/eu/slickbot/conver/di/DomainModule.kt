package eu.slickbot.conver.di

import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.search.ConverterSearch
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
  single { ConverterRegistry.default() }
  single { ConverterSearch(get()) }
}
