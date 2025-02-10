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
- Input validation errors
- Camera permission errors
- Database operation errors
- Image capture errors

## Installation

Add to your app's build.gradle:
```groovy
dependencies {
    implementation 'com.valify:registration-sdk:1.0.0'
}
```

## Required Permissions
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera.front" />
```

## Basic Usage

1. Initialize SDK:
```kotlin
ValifySDK.initialize(context)
```

2. Start Registration:
```kotlin
ValifyRegistration.startRegistration(
    onSuccess = { userId -> 
        // Handle success
    },
    onError = { error ->
        // Handle error
    }
)
```

## Validation Rules
- Username: Required
- Email: Valid email format
- Phone: Valid phone number format
- Password: Minimum requirements enforced

## Camera Features
- Front camera activation
- Real-time smile detection
- Automatic image capture
- Image storage in local database

## Responsive Design
- Supports different screen sizes
- Adapts to device orientations
- Handles runtime permissions

## Support
For issues and questions:
support@valify.com
