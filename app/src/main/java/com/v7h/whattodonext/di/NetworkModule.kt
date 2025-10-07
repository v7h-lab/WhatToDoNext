package com.v7h.whattodonext.di

import android.content.Context
import com.v7h.whattodonext.BuildConfig
import com.v7h.whattodonext.data.api.TmdbApiService
import com.v7h.whattodonext.data.api.TmdbApiConfig
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.storage.UserProfileStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Network Module - Hilt Dependency Injection
 * 
 * Provides:
 * - OkHttpClient with logging
 * - Retrofit instance for TMDB API
 * - TMDB API service
 * - Movie repository
 * 
 * Applied Rules: Debug logs, comments, Hilt DI, StateFlow
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Provide OkHttpClient with logging interceptor
     * Applied Rules: Debug logs, comments, Singleton scope
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        android.util.Log.d("NetworkModule", "Providing OkHttpClient with logging")
        
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Provide Retrofit instance configured for TMDB API
     * Applied Rules: Debug logs, comments, Singleton scope
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        android.util.Log.d("NetworkModule", "Providing Retrofit with base URL: ${TmdbApiConfig.BASE_URL}")
        
        return Retrofit.Builder()
            .baseUrl(TmdbApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Provide TMDB API service
     * Applied Rules: Debug logs, comments, Singleton scope
     */
    @Provides
    @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TmdbApiService {
        android.util.Log.d("NetworkModule", "Providing TMDB API service")
        
        return retrofit.create(TmdbApiService::class.java)
    }
    
    /**
     * Provide Movie Repository
     * Applied Rules: Debug logs, comments, Singleton scope, secure API key
     */
    @Provides
    @Singleton
    fun provideMovieRepository(tmdbApiService: TmdbApiService): MovieRepository {
        val apiKey = BuildConfig.TMDB_API_KEY
        android.util.Log.d("NetworkModule", "Providing MovieRepository with API key: ${if (apiKey.isNotEmpty()) "configured" else "empty"}")
        
        return MovieRepository(tmdbApiService, apiKey)
    }
    
    // Keep the old singleton instance for backward compatibility with non-Hilt code
    val movieRepository: MovieRepository by lazy {
        android.util.Log.d("NetworkModule", "Legacy: Creating Movie repository singleton")
        
        val apiKey = BuildConfig.TMDB_API_KEY
        val loggingInterceptor = createLoggingInterceptor()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(TmdbApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        val tmdbApiService = retrofit.create(TmdbApiService::class.java)
        MovieRepository(tmdbApiService, apiKey)
    }
    
    // Note: UserProfileStorage and UserProfileRepository providers are in AppModule.kt
    // to avoid duplicate bindings. Legacy function kept for backward compatibility.
    
    // Legacy function for backward compatibility with non-Hilt code
    fun legacyUserProfileRepository(context: Context): UserProfileRepository {
        android.util.Log.d("NetworkModule", "Legacy: Creating UserProfileRepository with persistent storage")
        
        return UserProfileRepository(UserProfileStorage(context))
    }
    
    /**
     * Create logging interceptor for debugging API calls
     * Applied Rules: Debug logs, HTTP request/response logging
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            android.util.Log.d("NetworkModule", "HTTP: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * Check if API key is configured (utility function)
     */
    fun isApiKeyConfigured(): Boolean {
        val apiKey = BuildConfig.TMDB_API_KEY
        val isConfigured = apiKey.isNotEmpty()
        android.util.Log.d("NetworkModule", "API key configured: $isConfigured")
        return isConfigured
    }
    
    /**
     * Get API key status for debugging (utility function)
     */
    fun getApiKeyStatus(): String {
        return if (isApiKeyConfigured()) {
            "API key is configured"
        } else {
            "API key not configured - using fallback data"
        }
    }
}
