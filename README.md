# Valify Registration SDK

A registration SDK for Android applications with selfie verification capabilities.

## Features

### Registration Step 1: User Data Collection
- Username validation
- Phone number validation
- Email validation
- Password validation
- Local database storage using Room

### Registration Step 2: Selfie Capture
- Front camera integration
- Smile detection
- Automatic capture
- Local storage of captured image

## Technical Stack
- Jetpack Compose for UI
- Navigation Component for flow management
- Hilt for dependency injection
- Coroutines for asynchronous operations
- Room for local database
- CameraX and ML Kit for selfie capture

## Architecture
Following Clean Architecture principles:
```
src/
├── data/           # Data layer
│   ├── local/      # Room database
│   └── repository/ # Repository implementations
├── domain/         # Business logic
│   ├── model/      # Domain models
│   ├── repository/ # Repository interfaces
│   ├── use_case/   # Use cases
│   └── validation/ # Validation logic
└── presentation/   # UI layer
    ├── components/ # Reusable UI components
    ├── registration/# Registration screen
    └── selfie/     # Selfie capture screen
```

## Error Handling

### Registration Errors
1. **Input Validation Errors**
   - Username: Must be at least 3 characters
   - Email: Must be in valid email format
   - Phone: Must be 10-13 digits with optional + prefix
   - Password: Must be 8+ characters with uppercase, lowercase, and number

2. **Database Errors**
   - Storage capacity errors
   - Database access errors
   - Data corruption errors

3. **Camera Errors**
   - Permission denied
   - Camera unavailable
   - Camera initialization failed
   - Low memory during capture

4. **Storage Errors**
   - Permission denied
   - Insufficient storage space
   - File access errors
   - File corruption

### Error Recovery Strategies
1. **Input Validation**
   ```kotlin
   when (val result = validationService.validateEmail(email)) {
       is ValidationResult.Error -> showError(result.message)
       is ValidationResult.Success -> proceedWithRegistration()
   }
   ```

2. **Camera Handling**
   ```kotlin
   try {
       cameraProvider.bindToLifecycle(
           lifecycleOwner,
           cameraSelector,
           preview,
           imageAnalyzer
       )
   } catch (e: Exception) {
       when (e) {
           is CameraUnavailableException -> showError("Camera unavailable")
           is SecurityException -> requestCameraPermission()
           else -> showError("Camera error: ${e.message}")
       }
   }
   ```

### Error Prevention
1. **Permission Checks**
   - Runtime permission requests
   - Graceful fallbacks
   - Clear user instructions

2. **Data Validation**
   - Input sanitization
   - Format verification
   - Size limitations

3. **Resource Management**
   - Memory monitoring
   - Storage space verification
   - Camera state tracking

## Installation

1. Add the registration SDK module to your project's `settings.gradle.kts`:
```kotlin
include(":app", ":registration_sdk")
```

2. Add the dependency to your app's `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":registration_sdk"))
}
```

## Required Permissions
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.front" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28"/>
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
```

## Integration Guide

### 1. Initialize SDK in Application Class

#### Option 1: Using SDK Theme
```kotlin
@HiltAndroidApp
class ValifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        ValifyRegistrationSDK.initialize(
            ValifyRegistrationConfig(
                context = applicationContext,
                onRegistrationComplete = { userId ->
                    // Handle successful registration
                },
                onRegistrationError = { error ->
                    // Handle registration error
                },
                enableAutoNavigation = true
            )
        )
    }
}
```

#### Option 2: Using App Theme
```kotlin
@HiltAndroidApp
class ValifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        ValifyRegistrationSDK.initialize(
            ValifyRegistrationConfig(
                context = applicationContext,
                onRegistrationComplete = { userId ->
                    // Handle successful registration
                },
                onRegistrationError = { error ->
                    // Handle registration error
                },
                enableAutoNavigation = true
                // No theme configuration - will use app's theme
            )
        )
    }
}
```

### 2. Implement in MainActivity
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ValifyTheme { // Use your app's theme here
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Let the SDK handle navigation
                    ValifyRegistrationSDK.getConfig().navigator.RegistrationNavigation(navController)
                }
            }
        }
    }
}
```

## Navigation Flow
1. Registration Screen: User enters registration details
2. Selfie Screen: User captures selfie
3. Completion: SDK triggers onRegistrationComplete callback

## Customization
- Use your app's theme by omitting the theme configuration in SDK initialization
- Navigation can be controlled with enableAutoNavigation flag
- Success and error callbacks can be customized in SDK initialization

## Best Practices
1. Initialize SDK in Application class
2. Use Hilt for dependency injection
3. Handle all error cases in callbacks
4. Follow Material Design guidelines for UI consistency
5. Implement proper permission handling
