package com.v7h.whattodonext.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.v7h.whattodonext.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UserProfileStorage - Persistent storage for user profile data
 * 
 * Handles:
 * - Saving/loading user profile using SharedPreferences
 * - JSON serialization for complex data structures
 * - Reactive updates via StateFlow
 * 
 * Applied Rules: Debug logs, comments, persistent storage, StateFlow
 */
class UserProfileStorage(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_profile_prefs", 
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    // Reactive state for user profile
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    // Keys for SharedPreferences
    private companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_SELECTED_ACTIVITIES = "selected_activities"
        const val KEY_ACTIVITY_PREFERENCES = "activity_preferences"
        const val KEY_HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
        const val KEY_CREATED_AT = "created_at"
        const val KEY_UPDATED_AT = "updated_at"
    }
    
    init {
        // Debug log for storage initialization
        android.util.Log.d("UserProfileStorage", "User profile storage initialized")
    }
    
    /**
     * Load user profile from SharedPreferences
     * Note: Onboarding completion is no longer tracked - users see onboarding every launch
     */
    private fun loadUserProfile(): UserProfile {
        return try {
            val userId = prefs.getString(KEY_USER_ID, "default_user") ?: "default_user"
            val createdAt = prefs.getLong(KEY_CREATED_AT, System.currentTimeMillis())
            val updatedAt = prefs.getLong(KEY_UPDATED_AT, System.currentTimeMillis())
            
            // Load selected activities (now single activity stored as list with 1 item)
            val selectedActivitiesJson = prefs.getString(KEY_SELECTED_ACTIVITIES, null)
            val selectedActivities = if (selectedActivitiesJson != null) {
                try {
                    val type = object : TypeToken<List<ActivityType>>() {}.type
                    gson.fromJson<List<ActivityType>>(selectedActivitiesJson, type) ?: emptyList()
                } catch (e: Exception) {
                    android.util.Log.e("UserProfileStorage", "Error parsing selected activities", e)
                    emptyList()
                }
            } else {
                emptyList()
            }
            
            // Load activity preferences
            val activityPreferencesJson = prefs.getString(KEY_ACTIVITY_PREFERENCES, null)
            val activityPreferences = if (activityPreferencesJson != null) {
                try {
                    val type = object : TypeToken<Map<String, ActivityPreferences>>() {}.type
                    gson.fromJson<Map<String, ActivityPreferences>>(activityPreferencesJson, type) ?: emptyMap()
                } catch (e: Exception) {
                    android.util.Log.e("UserProfileStorage", "Error parsing activity preferences", e)
                    emptyMap()
                }
            } else {
                emptyMap()
            }
            
            val profile = UserProfile(
                userId = userId,
                selectedActivities = selectedActivities,
                activityPreferences = activityPreferences,
                hasCompletedOnboarding = false, // Always false - onboarding shown every launch
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            
            // Debug log for profile loading
            android.util.Log.d("UserProfileStorage", "Loaded user profile: ${selectedActivities.size} activities")
            
            profile
        } catch (e: Exception) {
            android.util.Log.e("UserProfileStorage", "Error loading user profile", e)
            // Return default profile on error
            UserProfile()
        }
    }
    
    /**
     * Save user profile to SharedPreferences
     * Note: hasCompletedOnboarding is always set to false - not persisted
     */
    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            // Debug log for profile saving
            android.util.Log.d("UserProfileStorage", "Saving user profile: ${profile.selectedActivities.size} activities")
            
            prefs.edit().apply {
                putString(KEY_USER_ID, profile.userId)
                // Note: Not saving hasCompletedOnboarding - always false on load
                putLong(KEY_CREATED_AT, profile.createdAt)
                putLong(KEY_UPDATED_AT, profile.updatedAt)
                
                // Save selected activities as JSON (single activity as list)
                val selectedActivitiesJson = gson.toJson(profile.selectedActivities)
                putString(KEY_SELECTED_ACTIVITIES, selectedActivitiesJson)
                
                // Save activity preferences as JSON
                val activityPreferencesJson = gson.toJson(profile.activityPreferences)
                putString(KEY_ACTIVITY_PREFERENCES, activityPreferencesJson)
                
                apply()
            }
            
            // Update reactive state
            _userProfile.value = profile
            
            // Debug log for successful save
            android.util.Log.d("UserProfileStorage", "User profile saved successfully")
            
        } catch (e: Exception) {
            android.util.Log.e("UserProfileStorage", "Error saving user profile", e)
        }
    }
    
    /**
     * Update selected activities (now supports single activity selection)
     */
    suspend fun updateSelectedActivities(activities: List<ActivityType>) {
        val currentProfile = _userProfile.value
        val updatedProfile = currentProfile.copy(
            selectedActivities = activities,
            updatedAt = System.currentTimeMillis(),
            hasCompletedOnboarding = false // Always false - onboarding shown every launch
        )
        saveUserProfile(updatedProfile)
        
        // Debug log for activity update
        android.util.Log.d("UserProfileStorage", "Updated selected activities: ${activities.map { it.name }}")
    }
    
    /**
     * Update activity preferences
     */
    suspend fun updateActivityPreferences(activityId: String, preferences: ActivityPreferences) {
        val currentProfile = _userProfile.value
        val updatedPreferences = currentProfile.activityPreferences.toMutableMap()
        updatedPreferences[activityId] = preferences
        
        val updatedProfile = currentProfile.copy(
            activityPreferences = updatedPreferences,
            updatedAt = System.currentTimeMillis()
        )
        saveUserProfile(updatedProfile)
        
        // Debug log for preferences update
        android.util.Log.d("UserProfileStorage", "Updated preferences for activity: $activityId")
    }
    
    /**
     * Mark onboarding as completed
     * Note: This is now a no-op since onboarding is always shown on launch
     */
    @Deprecated("Onboarding completion is no longer tracked - users see onboarding every launch")
    suspend fun completeOnboarding() {
        // No-op - onboarding is always shown on launch
        android.util.Log.d("UserProfileStorage", "completeOnboarding called but ignored - onboarding shown every launch")
    }
    
    /**
     * Reset onboarding status (for testing purposes)
     * Note: This is now a no-op since onboarding is always shown on launch
     */
    @Deprecated("Onboarding completion is no longer tracked - users see onboarding every launch")
    suspend fun resetOnboarding() {
        // No-op - onboarding is always shown on launch
        android.util.Log.d("UserProfileStorage", "resetOnboarding called but ignored - onboarding shown every launch")
    }
    
    /**
     * Get current user profile
     */
    fun getCurrentProfile(): UserProfile {
        return _userProfile.value
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
        return _userProfile.value.selectedActivities
    }
    
    /**
     * Clear all user data (for testing purposes)
     */
    suspend fun clearAllData() {
        prefs.edit().clear().apply()
        _userProfile.value = UserProfile()
        
        // Debug log for data clearing
        android.util.Log.d("UserProfileStorage", "All user data cleared")
    }
}
