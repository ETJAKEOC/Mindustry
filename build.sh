#!/usr/bin/env bash
set -e

# Mindustry Build Interface Script
# Usage: ./build.sh [target]

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT/Mindustry"

print_usage() {
    echo "Mindustry Build Interface"
    echo "Usage: ./build.sh [target]"
    echo ""
    echo "Available Targets:"
    echo "  desktop    - Build Desktop runnable JAR (:desktop:dist)"
    echo "  android    - Build Android APK (:android:assembleDebug)"
    echo "  server     - Build Server JAR (:server:dist)"
    echo "  test       - Run all unit test suites (:test)"
    echo "  clean      - Clean build artifacts and temporary files"
    echo "  help       - Show this help message"
    echo ""
}

check_java() {
    if ! command -v java &> /dev/null; then
        echo "Error: Java is not installed or not in PATH."
        exit 1
    fi
}

TARGET="${1:-help}"

case "$TARGET" in
    desktop)
        check_java
        echo "[Build] Building Desktop Distribution..."
        ./gradlew :desktop:dist
        mkdir -p "$PROJECT_ROOT/output/desktop"
        cp desktop/build/libs/Mindustry.jar "$PROJECT_ROOT/output/desktop/Mindustry.jar"
        echo "[Build] Desktop build completed -> output/desktop/Mindustry.jar"
        ;;
    android)
        check_java
        echo "[Build] Building Android APK..."
        ./gradlew :android:assembleDebug
        mkdir -p "$PROJECT_ROOT/output/android"
        find android/build/outputs/apk -name "*.apk" -exec cp {} "$PROJECT_ROOT/output/android/" \;
        echo "[Build] Android build completed -> output/android/"
        ;;
    server)
        check_java
        echo "[Build] Building Server Distribution..."
        ./gradlew :server:dist
        mkdir -p "$PROJECT_ROOT/output/server"
        cp server/build/libs/server-release.jar "$PROJECT_ROOT/output/server/server.jar"
        echo "[Build] Server build completed -> output/server/server.jar"
        ;;
    test)
        check_java
        echo "[Build] Executing test suites..."
        ./gradlew test
        echo "[Build] Tests completed successfully."
        ;;
    clean)
        check_java
        echo "[Build] Cleaning project artifacts..."
        ./gradlew clean
        rm -rf build/packr/ deploy/ "$PROJECT_ROOT/output/"
        echo "[Build] Clean completed."
        ;;
    help|--help|-h)
        print_usage
        ;;
    *)
        echo "Unknown target: $TARGET"
        echo ""
        print_usage
        exit 1
        ;;
esac
