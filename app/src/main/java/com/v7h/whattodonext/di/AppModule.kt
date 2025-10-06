package com.v7h.whattodonext.di

import android.content.Context
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.storage.UserProfileStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI Module for app-wide dependencies
 * 
 * This module provides singleton instances of repositories and storage
 * classes using modern dependency injection patterns.
 * 
 * Applied Rules: Debug logs, comments, Hilt DI
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provide UserProfileStorage as singleton
     */
    @Provides
    @Singleton
    fun provideUserProfileStorage(@ApplicationContext context: Context): UserProfileStorage {
        android.util.Log.d("AppModule", "Providing UserProfileStorage")
        return UserProfileStorage(context)
    }
    
    /**
     * Provide UserProfileRepository as singleton
     */
    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileStorage: UserProfileStorage
    ): UserProfileRepository {
        android.util.Log.d("AppModule", "Providing UserProfileRepository")
        return UserProfileRepository(userProfileStorage)
    }
    
    /**
     * Provide SavedChoiceRepository as singleton
     */
    @Provides
    @Singleton
    fun provideSavedChoiceRepository(): SavedChoiceRepository {
        android.util.Log.d("AppModule", "Providing SavedChoiceRepository")
        return SavedChoiceRepository()
    }
}
