package com.v7h.whattodonext.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.v7h.whattodonext.presentation.ui.screen.deck.DeckScreen
import com.v7h.whattodonext.presentation.ui.screen.detail.DetailScreen
import com.v7h.whattodonext.presentation.ui.screen.savedchoices.SavedChoicesScreen
import com.v7h.whattodonext.presentation.ui.screen.profile.ProfileScreen
import com.v7h.whattodonext.data.repository.SavedChoiceRepository

/**
 * Main navigation component for the What To Do Next app
 * 
 * Handles navigation between all screens:
 * - Deck screen (main card interface)
 * - Detail screen (expanded card view)
 * - Saved Choices screen (saved items list)
 * - Profile screen (user settings)
 * 
 * Applied Rules: Debug logs, comments, bottom navigation, shared repository
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Shared repository instance for Step 3
    val savedChoiceRepository = remember { SavedChoiceRepository() }
    
    // Debug log for navigation setup
    LaunchedEffect(Unit) {
        android.util.Log.d("AppNavigation", "Navigation system initialized with bottom navigation")
    }
    
    // Get current route for bottom navigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    Scaffold(
        bottomBar = {
            // F-007: Bottom Navigation Bar
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Deck") },
                    label = { Text("Deck") },
                    selected = currentRoute == Screen.DECK,
                    onClick = {
                        android.util.Log.d("AppNavigation", "Bottom nav: Deck selected")
                        navController.navigate(Screen.DECK) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Choices") },
                    label = { Text("Choices") },
                    selected = currentRoute == Screen.SAVED_CHOICES,
                    onClick = {
                        android.util.Log.d("AppNavigation", "Bottom nav: Saved Choices selected")
                        navController.navigate(Screen.SAVED_CHOICES) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = currentRoute == Screen.PROFILE,
                    onClick = {
                        android.util.Log.d("AppNavigation", "Bottom nav: Profile selected")
                        navController.navigate(Screen.PROFILE) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.DECK,
            modifier = modifier
        ) {
            // S-002: Main Deck Screen (primary interface)
            composable(Screen.DECK) {
                DeckScreen(
                    onNavigateToDetail = { contentId ->
                        // Debug log for navigation
                        android.util.Log.d("AppNavigation", "Navigating to detail: $contentId")
                        navController.navigate(Screen.detailRoute(contentId))
                    },
                    savedChoiceRepository = savedChoiceRepository
                )
            }
            
            // S-003: Detail Screen (expanded card view)
            composable(
                route = Screen.detailRoute("{${Screen.Params.ACTIVITY_CONTENT_ID}}"),
                arguments = emptyList() // TODO: Add proper arguments in later steps
            ) { backStackEntry ->
                val contentId = backStackEntry.arguments?.getString(Screen.Params.ACTIVITY_CONTENT_ID) ?: ""
                
                DetailScreen(
                    contentId = contentId,
                    onNavigateBack = {
                        // Debug log for navigation
                        android.util.Log.d("AppNavigation", "Navigating back from detail")
                        navController.popBackStack()
                    }
                )
            }
            
            // S-004: Saved Choices Screen
            composable(Screen.SAVED_CHOICES) {
                SavedChoicesScreen(
                    savedChoiceRepository = savedChoiceRepository
                )
            }
            
            // S-005: Profile Screen
            composable(Screen.PROFILE) {
                ProfileScreen()
            }
            
            // TODO: Add S-001: Onboarding Screen in later steps
        }
    }
}
