#!/bin/bash

# Device Testing Script - Check connection and app status

echo "🔍 Device Testing - What To Do Next"
echo "=================================="

# Check ADB
echo "📱 Checking ADB connection..."
if ! command -v adb &> /dev/null; then
    echo "❌ ADB not found! Please install Android SDK"
    exit 1
fi

# List devices
echo ""
echo "📱 Connected devices:"
adb devices

echo ""
echo "📱 Device details:"
DEVICE_ID=$(adb devices | grep 'device$' | head -1 | cut -f1)
if [ -n "$DEVICE_ID" ]; then
    echo "Device ID: $DEVICE_ID"
    echo "Android Version: $(adb shell getprop ro.build.version.release)"
    echo "API Level: $(adb shell getprop ro.build.version.sdk)"
    echo "Model: $(adb shell getprop ro.product.model)"
    echo "Brand: $(adb shell getprop ro.product.brand)"
else
    echo "❌ No devices connected!"
    echo ""
    echo "To connect a device:"
    echo "1. Enable Developer Options on your Android device"
    echo "2. Enable USB Debugging"
    echo "3. Connect via USB cable"
    echo "4. Accept the debugging prompt on your device"
    exit 1
fi

echo ""
echo "📱 App status:"
if adb shell pm list packages | grep -q "com.v7h.whattodonext"; then
    echo "✅ App is installed"
    echo "Version: $(adb shell dumpsys package com.v7h.whattodonext | grep versionName | cut -d'=' -f2)"
else
    echo "❌ App not installed"
fi

echo ""
echo "✅ Device is ready for testing!"
