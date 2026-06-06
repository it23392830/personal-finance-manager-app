# 🧪 FinanceFlow Testing Documentation

This branch contains the complete testing implementation for the **FinanceFlow Personal Finance Management Application**.

The testing phase was conducted to ensure the reliability, correctness, and usability of the application's core functionalities including authentication, income management, expense management, savings, goals, navigation, and financial calculations.

---

## 📋 Testing Strategy

The application was tested using two primary testing approaches:

### ✅ Unit Testing

Unit tests validate individual business logic and calculations without requiring an Android device or emulator.

### ✅ Instrumentation / UI Testing

Instrumentation tests validate UI behavior, navigation, and user interactions using Android devices or emulators.

---

# 🔬 Unit Testing

### Technologies Used

- JUnit 4
- Kotlin Testing Framework

### Test Location

```text
app/src/test/java/com/example/financeflow/
```

### Implemented Unit Tests

#### DashboardRepositoryTest.kt

Tests:

- Monthly summary calculations
- Currency conversion calculations
- Total income calculations
- Total expense calculations
- Remaining balance calculations

---

#### ExpenseCalculationTest.kt

Tests:

- Total expense calculations
- Fixed expense calculations
- Optional expense calculations
- Remaining balance calculations
- Expense category totals

---

#### SavingsAndGoalsTest.kt

Tests:

- Savings rate calculations
- Goal progress calculations
- Goal completion logic
- Savings percentage calculations

---

#### GoalModelTest.kt

Tests:

- Goal progress percentage
- Remaining amount calculations
- Goal completion status
- Goal model computed properties

---

# 📱 Instrumentation / UI Testing

### Technologies Used

- AndroidX Test
- Espresso
- Jetpack Compose Testing

### Test Location

```text
app/src/androidTest/java/com/example/financeflow/
```

### Implemented UI Tests

#### AuthUiTest.kt

Tests:

- Login screen visibility
- Email field availability
- Password field availability
- Remember Me functionality
- Login navigation
- Logout functionality
- Remembered user flow

---

#### NavigationUiTest.kt

Tests:

- Home navigation
- Income navigation
- Expense navigation
- Savings navigation
- Goals navigation
- Insights navigation
- Chat Assistant navigation
- Back button functionality

---

# 📊 Test Coverage

| Module | Unit Tests | UI Tests |
|----------|----------|----------|
| Authentication | ❌ | ✅ |
| Income Management | ✅ | ✅ |
| Expense Management | ✅ | ✅ |
| Savings Management | ✅ | ✅ |
| Goal Management | ✅ | ✅ |
| Dashboard Calculations | ✅ | ❌ |
| Navigation | ❌ | ✅ |
| Chat Assistant | ❌ | ✅ |

---

# 🚀 Running Unit Tests

### Using Android Studio

1. Open the project.
2. Navigate to:

```text
app/src/test
```

3. Right-click a test file.

Example:

```text
DashboardRepositoryTest
```

4. Select:

```text
Run DashboardRepositoryTest
```

---

### Using Gradle

Run all unit tests:

```bash
./gradlew test
```

---

# 📲 Running Instrumentation Tests

### Requirements

- Android Emulator running

OR

- Physical Android Device connected

---

### Using Android Studio

Navigate to:

```text
app/src/androidTest
```

Right-click:

```text
AuthUiTest
```

or

```text
NavigationUiTest
```

Select:

```text
Run Tests
```

---

### Using Gradle

Run all instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

---

# 🎯 Expected Results

All tests should pass successfully.

Expected outcomes:

- Accurate financial calculations
- Correct goal progress calculations
- Reliable expense calculations
- Proper authentication flow
- Correct navigation behavior
- Functional Remember Me feature
- Stable Chat Assistant navigation
- Consistent user experience

---

# 🏆 Quality Assurance Summary

The testing implementation verifies:

- Business logic correctness
- Financial calculation accuracy
- Goal tracking reliability
- Savings calculation accuracy
- Authentication functionality
- Navigation consistency
- User interaction behavior
- Application stability

This testing process ensures that FinanceFlow delivers a reliable and accurate personal finance management experience while maintaining expected functionality across all major modules.

---

# 📂 Test Files Included

```text
app
└── src
    ├── test
    │   └── java/com/example/financeflow
    │       ├── repository
    │       │   ├── DashboardRepositoryTest.kt
    │       │   ├── ExpenseCalculationTest.kt
    │       │   └── SavingsAndGoalsTest.kt
    │       │
    │       └── model
    │           └── GoalModelTest.kt
    │
    └── androidTest
        └── java/com/example/financeflow
            └── ui
                ├── AuthUiTest.kt
                └── NavigationUiTest.kt
```

---

# 👥 Authors

**FinanceFlow Team**

Platform Based Development (SE3092)

Faculty of Computing

Sri Lanka Institute of Information Technology (SLIIT)