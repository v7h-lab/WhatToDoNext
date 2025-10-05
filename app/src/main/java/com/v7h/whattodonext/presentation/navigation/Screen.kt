package com.v7h.whattodonext.presentation.navigation

/**
 * Screen route definitions for the What To Do Next app
 * 
 * Following the specification:
 * - S-001: Onboarding Screen
 * - S-002: Main Deck Screen  
 * - S-003: Detail Screen
 * - S-004: Saved Choices Screen
 * - S-005: Profile Screen
 */
object Screen {
    // Onboarding flow
    const val ONBOARDING = "onboarding"
    
    // Main app screens
    const val DECK = "deck"
    const val DETAIL = "detail"
    const val SAVED_CHOICES = "saved_choices"
    const val PROFILE = "profile"
    
    // Parameter keys for navigation
    object Params {
        const val ACTIVITY_CONTENT_ID = "activity_content_id"
        const val ACTIVITY_TYPE = "activity_type"
    }
    
    // Route builders with parameters
    fun detailRoute(contentId: String): String = "detail/$contentId"
    fun deckRoute(activityType: String = "outdoor"): String = "deck/$activityType"
}
