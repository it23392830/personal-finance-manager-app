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

---

### 💵 Income Management
- Add income records
- Edit income records
- Delete income records
- Recent transactions history
- Expandable transaction details
- Quick income sources
- Custom income source creation
- Currency support (LKR, USD, etc.)
- Automatic currency conversion to LKR for calculations
- Monthly income filtering
- Income by source analysis

---

### 💸 Expense Management
- Add expenses
- Edit expenses
- Delete expenses
- Today's expenses
- View past expenses
- Fixed expenses
- Optional expenses
- Fixed payment tracking
- Quick add categories
- Expandable transaction details

---

### 🎯 Goal Management
- Create savings goals
- Goal progress tracking
- Goal completion tracking
- Remaining amount calculations
- Goal deadlines

---

### 💰 Savings Management
- Savings linked directly with goals
- Progress percentage calculations
- Remaining amount calculations
- Dynamic updates with goals

---

### 📊 Insights & Analytics
- Monthly summaries
- Income vs Expense analysis
- Savings analysis
- Expense category breakdown
- Financial health calculations
- Real-time dashboard calculations
- Activity calendar

---

### 🔥 Streak Tracking
- Daily expense streaks
- Missed day recovery
- Streak freeze functionality
- Daily engagement tracking

---

### 🔔 Notifications
- Daily reminders
- Missing transaction notifications
- Goal completion notifications
- Streak notifications
- Fixed payment reminders

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