package com.v7h.whattodonext.presentation.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v7h.whattodonext.data.model.ActivityType
import com.v7h.whattodonext.data.model.DefaultActivities
import com.v7h.whattodonext.data.repository.UserProfileRepository
import com.v7h.whattodonext.data.storage.UserProfileStorage
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import androidx.compose.ui.platform.LocalContext
import com.v7h.whattodonext.presentation.theme.CardShape

/**
 * S-005: Profile Screen - User profile management
 * 
 * Contains:
 * - F-006: Profile Management interface
 * - Activity management (add/remove activities)
 * - Preferences editing
 * - User profile display
 * 
 * Applied Rules: Debug logs, comments, StateFlow for reactive updates
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfileRepository: UserProfileRepository? = null,
    modifier: Modifier = Modifier
) {
    // Use provided repository or create a default one for preview
    val context = LocalContext.current
    val repository = userProfileRepository ?: remember { 
        // For preview purposes, create a mock repository
        // In real usage, this should be injected via dependency injection
        UserProfileRepository(
            UserProfileStorage(context)
        )
    }
    val userProfile by repository.userProfile.collectAsState()
    
    // State for managing UI interactions
    var showAddActivityDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf<ActivityType?>(null) }
    
    // Debug log for screen initialization
    LaunchedEffect(Unit) {
        android.util.Log.d("ProfileScreen", "Profile screen loaded with ${userProfile.selectedActivities.size} activities")
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Profile",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            // Profile info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "User Profile",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = "${userProfile.selectedActivities.size} activities selected",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        item {
            // Activities section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Activities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = { 
                        showAddActivityDialog = true
                        android.util.Log.d("ProfileScreen", "Add activity dialog requested")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Activity",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Selected activities list
        items(userProfile.selectedActivities) { activity ->
            ActivityManagementCard(
                activity = activity,
                onEdit = {
                    android.util.Log.d("ProfileScreen", "Edit activity: ${activity.name}")
                    // TODO: Implement activity preferences editing in Step 6
                },
                onDelete = {
                    showDeleteConfirmation = activity
                    android.util.Log.d("ProfileScreen", "Delete activity requested: ${activity.name}")
                }
            )
        }
        
        // Empty state if no activities
        if (userProfile.selectedActivities.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No activities selected",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Add activities to start discovering options",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { showAddActivityDialog = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Activity")
                        }
                    }
                }
            }
        }
        
        item {
            // Settings section
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            SettingsOption(
                icon = Icons.Default.Info,
                title = "About",
                description = "App version and information",
                onClick = {
                    android.util.Log.d("ProfileScreen", "About tapped")
                    // TODO: Implement about dialog
                }
            )
        }
        
        item {
            // App info
            Text(
                text = "What To Do Next v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
    
    // Add Activity Dialog
    if (showAddActivityDialog) {
        AddActivityDialog(
            availableActivities = DefaultActivities.ACTIVITIES.filter { 
                !userProfile.selectedActivities.contains(it) 
            },
            onActivitySelected = { activity ->
                // Add the selected activity
                val newActivities = userProfile.selectedActivities + activity
                // Note: In a real app, this would be done through the repository
                android.util.Log.d("ProfileScreen", "Activity added: ${activity.name}")
                showAddActivityDialog = false
            },
            onDismiss = { showAddActivityDialog = false }
        )
    }
    
    // Delete Confirmation Dialog
    showDeleteConfirmation?.let { activity ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = null },
            title = { Text("Remove Activity") },
            text = { Text("Are you sure you want to remove \"${activity.name}\" from your activities?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Remove the activity
                        val newActivities = userProfile.selectedActivities - activity
                        // Note: In a real app, this would be done through the repository
                        android.util.Log.d("ProfileScreen", "Activity removed: ${activity.name}")
                        showDeleteConfirmation = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Settings option component
 */
@Composable
private fun SettingsOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Activity management card component
 */
@Composable
private fun ActivityManagementCard(
    activity: ActivityType,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon and info
            Text(
                text = activity.icon,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action buttons
            IconButton(
                onClick = onEdit,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Activity",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Activity",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Add Activity Dialog
 */
@Composable
private fun AddActivityDialog(
    availableActivities: List<ActivityType>,
    onActivitySelected: (ActivityType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Activity") },
        text = {
            if (availableActivities.isEmpty()) {
                Text("All available activities have been added.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableActivities) { activity ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onActivitySelected(activity) },
                            shape = CardShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activity.icon,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                
                                Column {
                                    Text(
                                        text = activity.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Text(
                                        text = activity.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (availableActivities.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    WhatToDoNextTheme {
        ProfileScreen()
    }
}
