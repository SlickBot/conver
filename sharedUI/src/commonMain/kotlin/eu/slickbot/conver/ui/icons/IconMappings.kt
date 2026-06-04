package eu.slickbot.conver.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Boy
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Gradient
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Html
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import eu.slickbot.conver.domain.converter.Category
import eu.slickbot.conver.domain.converter.CategoryIcon
import eu.slickbot.conver.domain.converter.ConverterIcon
import eu.slickbot.conver.ui.theme.CategoryAccents

fun CategoryIcon.imageVector(): ImageVector = when (this) {
  CategoryIcon.Measurement -> Icons.Outlined.SquareFoot
  CategoryIcon.Money -> Icons.Outlined.Paid
  CategoryIcon.Time -> Icons.Outlined.Schedule
  CategoryIcon.Numbers -> Icons.Outlined.Tag
  CategoryIcon.Developer -> Icons.Outlined.Code
  CategoryIcon.Color -> Icons.Outlined.Palette
  CategoryIcon.Files -> Icons.Outlined.FolderOpen
  CategoryIcon.Everyday -> Icons.Outlined.Apps
}

fun Category.accent(): Color = when (this) {
  Category.Measurement -> CategoryAccents.Measurement
  Category.Money -> CategoryAccents.Money
  Category.Time -> CategoryAccents.Time
  Category.Numbers -> CategoryAccents.Numbers
  Category.Developer -> CategoryAccents.Developer
  Category.Color -> CategoryAccents.Color
  Category.Files -> CategoryAccents.Files
  Category.Everyday -> CategoryAccents.Everyday
}

fun ConverterIcon.imageVector(): ImageVector = when (this) {
  ConverterIcon.SwapHoriz -> Icons.Outlined.SwapHoriz
  ConverterIcon.TextFields -> Icons.Outlined.TextFields
  ConverterIcon.Build -> Icons.Outlined.Build
  ConverterIcon.Calculate -> Icons.Outlined.Calculate
  ConverterIcon.Straighten -> Icons.Outlined.Straighten
  ConverterIcon.Code -> Icons.Outlined.Code
  ConverterIcon.Link -> Icons.Outlined.Link
  ConverterIcon.Html -> Icons.Outlined.Html
  ConverterIcon.Fingerprint -> Icons.Outlined.Fingerprint
  ConverterIcon.Tag -> Icons.Outlined.Tag
  ConverterIcon.DataObject -> Icons.Outlined.DataObject
  ConverterIcon.Spellcheck -> Icons.Outlined.Spellcheck
  ConverterIcon.FormatQuote -> Icons.Outlined.FormatQuote
  ConverterIcon.GraphicEq -> Icons.Outlined.GraphicEq
  ConverterIcon.Lock -> Icons.Outlined.Lock
  ConverterIcon.Subtitles -> Icons.Outlined.Subtitles
  ConverterIcon.Numbers -> Icons.Outlined.Numbers
  ConverterIcon.Abc -> Icons.Outlined.Abc
  ConverterIcon.Translate -> Icons.Outlined.Translate
  ConverterIcon.CurrencyExchange -> Icons.Outlined.CurrencyExchange
  ConverterIcon.Savings -> Icons.Outlined.Savings
  ConverterIcon.Receipt -> Icons.Outlined.Receipt
  ConverterIcon.Percent -> Icons.Outlined.Percent
  ConverterIcon.Discount -> Icons.Outlined.Discount
  ConverterIcon.Schedule -> Icons.Outlined.Schedule
  ConverterIcon.HourglassTop -> Icons.Outlined.HourglassTop
  ConverterIcon.Balance -> Icons.Outlined.Balance
  ConverterIcon.DeviceThermostat -> Icons.Outlined.DeviceThermostat
  ConverterIcon.Waves -> Icons.Outlined.Waves
  ConverterIcon.CropSquare -> Icons.Outlined.CropSquare
  ConverterIcon.Speed -> Icons.Outlined.Speed
  ConverterIcon.Timer -> Icons.Outlined.Timer
  ConverterIcon.Compress -> Icons.Outlined.Compress
  ConverterIcon.Bolt -> Icons.Outlined.Bolt
  ConverterIcon.ElectricBolt -> Icons.Outlined.ElectricBolt
  ConverterIcon.RotateRight -> Icons.AutoMirrored.Outlined.RotateRight
  ConverterIcon.Hexagon -> Icons.Outlined.Hexagon
  ConverterIcon.Memory -> Icons.Outlined.Memory
  ConverterIcon.NetworkCheck -> Icons.Outlined.NetworkCheck
  ConverterIcon.LocalGasStation -> Icons.Outlined.LocalGasStation
  ConverterIcon.Gradient -> Icons.Outlined.Gradient
  ConverterIcon.Air -> Icons.Outlined.Air
  ConverterIcon.Tune -> Icons.Outlined.Tune
  ConverterIcon.Boy -> Icons.Outlined.Boy
  ConverterIcon.Description -> Icons.Outlined.Description
  ConverterIcon.LocalFireDepartment -> Icons.Outlined.LocalFireDepartment
  ConverterIcon.FitnessCenter -> Icons.Outlined.FitnessCenter
  ConverterIcon.DirectionsRun -> Icons.AutoMirrored.Outlined.DirectionsRun
  ConverterIcon.Public -> Icons.Outlined.Public
  ConverterIcon.Palette -> Icons.Outlined.Palette
}
