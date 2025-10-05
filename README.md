# What To Do Next

An Android app designed to reduce cognitive overload and help users make decisions faster using a card-swiping interface.

## 🎯 Project Overview

**What To Do Next** presents activity options one at a time in a swipeable card format, allowing users to quickly accept, decline, or save suggestions across various categories like outdoor activities, movies, restaurants, and more.

## ✅ Step 1 Completed: Core UI with Placeholder Data

### 🏗️ What's Built

#### **Core Architecture**
- **MainActivity.kt** - Entry point with Hilt DI and Compose setup
- **Custom Theme System** - Complete design system with your colors, fonts, and border radius
- **Navigation Structure** - Basic navigation between Deck and Detail screens

#### **Screens Implemented**
- **S-002: DeckScreen** - Main card interface with:
  - "What to do?" title with settings icon
  - Activity selector dropdown ("Outdoor activities")
  - Large card with park image, title, and description
  - Three action buttons (Decline/Save/Accept)
- **S-003: DetailScreen** - Expanded view with:
  - Hero image and detailed information
  - Duration, difficulty, and location details
  - Extended description and tips

#### **Design System Applied**
- **Colors**: Your custom palette with primary green (#38e07b)
- **Typography**: Plus Jakarta Sans font family structure
- **Border Radius**: 12dp default, 16dp large, 24dp XL, full circle
- **Dark Mode**: Complete light/dark theme support

### 🎨 UI Features Matching Reference

✅ **Header**: "What to do?" title + Settings gear icon  
✅ **Activity Selector**: "Outdoor activities" dropdown with chevron  
✅ **Main Card**: Large park image + "Go to a local park" title + description  
✅ **Action Buttons**: Three circular buttons (Decline/Save/Accept)  
✅ **Navigation**: Basic navigation between screens  

### 📱 Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose
- **DI**: Hilt (configured but not yet used)
- **Image Loading**: Coil
- **Theme**: Custom design system with dark mode

### 🚀 Ready for Next Steps

The app is now ready for **Step 2: Implement Core Swipe & Navigation** which will add:
- Card swiping mechanics
- Gesture detection for swipe left/right
- Enhanced navigation flow

## 🏃‍♂️ How to Run

1. Open project in Android Studio
2. Sync Gradle files
3. Run on device/emulator (API 24+)

## 📁 Project Structure

```
app/src/main/java/com/v7h/whattodonext/
├── MainActivity.kt
├── WhatToDoNextApplication.kt
└── presentation/
    ├── navigation/
    │   ├── AppNavigation.kt
    │   └── Screen.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   ├── Typography.kt
    │   └── Shape.kt
    └── ui/screen/
        ├── deck/DeckScreen.kt
        └── detail/DetailScreen.kt
```

## 🎯 Next: Step 2 - Core Swipe & Navigation

Ready to implement the card swiping mechanics and enhanced navigation flow!
