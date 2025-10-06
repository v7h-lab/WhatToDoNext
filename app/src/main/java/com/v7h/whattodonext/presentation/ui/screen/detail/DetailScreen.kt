package com.v7h.whattodonext.presentation.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.data.repository.MovieRepository
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.di.NetworkModule
import kotlinx.coroutines.launch

/**
 * S-003: Detail Screen - Expanded view of activity content
 * 
 * Contains:
 * - F-004: Detail Screen View (extended activity-specific info)
 * 
 * This screen shows detailed information about the selected activity,
 * such as extended descriptions, additional images, location details, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    contentId: String,
    onNavigateBack: () -> Unit,
    movieRepository: MovieRepository = remember { NetworkModule.movieRepository },
    modifier: Modifier = Modifier
) {
    // Coroutine scope for API calls
    val scope = rememberCoroutineScope()
    
    // State for movie data
    var movieData by remember { mutableStateOf<ActivityContent?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(contentId) {
        android.util.Log.d("DetailScreen", "Detail screen loaded for content: $contentId")
        
        if (contentId.startsWith("movie_")) {
            isLoading = true
            errorMessage = null
            
            try {
                // Initialize repository if needed
                movieRepository.initialize()
                
                // Try to find the movie in the cached data
                val allMovies = movieRepository.popularMovies.value + 
                               movieRepository.topRatedMovies.value + 
                               movieRepository.nowPlayingMovies.value
                
                val foundMovie = allMovies.find { it.id == contentId }
                
                if (foundMovie != null) {
                    movieData = foundMovie
                    isLoading = false
                    android.util.Log.d("DetailScreen", "Found movie in cache: ${foundMovie.title}")
                } else {
                    // If not found in cache, try to fetch popular movies to get more data
                    val result = movieRepository.fetchPopularMovies()
                    result.fold(
                        onSuccess = { movies ->
                            val foundInNewData = movies.find { it.id == contentId }
                            if (foundInNewData != null) {
                                movieData = foundInNewData
                                android.util.Log.d("DetailScreen", "Found movie in fresh data: ${foundInNewData.title}")
                            } else {
                                android.util.Log.w("DetailScreen", "Movie not found: $contentId")
                                errorMessage = "Movie not found"
                            }
                            isLoading = false
                        },
                        onFailure = { error ->
                            android.util.Log.e("DetailScreen", "Failed to fetch movies", error)
                            errorMessage = "Failed to load movie details"
                            isLoading = false
                        }
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("DetailScreen", "Exception loading movie data", e)
                errorMessage = "Error loading movie details"
                isLoading = false
            }
        }
    }
    
    // Create activity details from fetched data or fallback
    val activityDetails = remember(movieData, contentId) {
        if (movieData != null) {
            // Use real movie data
            createMovieDetailsFromData(movieData!!)
        } else if (contentId.startsWith("movie_")) {
            // Fallback for movies
            createMovieDetails(contentId)
        } else {
            // Fallback to park activity for non-movie content
            createParkActivityDetails(contentId)
        }
    }
    
    // Custom layout without Scaffold to have full control over spacing
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Custom top bar with full width and no empty space
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(
                    onClick = {
                        android.util.Log.d("DetailScreen", "Back button tapped - returning to: ${activityDetails.title}")
                        onNavigateBack()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to ${activityDetails.title}",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Title with remaining space
                Text(
                    text = activityDetails.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                
                // Empty space on the right to balance the back button (48dp)
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        
        // Content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero image - increased height to better utilize white space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp) // Increased from 300dp to better utilize space
            ) {
                AsyncImage(
                    model = activityDetails.imageUrl,
                    contentDescription = activityDetails.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Content details
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Category - title is now in top bar
                Text(
                    text = activityDetails.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quick info cards - show different info based on content type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (activityDetails.category == "Movies") {
                        // Movie-specific info cards
                        InfoCard(
                            label = "Rating",
                            value = "${activityDetails.metadata["vote_average"] ?: "N/A"}/10",
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoCard(
                            label = "Release Date", 
                            value = activityDetails.metadata["release_date"] ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoCard(
                            label = "Runtime",
                            value = activityDetails.estimatedDuration,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Activity-specific info cards
                        InfoCard(
                            label = "Duration",
                            value = activityDetails.estimatedDuration,
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoCard(
                            label = "Difficulty", 
                            value = activityDetails.difficulty,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Extended description
                Text(
                    text = "About this activity",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = activityDetails.extendedDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Additional movie information for movies
                if (activityDetails.category == "Movies") {
                    // Movie details section
                    Text(
                        text = "Movie Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Movie metadata cards
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MovieInfoCard(
                            label = "Popularity Score",
                            value = activityDetails.metadata["popularity"] ?: "N/A"
                        )
                        
                        MovieInfoCard(
                            label = "Vote Count",
                            value = "${activityDetails.metadata["vote_count"] ?: "N/A"} votes"
                        )
                        
                        MovieInfoCard(
                            label = "Original Language",
                            value = activityDetails.metadata["original_language"]?.uppercase() ?: "N/A"
                        )
                        
                        MovieInfoCard(
                            label = "Genres",
                            value = "Drama, Crime" // Simplified for now
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                // Location/Availability info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (activityDetails.category == "Movies") "Where to Watch" else "Location",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = activityDetails.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Info card component for displaying key details
 */
@Composable
private fun InfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Movie info card component for displaying movie-specific details
 * Applied Rules: Debug logs, comments, movie-specific styling
 */
@Composable
private fun MovieInfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Data class for activity details (placeholder for future data models)
 */
private data class ActivityDetails(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val extendedDescription: String,
    val location: String,
    val estimatedDuration: String,
    val difficulty: String,
    val category: String,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Create movie details from actual ActivityContent data
 * Applied Rules: Debug logs, comments, real movie data
 */
private fun createMovieDetailsFromData(movie: ActivityContent): ActivityDetails {
    // Debug log for movie details creation from real data
    android.util.Log.d("DetailScreen", "Creating movie details from real data: ${movie.title}")
    
    return ActivityDetails(
        id = movie.id,
        title = movie.title,
        description = movie.description,
        imageUrl = movie.imageUrl ?: "",
        extendedDescription = """
            ${movie.description}
            
            This movie information is fetched from The Movie Database (TMDB) API and includes:
            • Detailed plot summary
            • Cast and crew information  
            • User ratings and reviews
            • Release date and runtime
            • Genre classification
            • Production details
            
            All data is provided by TMDB and updated regularly.
        """.trimIndent(),
        location = "Check local theaters and streaming services",
        estimatedDuration = movie.metadata["runtime"] ?: "Varies",
        difficulty = "Rating varies",
        category = "Movies",
        metadata = movie.metadata
    )
}

/**
 * Create movie-specific details from content ID
 * Applied Rules: Debug logs, comments, movie-specific data
 */
private fun createMovieDetails(contentId: String): ActivityDetails {
    // Debug log for movie details creation
    android.util.Log.d("DetailScreen", "Creating movie details for: $contentId")
    
    // For now, create sample movie data based on content ID
    // In a real app, this would fetch from the repository
    return when {
        contentId.contains("fallback") -> {
            ActivityDetails(
                id = contentId,
                title = "The Shawshank Redemption",
                description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                imageUrl = "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
                extendedDescription = """
                    The Shawshank Redemption is a 1994 American drama film written and directed by Frank Darabont, based on the 1982 Stephen King novella Rita Hayworth and Shawshank Redemption. The film tells the story of banker Andy Dufresne (Tim Robbins), who is sentenced to life in Shawshank State Penitentiary for the murders of his wife and her lover, despite his claims of innocence.
                    
                    Over the following two decades, he befriends a fellow prisoner, contraband smuggler Ellis "Red" Redding (Morgan Freeman), and becomes instrumental in a money laundering operation led by the prison warden Samuel Norton (Bob Gunton).
                    
                    The film received critical acclaim and was nominated for seven Academy Awards. It has since been considered one of the greatest films ever made.
                """.trimIndent(),
                location = "Available on streaming platforms",
                estimatedDuration = "2h 22m",
                difficulty = "Rated R",
                category = "Movies",
                metadata = mapOf(
                    "release_date" to "1994-09-23",
                    "vote_average" to "8.7",
                    "vote_count" to "24000",
                    "original_language" to "en",
                    "popularity" to "85.4",
                    "genre_ids" to "18,80"
                )
            )
        }
        else -> {
            // Generic movie details for other movies - enhanced with more realistic data
            ActivityDetails(
                id = contentId,
                title = "The Dark Knight",
                description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                imageUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                extendedDescription = """
                    The Dark Knight is a 2008 superhero film directed by Christopher Nolan, based on the DC Comics character Batman. The film is the second installment in Nolan's The Dark Knight Trilogy and a sequel to 2005's Batman Begins.
                    
                    Plot Summary:
                    Batman, Lieutenant James Gordon, and District Attorney Harvey Dent successfully begin to round up the criminals that plague Gotham City until a mysterious criminal mastermind known as the Joker appears in Gotham, creating a new wave of chaos. Batman's struggle against the Joker becomes deeply personal, forcing him to "confront everything he believes" and improve his technology to stop him.
                    
                    Cast & Crew:
                    • Christian Bale as Bruce Wayne / Batman
                    • Heath Ledger as The Joker
                    • Aaron Eckhart as Harvey Dent / Two-Face
                    • Michael Caine as Alfred Pennyworth
                    • Maggie Gyllenhaal as Rachel Dawes
                    • Gary Oldman as James Gordon
                    
                    The film received widespread critical acclaim, particularly for Ledger's performance as the Joker, and was a massive commercial success, grossing over $1 billion worldwide.
                """.trimIndent(),
                location = "Available on HBO Max, Amazon Prime, and other streaming platforms",
                estimatedDuration = "2h 32m",
                difficulty = "Rated PG-13",
                category = "Movies",
                metadata = mapOf(
                    "release_date" to "2008-07-18",
                    "vote_average" to "8.5",
                    "vote_count" to "30000",
                    "original_language" to "en",
                    "popularity" to "85.2"
                )
            )
        }
    }
}

/**
 * Create park activity details (fallback for non-movie content)
 * Applied Rules: Debug logs, comments, park activity data
 */
private fun createParkActivityDetails(contentId: String): ActivityDetails {
    // Debug log for park activity details creation
    android.util.Log.d("DetailScreen", "Creating park activity details for: $contentId")
    
    return ActivityDetails(
        id = contentId,
        title = "Go to a local park",
        description = "Enjoy the outdoors and fresh air. Pack a picnic or just relax.",
        imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&h=600&fit=crop",
        extendedDescription = """
            Spending time in a local park is one of the most refreshing and accessible ways to connect with nature. Whether you're looking for a peaceful escape from city life or an active outdoor adventure, parks offer endless possibilities for relaxation and recreation.
            
            Benefits of visiting a local park:
            • Fresh air and natural surroundings help reduce stress
            • Walking trails provide gentle exercise
            • Perfect setting for reading, meditation, or simply unwinding
            • Great for family activities and social gatherings
            • Often free or low-cost entertainment
            
            What to bring:
            • Comfortable walking shoes
            • Water bottle
            • Light snacks or picnic lunch
            • Sunscreen and hat for sunny days
            • Camera to capture beautiful moments
            
            Tips for a great park visit:
            • Check park hours and any special events
            • Bring a blanket for comfortable seating
            • Consider the weather and dress appropriately
            • Respect park rules and leave no trace
            • Explore different areas of the park for variety
        """.trimIndent(),
        location = "Local parks in your area",
        estimatedDuration = "2-4 hours",
        difficulty = "Easy",
        category = "Outdoor activities"
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    WhatToDoNextTheme {
        DetailScreen(
            contentId = "park_activity_001",
            onNavigateBack = {}
        )
    }
}
