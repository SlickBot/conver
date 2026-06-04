package eu.slickbot.conver

import android.app.Application
import eu.slickbot.conver.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ConverApp : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidLogger(Level.INFO)
      androidContext(this@ConverApp)
      modules(appModule)
    }
  }
}
