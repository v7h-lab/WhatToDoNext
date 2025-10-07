package com.v7h.whattodonext.data.repository

import com.v7h.whattodonext.data.api.TmdbApiService
import com.v7h.whattodonext.data.api.TmdbApiConfig
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.model.TmdbConfiguration
import com.v7h.whattodonext.data.model.TmdbGenres
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * Movie Repository - TMDB API Integration
 * 
 * Handles:
 * - Fetching movie data from TMDB API
 * - Converting TMDB responses to ActivityContent
 * - Caching movie data for offline use
 * - Error handling and fallback data
 * 
 * Applied Rules: Debug logs, comments, StateFlow for reactive updates
 */
class MovieRepository(
    private val tmdbApiService: TmdbApiService,
    private val apiKey: String
) {
    
    // In-memory cache for movies
    private val _popularMovies = MutableStateFlow<List<ActivityContent>>(emptyList())
    val popularMovies: StateFlow<List<ActivityContent>> = _popularMovies.asStateFlow()
    
    private val _topRatedMovies = MutableStateFlow<List<ActivityContent>>(emptyList())
    val topRatedMovies: StateFlow<List<ActivityContent>> = _topRatedMovies.asStateFlow()
    
    private val _nowPlayingMovies = MutableStateFlow<List<ActivityContent>>(emptyList())
    val nowPlayingMovies: StateFlow<List<ActivityContent>> = _nowPlayingMovies.asStateFlow()
    
    // Configuration for image URLs
    private var tmdbConfiguration: TmdbConfiguration? = null
    
    init {
        // Debug log for repository initialization
        android.util.Log.d("MovieRepository", "Movie repository initialized with TMDB API")
    }
    
    /**
     * Initialize repository by fetching configuration
     * Includes robust error handling to prevent crashes
     */
    suspend fun initialize() {
        try {
            // Debug log for initialization
            android.util.Log.d("MovieRepository", "Initializing TMDB configuration...")
            
            // Check if API key is valid before making requests
            if (apiKey.isEmpty()) {
                android.util.Log.w("MovieRepository", "API key is empty, skipping configuration initialization")
                return
            }
            
            val response = tmdbApiService.getConfiguration(apiKey)
            if (response.isSuccessful) {
                tmdbConfiguration = response.body()
                tmdbConfiguration?.logConfiguration()
                android.util.Log.d("MovieRepository", "TMDB configuration loaded successfully")
            } else {
                android.util.Log.e("MovieRepository", "Failed to load TMDB configuration: ${response.code()} - ${response.message()}")
                // Don't crash, just log the error and continue with fallback
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Error initializing TMDB configuration", e)
            // Don't crash the app, just log the error and continue with fallback data
        }
    }
    
    /**
     * Fetch popular movies from TMDB
     */
    suspend fun fetchPopularMovies(page: Int = 1): Result<List<ActivityContent>> {
        return try {
            // Debug log for API call
            android.util.Log.d("MovieRepository", "Fetching popular movies - page $page")
            
            val response = tmdbApiService.getPopularMovies(apiKey, page)
            
            if (response.isSuccessful) {
                val movieResponse = response.body()
                if (movieResponse != null) {
                    val activityContent = movieResponse.results.map { it.toActivityContent() }
                    
                    // Update cache
                    if (page == 1) {
                        _popularMovies.value = activityContent
                    } else {
                        _popularMovies.value = _popularMovies.value + activityContent
                    }
                    
                    // Debug log for successful fetch
                    android.util.Log.d("MovieRepository", "Successfully fetched ${activityContent.size} popular movies")
                    movieResponse.logResponse()
                    
                    Result.success(activityContent)
                } else {
                    android.util.Log.e("MovieRepository", "Empty response body for popular movies")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                android.util.Log.e("MovieRepository", "API error for popular movies: ${response.code()}")
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Exception fetching popular movies", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch top rated movies from TMDB
     */
    suspend fun fetchTopRatedMovies(page: Int = 1): Result<List<ActivityContent>> {
        return try {
            // Debug log for API call
            android.util.Log.d("MovieRepository", "Fetching top rated movies - page $page")
            
            val response = tmdbApiService.getTopRatedMovies(apiKey, page)
            
            if (response.isSuccessful) {
                val movieResponse = response.body()
                if (movieResponse != null) {
                    val activityContent = movieResponse.results.map { it.toActivityContent() }
                    
                    // Update cache
                    if (page == 1) {
                        _topRatedMovies.value = activityContent
                    } else {
                        _topRatedMovies.value = _topRatedMovies.value + activityContent
                    }
                    
                    // Debug log for successful fetch
                    android.util.Log.d("MovieRepository", "Successfully fetched ${activityContent.size} top rated movies")
                    movieResponse.logResponse()
                    
                    Result.success(activityContent)
                } else {
                    android.util.Log.e("MovieRepository", "Empty response body for top rated movies")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                android.util.Log.e("MovieRepository", "API error for top rated movies: ${response.code()}")
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Exception fetching top rated movies", e)
            Result.failure(e)
        }
    }
    
    /**
     * Fetch now playing movies from TMDB
     */
    suspend fun fetchNowPlayingMovies(page: Int = 1): Result<List<ActivityContent>> {
        return try {
            // Debug log for API call
            android.util.Log.d("MovieRepository", "Fetching now playing movies - page $page")
            
            val response = tmdbApiService.getNowPlayingMovies(apiKey, page)
            
            if (response.isSuccessful) {
                val movieResponse = response.body()
                if (movieResponse != null) {
                    val activityContent = movieResponse.results.map { it.toActivityContent() }
                    
                    // Update cache
                    if (page == 1) {
                        _nowPlayingMovies.value = activityContent
                    } else {
                        _nowPlayingMovies.value = _nowPlayingMovies.value + activityContent
                    }
                    
                    // Debug log for successful fetch
                    android.util.Log.d("MovieRepository", "Successfully fetched ${activityContent.size} now playing movies")
                    movieResponse.logResponse()
                    
                    Result.success(activityContent)
                } else {
                    android.util.Log.e("MovieRepository", "Empty response body for now playing movies")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                android.util.Log.e("MovieRepository", "API error for now playing movies: ${response.code()}")
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Exception fetching now playing movies", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get cached movies for a specific category
     */
    fun getCachedMovies(category: String): List<ActivityContent> {
        return when (category.lowercase()) {
            "popular" -> _popularMovies.value
            "top_rated" -> _topRatedMovies.value
            "now_playing" -> _nowPlayingMovies.value
            else -> {
                android.util.Log.w("MovieRepository", "Unknown movie category: $category")
                _popularMovies.value // Default to popular
            }
        }
    }
    
    /**
     * Check if we have cached data for a category
     */
    fun hasCachedData(category: String): Boolean {
        return getCachedMovies(category).isNotEmpty()
    }
    
    /**
     * Fetch movies based on user preferences
     * This is the main method that will be called from the UI
     * Supports filtering by genres and languages
     */
    suspend fun fetchMoviesForUser(userPreferences: Map<String, Any>): Result<List<ActivityContent>> {
        return try {
            // Debug log for user preference filtering
            android.util.Log.d("MovieRepository", "Fetching movies with user preferences: $userPreferences")
            
            // Extract genre preferences
            val preferredGenres = userPreferences["genres"] as? List<String> ?: emptyList()
            val preferredLanguages = userPreferences["languages"] as? List<String> ?: emptyList()
            val minRating = userPreferences["min_rating"] as? Double ?: 6.0
            val maxRating = userPreferences["max_rating"] as? Double ?: 10.0
            val year = userPreferences["year"] as? Int
            
            // Convert genre names to TMDB genre IDs
            val genreIds = if (preferredGenres.isNotEmpty()) {
                getGenreIdsForNames(preferredGenres)
            } else null
            
            // Handle language filtering
            // If multiple languages selected, we'll fetch movies for each and combine
            // If no languages selected, use default (all languages)
            val languageFilter = if (preferredLanguages.isNotEmpty()) {
                preferredLanguages.first() // For now, use first language (TMDB API limitation)
            } else null
            
            // Use discover endpoint with user preferences
            val response = tmdbApiService.discoverMovies(
                apiKey = apiKey,
                withGenres = genreIds?.joinToString(","),
                withOriginalLanguage = languageFilter,
                minVoteAverage = minRating,
                maxVoteAverage = maxRating,
                year = year
            )
            
            if (response.isSuccessful) {
                val movieResponse = response.body()
                if (movieResponse != null) {
                    val activityContent = movieResponse.results.map { it.toActivityContent() }
                    
                    // Update popular movies cache with filtered results
                    _popularMovies.value = activityContent
                    
                    // Debug log for successful fetch
                    android.util.Log.d("MovieRepository", "Successfully fetched ${activityContent.size} movies with preferences: genres=$preferredGenres, languages=$preferredLanguages")
                    movieResponse.logResponse()
                    
                    Result.success(activityContent)
                } else {
                    android.util.Log.e("MovieRepository", "Empty response body for user preferences")
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                android.util.Log.e("MovieRepository", "API error for user preferences: ${response.code()}")
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Exception fetching movies with user preferences", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get genre IDs for genre names
     * Uses TmdbGenres constants for consistent mapping
     */
    private fun getGenreIdsForNames(genreNames: List<String>): List<Int> {
        val genreIds = TmdbGenres.getGenreIds(genreNames)
        
        // Debug log for genre mapping
        android.util.Log.d("MovieRepository", "Mapped genres: $genreNames -> $genreIds")
        
        return genreIds
    }
    
    /**
     * Shuffle movies for fresh suggestions each time
     */
    fun shuffleMovies(movies: List<ActivityContent>): List<ActivityContent> {
        android.util.Log.d("MovieRepository", "Shuffling ${movies.size} movies for fresh suggestions")
        return movies.shuffled()
    }
    
    /**
     * Filter out dismissed movies from the list
     */
    fun filterDismissedMovies(movies: List<ActivityContent>, dismissedMovieIds: Set<String>): List<ActivityContent> {
        val filtered = movies.filter { it.id !in dismissedMovieIds }
        android.util.Log.d("MovieRepository", "Filtered ${movies.size} movies to ${filtered.size} (removed ${movies.size - filtered.size} dismissed)")
        return filtered
    }
    
    /**
     * Get fresh shuffled movies excluding dismissed ones
     */
    fun getFreshShuffledMovies(dismissedMovieIds: Set<String> = emptySet()): List<ActivityContent> {
        // Combine all cached movies from different categories
        val allMovies = _popularMovies.value + _topRatedMovies.value + _nowPlayingMovies.value
        
        // Remove duplicates based on movie ID
        val uniqueMovies = allMovies.distinctBy { it.id }
        
        // Filter out dismissed movies
        val filteredMovies = filterDismissedMovies(uniqueMovies, dismissedMovieIds)
        
        // Shuffle for fresh order
        val shuffledMovies = shuffleMovies(filteredMovies)
        
        android.util.Log.d("MovieRepository", "Generated fresh shuffled list: ${shuffledMovies.size} unique movies")
        
        return shuffledMovies
    }
    
    /**
     * Fetch fresh movies with shuffling and dismissed filtering
     * This is the main method for getting dynamic movie suggestions
     */
    suspend fun fetchFreshMovies(dismissedMovieIds: Set<String> = emptySet()): Result<List<ActivityContent>> {
        return try {
            android.util.Log.d("MovieRepository", "Fetching fresh movies with ${dismissedMovieIds.size} dismissed movies filtered out")
            
            // Try to fetch from different categories to get variety
            val popularResult = fetchPopularMovies()
            val topRatedResult = fetchTopRatedMovies()
            val nowPlayingResult = fetchNowPlayingMovies()
            
            // Combine results from all categories
            val allMovies = mutableListOf<ActivityContent>()
            
            popularResult.getOrNull()?.let { allMovies.addAll(it) }
            topRatedResult.getOrNull()?.let { allMovies.addAll(it) }
            nowPlayingResult.getOrNull()?.let { allMovies.addAll(it) }
            
            if (allMovies.isNotEmpty()) {
                // Remove duplicates
                val uniqueMovies = allMovies.distinctBy { it.id }
                
                // Filter out dismissed movies
                val filteredMovies = filterDismissedMovies(uniqueMovies, dismissedMovieIds)
                
                // Shuffle for fresh order
                val shuffledMovies = shuffleMovies(filteredMovies)
                
                android.util.Log.d("MovieRepository", "Successfully fetched ${shuffledMovies.size} fresh movies")
                
                Result.success(shuffledMovies)
            } else {
                android.util.Log.w("MovieRepository", "No movies fetched from any category, using fallback")
                Result.success(getFallbackMovies())
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Exception fetching fresh movies", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get fallback data when API fails
     */
    fun getFallbackMovies(): List<ActivityContent> {
        android.util.Log.w("MovieRepository", "Using fallback movie data")
        return listOf(
            ActivityContent(
                id = "fallback_movie_1",
                title = "The Shawshank Redemption",
                description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                imageUrl = null,
                category = "movies",
                metadata = mapOf(
                    "release_date" to "1994-09-23",
                    "vote_average" to "8.7",
                    "genre" to "Drama"
                )
            ),
            ActivityContent(
                id = "fallback_movie_2",
                title = "The Godfather",
                description = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                imageUrl = null,
                category = "movies",
                metadata = mapOf(
                    "release_date" to "1972-03-24",
                    "vote_average" to "8.7",
                    "genre" to "Crime, Drama"
                )
            )
        )
    }
}
