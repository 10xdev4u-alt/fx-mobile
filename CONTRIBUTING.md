# Contributing to fx-mobile

> Thank you for your interest in contributing! This document will help you get started.

## Getting Prerequisites

### Required Tools
- **Android Studio** (latest stable) or **Android SDK command-line tools**
- **JDK 17** (OpenJDK recommended)
- **Git**

### Optional Tools
- **Zig 0.16+** (for future Zig core development)
- **Node.js 20+** (for landing page development)

## Setting Up Development Environment

### 1. Clone the Repository
```bash
git clone https://github.com/10xdev4u-alt/fx-mobile.git
cd fx-mobile
```

### 2. Set Up Android SDK

#### Option A: Android Studio (Recommended)
1. Download [Android Studio](https://developer.android.com/studio)
2. Open the project in Android Studio
3. Let it download the SDK automatically

#### Option B: Command-Line Tools
```bash
# Download SDK command-line tools
mkdir -p ~/Android/Sdk
cd ~/Android/Sdk
# Download from https://developer.android.com/studio#command-tools

# Set environment variables
export ANDROID_HOME=~/Android/Sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH

# Install required SDK components
sdkmanager "platforms;android-35"
sdkmanager "build-tools;35.0.0"
```

### 3. Create local.properties
```bash
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

### 4. Build the Project
```bash
./gradlew assembleDebug
```

### 5. Run Tests
```bash
./gradlew test
```

## Project Structure

```
fx-mobile/
├── app/                          # Main Android app
│   ├── src/main/
│   │   ├── java/dev/tenx/fxmobile/
│   │   │   ├── analytics/        # Analytics and telemetry
│   │   │   ├── bridge/           # JNI bridge to Zig core
│   │   │   ├── data/             # Data layer
│   │   │   │   ├── local/db/     # Room database
│   │   │   │   ├── remote/       # Retrofit API clients
│   │   │   │   ├── repository/   # Repositories
│   │   │   │   └── sync/         # Sync manager
│   │   │   ├── di/               # Hilt dependency injection
│   │   │   ├── domain/model/     # Domain models
│   │   │   ├── security/         # Security policy
│   │   │   ├── service/          # Foreground services
│   │   │   ├── subagent/         # Subagent manager
│   │   │   ├── terminal/         # Shell executor
│   │   │   ├── tools/            # Tool registry
│   │   │   ├── ui/               # User interface
│   │   │   ├── util/             # Utilities
│   │   │   ├── viewmodel/        # ViewModels
│   │   │   └── widget/           # Home screen widgets
│   │   └── res/                  # Resources
│   ├── src/test/                 # Unit tests
│   └── src/androidTest/          # Instrumented tests
├── docs/                         # Documentation
├── landing/                      # Landing page source
└── libfx/                        # Zig core (stub)
```

## Development Workflow

### 1. Create a Feature Branch
```bash
git checkout -b feat/your-feature-name
```

### 2. Make Your Changes
- Follow existing code patterns
- Write tests for new features
- Update documentation

### 3. Commit (Conventional Commits)
```
feat: add new terminal command
fix: resolve session loading bug
docs: update README
test: add unit tests for ToolRegistry
```

### 4. Push and Create PR
```bash
git push origin feat/your-feature-name
gh pr create --title "feat: your feature" --body "Description"
```

### 5. CI Checks Must Pass
- Lint (ktlint)
- Unit tests
- Build
- Code quality (Detekt)

## Coding Guidelines

### Kotlin Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Maximum line length: 120 characters

### Compose Guidelines
- Use Material 3 components
- Follow unidirectional data flow
- Use `collectAsStateWithLifecycle()` for flows

### Architecture Guidelines
- Keep business logic in ViewModels
- Use repositories for data access
- Inject dependencies with Hilt
- Use Flow for reactive data

## Testing

### Unit Tests
```kotlin
@Test
fun `test description`() = runTest {
    // Arrange
    val repository = SessionRepository(...)
    
    // Act
    val result = repository.sendMessage(...)
    
    // Assert
    assertTrue(result.isSuccess)
}
```

### Running Tests
```bash
# All tests
./gradlew test

# Single test class
./gradlew test --tests "dev.tenx.fxmobile.SessionRepositoryTest"

# With coverage
./gradlew testDebugUnitTest
```

## Building for Release

### Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Signed Release APK
Create `keystore.properties` in project root:
```properties
storeFile=/path/to/keystore.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

Then build:
```bash
./gradlew assembleRelease
```

## Troubleshooting

### SDK Location Not Found
```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable.
```
**Fix**: Create `local.properties` with `sdk.dir=/path/to/sdk`

### kapt Errors
```
Provided Metadata instance has version 2.2.0, while maximum supported version is 2.0.0.
```
**Fix**: Ensure coroutines version is 1.8.1 (not 1.11.0)

### Build Fails with AAR Metadata
```
Dependency 'androidx.core:core:1.19.0' requires compile against version 37.
```
**Fix**: Pin `androidx.core:core-ktx` to `1.13.1`

## Questions?

- Check existing [issues](https://github.com/10xdev4u-alt/fx-mobile/issues)
- Read the [ADRs](docs/adry/)
- Ask in the [discussions](https://github.com/10xdev4u-alt/fx-mobile/discussions)
