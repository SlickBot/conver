package eu.slickbot.conver.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.slickbot.conver.domain.converter.CalculatorConverter
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.domain.converter.MeasurementConverter
import eu.slickbot.conver.domain.converter.StandaloneConverter
import eu.slickbot.conver.domain.converter.TextConverter
import eu.slickbot.conver.ui.browse.BrowseScreen
import eu.slickbot.conver.ui.browse.CategoryDetailScreen
import eu.slickbot.conver.ui.converter.CalculatorScreen
import eu.slickbot.conver.ui.converter.MeasurementScreen
import eu.slickbot.conver.ui.converter.TextTransformScreen
import eu.slickbot.conver.ui.favorites.FavoritesScreen
import eu.slickbot.conver.ui.home.HomeScreen
import eu.slickbot.conver.ui.receiptsplit.ReceiptSplitScreen
import eu.slickbot.conver.ui.settings.SettingsScreen
import org.koin.compose.koinInject

/**
 * Stack model:
 *  - [Home] is always at the bottom and never popped (back from Home = exit app).
 *  - Switching to another tab: [Home, Tab]
 *  - Drilling from any screen: [Home, Tab?, Detail]
 *  - Clicking a tab always pops everything above Home, then pushes the tab root (or stays
 *    on Home if the Home tab was tapped).
 */
@Composable
fun ConverNavHost(
  navController: NavHostController = rememberNavController(),
) {
  var currentTab by rememberSaveable { mutableStateOf(TopLevelDestination.Home) }

  // Sync tab highlight when back-press lands on a tab root screen.
  val backStackEntry by navController.currentBackStackEntryAsState()
  val dest = backStackEntry?.destination
  val tabFromBackStack = TopLevelDestination.entries.firstOrNull { top ->
    dest?.hasRoute(top.route::class) == true
  }
  LaunchedEffect(tabFromBackStack) {
    if (tabFromBackStack != null) currentTab = tabFromBackStack
  }

  // Hide the bottom navigation while the keyboard is open, otherwise its reserved strip leaves a
  // gap between scrollable content and the keyboard. ime > navigation bar means a real keyboard
  // (at rest WindowInsets.ime reports the nav-bar height).
  val density = LocalDensity.current
  val keyboardOpen = WindowInsets.ime.getBottom(density) > WindowInsets.navigationBars.getBottom(density)
  val navLayoutType = if (keyboardOpen) {
    NavigationSuiteType.None
  } else {
    NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
  }

  NavigationSuiteScaffold(
    layoutType = navLayoutType,
    navigationSuiteItems = {
      TopLevelDestination.entries.forEach { top ->
        item(
          selected = top == currentTab,
          onClick = {
            currentTab = top
            if (top == TopLevelDestination.Home) {
              // Pop everything above Home. If already on Home → no-op.
              navController.popBackStack<Destination.Home>(inclusive = false)
            } else {
              // Pop to Home (keep it), push the target tab root.
              navController.navigate(top.route) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                launchSingleTop = true
              }
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
          onCategoryClick = { category ->
            navController.navigate(Destination.CategoryDetail(category.name))
          },
          onBrowseAllClick = {
            currentTab = TopLevelDestination.Browse
            navController.navigate(Destination.Browse) {
              popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
              launchSingleTop = true
            }
          },
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
      composable<Destination.CategoryDetail> { entry ->
        val args = entry.toRoute<Destination.CategoryDetail>()
        CategoryDetailScreen(
          categoryId = args.categoryId,
          onBack = { navController.popBackStack() },
          onConverterClick = { id -> navController.navigate(Destination.Converter(id)) },
        )
      }
      composable<Destination.Converter> { entry ->
        val args = entry.toRoute<Destination.Converter>()
        ConverterDispatch(args.converterId, onBack = { navController.popBackStack() })
      }
    }
  }
}

@Composable
private fun ConverterDispatch(converterId: String, onBack: () -> Unit) {
  val registry: ConverterRegistry = koinInject()
  when (registry[converterId]) {
    is MeasurementConverter -> MeasurementScreen(converterId = converterId, onBack = onBack)
    is TextConverter -> TextTransformScreen(converterId = converterId, onBack = onBack)
    is CalculatorConverter -> CalculatorScreen(converterId = converterId, onBack = onBack)
    is StandaloneConverter -> when ((registry[converterId] as StandaloneConverter).screenId) {
      "receipt-split" -> ReceiptSplitScreen(onBack = onBack)
      else -> {
        Box(
          modifier = Modifier.fillMaxSize().padding(32.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text("Screen not implemented: $converterId", style = MaterialTheme.typography.titleMedium)
        }
        LaunchedEffect(converterId) { onBack() }
      }
    }
    null -> {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          "Unknown converter: $converterId",
          style = MaterialTheme.typography.titleMedium,
        )
      }
      LaunchedEffect(converterId) { onBack() }
    }
  }
}
