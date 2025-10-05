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
import com.v7h.whattodonext.data.repository.SavedChoiceRepository

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
    onNavigateToDetail: (String) -> Unit = {},
    savedChoiceRepository: SavedChoiceRepository = remember { SavedChoiceRepository() }
) {
    // Debug log for screen initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("DeckScreen", "Main deck screen loaded with swipeable cards")
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
    
    // Placeholder card data for Step 2
    val currentCard = remember {
        mapOf(
            "id" to "park_activity_001",
            "title" to "Go to a local park",
            "description" to "Enjoy the outdoors and fresh air. Pack a picnic or just relax.",
            "imageUrl" to "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop",
            "activityType" to "Outdoor activities"
        )
    }
    
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
        
        // Swipeable Card (F-003) - Step 2 implementation with gesture detection
        SwipeableCard(
            imageUrl = currentCard["imageUrl"] ?: "",
            title = currentCard["title"] ?: "",
            description = currentCard["description"] ?: "",
            onCardClick = {
                android.util.Log.d("DeckScreen", "Card tapped - navigating to detail")
                onNavigateToDetail(currentCard["id"] ?: "")
            },
            onSwipeLeft = {
                android.util.Log.d("DeckScreen", "Card declined via swipe left")
                // TODO: Load next card in Step 4
            },
            onSwipeRight = {
                android.util.Log.d("DeckScreen", "Card accepted via swipe right")
                // Add to saved choices for Step 3
                val savedChoice = SavedChoice.create(
                    id = currentCard["id"] ?: "",
                    title = currentCard["title"] ?: "",
                    description = currentCard["description"] ?: "",
                    imageUrl = currentCard["imageUrl"] ?: "",
                    activityType = currentCard["activityType"] ?: ""
                )
                savedChoiceRepository.addSavedChoice(savedChoice)
                // TODO: Load next card in Step 4
            },
            onSave = {
                android.util.Log.d("DeckScreen", "Card saved via button")
                // Add to saved choices for Step 3
                val savedChoice = SavedChoice.create(
                    id = currentCard["id"] ?: "",
                    title = currentCard["title"] ?: "",
                    description = currentCard["description"] ?: "",
                    imageUrl = currentCard["imageUrl"] ?: "",
                    activityType = currentCard["activityType"] ?: ""
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
