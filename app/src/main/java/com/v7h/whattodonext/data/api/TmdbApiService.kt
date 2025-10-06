package com.v7h.whattodonext.data.api

import com.v7h.whattodonext.data.model.TmdbConfiguration
import com.v7h.whattodonext.data.model.TmdbGenresResponse
import com.v7h.whattodonext.data.model.TmdbMovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * TMDB API Service Interface
 * 
 * Based on The Movie Database API documentation:
 * https://developer.themoviedb.org/reference/configuration-details
 * 
 * Provides endpoints for:
 * - Movie discovery and search
 * - Genre information
 * - Configuration details
 * 
 * Applied Rules: Debug logs, comments, proper API structure
 */
interface TmdbApiService {
    
    /**
     * Get TMDB configuration details
     * Required for building proper image URLs
     */
    @GET("configuration")
    suspend fun getConfiguration(
        @Query("api_key") apiKey: String
    ): Response<TmdbConfiguration>
    
    /**
     * Get popular movies
     * Used as default content for the movie activity
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): Response<TmdbMovieResponse>
    
    /**
     * Get top rated movies
     * Alternative content source for high-quality recommendations
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): Response<TmdbMovieResponse>
    
    /**
     * Get now playing movies
     * Current releases for fresh content
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): Response<TmdbMovieResponse>
    
    /**
     * Discover movies with filters
     * Allows filtering by genre, year, rating, etc.
     */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("with_genres") withGenres: String? = null,
        @Query("year") year: Int? = null,
        @Query("vote_average.gte") minVoteAverage: Double? = null,
        @Query("vote_average.lte") maxVoteAverage: Double? = null
    ): Response<TmdbMovieResponse>
    
    /**
     * Search movies by query
     * For future search functionality
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false
    ): Response<TmdbMovieResponse>
    
    /**
     * Get movie genres
     * For filtering and categorization
     */
    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<TmdbGenresResponse>
}

/**
 * TMDB API configuration
 */
object TmdbApiConfig {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    
    // Image sizes for different use cases
    const val POSTER_SIZE_SMALL = "w185"
    const val POSTER_SIZE_MEDIUM = "w342"
    const val POSTER_SIZE_LARGE = "w500"
    const val POSTER_SIZE_ORIGINAL = "original"
    
    const val BACKDROP_SIZE_SMALL = "w300"
    const val BACKDROP_SIZE_MEDIUM = "w780"
    const val BACKDROP_SIZE_LARGE = "w1280"
    const val BACKDROP_SIZE_ORIGINAL = "original"
    
    // Debug helper
    fun logConfig() {
        android.util.Log.d("TmdbApiConfig", "Base URL: $BASE_URL, Image URL: $IMAGE_BASE_URL")
    }
}
