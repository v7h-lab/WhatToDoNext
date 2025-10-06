package com.v7h.whattodonext.presentation.ui.screen.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.presentation.ui.components.SwipeableCard
import com.v7h.whattodonext.data.model.SavedChoice
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.model.UserProfile
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.repository.DismissedMovieRepository
import com.v7h.whattodonext.di.NetworkModule
import kotlinx.coroutines.launch

/**
 * S-002: Main Deck Screen - Primary interface for card swiping
 * 
 * Contains:
 * - F-002: Activity Selector (dropdown) - positioned at top with settings icon
 * - F-003: Swipeable Card Deck (main card interface) - larger image for better visual impact
 * 
 * Layout changes:
 * - Removed "What to do?" title to maximize card space
 * - Activity dropdown and settings icon positioned side by side at top
 * - Increased card image size to use extra space from removed title area
 * - Three action buttons (Decline/Save/Accept)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    onNavigateToDetail: (String) -> Unit = {},
    savedChoiceRepository: SavedChoiceRepository = remember { SavedChoiceRepository() },
    movieRepository: MovieRepository = remember { NetworkModule.movieRepository },
    userProfileRepository: UserProfileRepository? = null
) {
    // Get context for DismissedMovieRepository
    val context = LocalContext.current
    
    // Initialize dismissed movie repository
    val dismissedMovieRepository = remember { DismissedMovieRepository(context) }
    
    // Coroutine scope for API calls
    val scope = rememberCoroutineScope()
    
    // Debug log for screen initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("DeckScreen", "Main deck screen loaded with TMDB integration")
        android.util.Log.d("DeckScreen", "API key status: ${NetworkModule.getApiKeyStatus()}")
    }
    
    // Local state for activity selector
    var selectedActivity by remember { mutableStateOf("Movies") }
    var expanded by remember { mutableStateOf(false) }
    
    // Get user's selected activities from profile
    val userProfile by userProfileRepository?.userProfile?.collectAsState() ?: remember { mutableStateOf(UserProfile()) }
    val userSelectedActivities = userProfile.selectedActivities.map { it.name }
    
    // Activity options - use user's selected activities, fallback to Movies if none selected
    val activities = if (userSelectedActivities.isNotEmpty()) {
        userSelectedActivities
    } else {
        listOf("Movies") // Fallback to Movies if no activities selected
    }
    
    // Set default selected activity to first available activity
    LaunchedEffect(activities) {
        if (activities.isNotEmpty() && selectedActivity !in activities) {
            selectedActivity = activities.first()
            android.util.Log.d("DeckScreen", "Set default activity to: $selectedActivity")
        }
    }
    
    // Movie data state - use rememberSaveable to persist across navigation
    var currentMovies by rememberSaveable { mutableStateOf<List<ActivityContent>>(emptyList()) }
    var currentMovieIndex by rememberSaveable { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Dismissed movies state
    val dismissedMovies by dismissedMovieRepository.dismissedMovies.collectAsState()
    val dismissedCount by dismissedMovieRepository.dismissedCount.collectAsState()
    
    // Track if we need to refresh movies when dismissed movies change
    var shouldRefreshOnDismissedChange by remember { mutableStateOf(false) }
    
    // Track if movies have been loaded to prevent refetching on navigation return
    // Use rememberSaveable to persist this state across navigation
    var moviesLoaded by rememberSaveable { mutableStateOf(false) }
    
    // Track previous activity to detect actual activity changes
    var previousActivity by rememberSaveable { mutableStateOf(selectedActivity) }
    
    // Track navigation state to prevent unnecessary refetching
    var hasNavigatedToDetail by rememberSaveable { mutableStateOf(false) }
    
    // Debug log for initial state after variable declarations
    LaunchedEffect(Unit) {
        android.util.Log.d("DeckScreen", "Initial state - moviesLoaded: $moviesLoaded, currentMovies: ${currentMovies.size}, currentIndex: $currentMovieIndex")
    }
    
    // Reset movies loaded flag when activity actually changes (not on navigation return)
    LaunchedEffect(selectedActivity) {
        if (selectedActivity != previousActivity) {
            android.util.Log.d("DeckScreen", "Activity changed from $previousActivity to $selectedActivity - resetting movies loaded flag")
            moviesLoaded = false
            previousActivity = selectedActivity
            hasNavigatedToDetail = false // Reset navigation tracking on activity change
        }
    }
    
    // Fetch fresh movies only when screen loads for the first time or activity changes
    // Skip refetching if returning from detail screen with same activity
    LaunchedEffect(selectedActivity, hasNavigatedToDetail) {
        if (selectedActivity == "Movies" && !moviesLoaded) {
            android.util.Log.d("DeckScreen", "Loading movies for the first time or activity changed")
            android.util.Log.d("DeckScreen", "Navigation state: hasNavigatedToDetail=$hasNavigatedToDetail, moviesLoaded=$moviesLoaded")
            isLoading = true
            errorMessage = null
            
            try {
                // Initialize repository if needed
                movieRepository.initialize()
                
                // Get user preferences for movies if available
                val userPreferences = userProfileRepository?.getCurrentProfile()?.activityPreferences?.get("movies")
                val preferencesMap = userPreferences?.filters ?: emptyMap()
                
                // Debug log for user preferences and dismissed movies
                android.util.Log.d("DeckScreen", "Using user preferences for movies: $preferencesMap")
                android.util.Log.d("DeckScreen", "Filtering out ${dismissedMovies.size} dismissed movies")
                
                // Try to fetch fresh movies with user preferences and dismissed filtering
                val result = if (preferencesMap.isNotEmpty()) {
                    // First fetch with user preferences, then filter and shuffle
                    movieRepository.fetchMoviesForUser(preferencesMap).map { movies ->
                        movieRepository.filterDismissedMovies(movies, dismissedMovies)
                            .let { movieRepository.shuffleMovies(it) }
                    }
                } else {
                    // Fetch fresh movies from multiple categories, filter dismissed, and shuffle
                    movieRepository.fetchFreshMovies(dismissedMovies)
                }
                
                result.fold(
                    onSuccess = { movies ->
                        // Remove duplicates by ID to prevent key conflicts
                        val uniqueMovies = movies.distinctBy { it.id }
                        
                        if (uniqueMovies.isEmpty()) {
                            // All movies filtered out - reset dismissed movies and try again
                            android.util.Log.w("DeckScreen", "All movies filtered out (${dismissedMovies.size} dismissed). Resetting dismissed movies and trying again...")
                            dismissedMovieRepository.clearAllDismissedMovies()
                            
                            // Try to fetch movies again without dismissed filtering
                            val retryResult = if (preferencesMap.isNotEmpty()) {
                                movieRepository.fetchMoviesForUser(preferencesMap).map { retryMovies ->
                                    movieRepository.shuffleMovies(retryMovies)
                                }
                            } else {
                                movieRepository.fetchFreshMovies(emptySet()) // No dismissed movies to filter
                            }
                            
                            retryResult.fold(
                                onSuccess = { retryMovies ->
                                    val retryUniqueMovies = retryMovies.distinctBy { it.id }
                                    currentMovies = retryUniqueMovies
                                    currentMovieIndex = 0
                                    isLoading = false
                                    moviesLoaded = true
                                    android.util.Log.d("DeckScreen", "Retry successful: Loaded ${retryUniqueMovies.size} movies after resetting dismissed list")
                                },
                                onFailure = { retryError ->
                                    android.util.Log.e("DeckScreen", "Retry failed", retryError)
                                    // Use fallback movies
                                    val fallbackMovies = movieRepository.getFallbackMovies()
                                    currentMovies = fallbackMovies.distinctBy { it.id }
                                    currentMovieIndex = 0
                                    isLoading = false
                                    moviesLoaded = true
                                    errorMessage = "No movies available - using sample data"
                                }
                            )
                        } else {
                            currentMovies = uniqueMovies
                            currentMovieIndex = 0
                            isLoading = false
                            moviesLoaded = true
                            android.util.Log.d("DeckScreen", "Loaded ${uniqueMovies.size} unique fresh movies (filtered ${dismissedMovies.size} dismissed)")
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.e("DeckScreen", "Failed to load fresh movies", error)
                        // Use fallback movies but still filter dismissed ones
                        val fallbackMovies = movieRepository.getFallbackMovies()
                        val filteredFallback = movieRepository.filterDismissedMovies(fallbackMovies, dismissedMovies)
                            .let { movieRepository.shuffleMovies(it) }
                        currentMovies = filteredFallback.distinctBy { it.id } // Remove duplicates
                        currentMovieIndex = 0
                        isLoading = false
                        moviesLoaded = true
                        errorMessage = "Using offline data"
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("DeckScreen", "Exception loading fresh movies", e)
                // Use fallback movies but still filter dismissed ones
                val fallbackMovies = movieRepository.getFallbackMovies()
                val filteredFallback = movieRepository.filterDismissedMovies(fallbackMovies, dismissedMovies)
                    .let { movieRepository.shuffleMovies(it) }
                currentMovies = filteredFallback.distinctBy { it.id } // Remove duplicates
                currentMovieIndex = 0
                isLoading = false
                moviesLoaded = true
                errorMessage = "Network error - using offline data"
            }
        } else if (selectedActivity == "Movies" && moviesLoaded) {
            android.util.Log.d("DeckScreen", "Movies already loaded, preserving current state for navigation")
        }
    }
    
    // Handle returning from detail screen - preserve current state
    LaunchedEffect(hasNavigatedToDetail) {
        if (hasNavigatedToDetail && selectedActivity == "Movies" && moviesLoaded && currentMovies.isNotEmpty()) {
            android.util.Log.d("DeckScreen", "Returned from detail screen - preserving current movie state")
            android.util.Log.d("DeckScreen", "Current movie index: $currentMovieIndex, total movies: ${currentMovies.size}")
            // Reset navigation tracking for next navigation
            hasNavigatedToDetail = false
        }
    }
    
    // Handle dismissed movies changes only when actively dismissing (not when returning from detail)
    LaunchedEffect(dismissedMovies, shouldRefreshOnDismissedChange) {
        if (shouldRefreshOnDismissedChange && selectedActivity == "Movies" && currentMovies.isNotEmpty()) {
            android.util.Log.d("DeckScreen", "Refreshing movies due to dismissed change")
            
            // Filter current movies to remove dismissed ones
            val filteredMovies = movieRepository.filterDismissedMovies(currentMovies, dismissedMovies)
            val shuffledMovies = movieRepository.shuffleMovies(filteredMovies)
            
            // Update current movies and adjust index if needed
            currentMovies = shuffledMovies
            if (currentMovieIndex >= shuffledMovies.size) {
                currentMovieIndex = 0
            }
            
            // Reset the flag
            shouldRefreshOnDismissedChange = false
            
            android.util.Log.d("DeckScreen", "Refreshed movies after dismissing, now showing ${shuffledMovies.size} movies")
        }
    }
    
    // Get current card data
    val currentCard = remember(currentMovies, currentMovieIndex, selectedActivity) {
        if (selectedActivity == "Movies" && currentMovies.isNotEmpty() && currentMovieIndex < currentMovies.size) {
            val movie = currentMovies[currentMovieIndex]
            mapOf(
                "id" to movie.id,
                "title" to movie.title,
                "description" to movie.description,
                "imageUrl" to movie.imageUrl,
                "activityType" to "Movies",
                "metadata" to movie.metadata
            )
        } else if (selectedActivity == "Movies") {
            // Fallback for movies with sample metadata
            mapOf(
                "id" to "fallback_movie_1",
                "title" to "Loading movies...",
                "description" to "Fetching the latest movies from TMDB",
                "imageUrl" to null,
                "activityType" to "Movies",
                "metadata" to mapOf(
                    "vote_average" to "8.5",
                    "genre" to "Drama"
                )
            )
        } else {
            // Fallback for other activities (Step 2 placeholder data)
            mapOf(
                "id" to "park_activity_001",
                "title" to "Go to a local park",
                "description" to "Enjoy the outdoors and fresh air. Pack a picnic or just relax.",
                "imageUrl" to "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop",
                "activityType" to selectedActivity
            )
        }
    }
    
    // Proactive movie loading when only 5 movies are left
    fun loadMoreMoviesProactively() {
        scope.launch {
            try {
                android.util.Log.d("DeckScreen", "Proactively loading more movies...")
                
                // Get user preferences
                val userPreferences = userProfileRepository?.getCurrentProfile()?.activityPreferences?.get("movies")
                val preferencesMap = userPreferences?.filters ?: emptyMap()
                
                // Fetch fresh movies with dismissed filtering
                val result = if (preferencesMap.isNotEmpty()) {
                    movieRepository.fetchMoviesForUser(preferencesMap).map { movies ->
                        movieRepository.filterDismissedMovies(movies, dismissedMovies)
                            .let { movieRepository.shuffleMovies(it) }
                    }
                } else {
                    movieRepository.fetchFreshMovies(dismissedMovies)
                }
                
                result.fold(
                    onSuccess = { newMovies ->
                        // Remove duplicates by ID and append to current movies
                        val existingIds = currentMovies.map { it.id }.toSet()
                        val uniqueNewMovies = newMovies.filter { it.id !in existingIds }
                        
                        if (uniqueNewMovies.isNotEmpty()) {
                            currentMovies = currentMovies + uniqueNewMovies
                            android.util.Log.d("DeckScreen", "Proactively loaded ${uniqueNewMovies.size} unique movies (total: ${currentMovies.size})")
                        } else {
                            android.util.Log.d("DeckScreen", "No new unique movies found")
                        }
                    },
                    onFailure = { error ->
                        android.util.Log.w("DeckScreen", "Failed to proactively load movies", error)
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("DeckScreen", "Exception in proactive movie loading", e)
            }
        }
    }
    
    // Load more movies when reaching the end
    suspend fun loadMoreMovies() {
        try {
            // Get user preferences
            val userPreferences = userProfileRepository?.getCurrentProfile()?.activityPreferences?.get("movies")
            val preferencesMap = userPreferences?.filters ?: emptyMap()
            
            // Fetch fresh movies with dismissed filtering
            val result = if (preferencesMap.isNotEmpty()) {
                movieRepository.fetchMoviesForUser(preferencesMap).map { movies ->
                    movieRepository.filterDismissedMovies(movies, dismissedMovies)
                        .let { movieRepository.shuffleMovies(it) }
                }
            } else {
                movieRepository.fetchFreshMovies(dismissedMovies)
            }
            
            result.fold(
                onSuccess = { newMovies ->
                    // Remove duplicates by ID and replace current movies
                    val existingIds = currentMovies.map { it.id }.toSet()
                    val uniqueNewMovies = newMovies.filter { it.id !in existingIds }
                    
                    if (uniqueNewMovies.isNotEmpty()) {
                        currentMovies = uniqueNewMovies
                        currentMovieIndex = 0 // Start from beginning of fresh list
                        android.util.Log.d("DeckScreen", "Loaded ${uniqueNewMovies.size} unique fresh movies")
                    } else {
                        android.util.Log.w("DeckScreen", "No unique movies found, cycling back to start")
                        currentMovieIndex = 0
                    }
                },
                onFailure = { 
                    android.util.Log.w("DeckScreen", "Failed to load fresh movies, cycling back to start")
                    currentMovieIndex = 0
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("DeckScreen", "Exception loading fresh movies", e)
            currentMovieIndex = 0
        }
    }
    
    // Function to load next card with fresh data fetching
    fun loadNextCard() {
        if (selectedActivity == "Movies" && currentMovies.isNotEmpty()) {
            if (currentMovieIndex < currentMovies.size - 1) {
                currentMovieIndex++
                android.util.Log.d("DeckScreen", "Loaded next movie: ${currentMovieIndex + 1}/${currentMovies.size}")
                
                // Proactive loading: fetch more movies when only 5 are left
                val remainingMovies = currentMovies.size - currentMovieIndex - 1
                if (remainingMovies <= 5) {
                    android.util.Log.d("DeckScreen", "Only $remainingMovies movies left, proactively loading more...")
                    loadMoreMoviesProactively()
                }
            } else {
                // Fetch fresh movies when we reach the end
                scope.launch {
                    android.util.Log.d("DeckScreen", "Reached end of current movies, fetching fresh data...")
                    loadMoreMovies()
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp) // Minimal padding, let Scaffold handle the rest
    ) {
        // Top header with settings icon and activity selector side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity Selector (F-002) - show dropdown only if multiple activities, otherwise show text
            if (activities.size > 1) {
                // Multiple activities - show dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedActivity,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { 
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Dropdown arrow"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clip(CardShape),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = CardShape
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        activities.forEach { activity ->
                            DropdownMenuItem(
                                text = { Text(activity) },
                                onClick = {
                                    selectedActivity = activity
                                    expanded = false
                                    android.util.Log.d("DeckScreen", "Activity selected: $activity")
                                }
                            )
                        }
                    }
                }
            } else {
                // Single activity - show as text without dropdown
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CardShape),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = selectedActivity,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Settings icon - positioned next to dropdown
            IconButton(onClick = { 
                android.util.Log.d("DeckScreen", "Settings icon tapped")
                // TODO: Navigate to settings in later steps
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp)) // Minimal spacing to maximize card space
        
        // Error message display
        errorMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Loading indicator
        if (isLoading && selectedActivity == "Movies") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading fresh movies from TMDB...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Show dismissed count for debugging
                    if (dismissedCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Filtered out $dismissedCount dismissed movies",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Swipeable Card (F-003) - Step 4 implementation with TMDB data and movie metadata
        SwipeableCard(
            imageUrl = (currentCard["imageUrl"] as? String) ?: "",
            title = (currentCard["title"] as? String) ?: "",
            description = (currentCard["description"] as? String) ?: "",
            onCardClick = {
                val movieId = (currentCard["id"] as? String) ?: ""
                val movieTitle = (currentCard["title"] as? String) ?: ""
                android.util.Log.d("DeckScreen", "Card tapped - navigating to detail: $movieTitle (ID: $movieId)")
                android.util.Log.d("DeckScreen", "Current movie index: $currentMovieIndex, total movies: ${currentMovies.size}")
                // Track navigation state to preserve movie list on return
                hasNavigatedToDetail = true
                onNavigateToDetail(movieId)
            },
            onSwipeLeft = {
                android.util.Log.d("DeckScreen", "Card declined via swipe left")
                // Track dismissed movie
                val currentMovieId = (currentCard["id"] as? String)
                if (currentMovieId != null && selectedActivity == "Movies") {
                    dismissedMovieRepository.addDismissedMovie(currentMovieId)
                    android.util.Log.d("DeckScreen", "Added dismissed movie: $currentMovieId")
                    // Set flag to refresh movies after dismissing
                    shouldRefreshOnDismissedChange = true
                }
                loadNextCard()
            },
            onSwipeRight = {
                android.util.Log.d("DeckScreen", "Card accepted via swipe right")
                // Add to saved choices
                val savedChoice = SavedChoice.create(
                    id = (currentCard["id"] as? String) ?: "",
                    title = (currentCard["title"] as? String) ?: "",
                    description = (currentCard["description"] as? String) ?: "",
                    imageUrl = (currentCard["imageUrl"] as? String) ?: "",
                    activityType = (currentCard["activityType"] as? String) ?: ""
                )
                savedChoiceRepository.addSavedChoice(savedChoice)
                loadNextCard()
            },
            onSave = {
                android.util.Log.d("DeckScreen", "Card saved via button")
                // Add to saved choices
                val savedChoice = SavedChoice.create(
                    id = (currentCard["id"] as? String) ?: "",
                    title = (currentCard["title"] as? String) ?: "",
                    description = (currentCard["description"] as? String) ?: "",
                    imageUrl = (currentCard["imageUrl"] as? String) ?: "",
                    activityType = (currentCard["activityType"] as? String) ?: ""
                )
                savedChoiceRepository.addSavedChoice(savedChoice)
            },
            // Movie metadata for enhanced display
            movieRating = (currentCard["metadata"] as? Map<String, String>)?.get("vote_average"),
            movieGenre = (currentCard["metadata"] as? Map<String, String>)?.get("genre"),
            activityType = (currentCard["activityType"] as? String)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun DeckScreenPreview() {
    WhatToDoNextTheme {
        DeckScreen()
    }
}
