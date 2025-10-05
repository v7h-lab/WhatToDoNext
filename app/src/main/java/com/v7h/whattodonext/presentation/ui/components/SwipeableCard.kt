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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.presentation.theme.CircularButtonShape
import com.v7h.whattodonext.presentation.theme.AcceptButton
import com.v7h.whattodonext.presentation.theme.DeclineButton
import com.v7h.whattodonext.presentation.theme.SaveButton

/**
 * Swipeable card component with button-based interactions for Step 2
 * 
 * Features:
 * - Button-based swipe actions (Decline/Save/Accept)
 * - Design system colors and typography
 * - Card tap to view details
 * - Visual feedback with proper styling
 * 
 * Applied Rules: Debug logs, comments, design system colors, typography
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
    modifier: Modifier = Modifier
) {
    // Debug log for card initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("SwipeableCard", "Swipeable card initialized with button interactions")
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Large image
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(CardShape),
                contentScale = ContentScale.Crop
            )
            
            // Title and description with design system typography
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action buttons with design system colors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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