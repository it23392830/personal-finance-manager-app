# FinanceFlow

## Project Overview

FinanceFlow is a comprehensive personal finance manager Android application designed to help users track their income, expenses, and savings. Built with modern Android development practices, it provides a seamless user experience for managing daily financial activities, viewing streaks, and receiving timely reminders.

Key Features:
- **Expense Tracking:** Monitor your daily spending and categorize expenses.
- **Savings Management:** Track your savings goals.
- **Streaks & Activity:** Maintain daily activity streaks to build healthy financial habits, complete with a Home Screen Widget.
- **Reminders:** Receive morning and night notifications to keep your finances in check.
- **Cloud Sync:** Securely store and sync data using Firebase (Authentication, Firestore, and Storage).

## Architectural Summary

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern combined with **Clean Architecture** principles to ensure separation of concerns, testability, and scalability.

- **UI Layer (Presentation):** Built entirely with **Jetpack Compose** and **Material 3**. State management is handled by **ViewModels** using Kotlin `StateFlow` and `SharedFlow`.
- **Domain Layer:** Contains the core business logic, including `usecase` classes (e.g., `CalculateStreakUseCase`) that act as intermediaries between ViewModels and Repositories.
- **Data Layer:** 
  - **Local Persistence:** Uses **Room Database** for offline caching and local data management.
  - **Remote Data:** Interacts with **Firebase Firestore** via `FirestoreService` and repository implementations.
- **Dependency Injection:** Powered by **Dagger Hilt** to provide dependencies across the application lifecycle.
- **Background Tasks:** Utilizes **WorkManager** (with Hilt integration) for scheduling periodic tasks like `MorningReminderWorker` and `NightReminderWorker`.
- **Navigation:** Uses **Jetpack Navigation Compose** for handling routing within the app.

## Firebase Configuration Steps

To run the application with full cloud functionality, you must configure Firebase:

1. **Create a Firebase Project:**
   - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. **Add an Android App:**
   - Register an Android app within your Firebase project.
   - Use the package name: `com.example.financeflow`.
   - (Optional) Provide the SHA-1 signing certificate if you plan to use Google Sign-In.
3. **Download Configuration File:**
   - Download the `google-services.json` file provided by Firebase.
   - Place this file in the `FinanceFlow/app/` directory of your project.
4. **Enable Firebase Services:**
   - **Authentication:** Enable Email/Password (and any other desired providers) in the Authentication section.
   - **Firestore Database:** Create a Cloud Firestore database (start in test mode for development).
   - **Firebase Storage:** Set up Firebase Storage for any media/image uploads.

## Build Instructions

### Prerequisites
- **Android Studio:** Jellyfish (or the latest stable version).
- **JDK:** Java 17.
- **Android SDK:** API Level 35 (Minimum SDK is 26).

### Steps to Run
1. **Clone the Repository:**
   ```bash
   git clone <repository_url>
   cd personal-finance-manager-app/FinanceFlow
   ```
2. **Add Firebase Config:** 
   Ensure your `google-services.json` is placed in the `FinanceFlow/app/` directory.
3. **Open in Android Studio:**
   Open the `FinanceFlow` directory using Android Studio and wait for Gradle to sync dependencies.
4. **Build the App:**
   - To build via Android Studio: Click on **Build > Make Project**.
   - To build via command line:
     ```bash
     ./gradlew assembleDebug
     ```
5. **Run the App:**
   Select an emulator or a physical device running Android 8.0 (API level 26) or higher, and click the **Run** button in Android Studio.