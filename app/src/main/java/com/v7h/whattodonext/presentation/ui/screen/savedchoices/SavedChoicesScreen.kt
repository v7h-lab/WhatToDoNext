package com.v7h.whattodonext.presentation.ui.screen.savedchoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.v7h.whattodonext.data.model.SavedChoice
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * S-004: Saved Choices Screen - Displays all saved items
 * 
 * Contains:
 * - F-005: Saved Choices List
 * - List of all items user has swiped right on
 * - Ability to remove items from saved list
 * 
 * Applied Rules: Debug logs, comments, LazyColumn for performance, StateFlow collection
 */
@Composable
fun SavedChoicesScreen(
    savedChoiceRepository: SavedChoiceRepository
) {
    // Debug log for screen initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("SavedChoicesScreen", "Saved choices screen loaded")
    }
    
    // Collect saved choices from repository with error handling
    val savedChoices by remember { 
        try {
            savedChoiceRepository.savedChoices
        } catch (e: Exception) {
            android.util.Log.e("SavedChoicesScreen", "Error accessing saved choices repository", e)
            kotlinx.coroutines.flow.MutableStateFlow<List<SavedChoice>>(emptyList()).asStateFlow()
        }
    }.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Saved Choices",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${savedChoices.size} items saved",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Saved choices list
        if (savedChoices.isEmpty()) {
            // Empty state
            EmptySavedChoicesState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = savedChoices,
                    key = { it.id }
                ) { savedChoice ->
                    SavedChoiceItem(
                        savedChoice = savedChoice,
                        onRemove = {
                            android.util.Log.d("SavedChoicesScreen", "Removing saved choice: ${savedChoice.title}")
                            try {
                                savedChoiceRepository.removeSavedChoice(savedChoice.id)
                            } catch (e: Exception) {
                                android.util.Log.e("SavedChoicesScreen", "Error removing saved choice", e)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual saved choice item component
 */
@Composable
private fun SavedChoiceItem(
    savedChoice: SavedChoice,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                android.util.Log.d("SavedChoicesScreen", "Saved choice tapped: ${savedChoice.title}")
                // TODO: Navigate to detail screen in later steps
            },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail image
            AsyncImage(
                model = savedChoice.imageUrl,
                contentDescription = savedChoice.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = savedChoice.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = savedChoice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = savedChoice.activityType,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Remove button
            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Empty state when no saved choices
 */
@Composable
private fun EmptySavedChoicesState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        Text(
            text = "No saved choices yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Swipe right on cards in the Deck to save them here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedChoicesScreenPreview() {
    WhatToDoNextTheme {
        SavedChoicesScreen(
            savedChoiceRepository = SavedChoiceRepository()
        )
    }
}
