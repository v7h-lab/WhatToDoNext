package com.v7h.whattodonext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.v7h.whattodonext.data.model.ActivityContent
import com.v7h.whattodonext.data.model.Result
import com.v7h.whattodonext.data.model.SavedChoice
import com.v7h.whattodonext.data.repository.SavedChoiceRepository
import com.v7h.whattodonext.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Deck Screen
 * 
 * This ViewModel follows 2025 Android best practices:
 * - Uses StateFlow for reactive state management
 * - Implements proper error handling with Result wrapper
 * - Uses Hilt for dependency injection
 * - Follows MVVM architecture pattern
 * 
 * Applied Rules: Debug logs, comments, ViewModel with StateFlow, Hilt DI, proper error handling
 */
@HiltViewModel
class DeckViewModel @Inject constructor(
    private val savedChoiceRepository: SavedChoiceRepository,
    private val userProfileRepository: UserProfileRepository
) : BaseViewModel() {
    
    // UI State for the deck screen
    private val _uiState = MutableStateFlow(DeckUiState())
    val uiState: StateFlow<DeckUiState> = _uiState.asStateFlow()
    
    // Current activity content
    private val _currentContent = MutableStateFlow<ActivityContent?>(null)
    val currentContent: StateFlow<ActivityContent?> = _currentContent.asStateFlow()
    
    init {
        // Debug log for ViewModel initialization
        android.util.Log.d("DeckViewModel", "DeckViewModel initialized with Hilt DI")
        
        // Load initial data
        loadInitialData()
    }
    
    /**
     * Load initial data for the deck screen
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Combine user profile and saved choices
                combine(
                    userProfileRepository.userProfile,
                    savedChoiceRepository.savedChoices
                ) { userProfile, savedChoices ->
                    DeckUiState(
                        selectedActivity = userProfile.selectedActivities.firstOrNull(),
                        savedChoicesCount = savedChoices.size,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                    android.util.Log.d("DeckViewModel", "UI state updated: ${newState.selectedActivity?.name}")
                }
            } catch (e: Exception) {
                handleError(e, "loadInitialData")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * Load activity content for the selected activity
     */
    fun loadActivityContent() {
        executeWithErrorHandling("loadActivityContent") {
            // TODO: Implement actual content loading from repository
            // For now, create a placeholder content
            val placeholderContent = ActivityContent(
                id = "placeholder_1",
                title = "Go to a local park",
                description = "Enjoy nature and get some fresh air at a nearby park.",
                imageUrl = "https://example.com/park.jpg",
                category = "outdoor",
                metadata = mapOf(
                    "duration" to "2-3 hours",
                    "difficulty" to "Easy",
                    "location" to "Local area"
                )
            )
            
            _currentContent.value = placeholderContent
            android.util.Log.d("DeckViewModel", "Loaded content: ${placeholderContent.title}")
        }
    }
    
    /**
     * Handle user action on the current card
     */
    fun handleCardAction(action: CardAction) {
        val content = _currentContent.value ?: return
        
        when (action) {
            CardAction.SAVE -> saveChoice(content)
            CardAction.ACCEPT -> acceptChoice(content)
            CardAction.DECLINE -> declineChoice(content)
        }
        
        // Load next content
        loadActivityContent()
    }
    
    /**
     * Save the current choice
     */
    private fun saveChoice(content: ActivityContent) {
        executeWithErrorHandling("saveChoice") {
            val savedChoice = SavedChoice(
                id = content.id,
                title = content.title,
                description = content.description,
                imageUrl = content.imageUrl ?: "",
                activityType = content.category,
                savedAt = System.currentTimeMillis()
            )
            
            savedChoiceRepository.addSavedChoice(savedChoice)
            android.util.Log.d("DeckViewModel", "Choice saved: ${content.title}")
        }
    }
    
    /**
     * Accept the current choice (save and proceed)
     */
    private fun acceptChoice(content: ActivityContent) {
        saveChoice(content)
        android.util.Log.d("DeckViewModel", "Choice accepted: ${content.title}")
    }
    
    /**
     * Decline the current choice
     */
    private fun declineChoice(content: ActivityContent) {
        android.util.Log.d("DeckViewModel", "Choice declined: ${content.title}")
        // Just log for now, no action needed
    }
    
    /**
     * Refresh the current content
     */
    fun refreshContent() {
        android.util.Log.d("DeckViewModel", "Refreshing content")
        loadActivityContent()
    }
}

/**
 * UI State for the Deck Screen
 */
data class DeckUiState(
    val selectedActivity: com.v7h.whattodonext.data.model.ActivityType? = null,
    val savedChoicesCount: Int = 0,
    val isLoading: Boolean = true
)

/**
 * Actions that can be performed on a card
 */
enum class CardAction {
    SAVE, ACCEPT, DECLINE
}
