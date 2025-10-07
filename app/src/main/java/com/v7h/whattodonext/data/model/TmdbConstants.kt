package com.v7h.whattodonext.data.model

/**
 * TMDB Constants - Genres and Languages
 * 
 * This file contains predefined constants for TMDB API filtering
 * to ensure only valid values are used in API calls.
 * 
 * Applied Rules: Debug logs, comments, organized constants
 */

/**
 * TMDB Movie Genres
 * These IDs match the official TMDB genre list
 */
object TmdbGenres {
    val GENRES = mapOf(
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
    
    // Get genre ID from name
    fun getGenreId(genreName: String): Int? = GENRES[genreName]
    
    // Get genre IDs from names
    fun getGenreIds(genreNames: List<String>): List<Int> {
        return genreNames.mapNotNull { getGenreId(it) }
    }
    
    // Get all genre names
    fun getAllGenreNames(): List<String> = GENRES.keys.toList()
}

/**
 * TMDB Supported Languages
 * These language codes are valid for TMDB API filtering
 * Each entry is a pair of (code, display name)
 */
object TmdbLanguages {
    val LANGUAGES = listOf(
        "en" to "English",
        "es" to "Spanish",
        "fr" to "French",
        "de" to "German",
        "hi" to "Hindi",
        "ja" to "Japanese",
        "ko" to "Korean",
        "zh" to "Chinese",
        "pt" to "Portuguese",
        "it" to "Italian",
        "ru" to "Russian",
        "ar" to "Arabic",
        "nl" to "Dutch",
        "pl" to "Polish",
        "tr" to "Turkish",
        "sv" to "Swedish",
        "da" to "Danish",
        "fi" to "Finnish",
        "no" to "Norwegian",
        "th" to "Thai",
        "id" to "Indonesian",
        "vi" to "Vietnamese",
        "he" to "Hebrew",
        "el" to "Greek"
    )
    
    // Get language display name from code
    fun getLanguageName(code: String): String? {
        return LANGUAGES.find { it.first == code }?.second
    }
    
    // Get all language codes
    fun getAllLanguageCodes(): List<String> = LANGUAGES.map { it.first }
    
    // Get all language display names
    fun getAllLanguageNames(): List<String> = LANGUAGES.map { it.second }
}

