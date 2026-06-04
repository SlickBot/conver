package eu.slickbot.conver

import android.app.Application
import eu.slickbot.conver.data.PlatformDataContext
import eu.slickbot.conver.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.dsl.module

class ConverApp : Application() {

  override fun onCreate() {
    super.onCreate()
    initKoin {
      androidLogger(Level.INFO)
      androidContext(this@ConverApp)
      modules(module { single { PlatformDataContext(this@ConverApp) } })
    }
  }
}
