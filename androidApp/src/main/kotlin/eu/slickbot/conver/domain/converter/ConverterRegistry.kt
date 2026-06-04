package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.ConverterRegistry.Companion.default
import eu.slickbot.conver.domain.converter.converters.angleConverter
import eu.slickbot.conver.domain.converter.converters.areaConverter
import eu.slickbot.conver.domain.converter.converters.base64Converter
import eu.slickbot.conver.domain.converter.converters.baseConverter
import eu.slickbot.conver.domain.converter.converters.bmiConverter
import eu.slickbot.conver.domain.converter.converters.caesarConverter
import eu.slickbot.conver.domain.converter.converters.caseConverter
import eu.slickbot.conver.domain.converter.converters.clothingSizeConverter
import eu.slickbot.conver.domain.converter.converters.colorConverter
import eu.slickbot.conver.domain.converter.converters.compoundInterestConverter
import eu.slickbot.conver.domain.converter.converters.cryptoConverter
import eu.slickbot.conver.domain.converter.converters.currencyConverter
import eu.slickbot.conver.domain.converter.converters.dataRateConverter
import eu.slickbot.conver.domain.converter.converters.dataStorageConverter
import eu.slickbot.conver.domain.converter.converters.densityConverter
import eu.slickbot.conver.domain.converter.converters.discountConverter
import eu.slickbot.conver.domain.converter.converters.durationConverter
import eu.slickbot.conver.domain.converter.converters.durationFormatConverter
import eu.slickbot.conver.domain.converter.converters.energyConverter
import eu.slickbot.conver.domain.converter.converters.forceConverter
import eu.slickbot.conver.domain.converter.converters.frequencyConverter
import eu.slickbot.conver.domain.converter.converters.fuelEconomyConverter
import eu.slickbot.conver.domain.converter.converters.hashConverter
import eu.slickbot.conver.domain.converter.converters.htmlEntityConverter
import eu.slickbot.conver.domain.converter.converters.jsonConverter
import eu.slickbot.conver.domain.converter.converters.lengthConverter
import eu.slickbot.conver.domain.converter.converters.loanConverter
import eu.slickbot.conver.domain.converter.converters.loremConverter
import eu.slickbot.conver.domain.converter.converters.massConverter
import eu.slickbot.conver.domain.converter.converters.morseConverter
import eu.slickbot.conver.domain.converter.converters.numberToWordsConverter
import eu.slickbot.conver.domain.converter.converters.ovenTempConverter
import eu.slickbot.conver.domain.converter.converters.paperSizeConverter
import eu.slickbot.conver.domain.converter.converters.planetAgeConverter
import eu.slickbot.conver.domain.converter.converters.powerConverter
import eu.slickbot.conver.domain.converter.converters.pressureConverter
import eu.slickbot.conver.domain.converter.converters.receiptSplitConverter
import eu.slickbot.conver.domain.converter.converters.romanConverter
import eu.slickbot.conver.domain.converter.converters.runningPaceConverter
import eu.slickbot.conver.domain.converter.converters.shoeSizeConverter
import eu.slickbot.conver.domain.converter.converters.slugConverter
import eu.slickbot.conver.domain.converter.converters.speedConverter
import eu.slickbot.conver.domain.converter.converters.taxConverter
import eu.slickbot.conver.domain.converter.converters.temperatureConverter
import eu.slickbot.conver.domain.converter.converters.timestampConverter
import eu.slickbot.conver.domain.converter.converters.torqueConverter
import eu.slickbot.conver.domain.converter.converters.unicodeConverter
import eu.slickbot.conver.domain.converter.converters.urlEncodeConverter
import eu.slickbot.conver.domain.converter.converters.uuidConverter
import eu.slickbot.conver.domain.converter.converters.volumeConverter
import eu.slickbot.conver.domain.converter.converters.wordCountConverter

/**
 * Central catalog of every Converter in the app. Browse, search, and deep-links all read from here.
 * Adding a new converter = append one entry to [default].
 */
class ConverterRegistry(val all: List<Converter>) {

  companion object {
    fun default() = ConverterRegistry(
      listOf(
        // Measurement
        lengthConverter(),
        massConverter(),
        temperatureConverter(),
        volumeConverter(),
        areaConverter(),
        speedConverter(),
        durationConverter(),
        pressureConverter(),
        energyConverter(),
        powerConverter(),
        angleConverter(),
        frequencyConverter(),
        dataStorageConverter(),
        dataRateConverter(),
        fuelEconomyConverter(),
        densityConverter(),
        forceConverter(),
        torqueConverter(),
        // Numbers
        baseConverter(),
        romanConverter(),
        unicodeConverter(),
        numberToWordsConverter(),
        // Developer
        base64Converter(),
        urlEncodeConverter(),
        htmlEntityConverter(),
        hashConverter(),
        uuidConverter(),
        jsonConverter(),
        caseConverter(),
        slugConverter(),
        wordCountConverter(),
        morseConverter(),
        caesarConverter(),
        loremConverter(),
        // Color
        colorConverter(),
        // Time
        timestampConverter(),
        durationFormatConverter(),
        // Money & Finance
        currencyConverter(),
        cryptoConverter(),
        receiptSplitConverter(),
        taxConverter(),
        discountConverter(),
        loanConverter(),
        compoundInterestConverter(),
        // Everyday
        shoeSizeConverter(),
        clothingSizeConverter(),
        paperSizeConverter(),
        ovenTempConverter(),
        bmiConverter(),
        runningPaceConverter(),
        planetAgeConverter(),
      )
    )
  }

  private val byId = all.associateBy { it.id }

  operator fun get(id: String): Converter? {
    return byId[id]
  }

  fun byCategory(category: Category): List<Converter> {
    return all.filter { it.category == category }
  }
}
