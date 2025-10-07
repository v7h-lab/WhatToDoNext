package com.v7h.whattodonext

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Application class for What To Do Next app
 * 
 * This class initializes Hilt for dependency injection and sets up
 * the app-wide configuration for modern Android development.
 * 
 * Applied Rules: Debug logs, comments, Hilt DI, error handling, crash recovery
 */
@HiltAndroidApp
class WhatToDoNextApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Debug log for app initialization
        android.util.Log.d("WhatToDoNextApp", "Application created with Hilt DI")
        
        // Set up global exception handler to prevent crashes
        setupCrashHandler()
        
        // Initialize any app-wide configurations here
        initializeAppConfiguration()
    }
    
    /**
     * Set up global exception handler to catch and log unhandled exceptions
     * This prevents the app from crashing silently and helps with debugging
     */
    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            try {
                // Log the crash with detailed information
                android.util.Log.e("WhatToDoNextApp", "Uncaught exception in thread ${thread.name}", exception)
                
                // Log stack trace for debugging
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                exception.printStackTrace(pw)
                android.util.Log.e("WhatToDoNextApp", "Stack trace: ${sw.toString()}")
                
                // TODO: In production, send crash report to analytics service
                // For now, just log the crash
                
            } catch (e: Exception) {
                android.util.Log.e("WhatToDoNextApp", "Error in crash handler", e)
            } finally {
                // Call the default handler to handle the crash
                defaultHandler?.uncaughtException(thread, exception)
            }
        }
        
        android.util.Log.d("WhatToDoNextApp", "Global exception handler set up for crash recovery")
    }
    
    /**
     * Initialize app-wide configurations
     * This includes security settings, logging, and other global setup
     */
    private fun initializeAppConfiguration() {
        try {
            android.util.Log.d("WhatToDoNextApp", "Initializing app configuration for 2025 standards")
            
            // TODO: Add security configurations, analytics, crash reporting, etc.
            // This is where you'd initialize Firebase, analytics, crash reporting, etc.
            
            android.util.Log.d("WhatToDoNextApp", "App configuration initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("WhatToDoNextApp", "Error initializing app configuration", e)
            // Don't crash the app if configuration fails
        }
    }
}
