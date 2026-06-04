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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
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
import eu.slickbot.conver.ui.browse.CategoryDetailScreen
import eu.slickbot.conver.ui.converter.CalculatorScreen
import eu.slickbot.conver.ui.converter.MeasurementScreen
import eu.slickbot.conver.ui.converter.TextTransformScreen
import eu.slickbot.conver.ui.favorites.FavoritesScreen
import eu.slickbot.conver.ui.home.HomeScreen
import eu.slickbot.conver.ui.receiptsplit.ReceiptSplitScreen
import eu.slickbot.conver.ui.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun ConverNavHost(
  navController: NavHostController = rememberNavController(),
) {
  var currentTab by rememberSaveable { mutableStateOf(TopLevelDestination.Home) }

  val backStackEntry by navController.currentBackStackEntryAsState()
  val dest = backStackEntry?.destination
  val tabFromBackStack = TopLevelDestination.entries.firstOrNull { top ->
    dest?.hasRoute(top.route::class) == true
  }
  LaunchedEffect(tabFromBackStack) {
    if (tabFromBackStack != null) currentTab = tabFromBackStack
  }

  val density = LocalDensity.current
  val keyboardOpen = WindowInsets.ime.getBottom(density) > WindowInsets.navigationBars.getBottom(density)
  val navLayoutType = if (keyboardOpen) {
    NavigationSuiteType.None
  } else {
    NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfoV2())
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
              navController.popBackStack<Destination.Home>(inclusive = false)
            } else {
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
    is StandaloneConverter -> if ((registry[converterId] as StandaloneConverter).screenId == "receipt-split") {
      ReceiptSplitScreen(onBack = onBack)
    } else {
      Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text("Screen not implemented: $converterId", style = MaterialTheme.typography.titleMedium)
      }
      LaunchedEffect(converterId) { onBack() }
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
