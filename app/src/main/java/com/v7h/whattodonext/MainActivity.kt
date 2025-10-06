package com.v7h.whattodonext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.v7h.whattodonext.presentation.navigation.AppNavigation
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Entry point for the What To Do Next app
 * 
 * This activity sets up the main UI structure with:
 * - Edge-to-edge display
 * - Custom theme with our design system
 * - Navigation system for all screens
 * - Hilt dependency injection
 * 
 * Applied Rules: Debug logs, comments, Hilt DI, Jetpack Compose
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for modern Android look
        enableEdgeToEdge()
        
        // Debug log for app startup
        android.util.Log.d("MainActivity", "What To Do Next app starting...")
        
        setContent {
            WhatToDoNextTheme {
                // Main app navigation without nested Scaffold
                AppNavigation(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        // Debug log for successful setup
        android.util.Log.d("MainActivity", "App setup completed successfully")
    }
}
