#!/bin/bash

echo "ANDROID_SDK is $ANDROID_SDK"

# Accept the license agreements to auto-download SDK components
mkdir -p "$ANDROID_SDK/licenses"
echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > "$ANDROID_SDK/licenses/android-sdk-license"

# Build the debug variants of the application
./gradlew clean assembleDebug -PdisablePreDex
