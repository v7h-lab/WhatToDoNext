package com.v7h.whattodonext.presentation.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Detail Screen - Manages movie detail state
 * 
 * Applied Rules: 
 * - State Management: ViewModel with StateFlow
 * - Unidirectional Data Flow: State flows down, events flow up
 * - Debug logs & comments
 * 
 * Uses SavedStateHandle to extract contentId from navigation arguments
 * and fetches the corresponding movie data from the repository.
 * 
 * Note: fetchMovieDetails must be called explicitly from the UI with the contentId
 * to ensure fresh data is loaded for each navigation.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Extract contentId from navigation arguments via SavedStateHandle (for reference)
    private val navigationContentId: String = savedStateHandle.get<String>(Screen.Params.ACTIVITY_CONTENT_ID) ?: ""
    
    // UI State exposed as StateFlow
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    
    init {
        // Debug log for ViewModel initialization
        android.util.Log.d("DetailViewModel", "DetailViewModel initialized with navigation contentId: $navigationContentId")
    }
    
    /**
     * Fetch movie details for the given contentId
     * This should be called from the UI with the actual contentId parameter
     * to ensure fresh data is loaded every time we navigate to detail screen
     */
    fun fetchMovieDetails(contentId: String) {
        // Set loading state immediately
        _uiState.value = DetailUiState.Loading
        android.util.Log.d("DetailViewModel", "Fetching movie details for: $contentId")
        
        viewModelScope.launch {
            try {
                if (contentId.startsWith("movie_")) {
                    // Initialize repository if needed
                    movieRepository.initialize()
                    
                    // Search for the movie in all cached categories
                    val allMovies = movieRepository.popularMovies.value + 
                                   movieRepository.topRatedMovies.value + 
                                   movieRepository.nowPlayingMovies.value
                    
                    val foundMovie = allMovies.find { it.id == contentId }
                    
                    if (foundMovie != null) {
                        // Movie found in cache
                        _uiState.value = DetailUiState.Success(foundMovie)
                        android.util.Log.d("DetailViewModel", "Movie found in cache: ${foundMovie.title}")
                    } else {
                        // Movie not in cache, fetch from API
                        android.util.Log.w("DetailViewModel", "Movie not found in cache, fetching from API...")
                        
                        val result = movieRepository.fetchPopularMovies()
                        result.fold(
                            onSuccess = { movies ->
                                val movie = movies.find { it.id == contentId }
                                if (movie != null) {
                                    _uiState.value = DetailUiState.Success(movie)
                                    android.util.Log.d("DetailViewModel", "Movie found via API: ${movie.title}")
                                } else {
                                    _uiState.value = DetailUiState.Error("Movie not found")
                                    android.util.Log.e("DetailViewModel", "Movie not found: $contentId")
                                }
                            },
                            onFailure = { error ->
                                _uiState.value = DetailUiState.Error("Failed to load movie: ${error.message}")
                                android.util.Log.e("DetailViewModel", "Failed to fetch movie", error)
                            }
                        )
                    }
                } else {
                    // Non-movie content - use fallback
                    _uiState.value = DetailUiState.Success(null)
                    android.util.Log.d("DetailViewModel", "Non-movie content: $contentId")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error("Error loading details: ${e.message}")
                android.util.Log.e("DetailViewModel", "Exception loading movie details", e)
            }
        }
    }
    
    /**
     * Retry fetching movie details (for error states)
     * Requires the contentId to be passed again
     */
    fun retry(contentId: String) {
        android.util.Log.d("DetailViewModel", "Retrying movie details fetch for: $contentId")
        fetchMovieDetails(contentId)
    }
}

/**
 * UI State for Detail Screen
 * Sealed class for type-safe state management
 */
sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val movie: ActivityContent?) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
