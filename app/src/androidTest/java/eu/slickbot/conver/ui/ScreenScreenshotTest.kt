package eu.slickbot.conver.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import eu.slickbot.conver.domain.converter.converters.base64Converter
import eu.slickbot.conver.domain.converter.converters.hashConverter
import eu.slickbot.conver.domain.converter.converters.lengthConverter
import eu.slickbot.conver.domain.converter.converters.temperatureConverter
import eu.slickbot.conver.ui.converter.MeasurementScreenContent
import eu.slickbot.conver.ui.converter.MeasurementUiState
import eu.slickbot.conver.ui.converter.TextTransformScreenContent
import eu.slickbot.conver.ui.converter.TextTransformUiState
import eu.slickbot.conver.ui.theme.ConverTheme
import eu.slickbot.conver.ui.theme.ThemeMode
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

/**
 * Instrumented screenshot tests — renders screens on a real device/emulator and saves PNGs.
 *
 * Run:  ./gradlew connectedDebugAndroidTest --tests "*.ScreenScreenshotTest"
 * Pull: adb pull /sdcard/Download/conver_screenshots/ screenshots/
 */
class ScreenScreenshotTest {

  @get:Rule
  val rule = createComposeRule()

  private val outDir = File("/sdcard/Download/conver_screenshots").also { it.mkdirs() }

  @Test
  fun measurement_length() {
    val converter = lengthConverter()
    rule.setContent {
      ConverTheme(themeMode = ThemeMode.Dark, dynamicColor = false) {
        MeasurementScreenContent(
          state = MeasurementUiState(converter, "1", "m", "ft", isFavorite = true),
          onInputChange = {}, onFromChange = {}, onToChange = {},
          onSwap = {}, onToggleFavorite = {}, onBack = {},
        )
      }
    }
    rule.waitForIdle()
    saveScreenshot("measurement_length")
  }

  @Test
  fun measurement_temperature() {
    val converter = temperatureConverter()
    rule.setContent {
      ConverTheme(themeMode = ThemeMode.Dark, dynamicColor = false) {
        MeasurementScreenContent(
          state = MeasurementUiState(converter, "100", "c", "f", isFavorite = false),
          onInputChange = {}, onFromChange = {}, onToChange = {},
          onSwap = {}, onToggleFavorite = {}, onBack = {},
        )
      }
    }
    rule.waitForIdle()
    saveScreenshot("measurement_temperature")
  }

  @Test
  fun text_hash() {
    val converter = hashConverter()
    rule.setContent {
      ConverTheme(themeMode = ThemeMode.Dark, dynamicColor = false) {
        TextTransformScreenContent(
          state = TextTransformUiState(converter, "Hello, World!", "sha256", isFavorite = false),
          onInputChange = {}, onModeChange = {}, onToggleFavorite = {}, onBack = {},
        )
      }
    }
    rule.waitForIdle()
    saveScreenshot("text_hash")
  }

  @Test
  fun text_base64() {
    val converter = base64Converter()
    rule.setContent {
      ConverTheme(themeMode = ThemeMode.Dark, dynamicColor = false) {
        TextTransformScreenContent(
          state = TextTransformUiState(converter, "Conver is the best app", "encode", isFavorite = true),
          onInputChange = {}, onModeChange = {}, onToggleFavorite = {}, onBack = {},
        )
      }
    }
    rule.waitForIdle()
    saveScreenshot("text_base64")
  }

  private fun saveScreenshot(name: String) {
    val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
    val file = File(outDir, "$name.png")
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
  }
}
