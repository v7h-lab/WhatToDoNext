package com.v7h.whattodonext.presentation.ui.screen.detail

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape

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
    modifier: Modifier = Modifier
) {
    // Debug log for screen initialization
    LaunchedEffect(contentId) {
        android.util.Log.d("DetailScreen", "Detail screen loaded for content: $contentId")
    }
    
    // Determine content type and create appropriate details
    val activityDetails = remember(contentId) {
        if (contentId.startsWith("movie_")) {
            // Movie content - create movie-specific details
            createMovieDetails(contentId)
        } else {
            // Fallback to park activity for non-movie content
            createParkActivityDetails(contentId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Activity Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        android.util.Log.d("DetailScreen", "Back button tapped")
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero image
            AsyncImage(
                model = activityDetails.imageUrl,
                contentDescription = activityDetails.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)), // XL radius for hero image
                contentScale = ContentScale.Crop
            )
            
            // Content details
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Title and category
                Text(
                    text = activityDetails.title,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
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
            // Generic movie details for other movies
            ActivityDetails(
                id = contentId,
                title = "Movie Title",
                description = "Movie description from TMDB API",
                imageUrl = "https://image.tmdb.org/t/p/w500/placeholder.jpg",
                extendedDescription = """
                    This movie details are fetched from The Movie Database (TMDB) API. 
                    The information includes cast, crew, plot summary, ratings, and more.
                    
                    Features:
                    • Detailed plot summary
                    • Cast and crew information
                    • User ratings and reviews
                    • Release date and runtime
                    • Genre classification
                    • Production details
                    
                    All data is provided by TMDB and updated regularly.
                """.trimIndent(),
                location = "Check local theaters and streaming services",
                estimatedDuration = "Varies",
                difficulty = "Rating varies",
                category = "Movies",
                metadata = mapOf(
                    "release_date" to "2024-01-01",
                    "vote_average" to "7.5",
                    "vote_count" to "1000",
                    "original_language" to "en",
                    "popularity" to "50.0"
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
