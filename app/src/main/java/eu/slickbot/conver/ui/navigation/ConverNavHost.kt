package eu.slickbot.conver.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.slickbot.conver.ui.browse.BrowseScreen
import eu.slickbot.conver.ui.converter.MeasurementScreen
import eu.slickbot.conver.ui.favorites.FavoritesScreen
import eu.slickbot.conver.ui.home.HomeScreen
import eu.slickbot.conver.ui.settings.SettingsScreen

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
          onConverterClick = { id -> navController.navigate(Destination.Measurement(id)) },
          onBrowseAllClick = { navController.navigate(Destination.Browse) },
        )
      }
      composable<Destination.Browse> {
        BrowseScreen(
          onConverterClick = { id -> navController.navigate(Destination.Measurement(id)) },
        )
      }
      composable<Destination.Favorites> {
        FavoritesScreen(
          onConverterClick = { id -> navController.navigate(Destination.Measurement(id)) },
        )
      }
      composable<Destination.Settings> { SettingsScreen() }
      composable<Destination.Measurement> { entry ->
        val args = entry.toRoute<Destination.Measurement>()
        MeasurementScreen(
          converterId = args.converterId,
          onBack = { navController.popBackStack() },
        )
      }
    }
  }
}
