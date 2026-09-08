package uk.co.andymarch.blogposter.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.util.UUID
import uk.co.andymarch.blogposter.JekyllPosterApp
import uk.co.andymarch.blogposter.ui.AppViewModelFactory
import uk.co.andymarch.blogposter.ui.drafts.DraftsScreen
import uk.co.andymarch.blogposter.ui.drafts.DraftsViewModel
import uk.co.andymarch.blogposter.ui.editor.EditorScreen
import uk.co.andymarch.blogposter.ui.editor.EditorViewModel
import uk.co.andymarch.blogposter.ui.login.LoginScreen
import uk.co.andymarch.blogposter.ui.login.LoginViewModel
import uk.co.andymarch.blogposter.ui.posts.RecentPostsScreen
import uk.co.andymarch.blogposter.ui.posts.RecentPostsViewModel
import uk.co.andymarch.blogposter.ui.settings.SettingsScreen
import uk.co.andymarch.blogposter.ui.settings.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private data class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomTabs = listOf(
    BottomTab(Routes.DRAFTS, "Drafts", Icons.Filled.Description),
    BottomTab(Routes.POSTS, "Posts", Icons.Filled.Article),
    BottomTab(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

@Composable
fun JekyllPosterNavHost(app: JekyllPosterApp) {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(app.tokenStore.hasToken()) }

    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Routes.EDITOR_PATTERN } == true,
                        onClick = { navController.navigate(Routes.editor(UUID.randomUUID().toString())) },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        label = { Text("New post") },
                    )
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Routes.DRAFTS else Routes.LOGIN,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.LOGIN) {
                val viewModel: LoginViewModel = viewModel(factory = AppViewModelFactory(app))
                LoginScreen(
                    viewModel = viewModel,
                    onConnected = {
                        isAuthenticated = true
                        navController.navigate(Routes.DRAFTS) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                )
            }
            composable(Routes.DRAFTS) {
                val viewModel: DraftsViewModel = viewModel(factory = AppViewModelFactory(app))
                DraftsScreen(viewModel = viewModel, onOpenDraft = { id -> navController.navigate(Routes.editor(id)) })
            }
            composable(Routes.POSTS) {
                val viewModel: RecentPostsViewModel = viewModel(factory = AppViewModelFactory(app))
                RecentPostsScreen(viewModel = viewModel, onOpenPost = { id -> navController.navigate(Routes.editor(id)) })
            }
            composable(Routes.SETTINGS) {
                val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(app))
                SettingsScreen(
                    viewModel = viewModel,
                    onSignedOut = {
                        isAuthenticated = false
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = Routes.EDITOR_PATTERN,
                arguments = listOf(navArgument("draftId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val draftId = backStackEntry.arguments?.getString("draftId") ?: return@composable
                val viewModel: EditorViewModel = viewModel(
                    key = draftId,
                    factory = AppViewModelFactory(app, draftId),
                )
                EditorScreen(viewModel = viewModel, onPublished = { navController.popBackStack() })
            }
        }
    }
}
