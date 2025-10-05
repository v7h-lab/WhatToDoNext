package com.v7h.whattodonext.data.repository

import com.v7h.whattodonext.data.model.SavedChoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing saved choices
 * 
 * In Step 3, this will handle:
 * - Adding items when user swipes right
 * - Retrieving all saved items
 * - Managing the saved choices list
 * 
 * Applied Rules: Debug logs, comments, StateFlow for state management
 */
class SavedChoiceRepository {
    
    // In-memory storage for Step 3 (will be replaced with persistent storage later)
    private val _savedChoices = MutableStateFlow<List<SavedChoice>>(emptyList())
    val savedChoices: StateFlow<List<SavedChoice>> = _savedChoices.asStateFlow()
    
    /**
     * Add a new saved choice when user swipes right
     */
    fun addSavedChoice(choice: SavedChoice) {
        android.util.Log.d("SavedChoiceRepository", "Adding saved choice: ${choice.title}")
        
        val currentList = _savedChoices.value.toMutableList()
        currentList.add(choice)
        _savedChoices.value = currentList
        
        android.util.Log.d("SavedChoiceRepository", "Total saved choices: ${currentList.size}")
    }
    
    /**
     * Get all saved choices
     */
    fun getAllSavedChoices(): List<SavedChoice> {
        android.util.Log.d("SavedChoiceRepository", "Retrieving ${_savedChoices.value.size} saved choices")
        return _savedChoices.value
    }
    
    /**
     * Remove a saved choice (for future use)
     */
    fun removeSavedChoice(choiceId: String) {
        android.util.Log.d("SavedChoiceRepository", "Removing saved choice: $choiceId")
        
        val currentList = _savedChoices.value.toMutableList()
        currentList.removeAll { it.id == choiceId }
        _savedChoices.value = currentList
        
        android.util.Log.d("SavedChoiceRepository", "Remaining saved choices: ${currentList.size}")
    }
}
