package com.v7h.whattodonext.data.repository

import com.v7h.whattodonext.data.model.*
import com.v7h.whattodonext.data.storage.UserProfileStorage
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing user profile data
 * 
 * Handles:
 * - User profile persistence using SharedPreferences storage
 * - Activity selection and preferences management
 * - Onboarding completion tracking
 * 
 * Applied Rules: Debug logs, comments, StateFlow for reactive updates, persistent storage
 */
class UserProfileRepository(
    private val userProfileStorage: UserProfileStorage
) {
    
    // Reactive state from persistent storage
    val userProfile: StateFlow<UserProfile> = userProfileStorage.userProfile
    
    // Available activities from the default set
    val availableActivities = DefaultActivities.ACTIVITIES
    
    init {
        // Debug log for repository initialization
        android.util.Log.d("UserProfileRepository", "User profile repository initialized with persistent storage")
    }
    
    /**
     * Update the user's selected activities
     * This is called during onboarding and profile management
     */
    suspend fun updateSelectedActivities(activities: List<ActivityType>) {
        userProfileStorage.updateSelectedActivities(activities)
    }
    
    /**
     * Update preferences for a specific activity
     */
    suspend fun updateActivityPreferences(activityId: String, preferences: ActivityPreferences) {
        userProfileStorage.updateActivityPreferences(activityId, preferences)
    }
    
    /**
     * Mark onboarding as completed
     * Note: This is now a no-op since onboarding is always shown on launch
     */
    @Deprecated("Onboarding completion is no longer tracked - users see onboarding every launch")
    suspend fun completeOnboarding() {
        userProfileStorage.completeOnboarding()
    }
    
    /**
     * Reset onboarding status (for testing purposes)
     * Note: This is now a no-op since onboarding is always shown on launch
     */
    @Deprecated("Onboarding completion is no longer tracked - users see onboarding every launch")
    suspend fun resetOnboarding() {
        userProfileStorage.resetOnboarding()
    }
    
    /**
     * Get current user profile
     */
    fun getCurrentProfile(): UserProfile {
        return userProfileStorage.getCurrentProfile()
    }
    
    /**
     * Check if user has completed onboarding
     * Note: Always returns false now - users see onboarding every launch
     */
    @Deprecated("Onboarding completion is no longer tracked - users see onboarding every launch")
    fun hasCompletedOnboarding(): Boolean {
        return false // Always false - onboarding shown every launch
    }
    
    /**
     * Get user's selected activities
     */
    fun getSelectedActivities(): List<ActivityType> {
        return userProfileStorage.getSelectedActivities()
    }
}
