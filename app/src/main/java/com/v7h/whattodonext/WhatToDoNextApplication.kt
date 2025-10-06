package com.v7h.whattodonext

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for What To Do Next app
 * 
 * This class initializes Hilt for dependency injection and sets up
 * the app-wide configuration for modern Android development.
 * 
 * Applied Rules: Debug logs, comments, Hilt DI
 */
@HiltAndroidApp
class WhatToDoNextApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Debug log for app initialization
        android.util.Log.d("WhatToDoNextApp", "Application created with Hilt DI")
        
        // Initialize any app-wide configurations here
        initializeAppConfiguration()
    }
    
    /**
     * Initialize app-wide configurations
     * This includes security settings, logging, and other global setup
     */
    private fun initializeAppConfiguration() {
        android.util.Log.d("WhatToDoNextApp", "Initializing app configuration for 2025 standards")
        
        // TODO: Add security configurations, analytics, crash reporting, etc.
        // This is where you'd initialize Firebase, analytics, crash reporting, etc.
    }
}
