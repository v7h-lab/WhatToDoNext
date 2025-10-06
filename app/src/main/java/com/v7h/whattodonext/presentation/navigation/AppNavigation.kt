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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.v7h.whattodonext.presentation.ui.screen.deck.DeckScreen
import com.v7h.whattodonext.presentation.ui.screen.detail.DetailScreen
import com.v7h.whattodonext.presentation.ui.screen.savedchoices.SavedChoicesScreen
import com.v7h.whattodonext.presentation.ui.screen.profile.ProfileScreen
import com.v7h.whattodonext.presentation.ui.screen.onboarding.OnboardingScreen
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
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
    val userProfileRepository = remember { NetworkModule.provideUserProfileRepository(context) }
    
    // Check if user has completed onboarding
    val userProfile by userProfileRepository.userProfile.collectAsState()
    val hasCompletedOnboarding = userProfile.hasCompletedOnboarding
    
    // Debug log for navigation setup
    LaunchedEffect(Unit) {
        android.util.Log.d("AppNavigation", "Navigation system initialized - Onboarding completed: $hasCompletedOnboarding")
    }
    
    // Get current route for bottom navigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // F-007: Bottom Navigation Bar (only show after onboarding)
            if (hasCompletedOnboarding) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) Screen.DECK else Screen.ONBOARDING,
            modifier = Modifier.padding(innerPadding)
        ) {
            // S-001: Onboarding Screen (shown first if not completed)
            composable(Screen.ONBOARDING) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        // Complete onboarding and navigate to main app
                        android.util.Log.d("AppNavigation", "Onboarding completed, navigating to main app")
                        
                        // Mark onboarding as completed in the repository
                        // Note: This should be done in a coroutine scope in a real app
                        // For now, we'll handle it synchronously
                        try {
                            // The onboarding completion will be handled by the repository
                            // when the user profile is updated with selected activities
                            android.util.Log.d("AppNavigation", "Onboarding completion will be saved when activities are selected")
                        } catch (e: Exception) {
                            android.util.Log.e("AppNavigation", "Error completing onboarding", e)
                        }
                        
                        navController.navigate(Screen.DECK) {
                            popUpTo(Screen.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            // S-002: Main Deck Screen (primary interface)
            composable(Screen.DECK) {
                DeckScreen(
                    onNavigateToDetail = { contentId ->
                        // Debug log for navigation
                        android.util.Log.d("AppNavigation", "Navigating to detail: $contentId")
                        navController.navigate(Screen.detailRoute(contentId))
                    },
                    savedChoiceRepository = savedChoiceRepository,
                    userProfileRepository = userProfileRepository
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
                ProfileScreen(userProfileRepository = userProfileRepository)
            }
        }
    }
}
