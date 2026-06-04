package eu.slickbot.conver.domain.converter

enum class Category(
  val displayName: String,
  val icon: CategoryIcon,
) {
  Measurement("Measurement", CategoryIcon.Measurement),
  Money("Money & Finance", CategoryIcon.Money),
  Time("Time & Date", CategoryIcon.Time),
  Numbers("Numbers", CategoryIcon.Numbers),
  Developer("Developer & Text", CategoryIcon.Developer),
  Color("Color", CategoryIcon.Color),
  Files("Files & Media", CategoryIcon.Files),
  Everyday("Everyday", CategoryIcon.Everyday),
}
