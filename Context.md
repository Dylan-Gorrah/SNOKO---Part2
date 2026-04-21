# Snokonoko App - Project Context

## What This Project Is

**Snokonoko** is a personal finance tracking Android app. Think of it as a digital wallet diary that helps you track money coming in (income) and money going out (expenses). It's built with a sleek dark theme inspired by modern South African design.

The app lets users:
- Create an account and log in securely
- Add transactions (money in or out)
- See their current balance at a glance
- View spending reports by category
- Set monthly budgets for different spending categories
- Stay logged in between app sessions

---

## What's Already in the Project

Right now, you have a **bare bones Android project** that was created from Android Studio's "Empty Views Activity" template. Here's what's there:

### Existing Files:
- **MainActivity.kt** - The entry point (currently just shows "Hello World!")
- **activity_main.xml** - A basic layout with a single text view
- **colors.xml** - Just black and white colors defined
- **AndroidManifest.xml** - App configuration pointing to MainActivity
- **build.gradle.kts** - Basic dependencies (no Room, no ViewBinding yet)

### What's Missing (Everything Else):
No database, no screens, no navigation, no styling, no icons - basically everything that makes an app actually work.

---

## The Build Plan - 12 Phases

The complete guide breaks this down into **12 phases** that build on each other. Here's the simplified roadmap:

### Phase 1: Project Setup
Create the Android Studio project with the right settings (package name: `com.snokonoko.app`, language: Kotlin, min SDK: API 26).

**Status:** ✅ Already done - project exists

---

### Phase 2: Gradle Configuration
Update the build file to include:
- **Room Database** - for saving data locally on the phone
- **ViewBinding** - for easy access to UI elements
- **Coroutines** - for running database operations smoothly
- **Lifecycle components** - for ViewModels and LiveData

**What needs changing:** The current `build.gradle.kts` needs Room dependencies and ViewBinding enabled.

---

### Phase 3: Colors and Theme
Replace the basic colors with the Snokonoko dark theme palette:
- Black background (`#000000`)
- Dark gray cards (`#0F0F0F`, `#161616`)
- Rose/pink accent color (`#D6415A`)
- Green for income/positive (`#30D158`)
- Red for overspending (`#FF453A`)
- Category colors (food red, transport blue, shopping purple, etc.)

**Files to create/update:** `colors.xml`, `themes.xml`, `strings.xml`

---

### Phase 4: Drawable Resources
Create visual assets:
- **Input backgrounds** - rounded corners for text fields
- **Card backgrounds** - rounded boxes for content
- **Button styles** - primary (rose), ghost (outline), danger (red)
- **Navigation icons** - home, reports, budget, account icons
- **Color selector** - for highlighting the active bottom nav item

**Files to create:** About 14 XML files in the `drawable/` folder plus nav color selector.

---

### Phase 5: Room Database (The Data Layer)
This is where we define what data looks like and how it's stored:

**Data Classes (Entities):**
- `User` - stores first name, surname, email, password
- `Transaction` - stores type (income/expense), category, description, amount, date
- `Budget` - stores category and monthly spending limit

**Data Access Objects (DAOs):**
- `UserDao` - for login/signup operations
- `TransactionDao` - for adding, updating, deleting transactions
- `BudgetDao` - for managing budget limits

**Database:**
- `AppDatabase` - ties everything together and provides the database instance

**Files to create:** 7 Kotlin files in `data/` package

---

### Phase 6: Repositories (The Middleman)
Repositories handle the business logic between the database and the rest of the app:
- `UserRepository` - handles registration, checks for duplicate emails, validates login
- `FinanceRepository` - manages transactions and budgets, exposes LiveData for UI updates

**Files to create:** 2 Kotlin files in `repository/` package

---

### Phase 7: ViewModels (The Brains)
ViewModels manage the UI state and survive screen rotations:
- `UserViewModel` - handles login/signup operations with sealed classes for results
- `MainViewModel` - provides transactions and budgets as LiveData, handles add/update/delete operations

**Files to create:** 2 Kotlin files in `viewmodel/` package

---

### Phase 8: Layouts (The Screens)
XML files that define what users see:

**Activities:**
- `activity_login.xml` - email, password fields, login button, link to signup
- `activity_signup.xml` - name, email, password fields, create account button
- `activity_main.xml` - fragment container with bottom navigation bar

**Fragments:**
- `fragment_home.xml` - balance card, income/expense summary, recent transactions list
- `fragment_reports.xml` - total income/spent cards, category breakdown with progress bars
- `fragment_budget.xml` - list of budgets with spending progress
- `fragment_account.xml` - user info, net balance, logout button

**Bottom Sheets:**
- `sheet_add_transaction.xml` - form for adding income/expense
- `sheet_add_budget.xml` - form for setting a category budget limit

**Files to create:** 11 XML files in `layout/` folder

---

### Phase 9: Kotlin UI Code (The Behavior)
Activities and Fragments that bring the layouts to life:

**Activities:**
- `LoginActivity.kt` - checks for saved session, validates credentials, routes to main screen
- `SignupActivity.kt` - validates form input, creates account, handles duplicate email errors

**Fragments:**
- `HomeFragment.kt` - shows balance, recent transactions as a list, FAB opens add transaction sheet
- `ReportsFragment.kt` - calculates totals, shows spending by category with visual bars
- `BudgetFragment.kt` - displays budgets with progress bars showing spent vs limit
- `AccountFragment.kt` - shows user info, calculates net balance, handles logout

**Bottom Sheets:**
- `AddTransactionSheet.kt` - toggles between income/expense, category picker, saves transaction
- `AddBudgetSheet.kt` - category picker, limit input, saves budget

**Helper functions** for category colors, labels, and emojis are included in `HomeFragment.kt`.

**Files to create:** 8 Kotlin files in `ui/` package, plus updating `MainActivity.kt`

---

### Phase 10: Android Manifest
Update to declare all activities and set `LoginActivity` as the entry point (the first screen users see).

**File to update:** `AndroidManifest.xml`

---

### Phase 11: Run and Test
The testing checklist covers:
- Sign up flow works
- Login works and persists between app restarts
- Adding transactions updates the balance
- Reports show correct totals
- Budgets track spending
- Logout clears session

---

### Phase 12: Future Features (Optional)
Ideas for later: edit/delete transactions, charts, savings goals, date picker, export to CSV.

---

## Current Project State Summary

| Component | Status | Files Needed |
|-----------|--------|--------------|
| Gradle Setup | ⚠️ Needs update | 1 file |
| Colors/Theme | ❌ Not started | 3 files |
| Drawables | ❌ Not started | ~14 files |
| Database (Room) | ❌ Not started | 7 files |
| Repositories | ❌ Not started | 2 files |
| ViewModels | ❌ Not started | 2 files |
| Layouts | ❌ Not started | 11 files |
| UI Code | ⚠️ MainActivity exists | 8 files |
| Manifest | ⚠️ Needs updating | 1 file |

**Bottom line:** You've got the foundation. Now we need to build 39+ files across data, UI, and configuration to make this a working finance app.

---

## Key Technical Decisions Made

1. **Room Database** - Everything stores locally on device (works offline)
2. **SharedPreferences** - Session persistence (stay logged in)
3. **MVVM Pattern** - Clean separation: data → repository → ViewModel → UI
4. **Bottom Navigation** - 4 main sections: Home, Reports, Budget, Account
5. **Bottom Sheets** - Slide-up forms for adding transactions and budgets
6. **ViewBinding** - Type-safe access to UI elements
7. **Dark Theme** - Hardcoded for consistent look

---

## The South African Touch

The app proudly shows "Made in South Africa" and uses the South African Rand (R) as the currency symbol. The color palette was chosen to feel modern and accessible for local users.

---

*Ready to start building? Pick a phase and let's go!*
