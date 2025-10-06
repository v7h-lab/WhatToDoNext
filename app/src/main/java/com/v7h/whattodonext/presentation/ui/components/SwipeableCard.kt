package com.v7h.whattodonext.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import coil.compose.AsyncImage
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.presentation.theme.CircularButtonShape
import com.v7h.whattodonext.presentation.theme.AcceptButton
import com.v7h.whattodonext.presentation.theme.DeclineButton
import com.v7h.whattodonext.presentation.theme.SaveButton
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme

/**
 * Swipeable card component with responsive layout and button interactions
 * 
 * Features:
 * - Responsive design that adapts to different screen sizes
 * - 3-line text limit with expandable "read more" functionality
 * - Guaranteed button visibility on all screen sizes
 * - Design system colors and typography
 * - Card tap to view details
 * - Movie ratings and genre display for movie cards
 * 
 * Applied Rules: Debug logs, comments, responsive design, text truncation, movie metadata display
 */
@Composable
fun SwipeableCard(
    imageUrl: String,
    title: String,
    description: String,
    onCardClick: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    // Movie-specific metadata for enhanced display
    movieRating: String? = null,
    movieGenre: String? = null,
    activityType: String? = null
) {
    // Debug log for card initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("SwipeableCard", "Swipeable card initialized with responsive layout")
    }
    
    // Use BoxWithConstraints for responsive design
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val screenHeight = maxHeight
        
        // Calculate responsive image height based on screen size
        val responsiveImageHeight = remember(screenHeight) {
            when {
                screenHeight < 600.dp -> 200.dp // Small screens
                screenHeight < 800.dp -> 280.dp // Medium screens  
                else -> 350.dp // Large screens
            }
        }
        
        // Calculate minimum space needed for buttons (always reserve space)
        val minButtonSpace = 100.dp // Space for buttons + padding
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() },
            shape = CardShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.heightIn(min = screenHeight - minButtonSpace)
            ) {
                // Responsive image height based on screen size
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(responsiveImageHeight)
                        .clip(CardShape),
                    contentScale = ContentScale.Crop
                )
                
                // Content area with flexible height
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(20.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Movie ratings and genre - prominent display for movies
                    if (activityType == "Movies" && (movieRating != null || movieGenre != null)) {
                        MovieMetadataRow(
                            rating = movieRating,
                            genre = movieGenre
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    // Expandable description with 3-line limit and read more
                    ExpandableText(
                        text = description,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Action buttons - always at bottom with guaranteed visibility
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .padding(bottom = 16.dp), // Optimized bottom padding since Scaffold handles bottom nav
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Decline button (red) - using design system color
                    ActionButton(
                        icon = Icons.Default.Close,
                        label = "Decline",
                        backgroundColor = DeclineButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Decline button tapped")
                            onSwipeLeft()
                        }
                    )
                    
                    // Save button (gray) - using design system color
                    ActionButton(
                        icon = Icons.Default.Bookmark,
                        label = "Save", 
                        backgroundColor = SaveButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Save button tapped")
                            onSave()
                        }
                    )
                    
                    // Accept button (green) - using design system color
                    ActionButton(
                        icon = Icons.Default.Check,
                        label = "Accept",
                        backgroundColor = AcceptButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Accept button tapped")
                            onSwipeRight()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Expandable text component with 3-line limit and read more functionality
 * 
 * Applied Rules: Debug logs, comments, responsive design
 */
@Composable
private fun ExpandableText(
    text: String,
    maxLines: Int,
    style: androidx.compose.ui.text.TextStyle,
    color: Color
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Debug log for text processing
    LaunchedEffect(text) {
        android.util.Log.d("SwipeableCard", "Processing expandable text: ${text.length} characters")
    }
    
    Column {
        Text(
            text = text,
            style = style,
            color = color,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            lineHeight = style.lineHeight
        )
        
        // Show "Read more" if text is truncated
        if (!expanded && shouldShowReadMore(text, maxLines, style)) {
            Text(
                text = "Read more...",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { 
                        expanded = true
                        android.util.Log.d("SwipeableCard", "Text expanded to full content")
                    }
                    .padding(top = 4.dp)
            )
        }
        
        // Show "Show less" if text is expanded
        if (expanded) {
            Text(
                text = "Show less",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { 
                        expanded = false
                        android.util.Log.d("SwipeableCard", "Text collapsed to 3 lines")
                    }
                    .padding(top = 4.dp)
            )
        }
    }
}

/**
 * Simple heuristic to determine if text should show "Read more" button
 */
private fun shouldShowReadMore(text: String, maxLines: Int, style: androidx.compose.ui.text.TextStyle): Boolean {
    // Estimate if text would exceed maxLines based on character count
    // This is a simple heuristic - in a production app, you'd want more sophisticated text measurement
    val estimatedCharsPerLine = 50 // Rough estimate
    return text.length > (estimatedCharsPerLine * maxLines)
}

/**
 * Movie metadata row component for displaying ratings and genre
 * 
 * Applied Rules: Debug logs, comments, prominent display for movie info
 */
@Composable
private fun MovieMetadataRow(
    rating: String?,
    genre: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Movie rating with star icon
        rating?.let { ratingValue ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⭐",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = ratingValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Movie genre with chip-like styling
        genre?.let { genreValue ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Text(
                    text = genreValue,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Circular action button component with design system colors and typography
 */
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            shape = CircularButtonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Preview component for movie card with metadata
 */
@Preview(showBackground = true)
@Composable
private fun MovieCardPreview() {
    WhatToDoNextTheme {
        SwipeableCard(
            imageUrl = "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
            title = "The Shawshank Redemption",
            description = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            onCardClick = {},
            onSwipeLeft = {},
            onSwipeRight = {},
            onSave = {},
            movieRating = "8.7",
            movieGenre = "Drama, Crime",
            activityType = "Movies"
        )
    }
}