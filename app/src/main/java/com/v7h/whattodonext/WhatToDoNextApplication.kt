package com.v7h.whattodonext

import android.app.Application

/**
 * Application class for What To Do Next app
 */
class WhatToDoNextApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Debug log for app initialization
        android.util.Log.d("WhatToDoNextApp", "Application created")
    }
}
