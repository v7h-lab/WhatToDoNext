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
import com.v7h.whattodonext.data.model.TmdbGenres
import com.v7h.whattodonext.data.model.TmdbLanguages
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
 * Updated flow:
 * - Step 1: Activity selection ("What to do next")
 * - Step 2: Activity-specific preferences (e.g., genres & languages for Movies)
 * 
 * Applied Rules: Debug logs, comments, StateFlow for state management, flexible architecture for future activities
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    userProfileRepository: com.v7h.whattodonext.data.repository.UserProfileRepository? = null,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedActivity by remember { mutableStateOf<ActivityType?>(null) }
    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLanguages by remember { mutableStateOf<List<String>>(emptyList()) }
    var isCompletingOnboarding by remember { mutableStateOf(false) }
    
    // Debug log for onboarding start
    LaunchedEffect(Unit) {
        android.util.Log.d("OnboardingScreen", "Onboarding started - Step $currentStep")
    }
    
    // Handle onboarding completion with repository
    LaunchedEffect(isCompletingOnboarding) {
        if (isCompletingOnboarding && userProfileRepository != null && selectedActivity != null) {
            try {
                // Update selected activity in the repository
                userProfileRepository.updateSelectedActivities(listOf(selectedActivity!!))
                
                // Save activity preferences (genres and languages for movies)
                if (selectedActivity!!.id == "movies" && (selectedGenres.isNotEmpty() || selectedLanguages.isNotEmpty())) {
                    val preferences = com.v7h.whattodonext.data.model.ActivityPreferences(
                        activityId = "movies",
                        filters = mapOf(
                            "genres" to selectedGenres,
                            "languages" to selectedLanguages
                        )
                    )
                    userProfileRepository.updateActivityPreferences("movies", preferences)
                    android.util.Log.d("OnboardingScreen", "Saved movie preferences: genres=$selectedGenres, languages=$selectedLanguages")
                }
                
                android.util.Log.d("OnboardingScreen", "Successfully saved selected activity: ${selectedActivity!!.name}")
                
                // Navigate to main app
                onOnboardingComplete()
            } catch (e: Exception) {
                android.util.Log.e("OnboardingScreen", "Error saving onboarding data", e)
                // Still navigate to main app even if save fails
                onOnboardingComplete()
            }
        } else if (isCompletingOnboarding && userProfileRepository == null) {
            android.util.Log.w("OnboardingScreen", "No userProfileRepository provided, completing onboarding without saving")
            onOnboardingComplete()
        } else if (isCompletingOnboarding && selectedActivity == null) {
            android.util.Log.w("OnboardingScreen", "No activity selected, completing onboarding without saving")
            onOnboardingComplete()
        }
    }
    
    // Calculate total steps based on selected activity
    val totalSteps = if (selectedActivity?.id == "movies") 2 else 2 // Future: other activities may have preferences too
    val currentStepNumber = currentStep + 1
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator (dynamic based on activity type)
        LinearProgressIndicator(
            progress = currentStepNumber.toFloat() / totalSteps.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        when (currentStep) {
            0 -> ActivitySelectionStep(
                selectedActivity = selectedActivity,
                onActivityChanged = { selectedActivity = it },
                onNext = { 
                    // Check if selected activity needs preferences screen
                    if (selectedActivity?.id == "movies") {
                        currentStep = 1
                        android.util.Log.d("OnboardingScreen", "Moving to preferences for: ${selectedActivity?.name}")
                    } else {
                        // Skip preferences for activities that don't need them (for now)
                        android.util.Log.d("OnboardingScreen", "No preferences needed for ${selectedActivity?.name}, completing onboarding")
                        isCompletingOnboarding = true
                    }
                }
            )
            1 -> {
                // Activity-specific preferences screen
                when (selectedActivity?.id) {
                    "movies" -> MoviePreferencesStep(
                        selectedGenres = selectedGenres,
                        selectedLanguages = selectedLanguages,
                        onGenresChanged = { selectedGenres = it },
                        onLanguagesChanged = { selectedLanguages = it },
                        onComplete = {
                            android.util.Log.d("OnboardingScreen", "Movie preferences completed: genres=$selectedGenres, languages=$selectedLanguages")
                            isCompletingOnboarding = true
                        },
                        onBack = {
                            currentStep = 0
                            android.util.Log.d("OnboardingScreen", "Going back to activity selection")
                        }
                    )
                    // Future: Add preference screens for other activities here
                    else -> {
                        // Fallback: complete onboarding if no preference screen exists
                        LaunchedEffect(Unit) {
                            isCompletingOnboarding = true
                        }
                    }
                }
            }
        }
    }
}

/**
 * Step 1: Activity selection (single selection only)
 * First screen with "What to do next" title
 */
@Composable
private fun ActivitySelectionStep(
    selectedActivity: ActivityType?,
    onActivityChanged: (ActivityType) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Step title - "What to do next"
        Text(
            text = "What to do next?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Select one activity you'd like to discover options for:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Activity selection grid (single selection)
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
                            isSelected = selectedActivity == activity,
                            onToggle = {
                                // Single selection: always set to the clicked activity
                                onActivityChanged(activity)
                                android.util.Log.d("OnboardingScreen", "Activity selected: ${activity.name}")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Next button (enabled only if one activity is selected)
        Button(
            onClick = onNext,
            enabled = selectedActivity != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue" + if (selectedActivity != null) " with ${selectedActivity.name}" else "")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

/**
 * Step 2: Movie Preferences - Genre and Language selection
 * Multi-select for genres and languages from TMDB
 */
@Composable
private fun MoviePreferencesStep(
    selectedGenres: List<String>,
    selectedLanguages: List<String>,
    onGenresChanged: (List<String>) -> Unit,
    onLanguagesChanged: (List<String>) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TMDB movie genres (from TmdbConstants)
    val availableGenres = TmdbGenres.getAllGenreNames()
    
    // TMDB supported languages (from TmdbConstants with valid API codes)
    val availableLanguages = TmdbLanguages.LANGUAGES
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Step title
        Text(
            text = "Movie Preferences",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Select your preferred genres and languages:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Scrollable content
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Genres section
            item {
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                // Genre chips in a flow layout
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableGenres.chunked(3)) { genreRow ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            genreRow.forEach { genre ->
                                FilterChip(
                                    selected = selectedGenres.contains(genre),
                                    onClick = {
                                        if (selectedGenres.contains(genre)) {
                                            onGenresChanged(selectedGenres - genre)
                                        } else {
                                            onGenresChanged(selectedGenres + genre)
                                        }
                                        android.util.Log.d("MoviePreferences", "Genre toggled: $genre, selected=${!selectedGenres.contains(genre)}")
                                    },
                                    label = { Text(genre) },
                                    leadingIcon = if (selectedGenres.contains(genre)) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
            
            // Languages section
            item {
                Text(
                    text = "Languages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            
            item {
                // Language chips in a flow layout
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableLanguages.chunked(3)) { langRow ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            langRow.forEach { (code, name) ->
                                FilterChip(
                                    selected = selectedLanguages.contains(code),
                                    onClick = {
                                        if (selectedLanguages.contains(code)) {
                                            onLanguagesChanged(selectedLanguages - code)
                                        } else {
                                            onLanguagesChanged(selectedLanguages + code)
                                        }
                                        android.util.Log.d("MoviePreferences", "Language toggled: $name ($code), selected=${!selectedLanguages.contains(code)}")
                                    },
                                    label = { Text(name) },
                                    leadingIcon = if (selectedLanguages.contains(code)) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            // Continue button (enabled even if no selection for flexibility)
            Button(
                onClick = onComplete,
                modifier = Modifier.weight(2f)
            ) {
                Text("Start Exploring")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
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
