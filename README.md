# 💰 Penny-Pilot (FinanceFlow)
### Smart Personal Finance Management Mobile Application

Penny-Pilot is a modern personal finance management mobile application designed to help users manage income, expenses, savings, goals, and financial insights in a single platform. The system helps users build better financial habits by using goal-based savings, spending analytics, reminders, notifications, and streak tracking.

---

## 📱 Features

### 🔐 Authentication
- User registration and login
- Forgot password functionality
- Remember Me functionality
- Persistent login session
- Firebase Authentication integration
- Secure user session management
- User-specific data isolation

---

### 💵 Income Management
- Add income records
- Edit income records
- Delete income records
- Recent transactions history
- Expandable transaction details
- Quick income sources
- Custom income source creation
- Monthly income filtering
- Income by source analysis
- Currency support (LKR, USD, etc.)
- Automatic currency conversion to LKR for calculations
- Transaction history management
- Real-time income calculations

---

### 💸 Expense Management
- Add expenses
- Edit expenses
- Delete expenses
- Today's expenses tracking
- View past expenses
- Fixed expenses
- Optional expenses
- Fixed payment tracking
- Quick add categories
- Expandable transaction details
- Essential and discretionary expense handling
- Real-time expense calculations

---

### 🎯 Goal Management
- Create savings goals
- Goal progress tracking
- Goal completion tracking
- Remaining amount calculations
- Goal deadlines
- Goal achievement monitoring
- Automatic goal completion detection

---

### 💰 Savings Management
- Savings linked directly with goals
- Goal-based savings allocation
- Progress percentage calculations
- Remaining amount calculations
- Dynamic updates with goals
- Real-time savings updates

---

### 📊 Insights & Analytics
- Daily financial reports
- Weekly financial reports
- Monthly financial reports
- Income vs Expense analysis
- Savings analysis
- Expense category breakdown
- Financial health calculations
- Real-time dashboard calculations
- Activity calendar
- Trend analysis
- Interactive financial insights

---

### 🔥 Streak Tracking
- Daily expense streaks
- Missed day recovery
- Streak freeze functionality
- Daily engagement tracking
- Streak progress monitoring

---

### 🔔 Notifications
- Daily reminders
- Missing transaction notifications
- Goal completion notifications
- Streak notifications
- Fixed payment reminders
- Smart user activity notifications

---

### 🏠 Home Dashboard
- Remaining balance calculation
- Total income summary
- Total expenses summary
- Total savings summary
- Goal progress overview
- Monthly summary visualization
- Quick navigation shortcuts

---

## 🏗 Architecture

The application follows MVVM architecture with Repository Pattern.

```text
UI Layer
    ↓
ViewModel
    ↓
Repository
   ↙      ↘
Room      Firebase
(Local)   (Cloud)
```

---

## 📂 Project Structure

```text
FinanceFlow
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   │
│   ├── remote
│   └── repository
│
├── di
│
├── model
│
├── navigation
│
├── ui
│   ├── auth
│   ├── dashboard
│   ├── income
│   ├── expenses
│   ├── savings
│   ├── goals
│   ├── insights
│   ├── notifications
│   ├── streak
│   └── components
│
├── utils
│
└── viewmodel
```

---

## ⚙ Technologies Used

### Frontend
- Kotlin
- Jetpack Compose
- Material Design 3

### Architecture
- MVVM Architecture
- Repository Pattern
- StateFlow
- Coroutines

### Dependency Injection
- Hilt

### Backend
- Firebase Authentication
- Firebase Cloud Firestore

### Local Storage
- Room Database
- DataStore Preferences

### Navigation
- Navigation Compose

### Development Tools
- Android Studio
- GitHub
- Firebase Console

---

## 🔥 Firebase Database Structure

```text
users
   └── userId
         ├── income
         ├── expenses
         ├── savings
         ├── goals
         ├── notifications
         ├── streak
         └── profile
```

---

## 🚀 Setup Instructions

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
```

### Open Project

Open using:

```text
Android Studio
```

### Configure Firebase

1. Create Firebase Project

2. Enable:

- Firebase Authentication
- Cloud Firestore

3. Download:

```text
google-services.json
```

4. Add file into:

```text
app/google-services.json
```

---

### Run Application

```bash
./gradlew build
```

Or run directly through Android Studio emulator/device.

---

## 📷 Application Screens

- Splash Screen
- Login Screen
- Registration Screen
- Home Dashboard
- Income Screen
- Expense Screen
- Savings Screen
- Goals Screen
- Insights Screen
- Notifications Screen
- Profile Screen

---

## 👥 Team Members

| Name | Responsibility |
|--------|----------------|
| W.W.G.S.N.Gunawardhana| Income Module, Authentication, Home Dashboard, Insights, Integration |
| H.M.J.D.Herath | Expense Module |
| R.M.C.S.Rathnayaka | Savings Module,Streak, Notification, User Profile |
| C.M.Suraweera | Goals Module |

---

## 🎯 Future Improvements

- AI Financial Recommendations
- Bill Prediction System
- Voice Assistant Integration
- OCR Receipt Scanning
- Google Authentication
- Financial Forecasting
- Investment Recommendations
- Multi-device synchronization
- Dark/Light theme customization

---

## 📄 License

This project was developed for educational and academic purposes.

---

## ❤️ Developed By

**FinanceFlow Team**  
SLIIT – Software Engineering Project