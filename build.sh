#!/bin/bash
# Build script for fx-mobile

set -e

echo "🔨 Building fx-mobile v1.0.0..."

# Check for Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    if [ -f "local.properties" ]; then
        echo "📝 Using SDK from local.properties"
    else
        echo "❌ No Android SDK found!"
        echo ""
        echo "To build the APK:"
        echo "1. Install Android Studio or SDK command-line tools"
        echo "2. Set ANDROID_HOME or ANDROID_SDK_ROOT environment variable"
        echo "3. Or create local.properties with sdk.dir=/path/to/sdk"
        echo ""
        echo "Alternatively, the APK is available from GitHub Releases."
        echo "See: https://github.com/10xdev4u-alt/fx-mobile/releases"
        exit 1
    fi
fi

# Check for Java 17
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "❌ Java 17 is required. Found version $JAVA_VERSION"
    echo "Install OpenJDK 17: sudo pacman -S jdk17-openjdk"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -1)"

# Build debug APK
echo "📦 Building debug APK..."
./gradlew assembleDebug --no-daemon

# Build release APK
echo "📦 Building release APK..."
./gradlew assembleRelease --no-daemon

echo ""
echo "✅ Build complete!"
echo ""
echo "📱 Debug APK: app/build/outputs/apk/debug/app-debug.apk"
echo "📦 Release APK: app/build/outputs/apk/release/app-release.apk"
echo ""
echo "To install on device:"
echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
