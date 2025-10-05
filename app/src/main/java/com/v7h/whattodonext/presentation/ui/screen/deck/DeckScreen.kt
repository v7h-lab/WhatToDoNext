package com.v7h.whattodonext.presentation.ui.screen.deck

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape
import com.v7h.whattodonext.presentation.theme.CircularButtonShape

/**
 * S-002: Main Deck Screen - Primary interface for card swiping
 * 
 * Contains:
 * - F-002: Activity Selector (dropdown)
 * - F-003: Swipeable Card Deck (main card interface)
 * 
 * Based on the UI reference showing:
 * - "What to do?" title with settings icon
 * - "Outdoor activities" dropdown
 * - Large card with park image, title, and description
 * - Three action buttons (Decline/Save/Accept)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    onNavigateToDetail: (String) -> Unit = {}
) {
    // Debug log for screen initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("DeckScreen", "Main deck screen loaded with placeholder data")
    }
    
    // Local state for activity selector
    var selectedActivity by remember { mutableStateOf("Outdoor activities") }
    var expanded by remember { mutableStateOf(false) }
    
    // Placeholder activity options
    val activities = listOf(
        "Outdoor activities",
        "Movies",
        "Restaurants", 
        "Books",
        "Travel destinations"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header with title and settings icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "What to do?",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
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
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Activity Selector Dropdown (F-002)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main Card (F-003) - Placeholder data matching UI reference
        ActivityCard(
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop",
            title = "Go to a local park",
            description = "Enjoy the outdoors and fresh air. Pack a picnic or just relax.",
            onCardClick = {
                android.util.Log.d("DeckScreen", "Card tapped - navigating to detail")
                onNavigateToDetail("park_activity_001")
            },
            onDecline = {
                android.util.Log.d("DeckScreen", "Card declined")
                // TODO: Implement swipe left logic in Step 2
            },
            onSave = {
                android.util.Log.d("DeckScreen", "Card saved")
                // TODO: Implement save logic in Step 3
            },
            onAccept = {
                android.util.Log.d("DeckScreen", "Card accepted")
                // TODO: Implement swipe right logic in Step 2
            }
        )
    }
}

/**
 * Main activity card component matching the UI reference
 * 
 * Features:
 * - Large image at top
 * - Title and description below
 * - Three action buttons (Decline/Save/Accept)
 */
@Composable
private fun ActivityCard(
    imageUrl: String,
    title: String,
    description: String,
    onCardClick: () -> Unit,
    onDecline: () -> Unit,
    onSave: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Large image matching UI reference
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(CardShape),
                contentScale = ContentScale.Crop
            )
            
            // Title and description
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
            
            // Action buttons matching UI reference
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Decline button (red)
                ActionButton(
                    icon = Icons.Filled.Close,
                    label = "Decline",
                    backgroundColor = MaterialTheme.colorScheme.error,
                    onClick = onDecline
                )
                
                // Save button (gray)
                ActionButton(
                    icon = Icons.Filled.Bookmark,
                    label = "Save", 
                    backgroundColor = Color(0xFF9E9E9E), // Keep gray for save button
                    onClick = onSave
                )
                
                // Accept button (green)
                ActionButton(
                    icon = Icons.Filled.Check,
                    label = "Accept",
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = onAccept
                )
            }
        }
    }
}

/**
 * Circular action button component with Material Symbols
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

@Preview(showBackground = true)
@Composable
private fun DeckScreenPreview() {
    WhatToDoNextTheme {
        DeckScreen()
    }
}
