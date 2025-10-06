package com.v7h.whattodonext.presentation.ui.screen.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.presentation.ui.components.SwipeableCard
import com.v7h.whattodonext.data.model.SavedChoice
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
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
    
    // Activity options (focusing on Movies for Step 4)
    val activities = listOf(
        "Movies",
        "Outdoor activities",
        "Restaurants", 
        "Books",
        "Travel destinations"
    )
    
    // Movie data state
    var currentMovies by remember { mutableStateOf<List<ActivityContent>>(emptyList()) }
    var currentMovieIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Fetch movies when screen loads or activity changes
    LaunchedEffect(selectedActivity) {
        if (selectedActivity == "Movies") {
            isLoading = true
            errorMessage = null
            
            try {
                // Initialize repository if needed
                movieRepository.initialize()
                
                // Get user preferences for movies if available
                val userPreferences = userProfileRepository?.getCurrentProfile()?.activityPreferences?.get("movies")
                val preferencesMap = userPreferences?.filters ?: emptyMap()
                
                // Debug log for user preferences
                android.util.Log.d("DeckScreen", "Using user preferences for movies: $preferencesMap")
                
                // Try to fetch movies with user preferences
                val result = if (preferencesMap.isNotEmpty()) {
                    movieRepository.fetchMoviesForUser(preferencesMap)
                } else {
                    // Fallback to popular movies if no preferences
                    movieRepository.fetchPopularMovies()
                }
                
                result.fold(
                    onSuccess = { movies ->
                        currentMovies = movies
                        currentMovieIndex = 0
                        isLoading = false
                        android.util.Log.d("DeckScreen", "Loaded ${movies.size} movies from TMDB with user preferences")
                    },
                    onFailure = { error ->
                        android.util.Log.e("DeckScreen", "Failed to load movies with preferences", error)
                        currentMovies = movieRepository.getFallbackMovies()
                        currentMovieIndex = 0
                        isLoading = false
                        errorMessage = "Using offline data"
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("DeckScreen", "Exception loading movies", e)
                currentMovies = movieRepository.getFallbackMovies()
                currentMovieIndex = 0
                isLoading = false
                errorMessage = "Network error - using offline data"
            }
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
            // Fallback for movies
            mapOf(
                "id" to "fallback_movie_1",
                "title" to "Loading movies...",
                "description" to "Fetching the latest movies from TMDB",
                "imageUrl" to null,
                "activityType" to "Movies"
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
    
    // Function to load next card
    fun loadNextCard() {
        if (selectedActivity == "Movies" && currentMovies.isNotEmpty()) {
            if (currentMovieIndex < currentMovies.size - 1) {
                currentMovieIndex++
                android.util.Log.d("DeckScreen", "Loaded next movie: ${currentMovieIndex + 1}/${currentMovies.size}")
            } else {
                // Load more movies if we're at the end
                scope.launch {
                    val result = movieRepository.fetchPopularMovies(page = 2)
                    result.fold(
                        onSuccess = { newMovies ->
                            currentMovies = currentMovies + newMovies
                            currentMovieIndex++
                            android.util.Log.d("DeckScreen", "Loaded more movies, total: ${currentMovies.size}")
                        },
                        onFailure = { 
                            android.util.Log.w("DeckScreen", "Failed to load more movies, cycling back to start")
                            currentMovieIndex = 0
                        }
                    )
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
        // Top header with settings icon and activity dropdown side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity Selector Dropdown (F-002) - moved to top and made smaller
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
                        text = "Loading movies from TMDB...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Swipeable Card (F-003) - Step 4 implementation with TMDB data
        SwipeableCard(
            imageUrl = (currentCard["imageUrl"] as? String) ?: "",
            title = (currentCard["title"] as? String) ?: "",
            description = (currentCard["description"] as? String) ?: "",
            onCardClick = {
                android.util.Log.d("DeckScreen", "Card tapped - navigating to detail")
                onNavigateToDetail((currentCard["id"] as? String) ?: "")
            },
            onSwipeLeft = {
                android.util.Log.d("DeckScreen", "Card declined via swipe left")
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
            }
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
