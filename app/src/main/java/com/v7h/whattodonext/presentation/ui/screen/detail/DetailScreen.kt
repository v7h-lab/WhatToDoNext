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
    
    // Placeholder data for the park activity (matching the card from DeckScreen)
    val activityDetails = remember(contentId) {
        ActivityDetails(
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
                
                // Quick info cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                
                // Location info
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
                            text = "Location",
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
    val category: String
)

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
