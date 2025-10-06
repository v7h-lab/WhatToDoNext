package com.v7h.whattodonext.data.model

/**
 * D-001: UserProfile - User's chosen activities and preferences
 * 
 * This data model stores:
 * - User's selected activities (e.g., movies, restaurants, outdoor activities)
 * - Preferences for each activity type
 * - User's onboarding completion status
 * 
 * Applied Rules: Debug logs, comments, simple data structure
 */
data class UserProfile(
    val userId: String = "default_user",
    val selectedActivities: List<ActivityType> = emptyList(),
    val activityPreferences: Map<String, ActivityPreferences> = emptyMap(),
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Debug helper
    fun logProfile() {
        android.util.Log.d("UserProfile", "Profile for user $userId: ${selectedActivities.size} activities, onboarding: $hasCompletedOnboarding")
    }
}

/**
 * Activity type definition
 * Each activity represents a category of choices (movies, restaurants, etc.)
 */
data class ActivityType(
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // Icon resource name or emoji
    val isEnabled: Boolean = true
) {
    // Debug helper
    fun logActivity() {
        android.util.Log.d("ActivityType", "Activity: $name ($id) - $description")
    }
}

/**
 * Activity-specific preferences
 * Contains filters and settings for each activity type
 */
data class ActivityPreferences(
    val activityId: String,
    val filters: Map<String, Any> = emptyMap(), // Generic filters (genre, price range, etc.)
    val customSettings: Map<String, String> = emptyMap(), // Activity-specific settings
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // Debug helper
    fun logPreferences() {
        android.util.Log.d("ActivityPreferences", "Preferences for $activityId: ${filters.size} filters, ${customSettings.size} settings")
    }
}

/**
 * Predefined activity types available in the app
 * These match the specification requirements for user selection
 */
object DefaultActivities {
    val ACTIVITIES = listOf(
        ActivityType(
            id = "movies",
            name = "Movies",
            description = "Discover new films to watch",
            icon = "🎬"
        ),
        ActivityType(
            id = "restaurants",
            name = "Restaurants", 
            description = "Find great places to eat",
            icon = "🍽️"
        ),
        ActivityType(
            id = "outdoor",
            name = "Outdoor Activities",
            description = "Explore outdoor adventures",
            icon = "🏞️"
        ),
        ActivityType(
            id = "shopping",
            name = "Shopping",
            description = "Discover new products and stores",
            icon = "🛍️"
        ),
        ActivityType(
            id = "books",
            name = "Books",
            description = "Find your next great read",
            icon = "📚"
        ),
        ActivityType(
            id = "music",
            name = "Music",
            description = "Discover new songs and artists",
            icon = "🎵"
        )
    )
    
    // Debug helper
    fun logAvailableActivities() {
        android.util.Log.d("DefaultActivities", "Available activities: ${ACTIVITIES.map { it.name }}")
    }
}
