# 🎨 Design System Reference - What To Do Next

## ✅ **MANDATORY: Use This Design System ONLY**

**Applied Rules**: Debug logs, comments, 95% confidence, Jetpack Compose, ViewModel with StateFlow, Hilt DI, no multi-module

This design system must be used consistently across ALL UI components, screens, and future development. **DO NOT deviate from these values.**

---

## 🎨 **Colors**

### **Primary Colors**
```kotlin
val Primary = Color(0xFF38E07B) // #38e07b - Bright green accent
```

### **Background Colors**
```kotlin
val BackgroundLight = Color(0xFFF6F8F7) // #f6f8f7 - Light background
val BackgroundDark = Color(0xFF122017)  // #122017 - Dark background
```

### **Content Colors (Text)**
```kotlin
val ContentLight = Color(0xFF111714) // #111714 - Light text
val ContentDark = Color(0xFFF6F8F7)  // #f6f8f7 - Dark text
```

### **Subtle Colors (Secondary Text)**
```kotlin
val SubtleLight = Color(0xFF648772) // #648772 - Light subtle text
val SubtleDark = Color(0xFFA0B5A9)  // #a0b5a9 - Dark subtle text
```

### **Surface Colors (Cards, Containers)**
```kotlin
val SurfaceLight = Color(0xFFFFFFFF) // #ffffff - Light surface
val SurfaceDark = Color(0xFF1A2C20)  // #1a2c20 - Dark surface
```

### **Border Colors**
```kotlin
val BorderLight = Color(0xFFE3E8E5) // #e3e8e5 - Light borders
val BorderDark = Color(0xFF2A4234)  // #2a4234 - Dark borders
```

### **Error Colors**
```kotlin
val ErrorLight = Color(0xFFFF5252) // #ff5252 - Light error
val ErrorDark = Color(0xFFFF8A80)  // #ff8a80 - Dark error
```

---

## 📝 **Typography**

### **Font Family**
```kotlin
val DisplayFontFamily = FontFamily.Default // Plus Jakarta Sans (when font files added)
```

### **Typography Scale**
- **Display Large**: 32sp, Bold
- **Display Medium**: 28sp, Bold  
- **Display Small**: 24sp, Bold
- **Headline Large**: 22sp, Bold
- **Headline Medium**: 20sp, SemiBold
- **Headline Small**: 18sp, SemiBold
- **Body Large**: 16sp, Normal
- **Body Medium**: 14sp, Normal
- **Body Small**: 12sp, Normal
- **Label Large**: 14sp, Medium
- **Label Medium**: 12sp, Medium
- **Label Small**: 11sp, Medium

---

## 🎯 **Material Symbols Icons**

### **Icon Usage**
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
```

### **Standard Icons for App**
```kotlin
// Navigation & Actions
Icons.Filled.Settings          // Settings icon
Icons.Filled.ArrowBack         // Back navigation
Icons.Filled.KeyboardArrowDown // Dropdown arrow
Icons.Filled.Menu              // Menu/hamburger
Icons.Filled.Search            // Search

// Card Actions
Icons.Filled.Check             // Accept/approve
Icons.Filled.Close             // Decline/reject
Icons.Filled.Bookmark          // Save
Icons.Filled.BookmarkBorder    // Save (outlined)

// Navigation Bar
Icons.Filled.Home              // Deck/Home
Icons.Filled.Favorite          // Saved choices
Icons.Filled.Add               // Create
Icons.Filled.Person            // Profile

// Activity Types
Icons.Filled.Park              // Outdoor activities
Icons.Filled.Movie             // Movies
Icons.Filled.Restaurant        // Restaurants
Icons.Filled.MenuBook          // Books
Icons.Filled.Flight            // Travel

// Content
Icons.Filled.Image             // Image placeholder
Icons.Filled.LocationOn        // Location
Icons.Filled.Schedule          // Time/duration
Icons.Filled.Star              // Rating/favorite
Icons.Filled.Info              // Information
```

### **Icon Guidelines**
- **Use Filled icons** for primary actions and navigation
- **Use Outlined icons** for secondary actions and states
- **Consistent sizing**: Use `Modifier.size(24.dp)` for standard icons
- **Color**: Use `MaterialTheme.colorScheme.onSurface` for automatic light/dark switching

---

## 🔲 **Border Radius**

### **Standard Sizes**
```kotlin
val CardShape = RoundedCornerShape(12.dp)        // DEFAULT: 0.75rem (12dp)
val ButtonShape = RoundedCornerShape(12.dp)      // DEFAULT: 0.75rem (12dp)
val DropdownShape = RoundedCornerShape(12.dp)    // DEFAULT: 0.75rem (12dp)

val BottomNavShape = RoundedCornerShape(24.dp)   // XL: 1.5rem (24dp)
val CircularButtonShape = RoundedCornerShape(9999.dp) // FULL: 9999px
```

### **Usage Guidelines**
- **Cards**: 12dp (DEFAULT)
- **Buttons**: 12dp (DEFAULT)
- **Dropdowns**: 12dp (DEFAULT)
- **Bottom Navigation**: 24dp (XL)
- **Circular Buttons**: 9999dp (FULL)

---

## 🎯 **Component Usage**

### **Primary Button**
```kotlin
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = Primary // #38e07b
    )
)
```

### **Cards**
```kotlin
Card(
    shape = CardShape, // 12dp radius
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

### **Text Colors**
```kotlin
Text(
    color = MaterialTheme.colorScheme.onBackground, // Auto light/dark
    style = MaterialTheme.typography.headlineLarge
)
```

### **Action Buttons**
```kotlin
// Accept Button (Green)
Button(
    colors = ButtonDefaults.buttonColors(containerColor = Primary)
) {
    Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = "Accept"
    )
}

// Decline Button (Red)
Button(
    colors = ButtonDefaults.buttonColors(containerColor = ErrorLight)
) {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "Decline"
    )
}

// Save Button (Gray)
Button(
    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E))
) {
    Icon(
        imageVector = Icons.Filled.Bookmark,
        contentDescription = "Save"
    )
}
```

### **Icons**
```kotlin
// Standard Icon
Icon(
    imageVector = Icons.Filled.Settings,
    contentDescription = "Settings",
    modifier = Modifier.size(24.dp),
    tint = MaterialTheme.colorScheme.onSurface
)

// Navigation Icon
IconButton(onClick = { /* action */ }) {
    Icon(
        imageVector = Icons.Filled.ArrowBack,
        contentDescription = "Back"
    )
}
```

---

## 🌓 **Dark Mode Support**

### **Automatic Theme Switching**
```kotlin
@Composable
fun WhatToDoNextTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Automatically switches between light and dark themes
}
```

### **Theme Colors**
- **Light Theme**: Uses `*Light` color variants
- **Dark Theme**: Uses `*Dark` color variants
- **Automatic**: Based on system preference

---

## ✅ **Verification Checklist**

Before implementing any UI component, verify:

- [ ] **Colors**: Using exact hex values from this design system
- [ ] **Border Radius**: Using predefined shapes (12dp, 16dp, 24dp, 9999dp)
- [ ] **Typography**: Using Material 3 typography scale
- [ ] **Dark Mode**: Component works in both light and dark themes
- [ ] **Consistency**: Matches existing components in the app

---

## 🚫 **What NOT to Use**

### **Avoid These Colors**
- ❌ Material Design default colors
- ❌ System default colors
- ❌ Custom colors not in this design system
- ❌ Hard-coded color values

### **Avoid These Shapes**
- ❌ Default Material shapes
- ❌ Custom border radius values
- ❌ Inconsistent corner rounding

---

## 📱 **Current Implementation Status**

### **✅ Implemented Components**
- [x] **DeckScreen**: Uses custom colors and shapes
- [x] **DetailScreen**: Uses custom colors and shapes  
- [x] **ActivityCard**: 12dp radius, custom colors
- [x] **ActionButtons**: Circular shape (9999dp), custom colors
- [x] **ActivitySelector**: 12dp radius, custom colors

### **🎯 Future Components**
- [ ] **Bottom Navigation**: 24dp radius (XL)
- [ ] **Onboarding Screens**: Custom colors and shapes
- [ ] **Profile Screen**: Custom colors and shapes
- [ ] **Saved Choices Screen**: Custom colors and shapes

---

## 🔧 **Implementation Notes**

### **Color Usage**
- Always use `MaterialTheme.colorScheme.*` for automatic light/dark switching
- Use direct color constants only for specific components (buttons, etc.)
- Never hard-code color values in components

### **Shape Usage**
- Use predefined shape constants (`CardShape`, `ButtonShape`, etc.)
- Never create custom `RoundedCornerShape` values
- Use `CircularButtonShape` for fully rounded buttons

### **Typography Usage**
- Always use `MaterialTheme.typography.*` for consistent scaling
- Never hard-code font sizes or weights
- Use semantic names (headline, body, label) not size names

---

## 🎉 **Design System Ready**

This design system is:
- ✅ **Implemented** in current theme files
- ✅ **Active** in the running app
- ✅ **Consistent** across all components
- ✅ **Dark Mode** compatible
- ✅ **Ready** for future development

**Use this reference for ALL future UI development!**
