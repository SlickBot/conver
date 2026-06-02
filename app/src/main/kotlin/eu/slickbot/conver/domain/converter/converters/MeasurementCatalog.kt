package eu.slickbot.conver.domain.converter.converters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Waves
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.MeasurementConverter

// ---------- Mass --------------------------------------------------------------------------------

fun massConverter(): MeasurementConverter = MeasurementConverter(
  id = "mass",
  name = "Mass",
  category = Category.Measurement,
  icon = Icons.Outlined.Balance,
  aliases = listOf("weight", "kg", "pounds", "grams", "ounces", "ton"),
  units = listOf(
    MeasureUnit.linear("mg", "Milligram", "mg", 1e-6),
    MeasureUnit.linear("g", "Gram", "g", 1e-3),
    MeasureUnit.linear("kg", "Kilogram", "kg", 1.0),
    MeasureUnit.linear("t", "Metric ton", "t", 1000.0),
    MeasureUnit.linear("oz", "Ounce", "oz", 0.028349523125),
    MeasureUnit.linear("lb", "Pound", "lb", 0.45359237),
    MeasureUnit.linear("st", "Stone", "st", 6.35029318),
    MeasureUnit.linear("us-ton", "US short ton", "ton (US)", 907.18474),
    MeasureUnit.linear("uk-ton", "UK long ton", "ton (UK)", 1016.0469088),
  ),
  defaultFromId = "kg",
  defaultToId = "lb",
)

// ---------- Temperature (affine, uses explicit toBase/fromBase) ---------------------------------

fun temperatureConverter(): MeasurementConverter = MeasurementConverter(
  id = "temperature",
  name = "Temperature",
  category = Category.Measurement,
  icon = Icons.Outlined.DeviceThermostat,
  aliases = listOf("temp", "celsius", "fahrenheit", "kelvin"),
  units = listOf(
    MeasureUnit(
      id = "c", name = "Celsius", symbol = "°C",
      toBase = { it + 273.15 }, fromBase = { it - 273.15 },
    ),
    MeasureUnit(
      id = "f", name = "Fahrenheit", symbol = "°F",
      toBase = { (it + 459.67) * 5.0 / 9.0 }, fromBase = { it * 9.0 / 5.0 - 459.67 },
    ),
    MeasureUnit(
      id = "k", name = "Kelvin", symbol = "K",
      toBase = { it }, fromBase = { it },
    ),
    MeasureUnit(
      id = "r", name = "Rankine", symbol = "°R",
      toBase = { it * 5.0 / 9.0 }, fromBase = { it * 9.0 / 5.0 },
    ),
  ),
  defaultFromId = "c",
  defaultToId = "f",
)

// ---------- Volume ------------------------------------------------------------------------------

fun volumeConverter(): MeasurementConverter = MeasurementConverter(
  id = "volume",
  name = "Volume",
  category = Category.Measurement,
  icon = Icons.Outlined.Waves,
  aliases = listOf("liters", "gallon", "cup", "ml", "capacity"),
  units = listOf(
    MeasureUnit.linear("ml", "Millilitre", "mL", 0.001),
    MeasureUnit.linear("cl", "Centilitre", "cL", 0.01),
    MeasureUnit.linear("l", "Litre", "L", 1.0),
    MeasureUnit.linear("m3", "Cubic metre", "m³", 1000.0),
    MeasureUnit.linear("in3", "Cubic inch", "in³", 0.016387064),
    MeasureUnit.linear("ft3", "Cubic foot", "ft³", 28.316846592),
    MeasureUnit.linear("gal-us", "Gallon (US)", "gal (US)", 3.785411784),
    MeasureUnit.linear("qt-us", "Quart (US)", "qt (US)", 0.946352946),
    MeasureUnit.linear("pt-us", "Pint (US)", "pt (US)", 0.473176473),
    MeasureUnit.linear("cup-us", "Cup (US)", "cup (US)", 0.2365882365),
    MeasureUnit.linear("floz-us", "Fluid ounce (US)", "fl oz (US)", 0.0295735295625),
    MeasureUnit.linear("tbsp", "Tablespoon (US)", "tbsp", 0.01478676478125),
    MeasureUnit.linear("tsp", "Teaspoon (US)", "tsp", 0.00492892159375),
    MeasureUnit.linear("gal-uk", "Gallon (UK)", "gal (UK)", 4.54609),
    MeasureUnit.linear("floz-uk", "Fluid ounce (UK)", "fl oz (UK)", 0.0284130625),
  ),
  defaultFromId = "l",
  defaultToId = "gal-us",
)

// ---------- Area --------------------------------------------------------------------------------

fun areaConverter(): MeasurementConverter = MeasurementConverter(
  id = "area",
  name = "Area",
  category = Category.Measurement,
  icon = Icons.Outlined.CropSquare,
  aliases = listOf("acre", "hectare", "square meter"),
  units = listOf(
    MeasureUnit.linear("mm2", "Square millimetre", "mm²", 1e-6),
    MeasureUnit.linear("cm2", "Square centimetre", "cm²", 1e-4),
    MeasureUnit.linear("m2", "Square metre", "m²", 1.0),
    MeasureUnit.linear("km2", "Square kilometre", "km²", 1e6),
    MeasureUnit.linear("ha", "Hectare", "ha", 1e4),
    MeasureUnit.linear("in2", "Square inch", "in²", 6.4516e-4),
    MeasureUnit.linear("ft2", "Square foot", "ft²", 0.09290304),
    MeasureUnit.linear("yd2", "Square yard", "yd²", 0.83612736),
    MeasureUnit.linear("ac", "Acre", "ac", 4046.8564224),
    MeasureUnit.linear("mi2", "Square mile", "mi²", 2.589988110336e6),
  ),
  defaultFromId = "m2",
  defaultToId = "ft2",
)

// ---------- Speed -------------------------------------------------------------------------------

fun speedConverter(): MeasurementConverter = MeasurementConverter(
  id = "speed",
  name = "Speed",
  category = Category.Measurement,
  icon = Icons.Outlined.Speed,
  aliases = listOf("velocity", "mph", "kmh", "knots"),
  units = listOf(
    MeasureUnit.linear("mps", "Metre per second", "m/s", 1.0),
    MeasureUnit.linear("kmh", "Kilometre per hour", "km/h", 1.0 / 3.6),
    MeasureUnit.linear("mph", "Mile per hour", "mph", 0.44704),
    MeasureUnit.linear("fps", "Foot per second", "ft/s", 0.3048),
    MeasureUnit.linear("knot", "Knot", "kn", 1852.0 / 3600.0),
  ),
  defaultFromId = "kmh",
  defaultToId = "mph",
)

// ---------- Duration ----------------------------------------------------------------------------

fun durationConverter(): MeasurementConverter = MeasurementConverter(
  id = "duration",
  name = "Duration",
  category = Category.Measurement,
  icon = Icons.Outlined.Timer,
  aliases = listOf("time", "minutes", "hours", "days"),
  units = listOf(
    MeasureUnit.linear("ms", "Millisecond", "ms", 1e-3),
    MeasureUnit.linear("s", "Second", "s", 1.0),
    MeasureUnit.linear("min", "Minute", "min", 60.0),
    MeasureUnit.linear("h", "Hour", "h", 3600.0),
    MeasureUnit.linear("d", "Day", "d", 86400.0),
    MeasureUnit.linear("wk", "Week", "wk", 604800.0),
    MeasureUnit.linear("mo", "Month (avg)", "mo", 2629746.0),
    MeasureUnit.linear("yr", "Year (avg)", "yr", 31556952.0),
  ),
  defaultFromId = "min",
  defaultToId = "s",
)

// ---------- Pressure ----------------------------------------------------------------------------

fun pressureConverter(): MeasurementConverter = MeasurementConverter(
  id = "pressure",
  name = "Pressure",
  category = Category.Measurement,
  icon = Icons.Outlined.Compress,
  aliases = listOf("psi", "bar", "pascal", "atm"),
  units = listOf(
    MeasureUnit.linear("pa", "Pascal", "Pa", 1.0),
    MeasureUnit.linear("kpa", "Kilopascal", "kPa", 1e3),
    MeasureUnit.linear("mpa", "Megapascal", "MPa", 1e6),
    MeasureUnit.linear("bar", "Bar", "bar", 1e5),
    MeasureUnit.linear("mbar", "Millibar", "mbar", 100.0),
    MeasureUnit.linear("atm", "Atmosphere", "atm", 101325.0),
    MeasureUnit.linear("psi", "Pound per sq inch", "psi", 6894.757293168),
    MeasureUnit.linear("torr", "Torr / mmHg", "Torr", 133.3223684211),
  ),
  defaultFromId = "bar",
  defaultToId = "psi",
)

// ---------- Energy ------------------------------------------------------------------------------

fun energyConverter(): MeasurementConverter = MeasurementConverter(
  id = "energy",
  name = "Energy",
  category = Category.Measurement,
  icon = Icons.Outlined.Bolt,
  aliases = listOf("joule", "calorie", "kwh", "btu"),
  units = listOf(
    MeasureUnit.linear("j", "Joule", "J", 1.0),
    MeasureUnit.linear("kj", "Kilojoule", "kJ", 1e3),
    MeasureUnit.linear("mj", "Megajoule", "MJ", 1e6),
    MeasureUnit.linear("cal", "Calorie", "cal", 4.184),
    MeasureUnit.linear("kcal", "Kilocalorie", "kcal", 4184.0),
    MeasureUnit.linear("wh", "Watt-hour", "Wh", 3600.0),
    MeasureUnit.linear("kwh", "Kilowatt-hour", "kWh", 3.6e6),
    MeasureUnit.linear("btu", "British thermal unit", "BTU", 1055.05585262),
    MeasureUnit.linear("ev", "Electron-volt", "eV", 1.602176634e-19),
  ),
  defaultFromId = "kj",
  defaultToId = "kcal",
)

// ---------- Power -------------------------------------------------------------------------------

fun powerConverter(): MeasurementConverter = MeasurementConverter(
  id = "power",
  name = "Power",
  category = Category.Measurement,
  icon = Icons.Outlined.ElectricBolt,
  aliases = listOf("watt", "horsepower", "kw"),
  units = listOf(
    MeasureUnit.linear("w", "Watt", "W", 1.0),
    MeasureUnit.linear("kw", "Kilowatt", "kW", 1e3),
    MeasureUnit.linear("mw", "Megawatt", "MW", 1e6),
    MeasureUnit.linear("hp-mech", "Horsepower (mech.)", "hp", 745.6998715823),
    MeasureUnit.linear("hp-metric", "Horsepower (metric)", "PS", 735.49875),
    MeasureUnit.linear("btuh", "BTU/hour", "BTU/h", 0.29307107),
    MeasureUnit.linear("ftlbs", "ft·lbf/second", "ft·lbf/s", 1.3558179483),
  ),
  defaultFromId = "kw",
  defaultToId = "hp-mech",
)

// ---------- Angle -------------------------------------------------------------------------------

fun angleConverter(): MeasurementConverter = MeasurementConverter(
  id = "angle",
  name = "Angle",
  category = Category.Measurement,
  icon = Icons.AutoMirrored.Outlined.RotateRight,
  aliases = listOf("degree", "radian", "gradian"),
  units = listOf(
    MeasureUnit.linear("rad", "Radian", "rad", 1.0),
    MeasureUnit.linear("deg", "Degree", "°", Math.PI / 180.0),
    MeasureUnit.linear("grad", "Gradian", "grad", Math.PI / 200.0),
    MeasureUnit.linear("arcmin", "Arcminute", "'", Math.PI / (180.0 * 60.0)),
    MeasureUnit.linear("arcsec", "Arcsecond", "\"", Math.PI / (180.0 * 3600.0)),
    MeasureUnit.linear("turn", "Turn", "turn", 2.0 * Math.PI),
  ),
  defaultFromId = "deg",
  defaultToId = "rad",
)

// ---------- Frequency ---------------------------------------------------------------------------

fun frequencyConverter(): MeasurementConverter = MeasurementConverter(
  id = "frequency",
  name = "Frequency",
  category = Category.Measurement,
  icon = Icons.Outlined.Hexagon,
  aliases = listOf("hertz", "hz", "rpm"),
  units = listOf(
    MeasureUnit.linear("hz", "Hertz", "Hz", 1.0),
    MeasureUnit.linear("khz", "Kilohertz", "kHz", 1e3),
    MeasureUnit.linear("mhz", "Megahertz", "MHz", 1e6),
    MeasureUnit.linear("ghz", "Gigahertz", "GHz", 1e9),
    MeasureUnit.linear("thz", "Terahertz", "THz", 1e12),
    MeasureUnit.linear("rpm", "Revolution/minute", "rpm", 1.0 / 60.0),
  ),
  defaultFromId = "hz",
  defaultToId = "khz",
)

// ---------- Data storage ------------------------------------------------------------------------

fun dataStorageConverter(): MeasurementConverter = MeasurementConverter(
  id = "data-storage",
  name = "Data storage",
  category = Category.Measurement,
  icon = Icons.Outlined.Memory,
  aliases = listOf("bytes", "gigabyte", "mb", "gb", "tb", "kib"),
  units = listOf(
    MeasureUnit.linear("bit", "Bit", "bit", 1.0 / 8.0),
    MeasureUnit.linear("byte", "Byte", "B", 1.0),
    MeasureUnit.linear("kb", "Kilobyte", "kB", 1e3),
    MeasureUnit.linear("mb", "Megabyte", "MB", 1e6),
    MeasureUnit.linear("gb", "Gigabyte", "GB", 1e9),
    MeasureUnit.linear("tb", "Terabyte", "TB", 1e12),
    MeasureUnit.linear("pb", "Petabyte", "PB", 1e15),
    MeasureUnit.linear("kib", "Kibibyte", "KiB", 1024.0),
    MeasureUnit.linear("mib", "Mebibyte", "MiB", 1048576.0),
    MeasureUnit.linear("gib", "Gibibyte", "GiB", 1073741824.0),
    MeasureUnit.linear("tib", "Tebibyte", "TiB", 1099511627776.0),
  ),
  defaultFromId = "mb",
  defaultToId = "mib",
)

// ---------- Data rate ---------------------------------------------------------------------------

fun dataRateConverter(): MeasurementConverter = MeasurementConverter(
  id = "data-rate",
  name = "Data rate",
  category = Category.Measurement,
  icon = Icons.Outlined.NetworkCheck,
  aliases = listOf("bandwidth", "mbps", "gbps", "throughput"),
  units = listOf(
    MeasureUnit.linear("bps", "Bit/second", "bps", 1.0),
    MeasureUnit.linear("kbps", "Kilobit/second", "kbps", 1e3),
    MeasureUnit.linear("mbps", "Megabit/second", "Mbps", 1e6),
    MeasureUnit.linear("gbps", "Gigabit/second", "Gbps", 1e9),
    MeasureUnit.linear("tbps", "Terabit/second", "Tbps", 1e12),
    MeasureUnit.linear("Bps", "Byte/second", "B/s", 8.0),
    MeasureUnit.linear("kBps", "Kilobyte/second", "kB/s", 8e3),
    MeasureUnit.linear("MBps", "Megabyte/second", "MB/s", 8e6),
    MeasureUnit.linear("GBps", "Gigabyte/second", "GB/s", 8e9),
  ),
  defaultFromId = "mbps",
  defaultToId = "MBps",
)

// ---------- Fuel economy (non-linear for L/100km) -----------------------------------------------

fun fuelEconomyConverter(): MeasurementConverter = MeasurementConverter(
  id = "fuel-economy",
  name = "Fuel economy",
  category = Category.Measurement,
  icon = Icons.Outlined.LocalGasStation,
  aliases = listOf("mpg", "l/100km", "mileage", "fuel"),
  units = listOf(
    // Base: km/L
    MeasureUnit(
      id = "kmpl", name = "Kilometre per litre", symbol = "km/L",
      toBase = { it }, fromBase = { it },
    ),
    MeasureUnit(
      id = "lp100km", name = "Litre per 100 km", symbol = "L/100km",
      toBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 100.0 / it },
      fromBase = { if (it == 0.0) Double.POSITIVE_INFINITY else 100.0 / it },
    ),
    MeasureUnit.linear("mpg-us", "Miles/gallon (US)", "mpg (US)", 1.609344 / 3.785411784),
    MeasureUnit.linear("mpg-uk", "Miles/gallon (UK)", "mpg (UK)", 1.609344 / 4.54609),
  ),
  defaultFromId = "lp100km",
  defaultToId = "mpg-us",
)

// ---------- Density -----------------------------------------------------------------------------

fun densityConverter(): MeasurementConverter = MeasurementConverter(
  id = "density",
  name = "Density",
  category = Category.Measurement,
  icon = Icons.Outlined.Gradient,
  aliases = listOf("g/cm3", "kg/m3", "lb/ft3"),
  units = listOf(
    MeasureUnit.linear("kg-m3", "kg per cubic metre", "kg/m³", 1.0),
    MeasureUnit.linear("g-cm3", "g per cubic cm", "g/cm³", 1000.0),
    MeasureUnit.linear("lb-ft3", "lb per cubic foot", "lb/ft³", 16.018463373960141),
    MeasureUnit.linear("lb-in3", "lb per cubic inch", "lb/in³", 27679.9047102031),
  ),
  defaultFromId = "kg-m3",
  defaultToId = "g-cm3",
)

// ---------- Force -------------------------------------------------------------------------------

fun forceConverter(): MeasurementConverter = MeasurementConverter(
  id = "force",
  name = "Force",
  category = Category.Measurement,
  icon = Icons.Outlined.Air,
  aliases = listOf("newton", "pound-force", "kgf"),
  units = listOf(
    MeasureUnit.linear("n", "Newton", "N", 1.0),
    MeasureUnit.linear("kn", "Kilonewton", "kN", 1e3),
    MeasureUnit.linear("kgf", "Kilogram-force", "kgf", 9.80665),
    MeasureUnit.linear("lbf", "Pound-force", "lbf", 4.4482216152605),
    MeasureUnit.linear("dyn", "Dyne", "dyn", 1e-5),
  ),
  defaultFromId = "n",
  defaultToId = "lbf",
)

// ---------- Torque ------------------------------------------------------------------------------

fun torqueConverter(): MeasurementConverter = MeasurementConverter(
  id = "torque",
  name = "Torque",
  category = Category.Measurement,
  icon = Icons.Outlined.Tune,
  aliases = listOf("moment", "n-m", "lb-ft"),
  units = listOf(
    MeasureUnit.linear("nm", "Newton-metre", "N·m", 1.0),
    MeasureUnit.linear("ncm", "Newton-centimetre", "N·cm", 0.01),
    MeasureUnit.linear("lbft", "Pound-foot", "lbf·ft", 1.355817948331),
    MeasureUnit.linear("lbin", "Pound-inch", "lbf·in", 0.112984829028),
    MeasureUnit.linear("kgfm", "Kilogram-force metre", "kgf·m", 9.80665),
  ),
  defaultFromId = "nm",
  defaultToId = "lbft",
)

