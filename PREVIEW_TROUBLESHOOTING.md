# 🔧 Preview Troubleshooting Guide

## 🚨 **Issue: "I'm still running into issues accessing the project files"**

This error typically means there are project configuration issues preventing Android Studio from properly recognizing the files. Here's how to fix it:

## ✅ **Step-by-Step Fix**

### **Step 1: Open Project Correctly**
```bash
# Navigate to the project directory
cd "/Users/Metal/Desktop/What to do/WhatToDoNext"

# Open the PROJECT DIRECTORY (not a file) in Android Studio
open -a "Android Studio" .
```

### **Step 2: Wait for Gradle Sync**
- Android Studio should automatically detect this is an Android project
- Wait for the "Gradle sync" notification at the bottom
- Click **"Sync Now"** if prompted
- Wait for sync to complete (progress bar at bottom)

### **Step 3: Test with Simple Preview**
1. Open: `app/src/main/java/com/v7h/whattodonext/presentation/ui/TestPreview.kt`
2. This is a simple test file with basic previews
3. Look for the **"Design"** tab at the bottom of the editor
4. Click **"Design"** to see the preview

### **Step 4: If Preview Still Doesn't Work**

#### **Option A: Clean and Rebuild**
1. **Build → Clean Project**
2. Wait for clean to complete
3. **Build → Rebuild Project**
4. Try preview again

#### **Option B: Invalidate Caches**
1. **File → Invalidate Caches and Restart**
2. Choose **"Invalidate and Restart"**
3. Wait for Android Studio to restart
4. Try preview again

#### **Option C: Check Project Structure**
1. **File → Project Structure**
2. Make sure **"Project SDK"** is set to Android API 34
3. Make sure **"Build Tools Version"** is set (usually auto-detected)
4. Click **"Apply"** and **"OK"**

### **Step 5: Verify Preview Works**
You should see:
- **TestPreviewLight**: Light theme with green button
- **TestPreviewDark**: Dark theme with green button
- Both should show "What To Do Next" title and test card

## 🎯 **Once Preview Works, Try Main Screens**

### **DeckScreen Preview**
1. Open: `app/src/main/java/com/v7h/whattodonext/presentation/ui/screen/deck/DeckScreen.kt`
2. Look for `@Preview` annotation at bottom
3. Click **"Design"** tab
4. You should see the park card with "Go to a local park"

### **DetailScreen Preview**
1. Open: `app/src/main/java/com/v7h/whattodonext/presentation/ui/screen/detail/DetailScreen.kt`
2. Look for `@Preview` annotation at bottom
3. Click **"Design"** tab
4. You should see detailed park information

## 🔍 **Common Issues & Solutions**

### **Issue: "Cannot resolve symbol" errors**
- **Solution**: Wait for Gradle sync to complete
- **Solution**: Try **File → Sync Project with Gradle Files**

### **Issue: Preview shows "Error"**
- **Solution**: Check that all imports are correct
- **Solution**: Make sure Compose dependencies are synced

### **Issue: "No preview available"**
- **Solution**: Look for `@Preview` annotations in the code
- **Solution**: Make sure you're in the **"Design"** tab, not **"Code"** tab

### **Issue: Project won't open**
- **Solution**: Make sure you're opening the FOLDER, not a file
- **Solution**: The folder should contain `build.gradle.kts` and `app/` directory

## 📱 **Alternative: Build and Run**

If preview still doesn't work, you can still test the app:

1. **Connect a device** or **start an emulator**
2. Click the **green "Run" button** (▶️)
3. Choose your device/emulator
4. The app will install and launch
5. You'll see the actual app running

## ✅ **Success Indicators**

You'll know it's working when you see:
- ✅ Gradle sync completes without errors
- ✅ TestPreview shows in Design tab
- ✅ DeckScreen shows park card
- ✅ DetailScreen shows detailed info
- ✅ Dark/light theme switching works

## 🆘 **Still Having Issues?**

If nothing works:
1. **Check Android Studio version** (should be recent)
2. **Check if Android SDK is installed**
3. **Try creating a new project** to test if Android Studio works
4. **Restart your computer** and try again

The project structure is correct - the issue is usually with Android Studio configuration or caching.
