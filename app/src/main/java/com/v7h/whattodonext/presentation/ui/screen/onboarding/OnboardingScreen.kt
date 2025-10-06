package com.v7h.whattodonext.presentation.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v7h.whattodonext.data.model.ActivityType
import com.v7h.whattodonext.data.model.DefaultActivities
import com.v7h.whattodonext.presentation.theme.WhatToDoNextTheme
import com.v7h.whattodonext.presentation.theme.CardShape

/**
 * S-001: Onboarding Screen - Multi-step setup process
 * 
 * Contains:
 * - F-001: Onboarding Flow with activity selection
 * - Step-by-step process for first-time users
 * - Activity selection with preferences setup
 * 
 * Applied Rules: Debug logs, comments, StateFlow for state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedActivities by remember { mutableStateOf(setOf<ActivityType>()) }
    
    // Debug log for onboarding start
    LaunchedEffect(Unit) {
        android.util.Log.d("OnboardingScreen", "Onboarding started - Step $currentStep")
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = (currentStep + 1) / 3f, // 3 steps total
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        when (currentStep) {
            0 -> WelcomeStep(
                onNext = { 
                    currentStep = 1
                    android.util.Log.d("OnboardingScreen", "Moving to step 1")
                }
            )
            1 -> ActivitySelectionStep(
                selectedActivities = selectedActivities,
                onActivitiesChanged = { selectedActivities = it },
                onNext = { 
                    currentStep = 2
                    android.util.Log.d("OnboardingScreen", "Moving to step 2 with ${selectedActivities.size} activities")
                }
            )
            2 -> CompletionStep(
                selectedActivities = selectedActivities,
                onComplete = {
                    android.util.Log.d("OnboardingScreen", "Onboarding completed with ${selectedActivities.size} activities")
                    onOnboardingComplete()
                }
            )
        }
    }
}

/**
 * Step 1: Welcome and introduction
 */
@Composable
private fun WelcomeStep(
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome title
        Text(
            text = "Welcome to\nWhat To Do Next",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Description
        Text(
            text = "Let's help you make decisions faster by setting up your personalized activity preferences.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Next button
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * Step 2: Activity selection
 */
@Composable
private fun ActivitySelectionStep(
    selectedActivities: Set<ActivityType>,
    onActivitiesChanged: (Set<ActivityType>) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Step title
        Text(
            text = "Choose Your Interests",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Select the activities you'd like to discover options for:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Activity selection grid
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(DefaultActivities.ACTIVITIES.chunked(2)) { activityRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    activityRow.forEach { activity ->
                        ActivitySelectionCard(
                            activity = activity,
                            isSelected = selectedActivities.contains(activity),
                            onToggle = {
                                val newSelection = if (selectedActivities.contains(activity)) {
                                    selectedActivities - activity
                                } else {
                                    selectedActivities + activity
                                }
                                onActivitiesChanged(newSelection)
                                android.util.Log.d("OnboardingScreen", "Activity ${activity.name} toggled: ${!selectedActivities.contains(activity)}")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Next button (enabled only if at least one activity is selected)
        Button(
            onClick = onNext,
            enabled = selectedActivities.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue (${selectedActivities.size} selected)")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * Step 3: Completion summary
 */
@Composable
private fun CompletionStep(
    selectedActivities: Set<ActivityType>,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success icon
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        // Completion title
        Text(
            text = "All Set!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Summary
        Text(
            text = "You've selected ${selectedActivities.size} activities to discover:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Selected activities list
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            items(selectedActivities.toList()) { activity ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "${activity.icon} ${activity.name}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Complete button
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Exploring")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * Individual activity selection card
 */
@Composable
private fun ActivitySelectionCard(
    activity: ActivityType,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onToggle() },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = activity.icon,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    WhatToDoNextTheme {
        OnboardingScreen(
            onOnboardingComplete = { /* Preview */ }
        )
    }
}
