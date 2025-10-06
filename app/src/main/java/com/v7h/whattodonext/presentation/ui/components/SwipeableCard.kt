package com.v7h.whattodonext.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.coroutines.launch

/**
 * Swipeable card component with comprehensive gesture interactions and button fallbacks
 * 
 * Features:
 * - Swipe left/right gestures for decline/accept actions
 * - Long press for save functionality
 * - Tap gestures for card details
 * - Visual feedback and animations during gestures
 * - Button fallbacks for accessibility
 * - Responsive design that adapts to different screen sizes
 * - 3-line text limit with expandable "read more" functionality
 * - Guaranteed button visibility on all screen sizes
 * - Design system colors and typography
 * - Movie ratings and genre display for movie cards
 * - Crash prevention with robust error handling
 * 
 * Applied Rules: Debug logs, comments, responsive design, text truncation, movie metadata display, gesture interactions, crash prevention
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
        android.util.Log.d("SwipeableCard", "Swipeable card initialized with gesture interactions and responsive layout")
    }
    
    // Gesture state management with crash prevention and null safety
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    var scale by remember { mutableStateOf(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var gestureInProgress by remember { mutableStateOf(false) }
    var isDisposed by remember { mutableStateOf(false) }
    
    // Safe animation states with null checks and crash prevention
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging && !isDisposed) offsetX.coerceIn(-1000f, 1000f) else 0f,
        animationSpec = tween(300),
        label = "offsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging && !isDisposed) offsetY.coerceIn(-1000f, 1000f) else 0f,
        animationSpec = tween(300),
        label = "offsetY"
    )
    val animatedRotation by animateFloatAsState(
        targetValue = if (isDragging && !isDisposed) rotation.coerceIn(-90f, 90f) else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging && !isDisposed) scale.coerceIn(0.5f, 2f) else 1f,
        animationSpec = tween(300),
        label = "scale"
    )
    
    // Safe gesture thresholds with null checks
    val density = LocalDensity.current
    val screenWidth = try {
        density.run { 400.dp.toPx() }.coerceAtLeast(200f)
    } catch (e: Exception) {
        android.util.Log.e("SwipeableCard", "Error calculating screen width", e)
        400f // Fallback width
    }
    val swipeThreshold = screenWidth * 0.3f // 30% of screen width
    val rotationThreshold = 15f // degrees
    
    // Safe coroutine scope with lifecycle management
    val coroutineScope = rememberCoroutineScope()
    
    // Cleanup on disposal to prevent memory leaks and crashes
    DisposableEffect(Unit) {
        onDispose {
            isDisposed = true
            gestureInProgress = false
            isDragging = false
            android.util.Log.d("SwipeableCard", "SwipeableCard disposed - cleaning up gesture state")
        }
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
                .graphicsLayer {
                    translationX = animatedOffsetX
                    translationY = animatedOffsetY
                    rotationZ = animatedRotation
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
                .pointerInput(Unit) {
                    // Comprehensive gesture detection with crash prevention and state safety
                    try {
                        detectDragGestures(
                            onDragStart = { _ ->
                                if (!gestureInProgress && !isDisposed) {
                                    gestureInProgress = true
                                    isDragging = true
                                    android.util.Log.d("SwipeableCard", "Drag gesture started")
                                }
                            },
                            onDragEnd = {
                                if (gestureInProgress && !isDisposed) {
                                    android.util.Log.d("SwipeableCard", "Drag gesture ended - offsetX: $offsetX, rotation: $rotation")
                                    
                                    // Safe action determination with null checks
                                    try {
                                        when {
                                            offsetX > swipeThreshold || rotation > rotationThreshold -> {
                                                // Swipe right or rotate right - Accept
                                                android.util.Log.d("SwipeableCard", "Gesture: Accept (swipe right/rotate right)")
                                                try {
                                                    onSwipeRight()
                                                } catch (e: Exception) {
                                                    android.util.Log.e("SwipeableCard", "Error in onSwipeRight callback", e)
                                                }
                                            }
                                            offsetX < -swipeThreshold || rotation < -rotationThreshold -> {
                                                // Swipe left or rotate left - Decline
                                                android.util.Log.d("SwipeableCard", "Gesture: Decline (swipe left/rotate left)")
                                                try {
                                                    onSwipeLeft()
                                                } catch (e: Exception) {
                                                    android.util.Log.e("SwipeableCard", "Error in onSwipeLeft callback", e)
                                                }
                                            }
                                            else -> {
                                                // Return to center
                                                android.util.Log.d("SwipeableCard", "Gesture: Return to center")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("SwipeableCard", "Error in gesture action determination", e)
                                    }
                                    
                                    // Safe state reset with coroutine safety
                                    if (!isDisposed) {
                                        coroutineScope.launch {
                                            try {
                                                offsetX = 0f
                                                offsetY = 0f
                                                rotation = 0f
                                                scale = 1f
                                                isDragging = false
                                                gestureInProgress = false
                                            } catch (e: Exception) {
                                                android.util.Log.e("SwipeableCard", "Error resetting gesture state", e)
                                                // Force reset on error
                                                offsetX = 0f
                                                offsetY = 0f
                                                rotation = 0f
                                                scale = 1f
                                                isDragging = false
                                                gestureInProgress = false
                                            }
                                        }
                                    }
                                }
                            }
                        ) { change, _ ->
                            if (gestureInProgress && !isDisposed) {
                                try {
                                    // Safe position updates with null checks
                                    val deltaX = change.position.x - change.previousPosition.x
                                    val deltaY = change.position.y - change.previousPosition.y
                                    
                                    // Update position and rotation based on drag with safety bounds
                                    offsetX = (offsetX + deltaX).coerceIn(-screenWidth * 0.6f, screenWidth * 0.6f)
                                    offsetY = (offsetY + deltaY).coerceIn(-300f, 300f)
                                    
                                    // Calculate rotation based on horizontal movement with safety bounds
                                    rotation = (offsetX / screenWidth * 30f).coerceIn(-60f, 60f)
                                    
                                    // Calculate scale based on vertical movement (slight zoom effect) with safety bounds
                                    scale = (1f + (kotlin.math.abs(offsetY) / 1000f) * 0.1f).coerceIn(0.7f, 1.3f)
                                    
                                } catch (e: Exception) {
                                    android.util.Log.e("SwipeableCard", "Error updating gesture position", e)
                                    // Reset to safe values on error
                                    offsetX = 0f
                                    offsetY = 0f
                                    rotation = 0f
                                    scale = 1f
                                }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SwipeableCard", "Error in drag gesture detection", e)
                        // Reset state on error to prevent crashes
                        gestureInProgress = false
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                        rotation = 0f
                        scale = 1f
                    }
                }
                .pointerInput(Unit) {
                    // Long press for save functionality with crash prevention and state safety
                    try {
                        detectTapGestures(
                            onLongPress = {
                                if (!isDisposed && !gestureInProgress) {
                                    android.util.Log.d("SwipeableCard", "Long press detected - Save action")
                                    try {
                                        onSave()
                                    } catch (e: Exception) {
                                        android.util.Log.e("SwipeableCard", "Error in onSave callback", e)
                                    }
                                }
                            },
                            onTap = {
                                if (!isDisposed && !gestureInProgress) {
                                    android.util.Log.d("SwipeableCard", "Tap detected - Card details")
                                    try {
                                        onCardClick()
                                    } catch (e: Exception) {
                                        android.util.Log.e("SwipeableCard", "Error in onCardClick callback", e)
                                    }
                                }
                            }
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("SwipeableCard", "Error in tap gesture detection", e)
                    }
                },
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
                
                // Visual feedback overlay for gestures
                if (isDragging) {
                    GestureFeedbackOverlay(
                        offsetX = offsetX,
                        rotation = rotation,
                        swipeThreshold = swipeThreshold,
                        rotationThreshold = rotationThreshold
                    )
                }
                
                // Action buttons - always at bottom with guaranteed visibility and gesture hints
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .padding(bottom = 16.dp), // Optimized bottom padding since Scaffold handles bottom nav
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Decline button (red) - using design system color with gesture hint
                    ActionButtonWithGestureHint(
                        icon = Icons.Default.Close,
                        label = "Decline",
                        gestureHint = "Swipe Left",
                        backgroundColor = DeclineButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Decline button tapped")
                            try {
                                onSwipeLeft()
                            } catch (e: Exception) {
                                android.util.Log.e("SwipeableCard", "Error in decline button callback", e)
                            }
                        }
                    )
                    
                    // Save button (gray) - using design system color with gesture hint
                    ActionButtonWithGestureHint(
                        icon = Icons.Default.Bookmark,
                        label = "Save",
                        gestureHint = "Long Press",
                        backgroundColor = SaveButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Save button tapped")
                            try {
                                onSave()
                            } catch (e: Exception) {
                                android.util.Log.e("SwipeableCard", "Error in save button callback", e)
                            }
                        }
                    )
                    
                    // Accept button (green) - using design system color with gesture hint
                    ActionButtonWithGestureHint(
                        icon = Icons.Default.Check,
                        label = "Accept",
                        gestureHint = "Swipe Right",
                        backgroundColor = AcceptButton,
                        onClick = {
                            android.util.Log.d("SwipeableCard", "Accept button tapped")
                            try {
                                onSwipeRight()
                            } catch (e: Exception) {
                                android.util.Log.e("SwipeableCard", "Error in accept button callback", e)
                            }
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
private fun shouldShowReadMore(text: String, maxLines: Int, @Suppress("UNUSED_PARAMETER") style: androidx.compose.ui.text.TextStyle): Boolean {
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
 * Visual feedback overlay for gesture interactions
 * 
 * Applied Rules: Debug logs, comments, visual feedback, crash prevention
 */
@Composable
private fun GestureFeedbackOverlay(
    offsetX: Float,
    rotation: Float,
    swipeThreshold: Float,
    rotationThreshold: Float
) {
    // Determine feedback color based on gesture direction
    val feedbackColor = when {
        offsetX > swipeThreshold || rotation > rotationThreshold -> AcceptButton.copy(alpha = 0.3f)
        offsetX < -swipeThreshold || rotation < -rotationThreshold -> DeclineButton.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    
    // Show feedback only when gesture is significant
    if (kotlin.math.abs(offsetX) > 50f || kotlin.math.abs(rotation) > 5f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(feedbackColor)
        ) {
            // Optional: Add directional indicators
            if (kotlin.math.abs(offsetX) > swipeThreshold || kotlin.math.abs(rotation) > rotationThreshold) {
                Text(
                    text = if (offsetX > 0 || rotation > 0) "✓ Accept" else "✗ Decline",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Enhanced action button with gesture hints and improved accessibility
 * 
 * Applied Rules: Debug logs, comments, accessibility, gesture hints
 */
@Composable
private fun ActionButtonWithGestureHint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    gestureHint: String,
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
        
        // Button label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Gesture hint
        Text(
            text = gestureHint,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * Circular action button component with design system colors and typography
 * 
 * Applied Rules: Debug logs, comments, design system compliance
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