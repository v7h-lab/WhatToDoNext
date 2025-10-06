#!/bin/bash

# Build and Preview Script - What To Do Next App
# This script builds the app and helps you preview it on device

echo "🚀 What To Do Next - Build & Preview"
echo "===================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the WhatToDoNext directory"
    exit 1
fi

# Check ADB
echo "📱 Checking ADB connection..."
if ! command -v adb &> /dev/null; then
    echo "❌ ADB not found! Please install Android SDK"
    echo "   Download from: https://developer.android.com/studio"
    exit 1
fi

# Check for connected devices
echo ""
echo "📱 Checking connected devices..."
DEVICE_ID=$(adb devices | grep 'device$' | head -1 | cut -f1)
if [ -z "$DEVICE_ID" ]; then
    echo "❌ No devices connected!"
    echo ""
    echo "To connect a device:"
    echo "1. Enable Developer Options on your Android device"
    echo "2. Enable USB Debugging"
    echo "3. Connect via USB cable"
    echo "4. Accept the debugging prompt on your device"
    echo ""
    echo "Alternatively, you can use an emulator:"
    echo "1. Open Android Studio"
    echo "2. Go to Tools > AVD Manager"
    echo "3. Create a new Virtual Device"
    echo "4. Start the emulator"
    exit 1
fi

echo "✅ Device connected: $DEVICE_ID"
echo "   Android Version: $(adb shell getprop ro.build.version.release)"
echo "   API Level: $(adb shell getprop ro.build.version.sdk)"
echo "   Model: $(adb shell getprop ro.product.model)"

# Clean and build
echo ""
echo "🔨 Building the app..."
echo "====================="

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "🔨 Building debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Please check the errors above."
    exit 1
fi

echo "✅ Build successful!"

# Install and launch
echo ""
echo "📱 Installing and launching app..."
echo "================================="

# Install the APK
echo "📦 Installing APK..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

if [ $? -ne 0 ]; then
    echo "❌ Installation failed!"
    exit 1
fi

echo "✅ App installed successfully!"

# Launch the app
echo "🚀 Launching app..."
adb shell am start -n com.v7h.whattodonext/.MainActivity

echo ""
echo "🎉 App launched successfully!"
echo ""
echo "📱 What to expect:"
echo "   • Onboarding screen (first time)"
echo "   • Main deck with swipeable cards"
echo "   • TMDB movie data (if API key is configured)"
echo "   • Gesture-based swiping (swipe left/right)"
echo "   • Button-based actions (Decline/Save/Accept)"
echo ""
echo "🔧 Debug features:"
echo "   • Check Android Studio Logcat for debug logs"
echo "   • Look for 'WhatToDoNext' tag in logs"
echo "   • Swipe gestures are logged with 'SwipeableCard' tag"
echo ""
echo "📱 To view logs:"
echo "   adb logcat | grep -E '(WhatToDoNext|SwipeableCard|MovieRepository|UserProfile)'"
echo ""
echo "✅ Ready to test the app!"
