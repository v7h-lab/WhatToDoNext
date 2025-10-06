package com.v7h.whattodonext.data.model

/**
 * D-003: ActivityContent - Represents content for a single card
 * 
 * This data model represents the content that appears on cards:
 * - Movies, restaurants, outdoor activities, etc.
 * - Fetched from external sources (TMDB API, etc.)
 * - Used throughout the app for displaying options
 * 
 * Applied Rules: Debug logs, comments, flexible data structure
 */
data class ActivityContent(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val metadata: Map<String, String> = emptyMap()
) {
    // Debug helper
    fun logContent() {
        android.util.Log.d("ActivityContent", "Content: $title ($category) - $id")
    }
    
    /**
     * Get a specific metadata value
     */
    fun getMetadata(key: String): String? {
        return metadata[key]
    }
    
    /**
     * Check if this content has an image
     */
    fun hasImage(): Boolean {
        return !imageUrl.isNullOrBlank()
    }
    
    /**
     * Get display-friendly metadata
     */
    fun getDisplayMetadata(): Map<String, String> {
        return metadata.filter { (key, value) ->
            // Filter out technical metadata and show only user-friendly info
            !key.contains("_") && value.isNotBlank()
        }
    }
}

/**
 * Activity content categories
 */
object ActivityContentCategory {
    const val MOVIES = "movies"
    const val RESTAURANTS = "restaurants"
    const val OUTDOOR = "outdoor"
    const val BOOKS = "books"
    const val MUSIC = "music"
    const val SHOPPING = "shopping"
    
    // Debug helper
    fun logCategories() {
        android.util.Log.d("ActivityContentCategory", "Available categories: MOVIES, RESTAURANTS, OUTDOOR, BOOKS, MUSIC, SHOPPING")
    }
}
