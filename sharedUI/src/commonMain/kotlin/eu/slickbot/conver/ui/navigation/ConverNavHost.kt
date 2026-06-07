package eu.slickbot.conver.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import eu.slickbot.conver.domain.converter.ConverterRegistry
import eu.slickbot.conver.ui.browse.CategoryDetailScreen
import eu.slickbot.conver.ui.favorites.FavoritesScreen
import eu.slickbot.conver.ui.home.HomeScreen
import eu.slickbot.conver.ui.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun ConverNavHost(
  navController: NavHostController = rememberNavController(),
  onNavHostReady: suspend (NavHostController) -> Unit = {},
) {
  LaunchedEffect(navController) { onNavHostReady(navController) }

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

  fun selectTab(top: TopLevelDestination) {
    currentTab = top
    if (top == TopLevelDestination.Home) {
      navController.popBackStack<Destination.Home>(inclusive = false)
    } else {
      navController.navigate(top.route) {
        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
        launchSingleTop = true
      }
    }
  }

  fun selectConverter(converterId: String) {
    navController.navigate(Destination.Converter(converterId)) {
      popUpTo<Destination.Converter> { inclusive = true }
      launchSingleTop = true
    }
  }

  val navHost = @Composable {
    ConverNavGraph(
      navController = navController,
      onSelectConverter = ::selectConverter,
      onLeaveTab = { selectTab(TopLevelDestination.Home) },
    )
  }

  BoxWithConstraints(Modifier.fillMaxSize()) {
    if (maxWidth >= SidebarBreakpoint) {
      // Wide screens: branded sidebar + content area.
      Row(Modifier.fillMaxSize()) {
        BrandedSidebar(currentTab = currentTab, onSelect = ::selectTab)
        Box(Modifier.weight(1f).fillMaxHeight()) { navHost() }
      }
    } else {
      // Phones / narrow: bottom navigation bar (hidden while the keyboard is open).
      NavigationSuiteScaffold(
        layoutType = if (keyboardOpen) NavigationSuiteType.None else NavigationSuiteType.NavigationBar,
        navigationSuiteItems = {
          TopLevelDestination.entries.forEach { top ->
            item(
              selected = top == currentTab,
              onClick = { selectTab(top) },
              icon = { Icon(top.icon, contentDescription = top.label) },
              label = { Text(top.label) },
            )
          }
        },
      ) { navHost() }
    }
  }
}

@Composable
private fun ConverNavGraph(
  navController: NavHostController,
  onSelectConverter: (String) -> Unit,
  onLeaveTab: () -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = Destination.Home,
  ) {
    composable<Destination.Home> {
      HomeScreen(
        onConverterClick = onSelectConverter,
        onCategoryClick = { category ->
          navController.navigate(Destination.CategoryDetail(category.name))
        },
      )
    }
    composable<Destination.Favorites> {
      FavoritesScreen(
        onConverterClick = onSelectConverter,
        onBack = onLeaveTab,
      )
    }
    composable<Destination.Settings> {
      SettingsScreen(onBack = onLeaveTab)
    }
    composable<Destination.CategoryDetail> { entry ->
      val args = entry.toRoute<Destination.CategoryDetail>()
      CategoryDetailScreen(
        categoryId = args.categoryId,
        selectedConverterId = null,
        onBack = { navController.popBackStack() },
        onSelectConverter = onSelectConverter,
      )
    }
    composable<Destination.Converter> { entry ->
      val args = entry.toRoute<Destination.Converter>()
      val registry: ConverterRegistry = koinInject()
      val categoryId = registry[args.converterId]?.category?.name ?: ""
      CategoryDetailScreen(
        categoryId = categoryId,
        selectedConverterId = args.converterId,
        onBack = { navController.popBackStack() },
        onSelectConverter = onSelectConverter,
      )
    }
  }
}

private val SidebarBreakpoint = 840.dp

@Composable
private fun BrandedSidebar(
  currentTab: TopLevelDestination,
  onSelect: (TopLevelDestination) -> Unit,
) {
  Surface(
    modifier = Modifier.width(248.dp).fillMaxHeight(),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
      Text(
        text = "Conver",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .padding(top = 4.dp, bottom = 20.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onSelect(TopLevelDestination.Home) }
          .padding(horizontal = 14.dp, vertical = 10.dp),
      )
      TopLevelDestination.entries.forEach { top ->
        val selected = top == currentTab
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect(top) }
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .padding(horizontal = 14.dp, vertical = 13.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(top.icon, contentDescription = null, modifier = Modifier.size(22.dp))
          Spacer(Modifier.width(16.dp))
          Text(
            text = top.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
          )
        }
      }
    }
  }
}
