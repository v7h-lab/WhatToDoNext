package com.v7h.whattodonext.data.repository

import com.v7h.whattodonext.data.api.TmdbApiService
import com.v7h.whattodonext.data.api.TmdbApiConfig
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.model.TmdbConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
     */
    suspend fun initialize() {
        try {
            // Debug log for initialization
            android.util.Log.d("MovieRepository", "Initializing TMDB configuration...")
            
            val response = tmdbApiService.getConfiguration(apiKey)
            if (response.isSuccessful) {
                tmdbConfiguration = response.body()
                tmdbConfiguration?.logConfiguration()
                android.util.Log.d("MovieRepository", "TMDB configuration loaded successfully")
            } else {
                android.util.Log.e("MovieRepository", "Failed to load TMDB configuration: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieRepository", "Error initializing TMDB configuration", e)
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
     */
    suspend fun fetchMoviesForUser(userPreferences: Map<String, Any>): Result<List<ActivityContent>> {
        return try {
            // Debug log for user preference filtering
            android.util.Log.d("MovieRepository", "Fetching movies with user preferences: $userPreferences")
            
            // Extract genre preferences
            val preferredGenres = userPreferences["genres"] as? List<String> ?: emptyList()
            val minRating = userPreferences["min_rating"] as? Double ?: 6.0
            val maxRating = userPreferences["max_rating"] as? Double ?: 10.0
            val year = userPreferences["year"] as? Int
            
            // Convert genre names to TMDB genre IDs
            val genreIds = if (preferredGenres.isNotEmpty()) {
                getGenreIdsForNames(preferredGenres)
            } else null
            
            // Use discover endpoint with user preferences
            val response = tmdbApiService.discoverMovies(
                apiKey = apiKey,
                withGenres = genreIds?.joinToString(","),
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
                    android.util.Log.d("MovieRepository", "Successfully fetched ${activityContent.size} movies with user preferences")
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
     * This would typically fetch from TMDB genres API, but for now we'll use a mapping
     */
    private fun getGenreIdsForNames(genreNames: List<String>): List<Int> {
        // TMDB genre mapping (simplified - in production, fetch from API)
        val genreMapping = mapOf(
            "Action" to 28,
            "Adventure" to 12,
            "Animation" to 16,
            "Comedy" to 35,
            "Crime" to 80,
            "Documentary" to 99,
            "Drama" to 18,
            "Family" to 10751,
            "Fantasy" to 14,
            "History" to 36,
            "Horror" to 27,
            "Music" to 10402,
            "Mystery" to 9648,
            "Romance" to 10749,
            "Science Fiction" to 878,
            "TV Movie" to 10770,
            "Thriller" to 53,
            "War" to 10752,
            "Western" to 37
        )
        
        val genreIds = genreNames.mapNotNull { genreName ->
            genreMapping[genreName]
        }
        
        // Debug log for genre mapping
        android.util.Log.d("MovieRepository", "Mapped genres: $genreNames -> $genreIds")
        
        return genreIds
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
