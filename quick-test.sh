#!/bin/bash

# Quick Test Script - Fast build and deploy for development

set -e

echo "⚡ Quick Test - What To Do Next"
echo "=============================="

# Check device
DEVICE_COUNT=$(adb devices | grep -c "device$" || echo "0")
if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "❌ No device connected!"
    exit 1
fi

echo "✅ Device ready"

# Fast build and install
echo "🔨 Quick build..."
./gradlew assembleDebug --quiet

echo "📱 Installing..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "🚀 Launching..."
adb shell am start -n com.v7h.whattodonext/.MainActivity

echo "✅ Done! Check your device"
