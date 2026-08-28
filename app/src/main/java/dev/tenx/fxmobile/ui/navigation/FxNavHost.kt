package dev.tenx.fxmobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.tenx.fxmobile.ui.screen.main.MainScreen
import dev.tenx.fxmobile.ui.screen.settings.SettingsScreen
import dev.tenx.fxmobile.ui.screen.terminal.TerminalScreen
import dev.tenx.fxmobile.ui.screen.sessions.SessionsScreen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Terminal : Screen("terminal")
    data object Sessions : Screen("sessions")
    data object Settings : Screen("settings")
}

@Composable
fun FxNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier
    ) {
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(Screen.Terminal.route) {
            TerminalScreen(navController = navController)
        }
        composable(Screen.Sessions.route) {
            SessionsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
