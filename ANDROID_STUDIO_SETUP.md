# Android Studio Setup Guide

## 🚀 Quick Start

### 1. **Open Project in Android Studio**
```bash
# Navigate to the project directory
cd "/Users/Metal/Desktop/What to do/WhatToDoNext"

# Open in Android Studio
open -a "Android Studio" .
```

### 2. **Configure SDK Path**
If Android Studio asks for SDK path, it should automatically detect it. If not:
- Go to **File → Project Structure → SDK Location**
- Set Android SDK location to: `/Users/YOUR_USERNAME/Library/Android/sdk`

### 3. **Sync Project**
- Click **"Sync Now"** when prompted
- Or go to **File → Sync Project with Gradle Files**

### 4. **Preview in Android Studio**

#### **Live Preview (Recommended)**
1. Open any `.kt` file with `@Preview` annotations
2. Look for the **"Design"** tab at the bottom
3. Click **"Design"** to see live preview
4. You can see both `DeckScreenPreview` and `DetailScreenPreview`

#### **Interactive Preview**
1. In the preview pane, click the **"Interactive"** button
2. You can interact with buttons, dropdowns, and navigation
3. Perfect for testing the UI without running the app

#### **Build and Run**
1. Click the **"Run"** button (green play icon)
2. Choose a device/emulator
3. The app will install and launch

## 📱 What You'll See

### **DeckScreen Preview**
- "What to do?" title with settings icon
- "Outdoor activities" dropdown
- Large park card with image
- Three action buttons (Decline/Save/Accept)

### **DetailScreen Preview**  
- Hero image and detailed information
- Duration, difficulty, and location cards
- Extended description and tips

## 🎨 Design System Active

Your custom design system is fully integrated:
- **Primary Color**: `#38e07b` (bright green)
- **Typography**: Plus Jakarta Sans structure
- **Border Radius**: 12dp default, 16dp large, 24dp XL
- **Dark Mode**: Automatically switches based on system

## 🔧 Troubleshooting

### **Build Issues**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### **SDK Issues**
- Make sure Android SDK is installed
- Check `local.properties` has correct SDK path
- Ensure Android Studio is up to date

### **Preview Not Working**
- Make sure you have a device/emulator configured
- Check that Compose dependencies are synced
- Try **Build → Clean Project** then **Build → Rebuild Project**

## 🎯 Next Steps

Once you can see the previews working:
1. **Step 2**: Implement card swiping mechanics
2. **Step 3**: Add bottom navigation and saved list
3. **Step 4**: Connect to live data source

## 📋 Project Structure

```
WhatToDoNext/
├── app/
│   ├── src/main/java/com/v7h/whattodonext/
│   │   ├── MainActivity.kt                    # Entry point
│   │   ├── WhatToDoNextApplication.kt         # Hilt setup
│   │   └── presentation/
│   │       ├── theme/                         # Your design system
│   │       ├── navigation/                    # Navigation setup
│   │       └── ui/screen/                     # Deck & Detail screens
│   ├── build.gradle.kts                       # Dependencies
│   └── src/main/res/                          # Resources
├── build.gradle.kts                           # Project config
├── settings.gradle.kts                        # Project settings
└── local.properties                          # SDK path
```

## ✅ Verification Checklist

- [ ] Project opens in Android Studio
- [ ] Gradle sync completes successfully  
- [ ] No build errors in console
- [ ] Preview shows DeckScreen with park card
- [ ] Preview shows DetailScreen with detailed info
- [ ] Dark mode toggle works in preview
- [ ] App runs on device/emulator

**Ready to preview!** 🎉
