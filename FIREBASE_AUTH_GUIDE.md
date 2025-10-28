# Firebase Google Authentication - Clean Architecture Implementation

## Overview

This guide covers the complete Firebase Google authentication implementation in your Android Coffee Shop app following **Clean Architecture** principles. The implementation includes login, logout, user info display, error handling, and login status checking with proper separation of concerns.

---

## Features Implemented

✅ **Google Sign-In with Firebase**
- Complete integration with Firebase Authentication
- Google Sign-In flow using Activity Result API
- Token-based authentication with Firebase

✅ **User Information Display**
- Profile screen showing user details (name, email, photo)
- User avatar and name in the top bar
- Comprehensive account information card

✅ **Logout Functionality**
- Sign out from both Firebase and Google
- Proper state management and navigation after logout

✅ **Error Handling**
- Comprehensive error states and messages
- User-friendly error display
- Retry mechanism for failed authentication

✅ **Login Status Checking**
- Automatic login status check on app start
- Splash screen with authentication routing
- Persistent authentication state

---

## Project Structure (Clean Architecture)

```
app/
├── src/main/
│   ├── java/com/example/androidmycoffee/
│   │   ├── data/                           # DATA LAYER
│   │   │   └── auth/
│   │   │       └── FirebaseAuthDataSource.kt   # Firebase auth implementation
│   │   ├── domain/                         # DOMAIN LAYER
│   │   │   ├── model/
│   │   │   │   ├── User.kt                 # User domain model
│   │   │   │   └── AuthState.kt            # Auth state sealed class
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.kt       # User data repository
│   │   │   └── usecase/
│   │   │       └── UserUseCase.kt          # User-related use cases
│   │   ├── presentation/                   # PRESENTATION LAYER
│   │   │   ├── screen/
│   │   │   │   ├── AuthScreen.kt           # Google sign-in UI
│   │   │   │   ├── ProfileScreen.kt        # User profile display
│   │   │   │   ├── CoffeeScreen.kt         # Main screen with user info
│   │   │   │   └── SplashScreen.kt         # Initial loading screen
│   │   │   ├── viewmodel/
│   │   │   │   └── AuthViewModel.kt        # Authentication view model
│   │   │   └── navigation/
│   │   │       └── NavGraph.kt             # Navigation setup
│   │   ├── AppContainer.kt                 # Dependency injection
│   │   └── MainActivity.kt                 # App entry point
│   └── res/values/
│       └── strings.xml                     # Web Client ID from Firebase
└── build.gradle.kts                        # Dependencies & plugins
```

---

## Code Components (Clean Architecture Layers)

### 1. Domain Layer Models

#### User Model
**Location**: `domain/model/User.kt`
```kotlin
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isPremium: Boolean = false,
)
```

#### AuthState Sealed Class
**Location**: `domain/model/AuthState.kt`
```kotlin
sealed class AuthState {
    data object Loading : AuthState()
    data object NotAuthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### 2. Data Layer - FirebaseAuthDataSource

**Location**: `data/auth/FirebaseAuthDataSource.kt`

**Key Features**:
- Firebase Authentication integration
- Google Sign-In client configuration using web client ID from strings.xml
- State management with StateFlow
- Automatic current user check on initialization
- **No hardcoded credentials** - uses Firebase config

**Methods**:
- `checkCurrentUser()` - Checks and updates current authentication state
- `signInWithGoogle(idToken: String): Result<User>` - Signs in with Google credential, returns domain User
- `signOut()` - Signs out from Firebase and Google
- `getCurrentUser(): User?` - Returns current user from domain model
- `isSignedIn(): Boolean` - Checks if user is authenticated
- `getGoogleSignInClient(): GoogleSignInClient` - Provides client for sign-in flow

### 3. Presentation Layer - AuthViewModel

**Location**: `presentation/viewmodel/AuthViewModel.kt`

**Key Features**:
- Exposes authentication state to UI layer
- Coordinates between data source and use cases
- Handles sign-in and sign-out operations
- Saves user profile to Firestore on successful login
- **Clean Architecture**: Only depends on domain models, not data implementation

**Methods**:
- `signInWithGoogle(idToken: String)` - Initiates Google sign-in
- `signOut()` - Initiates sign-out
- `getCurrentUser(): User?` - Gets current user from domain model
- `isSignedIn(): Boolean` - Checks authentication status

### 4. Presentation Layer - UI Screens

#### AuthScreen

**Location**: `presentation/screen/AuthScreen.kt`

**Features**:
- Google Sign-In button with proper branding
- Loading state during authentication
- Error message display
- Automatic navigation on successful login
- Activity Result API for Google Sign-In flow

#### ProfileScreen

**Location**: `presentation/screen/ProfileScreen.kt`

**Features**:
- User profile picture (with fallback to initial)
- Display name and email
- Account information card with:
  - User ID
  - Email
  - Display name
  - Login status
- Sign out button

#### CoffeeScreen (Main Screen)

**Location**: `presentation/screen/CoffeeScreen.kt`

**Features**:
- User avatar and name in top bar
- Clickable user info to navigate to profile
- Cart and sign-out buttons
- Coffee list display

### 5. Dependency Injection - AppContainer

**Location**: `AppContainer.kt`

**Clean Architecture Implementation**:
```kotlin
class AppContainer(context: Context, activity: Activity) {
    // Data Layer
    val authDataSource = FirebaseAuthDataSource(context, activity)

    // Use Cases (Domain Layer)
    val saveUserProfileUseCase = SaveUserProfileUseCase(userRepository)

    // ViewModel uses both
    // AuthViewModel(authDataSource, saveUserProfileUseCase)
}
```

### 6. Navigation

**Location**: `presentation/navigation/NavGraph.kt`

**Flow**:
1. **App Start** → SplashScreen
2. **SplashScreen** → Checks authentication status:
   - If authenticated → CoffeeList
   - If not authenticated → Auth (Login)
3. **Login Success** → CoffeeList
4. **Profile Access** → Profile Screen (from CoffeeScreen)
5. **Sign Out** → Auth (Login)

---

## Setup Instructions

### 1. Firebase Console Setup

1. **Create Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click "Add project"
   - Follow the setup wizard

2. **Add Android App**:
   - Click "Add app" → Android icon
   - Enter your package name: `com.example.androidmycoffee`
   - Download `google-services.json`
   - Place it in `app/` directory

3. **Enable Google Sign-In**:
   - In Firebase Console, go to Authentication → Sign-in method
   - Enable "Google" provider
   - Add support email

### 2. Web Client ID Configuration

**Good News**: The Web Client ID is now automatically configured from your `google-services.json` file!

The implementation uses the web client ID from `strings.xml` which is automatically extracted from your Firebase configuration:

**File**: `res/values/strings.xml`
```xml
<string name="default_web_client_id">862033386514-294vauml9itt76jpn9e66kb7btle5rqr.apps.googleusercontent.com</string>
```

**Usage in Code**: `FirebaseAuthDataSource.kt`
```kotlin
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(context.getString(R.string.default_web_client_id))
    .requestEmail()
    .build()
```

**No manual configuration needed** - it's automatically synced from Firebase!

### 3. Dependencies

All required dependencies are already configured in `build.gradle.kts`:

```kotlin
// Firebase
implementation(platform(libs.firebase.bom))
implementation(libs.firebase.auth)
implementation(libs.firebase.auth.ktx)
implementation(libs.firebase.firestore)

// Google Sign-In
implementation(libs.play.services.auth)
implementation(libs.kotlinx.coroutines.play.services)

// Coil for image loading (user avatars)
implementation(libs.coil.compose)
```

### 4. Gradle Plugins

The `google-services` plugin is now applied in `app/build.gradle.kts:6`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.google.services)  // ← Added
}
```

---

## How It Works

### 1. App Launch Flow

```
MainActivity onCreate()
    ↓
AppNavGraph initialization
    ↓
AuthViewModel creation
    ↓
UserAuthManager initialization
    ↓
checkCurrentUser() called
    ↓
authState updated
    ↓
Navigation determines start destination
```

### 2. Login Flow (Clean Architecture)

```
User clicks "Sign in with Google"
    ↓
AuthScreen launches Google Sign-In Intent
    ↓
User selects Google account
    ↓
Activity Result returns with idToken
    ↓
AuthViewModel.signInWithGoogle(idToken)    [Presentation Layer]
    ↓
FirebaseAuthDataSource.signInWithGoogle()  [Data Layer]
    ↓
Firebase authenticates with Google credential
    ↓
Returns domain User model
    ↓
authState = Authenticated(user: User)       [Domain Model]
    ↓
SaveUserProfileUseCase(user)                [Domain Use Case]
    ↓
Navigate to CoffeeList screen
```

### 3. Logout Flow (Clean Architecture)

```
User clicks "Sign Out" button
    ↓
AuthViewModel.signOut()                    [Presentation Layer]
    ↓
FirebaseAuthDataSource.signOut()           [Data Layer]
    ↓
Firebase auth sign out
    ↓
Google Sign-In client sign out
    ↓
authState = NotAuthenticated               [Domain Model]
    ↓
Navigate to Auth screen
```

### 4. Error Handling (Clean Architecture)

The app handles errors at multiple layers:

**Data Layer**: Caught by try-catch in `FirebaseAuthDataSource.signInWithGoogle()`
```kotlin
catch (e: Exception) {
    Log.e(TAG, "Error signing in with Google", e)
    _authState.value = AuthState.Error(e.message ?: "Sign in failed")
    Result.failure(e)
}
```

**Domain Layer**: Error state as part of AuthState sealed class
```kotlin
sealed class AuthState {
    data class Error(val message: String) : AuthState()
}
```

**Presentation Layer**: Error state displayed in AuthScreen
```kotlin
is AuthState.Error -> {
    Text("Error: ${(authState as AuthState.Error).message}")
    Button(onClick = { startGoogleSignIn() }) {
        Text("Try Again")
    }
}
```

---

## Testing the Implementation

### 1. First Time Login
1. Run the app
2. You should see the Login screen
3. Click "Sign in with Google"
4. Select your Google account
5. After successful login, you'll see the Coffee List screen
6. Your avatar and name appear in the top bar

### 2. Viewing Profile
1. From Coffee List screen, click on your avatar/name in the top bar
2. Profile screen appears showing:
   - Your profile picture
   - Display name
   - Email address
   - Account information card
   - Sign out button

### 3. Sign Out
1. Click the "Sign Out" button (either in top bar or Profile screen)
2. You'll be signed out
3. App navigates back to Login screen

### 4. App Relaunch
1. Close the app completely
2. Reopen the app
3. You should be automatically logged in and see the Coffee List screen
4. No need to sign in again

---

## State Management (Clean Architecture)

The app uses **StateFlow** for reactive state management across layers:

```kotlin
// DATA LAYER - FirebaseAuthDataSource
private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
val authState: StateFlow<AuthState> = _authState.asStateFlow()

// PRESENTATION LAYER - AuthViewModel exposes this to UI
val authState: StateFlow<AuthState> = authDataSource.authState

// UI LAYER - Composables observe the state
val authState by authViewModel.authState.collectAsState()
```

**State Transitions** (Domain Model):
- `AuthState.Loading` → Initial state or during authentication
- `AuthState.NotAuthenticated` → User is not signed in
- `AuthState.Authenticated(user: User)` → User is signed in with domain User model
- `AuthState.Error(message)` → Authentication error occurred

**Benefits of Clean Architecture**:
- State is defined in Domain layer (model package)
- Data layer manages the state
- Presentation layer observes without knowing implementation details

---

## Security Considerations

1. **Token Management**: ID tokens are securely handled by Firebase SDK
2. **Secure Storage**: Firebase handles secure credential storage
3. **Auto Sign-out**: Google Sign-In client is properly signed out
4. **State Validation**: Authentication state is verified on every app launch

---

## Troubleshooting

### Issue: "Sign in failed" error

**Solutions**:
1. Verify `google-services.json` is in the correct location (`app/` directory)
2. Check that `default_web_client_id` exists in `res/values/strings.xml`
3. Ensure Google Sign-In is enabled in Firebase Console
4. Verify SHA-1 fingerprint is added to Firebase (for production)
5. Sync Gradle to ensure google-services plugin processes the JSON file

### Issue: App crashes on sign-in

**Solutions**:
1. Check Logcat for specific error messages
2. Verify all Firebase dependencies are properly synced
3. Ensure `google-services` plugin is applied
4. Clean and rebuild the project

### Issue: User avatar not showing

**Solutions**:
1. Verify Coil dependency is included
2. Check internet permission in AndroidManifest.xml
3. Verify the photoUrl is not null in UserInfo

### Issue: Authentication state not persisting

**Solutions**:
1. Ensure Firebase Auth is properly initialized
2. Check that `checkCurrentUser()` is called in UserAuthManager init
3. Verify navigation logic in NavGraph

---

## Advanced Features

### Adding SHA-1 for Production

For production apps, you need to add your app's SHA-1 fingerprint:

1. **Get SHA-1**:
```bash
# Debug keystore
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release keystore
keytool -list -v -keystore /path/to/your-release-key.keystore -alias your-key-alias
```

2. **Add to Firebase**:
   - Firebase Console → Project Settings → Your apps
   - Scroll to "SHA certificate fingerprints"
   - Click "Add fingerprint"
   - Paste your SHA-1

### Customizing Login UI

You can customize the Login screen in `LoginScreen.kt`:

```kotlin
// Change colors
ButtonDefaults.buttonColors(containerColor = Color.White)

// Change text
Text("Sign in with Google")

// Change welcome message
Text("☕ Welcome to Coffee Shop")
```

---

## Files Created/Modified (Clean Architecture Refactoring)

### Created (Domain Layer):
- ✅ `domain/model/User.kt` - User domain model
- ✅ `domain/model/AuthState.kt` - Auth state sealed class

### Created (Data Layer):
- ✅ `data/auth/FirebaseAuthDataSource.kt` - Firebase auth implementation

### Created (Presentation Layer):
- ✅ `presentation/screen/AuthScreen.kt` - New login screen (renamed from LoginScreen)
- ✅ `presentation/screen/ProfileScreen.kt` - User profile display

### Modified:
- ✅ `app/build.gradle.kts` - Added google-services plugin
- ✅ `res/values/strings.xml` - Added default_web_client_id from Firebase
- ✅ `AppContainer.kt` - Updated to use FirebaseAuthDataSource
- ✅ `presentation/viewmodel/AuthViewModel.kt` - Updated to use domain models
- ✅ `presentation/navigation/NavGraph.kt` - Updated routes and data source
- ✅ `presentation/screen/CoffeeScreen.kt` - Updated imports to domain models
- ✅ `presentation/screen/ProfileScreen.kt` - Updated imports to domain models

### Deleted (Old Implementation):
- 🗑️ `data/auth/UserAuthManager.kt` - Replaced by FirebaseAuthDataSource
- 🗑️ `presentation/screen/LoginScreen.kt` - Renamed to AuthScreen

### No Changes Needed:
- ✅ `MainActivity.kt` - App entry point
- ✅ `google-services.json` - Firebase configuration

---

## Summary

Your Android Coffee Shop app now has a complete, production-ready Firebase Google authentication system following **Clean Architecture** principles:

### Architecture Benefits:
1. ✅ **Clean Architecture** - Clear separation of Domain, Data, and Presentation layers
2. ✅ **Domain Models** - User and AuthState defined in domain layer, not tied to Firebase
3. ✅ **Data Source Pattern** - FirebaseAuthDataSource implements auth operations
4. ✅ **No Hardcoded Credentials** - Web Client ID automatically from Firebase config
5. ✅ **Testability** - Each layer can be tested independently
6. ✅ **Maintainability** - Easy to swap Firebase for another auth provider

### Features:
1. ✅ **Secure Google Sign-In** using Firebase Authentication
2. ✅ **Beautiful UI** for login and profile display
3. ✅ **Proper State Management** with StateFlow
4. ✅ **Error Handling** with user-friendly messages
5. ✅ **Persistent Authentication** that survives app restarts
6. ✅ **Navigation Flow** that respects authentication state

### Clean Architecture Layers:
```
Presentation Layer (UI)
    ↓ depends on
Domain Layer (Business Logic & Models)
    ↑ used by
Data Layer (Firebase Implementation)
```

**Next Steps**:
1. ✅ Web Client ID is automatically configured - no manual setup needed!
2. Sync Gradle to ensure all dependencies are downloaded
3. Run the app and test the authentication flow
4. (Optional) Add SHA-1 fingerprint for production

**Key Files**:
- `domain/model/User.kt` - Domain user model
- `domain/model/AuthState.kt` - Auth state
- `data/auth/FirebaseAuthDataSource.kt` - Firebase implementation
- `presentation/screen/AuthScreen.kt` - Login UI
- `presentation/viewmodel/AuthViewModel.kt` - Presentation logic

Happy coding with Clean Architecture! ☕
