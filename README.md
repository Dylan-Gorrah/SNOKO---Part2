# Snokonoko

A personal finance tracking app for Android built with modern architecture and clean design principles.

---

## Overview

Snokonoko helps you track income and expenses with a focus on simplicity and clarity. The app is designed for everyday users who want to understand their spending habits without the complexity of traditional finance applications.

The core philosophy is mindful tracking - when you manually record each transaction, you become more aware of your spending patterns. The clean interface provides instant insights without overwhelming you with unnecessary features.

**Target Users**
- Individuals wanting to track personal finances
- Students managing limited budgets
- Anyone seeking a simple alternative to complex banking apps
- Users who prefer manual entry for better spending awareness

---

## Key Features

### User Authentication
Secure local account system with email and password login. Each user's data is isolated, ensuring privacy even on shared devices. Multi-user support allows different people to use the same app with completely separate financial data.

### Home Dashboard
Instant financial snapshot showing current balance, monthly income and expenses, recent transactions, and monthly goal progress. The dashboard provides immediate visibility into your financial health without navigation.

### Transaction Management
Quick transaction entry via bottom sheet with income/expense toggle, category selection, description, amount, date, and optional photo attachment. Edit and delete functionality ensures data accuracy. The type badge clearly indicates whether you're adding an expense or income.

### Custom Categories
Built-in categories (food, transport, shopping, medical, etc.) with the ability to create custom categories. Each category has a distinct color for visual identification in charts and lists.

### Budget Tracking
Set spending limits per category with real-time progress tracking. Visual indicators show when you're approaching budget limits, helping prevent overspending before it happens.

### Monthly Goals
Define minimum and maximum spending targets for each month. Progress bars on the home screen show how close you are to your goals, providing big-picture financial context.

### Date Filtering
Filter transactions by custom date ranges to analyze specific periods. Useful for tracking spending during vacations, events, or any time frame that doesn't align with calendar months.

### Category Breakdown
View spending totals by category within any date range. Identifies largest spending areas to inform better financial decisions.

### Analytics
Visual charts including pie charts for category distribution and bar charts for income vs expense comparison. Charts are integrated into the Reports tab for easy access.

### Photo Attachments
Attach receipt photos or item images to transactions. Useful for warranty claims, returns, tax documentation, or simply remembering what you purchased.

### Profile Management
Users can update their name and surname through the settings screen. Email remains read-only for account stability.

### Admin Access
View registered users protected by admin password (555). Allows administrators to see all accounts in the system.

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK API 24+ (Android 7.0)
- Kotlin 1.9+

### Opening in Android Studio

1. Clone or download the repository
2. Open Android Studio
3. Select "Open an Existing Project"
4. Navigate to the project directory and select it
5. Wait for Gradle sync to complete
6. Connect an Android device or start an emulator
7. Click the Run button or press Shift+F10

### Building from Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

---

## File Structure

```
MoneyThing/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/snokonoko/app/
│           │   ├── data/
│           │   │   ├── AppDatabase.kt           # Room database configuration
│           │   │   ├── User.kt                   # User entity
│           │   │   ├── Transaction.kt            # Transaction entity
│           │   │   ├── Budget.kt                 # Budget entity
│           │   │   ├── Category.kt               # Category entity
│           │   │   ├── MonthlyGoal.kt            # Monthly goal entity
│           │   │   ├── UserDao.kt                # User data access
│           │   │   ├── TransactionDao.kt         # Transaction data access
│           │   │   ├── BudgetDao.kt              # Budget data access
│           │   │   ├── CategoryDao.kt            # Category data access
│           │   │   └── MonthlyGoalDao.kt         # Monthly goal data access
│           │   │
│           │   ├── repository/
│           │   │   ├── FinanceRepository.kt      # Main data repository
│           │   │   └── UserRepository.kt         # Authentication repository
│           │   │
│           │   ├── viewmodel/
│           │   │   ├── MainViewModel.kt          # Core business logic
│           │   │   └── UserViewModel.kt          # Authentication logic
│           │   │
│           │   ├── ui/
│           │   │   ├── MainActivity.kt           # Main activity with navigation
│           │   │   ├── LoginActivity.kt          # Login/signup screen
│           │   │   ├── HomeFragment.kt           # Dashboard
│           │   │   ├── ReportsFragment.kt        # Analytics and charts
│           │   │   ├── BudgetFragment.kt         # Budget management
│           │   │   ├── AccountFragment.kt        # Settings and profile
│           │   │   ├── CategoriesFragment.kt     # Category management
│           │   │   ├── AddTransactionSheet.kt    # Transaction form
│           │   │   ├── AddBudgetSheet.kt         # Budget form
│           │   │   ├── DateFilterFragment.kt     # Date range filter
│           │   │   └── CategoryTotalsFragment.kt # Category breakdown
│           │   │
│           │   └── SnokonokoApplication.kt       # Application class
│           │
│           └── res/
│               ├── layout/
│               │   ├── activity_main.xml
│               │   ├── activity_login.xml
│               │   ├── fragment_home.xml
│               │   ├── fragment_reports.xml
│               │   ├── fragment_budget.xml
│               │   ├── fragment_account.xml
│               │   ├── fragment_categories.xml
│               │   ├── sheet_add_transaction.xml
│               │   ├── sheet_add_budget.xml
│               │   └── ...
│               │
│               ├── drawable/
│               │   ├── btn_primary.xml
│               │   ├── btn_ghost.xml
│               │   ├── btn_danger.xml
│               │   ├── input_background.xml
│               │   └── card_background.xml
│               │
│               ├── values/
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               │
│               └── menu/
│                   └── bottom_nav.xml
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## Architecture

The app follows MVVM (Model-View-ViewModel) architecture with a Repository pattern for data access.

### Data Layer
- **Room Database**: Local SQLite database with 5 entities
- **Entities**: User, Transaction, Budget, Category, MonthlyGoal
- **DAOs**: Data access objects for database operations
- **Repository**: FinanceRepository and UserRepository mediate data access

### ViewModel Layer
- **MainViewModel**: Handles transactions, budgets, categories, and goals
- **UserViewModel**: Manages authentication and user operations
- **LiveData**: Reactive data that automatically updates UI on changes

### View Layer
- **Fragments**: Reusable UI components for each screen
- **Bottom Sheets**: Modal forms for data entry
- **XML Layouts**: Declarative UI definitions

### Key Patterns
- **Repository Pattern**: Centralizes data access logic
- **Observer Pattern**: LiveData for reactive UI updates
- **Dependency Injection**: Manual DI through ViewModel providers
- **Coroutines**: Asynchronous database operations

---

## Database Schema

### Users Table
- id (Primary Key, Auto-increment)
- firstName
- surname
- email (Unique)
- password

### Transactions Table
- id (Primary Key, Auto-increment)
- userId (Foreign Key)
- type (income/expense)
- category
- description
- amount
- date
- startTime (Optional)
- endTime (Optional)
- photoPath (Optional)

### Budgets Table
- id (Primary Key, Auto-increment)
- userId (Foreign Key)
- category
- limitAmount

### Categories Table
- id (Primary Key, Auto-increment)
- userId (Foreign Key)
- name
- colour
- isDefault

### Monthly Goals Table
- userId (Primary Key)
- monthYear (Primary Key)
- minGoal
- maxGoal

---

## Technical Decisions

### Room Database
Chosen over raw SQLite for compile-time query validation, automatic migrations, and coroutine support. Room is the recommended Android persistence solution.

### MVVM Architecture
Google's recommended architecture pattern. Separates concerns, improves testability, and handles configuration changes gracefully. ViewModels survive screen rotations.

### Manual Transaction Entry
Deliberate choice to promote spending awareness. Automatic bank imports create invisible data that users rarely review. Manual entry forces reflection on each transaction.

### Bottom Sheet for Forms
Modal UI pattern that maintains context. Users can see the underlying screen while entering data. Modern UI pattern that feels natural on mobile.

### Dark Theme Default
Reduces eye strain, saves battery on OLED screens, and provides a premium aesthetic. Pink accent color creates visual distinction from typical finance apps.

### Local-Only Storage
No cloud sync for privacy and simplicity. Data stays on the device. Future cloud sync can be added through the Repository layer without changing ViewModels.

---

## Summary

Snokonoko is a thoughtfully designed personal finance application that prioritizes user experience and simplicity over feature complexity. The app demonstrates modern Android development practices including MVVM architecture, Room persistence, and reactive programming with LiveData.

The multi-user data isolation ensures privacy while the category system, budget tracking, and analytics provide meaningful insights into spending patterns. The clean, dark-themed interface with pink accents creates a distinctive and pleasant user experience.

Key technical achievements include proper separation of concerns through the Repository pattern, reactive UI updates via LiveData, and a well-structured database schema supporting the full feature set. The codebase serves as a reference for implementing modern Android applications with local data persistence.
