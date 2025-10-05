package com.v7h.whattodonext.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Custom border radius values from design system
val CustomShapes = Shapes(
    // Default border radius: 0.75rem (12dp)
    small = RoundedCornerShape(12.dp),
    // Large border radius: 1rem (16dp) 
    medium = RoundedCornerShape(16.dp),
    // Extra large border radius: 1.5rem (24dp)
    large = RoundedCornerShape(24.dp),
    // Full border radius: 9999px (fully rounded)
    extraLarge = RoundedCornerShape(9999.dp)
)

// Additional custom shapes for specific components
val CardShape = RoundedCornerShape(12.dp) // DEFAULT
val ButtonShape = RoundedCornerShape(12.dp) // DEFAULT  
val CircularButtonShape = RoundedCornerShape(9999.dp) // Full circle for action buttons
val DropdownShape = RoundedCornerShape(12.dp) // DEFAULT
val BottomNavShape = RoundedCornerShape(24.dp) // XL for bottom nav
