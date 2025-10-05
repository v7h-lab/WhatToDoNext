#!/bin/bash

# What To Do Next - Build and Deploy Script for Cursor
# This script builds the app and deploys it directly to your connected device

set -e  # Exit on any error

echo "🚀 What To Do Next - Building and Deploying to Device"
echo "=================================================="

# Check if device is connected
echo "📱 Checking connected devices..."
DEVICE_COUNT=$(adb devices | grep -c "device$" || echo "0")

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "❌ No Android devices connected!"
    echo "Please connect your device and enable USB debugging"
    exit 1
fi

echo "✅ Device connected: $(adb devices | grep 'device$' | cut -f1)"

# Navigate to project directory
cd "$(dirname "$0")"

echo ""
echo "🔨 Building APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "📦 Installing APK to device..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

if [ $? -ne 0 ]; then
    echo "❌ Installation failed!"
    exit 1
fi

echo ""
echo "🎉 Success! Launching app..."
adb shell am start -n com.v7h.whattodonext/.MainActivity

echo ""
echo "✅ What To Do Next app is now running on your device!"
echo "📱 Check your device to see the app in action"
