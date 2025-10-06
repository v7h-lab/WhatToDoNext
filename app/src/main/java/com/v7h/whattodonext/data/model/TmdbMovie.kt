package com.v7h.whattodonext.data.model

import com.google.gson.annotations.SerializedName

/**
 * TMDB Movie API response models
 * 
 * Based on The Movie Database API documentation:
 * https://developer.themoviedb.org/reference/configuration-details
 * 
 * Applied Rules: Debug logs, comments, proper data structure
 */

/**
 * Main movie response from TMDB API
 */
data class TmdbMovieResponse(
    val page: Int,
    val results: List<TmdbMovie>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) {
    // Debug helper
    fun logResponse() {
        android.util.Log.d("TmdbMovieResponse", "Page $page: ${results.size} movies, Total: $totalResults")
    }
}

/**
 * Individual movie data from TMDB
 */
data class TmdbMovie(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
) {
    // Debug helper
    fun logMovie() {
        android.util.Log.d("TmdbMovie", "Movie: $title ($releaseDate) - Rating: $voteAverage")
    }
    
    // Convert to our ActivityContent format
    fun toActivityContent(): ActivityContent {
        return ActivityContent(
            id = "movie_${id}",
            title = title,
            description = overview,
            imageUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            category = "movies",
            metadata = mapOf(
                "release_date" to releaseDate,
                "vote_average" to voteAverage.toString(),
                "vote_count" to voteCount.toString(),
                "original_language" to originalLanguage,
                "popularity" to popularity.toString(),
                "genre_ids" to genreIds.joinToString(",")
            )
        )
    }
}

/**
 * TMDB Genre mapping
 */
data class TmdbGenre(
    val id: Int,
    val name: String
) {
    // Debug helper
    fun logGenre() {
        android.util.Log.d("TmdbGenre", "Genre: $name (ID: $id)")
    }
}

/**
 * TMDB Genres response
 */
data class TmdbGenresResponse(
    val genres: List<TmdbGenre>
) {
    // Debug helper
    fun logGenres() {
        android.util.Log.d("TmdbGenresResponse", "Available genres: ${genres.map { it.name }}")
    }
}

/**
 * TMDB Configuration for image URLs
 */
data class TmdbConfiguration(
    val images: TmdbImages,
    @SerializedName("change_keys")
    val changeKeys: List<String>
) {
    // Debug helper
    fun logConfiguration() {
        android.util.Log.d("TmdbConfiguration", "Base URL: ${images.secureBaseUrl}")
    }
}

data class TmdbImages(
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String,
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>,
    @SerializedName("logo_sizes")
    val logoSizes: List<String>,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>,
    @SerializedName("profile_sizes")
    val profileSizes: List<String>,
    @SerializedName("still_sizes")
    val stillSizes: List<String>
)
