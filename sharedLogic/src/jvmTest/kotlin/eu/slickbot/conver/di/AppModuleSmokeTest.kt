package eu.slickbot.conver.di

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

class AppModuleSmokeTest {

  @OptIn(KoinExperimentalAPI::class)
  @Test fun `koin domain module graph resolves`() {
    domainModule.verify()
  }
}
