package com.v7h.whattodonext.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.v7h.whattodonext.presentation.ui.screen.deck.DeckScreen
import com.v7h.whattodonext.presentation.ui.screen.detail.DetailScreen
import com.v7h.whattodonext.presentation.ui.screen.savedchoices.SavedChoicesScreen
import com.v7h.whattodonext.presentation.ui.screen.profile.ProfileScreen
import com.v7h.whattodonext.presentation.ui.screen.onboarding.OnboardingScreen
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.di.NetworkModule
import androidx.compose.ui.platform.LocalContext

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
    // Get context for dependency injection
    val context = LocalContext.current
    
    // Shared repository instances with dependency injection
    val savedChoiceRepository = remember { SavedChoiceRepository() }
    val userProfileRepository = remember { NetworkModule.legacyUserProfileRepository(context) }
    val movieRepository = remember { NetworkModule.movieRepository }
    
    // Debug log for navigation setup
    LaunchedEffect(Unit) {
        android.util.Log.d("AppNavigation", "Navigation system initialized - Always starting with onboarding")
    }
    
    // Get current route for bottom navigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // F-007: Bottom Navigation Bar (hide during onboarding, show after onboarding is completed in current session)
            android.util.Log.d("AppNavigation", "Bottom bar visibility check - currentRoute: $currentRoute")
            if (currentRoute != Screen.ONBOARDING) {
                android.util.Log.d("AppNavigation", "Showing bottom navigation bar")
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
            } else {
                android.util.Log.d("AppNavigation", "Hiding bottom navigation bar - onboarding in progress")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ONBOARDING, // Always start with onboarding
            modifier = Modifier.padding(innerPadding)
        ) {
            // S-001: Onboarding Screen (always shown on app launch)
            composable(Screen.ONBOARDING) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        // Navigate to main app after user selects an activity
                        android.util.Log.d("AppNavigation", "Onboarding completed for this session, navigating to main app")
                        
                        navController.navigate(Screen.DECK) {
                            popUpTo(Screen.ONBOARDING) { inclusive = true }
                        }
                    },
                    userProfileRepository = userProfileRepository
                )
            }
            // S-002: Main Deck Screen (primary interface)
            composable(Screen.DECK) {
                DeckScreen(
                    onNavigateToDetail = { contentId, movieData ->
                        // Debug log for navigation with movie data
                        android.util.Log.d("AppNavigation", "Navigating to detail: $contentId with movie: ${movieData?.title ?: "null"}")
                        
                        // Store movie data in saved state handle for DetailScreen
                        navController.currentBackStackEntry?.savedStateHandle?.set("movie_data", movieData)
                        
                        navController.navigate(Screen.detailRoute(contentId))
                    },
                    savedChoiceRepository = savedChoiceRepository,
                    userProfileRepository = userProfileRepository
                )
            }
            
            // S-003: Detail Screen (expanded card view)
            // Rule: Proper navigation argument configuration to prevent stale state
            composable(
                route = Screen.detailRoute("{${Screen.Params.ACTIVITY_CONTENT_ID}}"),
                arguments = listOf(
                    navArgument(Screen.Params.ACTIVITY_CONTENT_ID) {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                // Extract contentId from navigation arguments
                val contentId = backStackEntry.arguments?.getString(Screen.Params.ACTIVITY_CONTENT_ID) ?: ""
                
                // Get movie data from previous screen's saved state
                val previousEntry = navController.previousBackStackEntry
                val movieData = previousEntry?.savedStateHandle?.get<com.v7h.whattodonext.data.model.ActivityContent>("movie_data")
                
                android.util.Log.d("AppNavigation", "Detail screen route matched with contentId: $contentId, has movie data: ${movieData != null}")
                
                // Use key to force recreation of DetailScreen when contentId changes
                // This prevents state from previous screen being reused
                key(contentId) {
                    DetailScreen(
                        contentId = contentId,
                        initialMovieData = movieData,
                        onNavigateBack = {
                            // Debug log for navigation
                            android.util.Log.d("AppNavigation", "Navigating back from detail")
                            navController.popBackStack()
                        },
                        movieRepository = movieRepository
                    )
                }
            }
            
            // S-004: Saved Choices Screen
            composable(Screen.SAVED_CHOICES) {
                SavedChoicesScreen(
                    savedChoiceRepository = savedChoiceRepository
                )
            }
            
            // S-005: Profile Screen
            composable(Screen.PROFILE) {
                ProfileScreen(userProfileRepository = userProfileRepository)
            }
        }
    }
}
