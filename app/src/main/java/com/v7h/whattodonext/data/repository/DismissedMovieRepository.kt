package com.v7h.whattodonext.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing dismissed movies
 * 
 * Handles:
 * - Tracking movies that have been dismissed (swiped left)
 * - Persistent storage using SharedPreferences
 * - Filtering out dismissed movies from suggestions
 * - Debug logging for dismissed movie tracking
 * 
 * Applied Rules: Debug logs, comments, StateFlow for state management, persistent storage
 */
class DismissedMovieRepository(
    private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "dismissed_movies_prefs"
        private const val KEY_DISMISSED_MOVIES = "dismissed_movie_ids"
        private const val KEY_DISMISSED_COUNT = "dismissed_count"
    }
    
    // SharedPreferences for persistent storage
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // In-memory state for dismissed movies
    private val _dismissedMovies = MutableStateFlow<Set<String>>(emptySet())
    val dismissedMovies: StateFlow<Set<String>> = _dismissedMovies.asStateFlow()
    
    // Dismissed movies count for analytics
    private val _dismissedCount = MutableStateFlow(0)
    val dismissedCount: StateFlow<Int> = _dismissedCount.asStateFlow()
    
    init {
        // Load dismissed movies from persistent storage
        loadDismissedMovies()
        
        // Debug log for repository initialization
        android.util.Log.d("DismissedMovieRepository", "Dismissed movie repository initialized with ${_dismissedMovies.value.size} dismissed movies")
    }
    
    /**
     * Load dismissed movies from SharedPreferences
     */
    private fun loadDismissedMovies() {
        try {
            val dismissedIdsJson = sharedPreferences.getString(KEY_DISMISSED_MOVIES, "[]")
            val dismissedIds = if (dismissedIdsJson.isNullOrEmpty()) {
                emptySet()
            } else {
                // Parse JSON array of movie IDs
                dismissedIdsJson.removePrefix("[").removeSuffix("]")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
                    .filter { it.isNotEmpty() }
                    .toSet()
            }
            
            val count = sharedPreferences.getInt(KEY_DISMISSED_COUNT, 0)
            
            _dismissedMovies.value = dismissedIds
            _dismissedCount.value = count
            
            android.util.Log.d("DismissedMovieRepository", "Loaded ${dismissedIds.size} dismissed movies from storage")
        } catch (e: Exception) {
            android.util.Log.e("DismissedMovieRepository", "Error loading dismissed movies", e)
            _dismissedMovies.value = emptySet()
            _dismissedCount.value = 0
        }
    }
    
    /**
     * Add a movie ID to dismissed list
     */
    fun addDismissedMovie(movieId: String) {
        android.util.Log.d("DismissedMovieRepository", "Adding dismissed movie: $movieId")
        
        val currentDismissed = _dismissedMovies.value.toMutableSet()
        currentDismissed.add(movieId)
        
        _dismissedMovies.value = currentDismissed
        _dismissedCount.value = _dismissedCount.value + 1
        
        // Save to persistent storage
        saveDismissedMovies()
        
        android.util.Log.d("DismissedMovieRepository", "Total dismissed movies: ${currentDismissed.size}")
    }
    
    /**
     * Check if a movie has been dismissed
     */
    fun isMovieDismissed(movieId: String): Boolean {
        val isDismissed = movieId in _dismissedMovies.value
        android.util.Log.d("DismissedMovieRepository", "Movie $movieId dismissed status: $isDismissed")
        return isDismissed
    }
    
    /**
     * Filter out dismissed movies from a list
     */
    fun filterDismissedMovies(movies: List<String>): List<String> {
        val filtered = movies.filter { !isMovieDismissed(it) }
        android.util.Log.d("DismissedMovieRepository", "Filtered ${movies.size} movies to ${filtered.size} (removed ${movies.size - filtered.size} dismissed)")
        return filtered
    }
    
    /**
     * Get all dismissed movie IDs
     */
    fun getAllDismissedMovies(): Set<String> {
        android.util.Log.d("DismissedMovieRepository", "Retrieving ${_dismissedMovies.value.size} dismissed movies")
        return _dismissedMovies.value
    }
    
    /**
     * Clear all dismissed movies (for testing or reset functionality)
     */
    fun clearAllDismissedMovies() {
        android.util.Log.d("DismissedMovieRepository", "Clearing all dismissed movies")
        
        _dismissedMovies.value = emptySet()
        _dismissedCount.value = 0
        
        // Clear from persistent storage
        sharedPreferences.edit()
            .remove(KEY_DISMISSED_MOVIES)
            .remove(KEY_DISMISSED_COUNT)
            .apply()
        
        android.util.Log.d("DismissedMovieRepository", "All dismissed movies cleared")
    }
    
    /**
     * Remove a specific movie from dismissed list (undo functionality)
     */
    fun removeDismissedMovie(movieId: String) {
        android.util.Log.d("DismissedMovieRepository", "Removing movie from dismissed list: $movieId")
        
        val currentDismissed = _dismissedMovies.value.toMutableSet()
        val wasRemoved = currentDismissed.remove(movieId)
        
        if (wasRemoved) {
            _dismissedMovies.value = currentDismissed
            _dismissedCount.value = maxOf(0, _dismissedCount.value - 1)
            
            // Save to persistent storage
            saveDismissedMovies()
            
            android.util.Log.d("DismissedMovieRepository", "Movie removed from dismissed list, remaining: ${currentDismissed.size}")
        } else {
            android.util.Log.w("DismissedMovieRepository", "Movie $movieId was not in dismissed list")
        }
    }
    
    /**
     * Save dismissed movies to SharedPreferences
     */
    private fun saveDismissedMovies() {
        try {
            val dismissedIdsJson = _dismissedMovies.value.joinToString(",", "[", "]") { "\"$it\"" }
            
            sharedPreferences.edit()
                .putString(KEY_DISMISSED_MOVIES, dismissedIdsJson)
                .putInt(KEY_DISMISSED_COUNT, _dismissedCount.value)
                .apply()
            
            android.util.Log.d("DismissedMovieRepository", "Saved ${_dismissedMovies.value.size} dismissed movies to storage")
        } catch (e: Exception) {
            android.util.Log.e("DismissedMovieRepository", "Error saving dismissed movies", e)
        }
    }
    
    /**
     * Get statistics about dismissed movies
     */
    fun getDismissedStats(): Map<String, Any> {
        return mapOf(
            "totalDismissed" to _dismissedMovies.value.size,
            "dismissedCount" to _dismissedCount.value,
            "dismissedMovies" to _dismissedMovies.value.toList()
        )
    }
}
