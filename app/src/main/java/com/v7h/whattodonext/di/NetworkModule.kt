package com.v7h.whattodonext.di

import android.content.Context
import com.v7h.whattodonext.BuildConfig
import com.v7h.whattodonext.data.api.TmdbApiService
import com.v7h.whattodonext.data.api.TmdbApiConfig
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.storage.UserProfileStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network Module - Dependency Injection
 * 
 * Provides:
 * - OkHttpClient with logging
 * - Retrofit instance for TMDB API
 * - TMDB API service
 * - Movie repository
 * 
 * Applied Rules: Debug logs, comments, proper dependency injection
 */
object NetworkModule {
    
    // TMDB API Key - Loaded securely from local.properties via BuildConfig
    // Applied Rules: Debug logs, comments, secure API key configuration
    private val TMDB_API_KEY: String by lazy { 
        BuildConfig.TMDB_API_KEY.also {
            android.util.Log.d("NetworkModule", "API key loaded from BuildConfig: ${if (it.isNotEmpty()) "configured" else "empty"}")
        }
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        // Debug log for HTTP client setup
        android.util.Log.d("NetworkModule", "Setting up OkHttpClient with logging")
        
        OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        // Debug log for Retrofit setup
        android.util.Log.d("NetworkModule", "Setting up Retrofit with base URL: ${TmdbApiConfig.BASE_URL}")
        
        Retrofit.Builder()
            .baseUrl(TmdbApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val tmdbApiService: TmdbApiService by lazy {
        // Debug log for API service creation
        android.util.Log.d("NetworkModule", "Creating TMDB API service")
        
        retrofit.create(TmdbApiService::class.java)
    }
    
    val movieRepository: MovieRepository by lazy {
        // Debug log for repository creation
        android.util.Log.d("NetworkModule", "Creating Movie repository with API key")
        
        MovieRepository(tmdbApiService, TMDB_API_KEY)
    }
    
    /**
     * User Profile Storage - Persistent storage for user data
     * Requires Context for SharedPreferences
     */
    fun provideUserProfileStorage(context: Context): UserProfileStorage {
        // Debug log for storage creation
        android.util.Log.d("NetworkModule", "Creating UserProfileStorage with SharedPreferences")
        
        return UserProfileStorage(context)
    }
    
    /**
     * User Profile Repository - Manages user profile data
     */
    fun provideUserProfileRepository(context: Context): UserProfileRepository {
        // Debug log for repository creation
        android.util.Log.d("NetworkModule", "Creating UserProfileRepository with persistent storage")
        
        return UserProfileRepository(provideUserProfileStorage(context))
    }
    
    /**
     * Create logging interceptor for debugging API calls
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            // Debug log for HTTP requests/responses
            android.util.Log.d("NetworkModule", "HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * Check if API key is configured
     */
    fun isApiKeyConfigured(): Boolean {
        val isConfigured = TMDB_API_KEY.isNotEmpty()
        android.util.Log.d("NetworkModule", "API key configured: $isConfigured")
        return isConfigured
    }
    
    /**
     * Get API key status for debugging
     */
    fun getApiKeyStatus(): String {
        return if (isApiKeyConfigured()) {
            "API key is configured"
        } else {
            "API key not configured - using fallback data"
        }
    }
}
