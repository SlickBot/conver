package eu.slickbot.conver.domain.converter.converters

import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.domain.converter.MeasureUnit
import eu.slickbot.conver.domain.converter.MeasurementConverter

/**
 * Length. Base unit = metre. All factors are exact where possible:
 *  - 1 inch = 0.0254 m (definition)
 *  - 1 foot = 0.3048 m (12 in)
 *  - 1 yard = 0.9144 m (3 ft)
 *  - 1 mile = 1609.344 m (5280 ft)
 *  - 1 nautical mile = 1852 m (international)
 */
fun lengthConverter(): MeasurementConverter = MeasurementConverter(
  id = "length",
  name = "Length",
  category = Category.Measurement,
  icon = ConverterIcon.Straighten,
  aliases = listOf("distance", "size", "metres", "meters", "feet", "inches", "miles", "km"),
  units = listOf(
    MeasureUnit.linear("mm", "Millimetre", "mm", 0.001),
    MeasureUnit.linear("cm", "Centimetre", "cm", 0.01),
    MeasureUnit.linear("m", "Metre", "m", 1.0),
    MeasureUnit.linear("km", "Kilometre", "km", 1000.0),
    MeasureUnit.linear("in", "Inch", "in", 0.0254),
    MeasureUnit.linear("ft", "Foot", "ft", 0.3048),
    MeasureUnit.linear("yd", "Yard", "yd", 0.9144),
    MeasureUnit.linear("mi", "Mile", "mi", 1609.344),
    MeasureUnit.linear("nmi", "Nautical mile", "NM", 1852.0),
  ),
  defaultFromId = "m",
  defaultToId = "ft",
)
