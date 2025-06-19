# MyGuardian

**MyGuardian** is a Kotlin-based Android application providing real-time personal safety features built on a Clean Architecture paradigm. It leverages Google Maps SDK, Firebase (Auth, Firestore, FCM, Dynamic Links), and Android Jetpack components to deliver secure location sharing, SOS alerts, geofencing, and a crowd-sourced community safety heatmap.

---

## Table of Contents

* [Architecture](#architecture)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Prerequisites](#prerequisites)
* [Project Structure](#project-structure)
* [Setup & Configuration](#setup--configuration)
* [Build & Run](#build--run)
* [Testing](#testing)
* [Future Enhancements](#future-enhancements)
* [License](#license)

---

## Architecture

MyGuardian adheres to a **Clean Architecture** layering:

1. **Domain Layer**: Core business models (`Team`, `SOSEvent`, `CommunityReport`), repository interfaces, and use cases encapsulating application logic.
2. **Data Layer**: Repositories implementing domain interfaces, backed by Firebase Firestore, Auth, Cloud Messaging, and Dynamic Links. Contains remote (and local, where needed) data sources.
3. **Presentation Layer**: Android UI built with Fragments, ViewModels, and Android Jetpack Navigation. Contains RecyclerView adapters, Maps integrations, and service classes (e.g., foreground location service).

Dependencies are injected manually via factory classes to avoid reflection and reduce method count.

---

## Features

* **Authentication**: Email/password signup with phone number registration for invite lookups.
* **Team Management**: Create/delete teams, add/remove members from contacts, and in-app invitations via Firebase Dynamic Links or SMS fallback.
* **Live Location Sharing**: Foreground service streams GPS coordinates to Firestore; teammates receive notifications and map markers.
* **Geofencing**: User-defined safe zones trigger exit alerts.
* **SOS Alerts**: One-tap SOS triggers FCM broadcast to teammates; falls back to SMS for emergency authorities with configurable recipients.
* **Community Heatmap**: Crowd-sourced unsafe-area reporting with severity ratings, real-time upvotes, and Google Maps heatmap overlay.
* **Offline Resilience**: SMS fallback when data connectivity is lost; permission checks guard security exceptions.

---

## Tech Stack

* **Language**: Kotlin (Coroutines, Flow)
* **Android SDK**: API level 21+ (minSdk 21)
* **Architecture Components**: Jetpack Navigation, ViewModel, DataBinding
* **Firebase**: Auth, Firestore, Cloud Messaging, Dynamic Links
* **Maps**: Google Maps SDK, Maps Utils (Heatmap)
* **Location**: FusedLocationProviderClient, Geofencing API
* **Build**: Gradle, AndroidX

---

## Prerequisites

* Android Studio Flamingo (or later)
* JDK 11+
* Google Cloud project with enabled:

  * Maps SDK for Android
  * Firebase Authentication, Firestore, Cloud Messaging, Dynamic Links
* SHA-1 fingerprint registered in Firebase console

---

## Project Structure

```
com.example.mygaurdian/
├── data/
│   ├── local/                # Local data sources (contacts)
│   ├── remote/               # Firestore & Firebase remote sources
│   └── repository/           # Repository implementations
├── domain/                   # Models, repository interfaces, use cases
├── presentation/
│   ├── auth/                 # AuthActivity, SignIn/SignUpFragments
│   ├── home/                 # Team list, detail, geofencing, location service
│   ├── dashboard/            # SOS feature
│   ├── notifications/        # Community heatmap and reports
│   └── profile/              # Profile & logout
└── service/                  # Foreground LocationService
```

---

## Setup & Configuration

1. **Clone repo**:

   ```bash
   git clone https://github.com/<your‑org>/MyGuardian.git
   cd MyGuardian
   ```

2. **Configure API keys**:

   * Create `app/src/main/res/values/google_maps_api.xml` with your Maps API key.
   * Ensure `AndroidManifest.xml` includes:

     ```xml
     <meta-data android:name="com.google.android.geo.API_KEY"
                android:value="@string/google_maps_key"/>
     ```

3. **Firebase**:

   * Download `google-services.json` and place under `app/`.
   * Verify `build.gradle` has `apply plugin: 'com.google.gms.google-services'`.

4. **Permissions**:

   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   <uses-permission android:name="android.permission.SEND_SMS"/>
   <uses-permission android:name="android.permission.READ_CONTACTS"/>
   ```

---

## Build & Run

```bash
# From project root
gradlew clean assembleDebug
# Then run on device via Android Studio or
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Testing

* **Unit tests**: none included; use Mockito or MockK to mock repository interfaces.
* **UI tests**: leverage Espresso for fragment navigation and map interactions.

---

## Future Enhancements

* **Real-time push**: Cloud Function to multicast FCM SOS alerts to team members.
* **Photo uploads**: Integrate Firebase Storage for community report images.
* **Voice Activation**: App Actions for Google Assistant integration.
* **Offline caching**: Room DB for storing last-known states.

---

## License

[MIT License](LICENSE)
