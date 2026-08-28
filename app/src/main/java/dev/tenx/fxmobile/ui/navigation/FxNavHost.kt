package dev.tenx.fxmobile.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tenx.fxmobile.ui.screen.main.MainScreen
import dev.tenx.fxmobile.ui.screen.sessions.SessionsScreen
import dev.tenx.fxmobile.ui.screen.settings.ApiKeyScreen
import dev.tenx.fxmobile.ui.screen.settings.SettingsScreen
import dev.tenx.fxmobile.ui.screen.terminal.TerminalScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Main : Screen("main", "Home", Icons.Default.Home)
    data object Sessions : Screen("sessions", "Sessions", Icons.Default.History)
    data object Terminal : Screen("terminal", "Terminal", Icons.Default.Terminal)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(Screen.Main, Screen.Sessions, Screen.Terminal, Screen.Settings)

@Composable
fun FxApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainScreen(navController = navController)
            }
            composable(Screen.Sessions.route) {
                SessionsScreen(navController = navController)
            }
            composable(Screen.Terminal.route) {
                TerminalScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable("api_key") {
                ApiKeyScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
