package eu.slickbot.conver.domain.converter

import eu.slickbot.conver.domain.converter.converters.areaConverter
import eu.slickbot.conver.domain.converter.converters.dataStorageConverter
import eu.slickbot.conver.domain.converter.converters.durationConverter
import eu.slickbot.conver.domain.converter.converters.energyConverter
import eu.slickbot.conver.domain.converter.converters.fuelEconomyConverter
import eu.slickbot.conver.domain.converter.converters.massConverter
import eu.slickbot.conver.domain.converter.converters.pressureConverter
import eu.slickbot.conver.domain.converter.converters.speedConverter
import eu.slickbot.conver.domain.converter.converters.temperatureConverter
import eu.slickbot.conver.domain.converter.converters.volumeConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class MeasurementCatalogTest {

  @Test fun `temperature 0C equals 32F`() {
    val t = temperatureConverter()
    assertEquals(32.0, t.convert(0.0, "c", "f"), 1e-9)
    assertEquals(0.0, t.convert(32.0, "f", "c"), 1e-9)
  }

  @Test fun `temperature 100C equals 212F`() {
    val t = temperatureConverter()
    assertEquals(212.0, t.convert(100.0, "c", "f"), 1e-9)
  }

  @Test fun `absolute zero is 0 K and -273_15 C and -459_67 F`() {
    val t = temperatureConverter()
    assertEquals(-273.15, t.convert(0.0, "k", "c"), 1e-9)
    assertEquals(-459.67, t.convert(0.0, "k", "f"), 1e-9)
  }

  @Test fun `mass 1 lb equals 0_45359237 kg exactly`() {
    assertEquals(0.45359237, massConverter().convert(1.0, "lb", "kg"), 1e-12)
  }

  @Test fun `mass 1 stone equals 14 lb`() {
    assertEquals(14.0, massConverter().convert(1.0, "st", "lb"), 1e-9)
  }

  @Test fun `area 1 acre equals 4046_8564224 m2 exactly`() {
    assertEquals(4046.8564224, areaConverter().convert(1.0, "ac", "m2"), 1e-9)
  }

  @Test fun `volume 1 US gallon equals 3_785411784 L`() {
    assertEquals(3.785411784, volumeConverter().convert(1.0, "gal-us", "l"), 1e-9)
  }

  @Test fun `speed 60 mph equals roughly 96_56 kmh`() {
    assertEquals(96.5606, speedConverter().convert(60.0, "mph", "kmh"), 1e-3)
  }

  @Test fun `speed 1 knot equals 1852 metres per hour`() {
    // 1 knot = 1852 m / 3600 s = 0.5144... m/s; in km/h that's 1.852
    assertEquals(1.852, speedConverter().convert(1.0, "knot", "kmh"), 1e-9)
  }

  @Test fun `duration 1 hour equals 3600 seconds`() {
    assertEquals(3600.0, durationConverter().convert(1.0, "h", "s"), 1e-9)
  }

  @Test fun `pressure 1 bar equals 100000 Pa`() {
    assertEquals(100_000.0, pressureConverter().convert(1.0, "bar", "pa"), 1e-9)
  }

  @Test fun `pressure 1 atm equals 101325 Pa`() {
    assertEquals(101_325.0, pressureConverter().convert(1.0, "atm", "pa"), 1e-9)
  }

  @Test fun `energy 1 kWh equals 3_6 million joules`() {
    assertEquals(3_600_000.0, energyConverter().convert(1.0, "kwh", "j"), 1e-6)
  }

  @Test fun `data storage 1 MiB equals 1048576 bytes`() {
    assertEquals(1_048_576.0, dataStorageConverter().convert(1.0, "mib", "byte"), 1e-9)
  }

  @Test fun `data storage 1 MB equals 1e6 bytes`() {
    assertEquals(1_000_000.0, dataStorageConverter().convert(1.0, "mb", "byte"), 1e-9)
  }

  @Test fun `fuel economy round trip L per 100 and km per L`() {
    val f = fuelEconomyConverter()
    // 10 L/100km <-> 10 km/L
    assertEquals(10.0, f.convert(10.0, "lp100km", "kmpl"), 1e-9)
    // and 10 L/100km ≈ 23.52 mpg US (235.2145 / 10)
    assertEquals(23.52145, f.convert(10.0, "lp100km", "mpg-us"), 1e-3)
  }
}
