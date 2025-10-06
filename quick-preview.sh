#!/bin/bash

# Quick Preview Script - What To Do Next App
# Quick check and preview of the app

echo "⚡ Quick Preview - What To Do Next"
echo "=================================="

# Check if we're in the right directory
if [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Please run this script from the WhatToDoNext directory"
    exit 1
fi

# Check device connection
echo "📱 Checking device connection..."
DEVICE_ID=$(adb devices | grep 'device$' | head -1 | cut -f1)
if [ -z "$DEVICE_ID" ]; then
    echo "❌ No device connected!"
    echo "   Connect a device or start an emulator first"
    exit 1
fi

echo "✅ Device: $DEVICE_ID"

# Check if app is already installed
if adb shell pm list packages | grep -q "com.v7h.whattodonext"; then
    echo "✅ App is already installed"
    echo "🚀 Launching app..."
    adb shell am start -n com.v7h.whattodonext/.MainActivity
    echo ""
    echo "🎉 App launched! Check your device."
    echo ""
    echo "📱 Features to test:"
    echo "   • Swipe left/right on cards (gesture-based)"
    echo "   • Tap cards to view details"
    echo "   • Use action buttons (Decline/Save/Accept)"
    echo "   • Check bottom navigation"
    echo "   • Test with Movies activity (TMDB integration)"
    echo ""
    echo "🔧 View logs: adb logcat | grep WhatToDoNext"
else
    echo "❌ App not installed"
    echo "   Run: ./build-and-preview.sh to build and install"
fi
