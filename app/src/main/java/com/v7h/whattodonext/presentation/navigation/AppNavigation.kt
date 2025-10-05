package com.v7h.whattodonext.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.v7h.whattodonext.presentation.ui.screen.deck.DeckScreen
import com.v7h.whattodonext.presentation.ui.screen.detail.DetailScreen

/**
 * Main navigation component for the What To Do Next app
 * 
 * Handles navigation between all screens:
 * - Deck screen (main card interface)
 * - Detail screen (expanded card view)
 * - Onboarding, Saved Choices, Profile (to be added in later steps)
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Debug log for navigation setup
    androidx.compose.runtime.LaunchedEffect(Unit) {
        android.util.Log.d("AppNavigation", "Navigation system initialized")
    }
    
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
                }
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
        
        // TODO: Add other screens in later build steps:
        // - S-001: Onboarding Screen
        // - S-004: Saved Choices Screen  
        // - S-005: Profile Screen
    }
}
