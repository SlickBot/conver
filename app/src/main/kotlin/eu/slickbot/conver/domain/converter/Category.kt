package eu.slickbot.conver.domain.converter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import eu.slickbot.conver.ui.theme.CategoryAccents

enum class Category(
  val displayName: String,
  val icon: ImageVector,
  val accent: Color,
) {
  Measurement("Measurement", Icons.Outlined.SquareFoot, CategoryAccents.Measurement),
  Money("Money & Finance", Icons.Outlined.Paid, CategoryAccents.Money),
  Time("Time & Date", Icons.Outlined.Schedule, CategoryAccents.Time),
  Numbers("Numbers", Icons.Outlined.Tag, CategoryAccents.Numbers),
  Developer("Developer & Text", Icons.Outlined.Code, CategoryAccents.Developer),
  Color("Color", Icons.Outlined.Palette, CategoryAccents.Color),
  Files("Files & Media", Icons.Outlined.FolderOpen, CategoryAccents.Files),
  Everyday("Everyday", Icons.Outlined.Apps, CategoryAccents.Everyday),
}
