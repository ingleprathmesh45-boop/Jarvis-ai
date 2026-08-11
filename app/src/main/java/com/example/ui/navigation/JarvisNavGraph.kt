package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ui.JarvisViewModel
import com.example.ui.screens.*

sealed class NavRoute(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavRoute("home", "HUD", Icons.Default.Home)
    object Chat : NavRoute("chat", "Chat", Icons.Default.Chat)
    object ScreenVision : NavRoute("screen_vision", "Vision", Icons.Default.Visibility)
    object Notifications : NavRoute("notifications", "Alerts", Icons.Default.Notifications)
    object Automations : NavRoute("automations", "Rules", Icons.Default.Autorenew)
    object Memory : NavRoute("memory", "Memory", Icons.Default.Psychology)
    object Settings : NavRoute("settings", "Settings", Icons.Default.Settings)
    object Permissions : NavRoute("permissions", "Audit", Icons.Default.Security)
}

@Composable
fun JarvisAppScaffold(
    viewModel: JarvisViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        NavRoute.Home,
        NavRoute.Chat,
        NavRoute.ScreenVision,
        NavRoute.Notifications,
        NavRoute.Settings
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { navController.navigate(NavRoute.Chat.route) },
                    onNavigateToNotifications = { navController.navigate(NavRoute.Notifications.route) },
                    onNavigateToAutomations = { navController.navigate(NavRoute.Automations.route) },
                    onNavigateToSettings = { navController.navigate(NavRoute.Settings.route) }
                )
            }

            composable(NavRoute.Chat.route) {
                ChatScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.ScreenVision.route) {
                ScreenVisionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToChat = { navController.navigate(NavRoute.Chat.route) }
                )
            }

            composable(NavRoute.Notifications.route) {
                NotificationAssistantScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToChat = { navController.navigate(NavRoute.Chat.route) }
                )
            }

            composable(NavRoute.Automations.route) {
                AutomationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.Memory.route) {
                MemoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoute.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToPermissions = { navController.navigate(NavRoute.Permissions.route) },
                    onNavigateToMemory = { navController.navigate(NavRoute.Memory.route) }
                )
            }

            composable(NavRoute.Permissions.route) {
                PermissionsDashboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
