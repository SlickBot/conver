package eu.slickbot.conver.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.ui.browse.BrowseScreen
import eu.slickbot.conver.ui.converter.MeasurementScreen
import eu.slickbot.conver.ui.converter.TextTransformScreen
import eu.slickbot.conver.ui.favorites.FavoritesScreen
import eu.slickbot.conver.ui.home.HomeScreen
import eu.slickbot.conver.ui.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun ConverNavHost(
  navController: NavHostController = rememberNavController(),
) {
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route

  val selectedTop = TopLevelDestination.entries.firstOrNull { top ->
    currentRoute == top.destination::class.qualifiedName
  } ?: TopLevelDestination.Home

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      TopLevelDestination.entries.forEach { top ->
        item(
          selected = top == selectedTop,
          onClick = {
            navController.navigate(top.destination) {
              popUpTo(navController.graph.findStartDestination().id) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          icon = { Icon(top.icon, contentDescription = top.label) },
          label = { Text(top.label) },
        )
      }
    },
  ) {
    NavHost(
      navController = navController,
      startDestination = Destination.Home,
    ) {
      composable<Destination.Home> {
        HomeScreen(
          onConverterClick = { id -> navController.navigate(Destination.Converter(id)) },
          onBrowseAllClick = { navController.navigate(Destination.Browse) },
        )
      }
      composable<Destination.Browse> {
        BrowseScreen(
          onConverterClick = { id -> navController.navigate(Destination.Converter(id)) },
        )
      }
      composable<Destination.Favorites> {
        FavoritesScreen(
          onConverterClick = { id -> navController.navigate(Destination.Converter(id)) },
        )
      }
      composable<Destination.Settings> { SettingsScreen() }
      composable<Destination.Converter> { entry ->
        val args = entry.toRoute<Destination.Converter>()
        ConverterDispatch(
          converterId = args.converterId,
          onBack = { navController.popBackStack() },
        )
      }
    }
  }
}

/** Resolves a converter's kind and renders the right screen template. */
@Composable
private fun ConverterDispatch(
  converterId: String,
  onBack: () -> Unit,
) {
  val registry: ConverterRegistry = koinInject()
  val converter = registry[converterId]
  when (converter) {
    is MeasurementConverter -> MeasurementScreen(converterId = converterId, onBack = onBack)
    is TextConverter -> TextTransformScreen(converterId = converterId, onBack = onBack)
    null -> UnknownConverterPlaceholder(converterId = converterId, onBack = onBack)
  }
}

@Composable
private fun UnknownConverterPlaceholder(converterId: String, onBack: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "Unknown converter: $converterId",
      style = MaterialTheme.typography.titleMedium,
    )
  }
  // Trigger a pop so the nav stack doesn't get stuck on this placeholder.
  androidx.compose.runtime.LaunchedEffect(converterId) { onBack() }
}
