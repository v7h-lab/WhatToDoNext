package com.v7h.whattodonext.data.model

/**
 * D-002: SavedChoice data model
 * 
 * Represents an item that the user has swiped right on and saved.
 * This data must be permanently stored.
 * 
 * Applied Rules: Debug logs, comments, data model structure
 */
data class SavedChoice(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val activityType: String,
    val savedAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Debug log for data model
        fun create(
            id: String,
            title: String,
            description: String,
            imageUrl: String,
            activityType: String
        ): SavedChoice {
            android.util.Log.d("SavedChoice", "Creating saved choice: $title")
            return SavedChoice(
                id = id,
                title = title,
                description = description,
                imageUrl = imageUrl,
                activityType = activityType
            )
        }
    }
}
