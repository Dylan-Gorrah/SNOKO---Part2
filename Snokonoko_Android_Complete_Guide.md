# Snokonoko — Complete Android Build Guide
### From Zero to Full App · Kotlin · Room · MVVM · BottomNav
> Made in South Africa 🇿🇦 · Copy-paste every file in order

---

## What You're Building

A real Android app that matches the Snokonoko HTML demo exactly:

- 🔐 **Login + Sign Up screens** — saves users to Room database
- 🏠 **Home** — balance, income/expense summary, recent transactions
- 📊 **Reports** — spending by category with progress bars
- 💰 **Budget** — set limits per category, see what you've spent
- 👤 **Account** — user info, savings goal, log out
- 💾 **Room Database** — everything saved on device, survives app close
- 🔒 **Session** — stays logged in between sessions

---

## Colour Reference (Dark Theme)

These come directly from the HTML prototype:

| Name | Hex | Use |
|---|---|---|
| Background | `#000000` | Screen background |
| Surface 1 | `#0F0F0F` | Cards |
| Surface 2 | `#161616` | Inputs, bottom sheets |
| Surface 3 | `#222222` | Progress bar track |
| Border | `#1A1A1A` | Card borders |
| Text Primary | `#FFFFFF` | Headings, amounts |
| Text Secondary | `#8C8C8C` | Labels, subtitles |
| Text Tertiary | `#474747` | Hints, timestamps |
| Accent (Red) | `#D6415A` | Primary buttons, active nav |
| Green | `#30D158` | Income, positive balance |
| Red | `#FF453A` | Overspent, delete |
| Orange | `#FF9F0A` | Budget warnings |

---

## Final Folder Structure

```
app/src/main/
├── java/com/snokonoko/app/
│   ├── data/
│   │   ├── User.kt
│   │   ├── Transaction.kt
│   │   ├── Budget.kt
│   │   ├── UserDao.kt
│   │   ├── TransactionDao.kt
│   │   ├── BudgetDao.kt
│   │   └── AppDatabase.kt
│   ├── repository/
│   │   ├── UserRepository.kt
│   │   └── FinanceRepository.kt
│   ├── viewmodel/
│   │   ├── UserViewModel.kt
│   │   └── MainViewModel.kt
│   ├── ui/
│   │   ├── LoginActivity.kt
│   │   ├── SignupActivity.kt
│   │   ├── HomeFragment.kt
│   │   ├── ReportsFragment.kt
│   │   ├── BudgetFragment.kt
│   │   ├── AccountFragment.kt
│   │   ├── AddTransactionSheet.kt
│   │   └── AddBudgetSheet.kt
│   └── MainActivity.kt
├── res/
│   ├── layout/
│   │   ├── activity_login.xml
│   │   ├── activity_signup.xml
│   │   ├── activity_main.xml
│   │   ├── fragment_home.xml
│   │   ├── fragment_reports.xml
│   │   ├── fragment_budget.xml
│   │   ├── fragment_account.xml
│   │   ├── item_transaction.xml
│   │   ├── item_budget.xml
│   │   ├── sheet_add_transaction.xml
│   │   └── sheet_add_budget.xml
│   ├── drawable/
│   │   ├── input_background.xml
│   │   ├── input_background_focused.xml
│   │   ├── btn_primary.xml
│   │   ├── btn_ghost.xml
│   │   ├── btn_danger.xml
│   │   ├── chip_green.xml
│   │   ├── chip_red.xml
│   │   ├── chip_orange.xml
│   │   ├── card_background.xml
│   │   ├── ic_nav_home.xml
│   │   ├── ic_nav_reports.xml
│   │   ├── ic_nav_budget.xml
│   │   └── ic_nav_account.xml
│   ├── color/
│   │   └── nav_item_color.xml
│   ├── menu/
│   │   └── bottom_nav.xml
│   └── values/
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
└── AndroidManifest.xml
```

---

---

# PHASE 1 — Create the Project

---

## Step 1 — Download Android Studio

1. Go to **https://developer.android.com/studio**
2. Download and install — keep all defaults
3. Launch it. Let it download the SDK components (5–10 min first launch)

---

## Step 2 — Create a New Project

1. Click **New Project**
2. Select **Empty Views Activity** (NOT Jetpack Compose)
3. Click **Next**

Fill in:

| Field | Value |
|---|---|
| Name | `Snokonoko` |
| Package name | `com.snokonoko.app` |
| Save location | Anywhere you'll remember |
| Language | `Kotlin` |
| Minimum SDK | `API 26 (Android 8.0 Oreo)` |

4. Click **Finish**
5. Wait for the Gradle sync to finish before doing anything else (progress bar at the bottom)

---

---

# PHASE 2 — Gradle Setup

---

## Step 3 — Edit build.gradle (Module: app)

In the Project panel (left side): **app → Gradle Scripts → build.gradle (Module: app)**

**Delete the entire file. Paste this:**

```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
}

android {
    namespace 'com.snokonoko.app'
    compileSdk 34

    defaultConfig {
        applicationId "com.snokonoko.app"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding true
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'

    // Room Database
    def room_version = "2.6.1"
    implementation "androidx.room:room-runtime:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    implementation "androidx.room:room-ktx:$room_version"

    // ViewModel + LiveData
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
    implementation "androidx.activity:activity-ktx:1.8.2"
    implementation "androidx.fragment:fragment-ktx:1.6.2"

    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

After saving, click **Sync Now** in the yellow banner at the top.

> Wait for sync to finish. If you see red errors, re-check the text above for typos.

---

---

# PHASE 3 — Values (Colors, Strings, Theme)

---

## Step 4 — colors.xml

**app → res → values → colors.xml**

Delete everything, paste:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Background layers -->
    <color name="bg">#000000</color>
    <color name="surface1">#0F0F0F</color>
    <color name="surface2">#161616</color>
    <color name="surface3">#222222</color>
    <color name="border">#1A1A1A</color>

    <!-- Text -->
    <color name="text1">#FFFFFF</color>
    <color name="text2">#8C8C8C</color>
    <color name="text3">#474747</color>

    <!-- Brand -->
    <color name="accent">#D6415A</color>
    <color name="accent_dim">#1FD6415A</color>

    <!-- Semantic -->
    <color name="green">#30D158</color>
    <color name="green_dim">#1930D158</color>
    <color name="red">#FF453A</color>
    <color name="red_dim">#1AFF453A</color>
    <color name="orange">#FF9F0A</color>
    <color name="orange_dim">#1AFF9F0A</color>

    <!-- Category colours -->
    <color name="cat_food">#FF6B6B</color>
    <color name="cat_transport">#0A84FF</color>
    <color name="cat_shopping">#BF5AF2</color>
    <color name="cat_entertainment">#FF9F0A</color>
    <color name="cat_fitness">#FF375F</color>
    <color name="cat_utilities">#636366</color>
    <color name="cat_medical">#34C759</color>
    <color name="cat_education">#5AC8FA</color>
    <color name="cat_other">#8E8E93</color>
    <color name="cat_income">#30D158</color>
</resources>
```

---

## Step 5 — themes.xml

**app → res → values → themes.xml**

Delete everything, paste:

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.Snokonoko" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/accent</item>
        <item name="colorPrimaryVariant">#B83450</item>
        <item name="colorOnPrimary">@color/text1</item>
        <item name="colorSecondary">@color/green</item>
        <item name="colorOnSecondary">@color/bg</item>
        <item name="android:windowBackground">@color/bg</item>
        <item name="android:statusBarColor">@color/bg</item>
        <item name="android:navigationBarColor">@color/surface1</item>
        <item name="android:windowLightStatusBar" tools:targetApi="m">false</item>
    </style>

    <!-- Bottom sheet style -->
    <style name="BottomSheetStyle" parent="Theme.MaterialComponents.BottomSheetDialog">
        <item name="android:windowIsFloating">false</item>
        <item name="bottomSheetStyle">@style/BottomSheetShape</item>
    </style>
    <style name="BottomSheetShape" parent="Widget.MaterialComponents.BottomSheet">
        <item name="shapeAppearanceOverlay">@style/ShapeBottomSheet</item>
        <item name="backgroundTint">@color/surface1</item>
    </style>
    <style name="ShapeBottomSheet">
        <item name="cornerFamilyTopLeft">rounded</item>
        <item name="cornerFamilyTopRight">rounded</item>
        <item name="cornerSizeTopLeft">22dp</item>
        <item name="cornerSizeTopRight">22dp</item>
    </style>
</resources>
```

> **Also check** `res/values/themes.xml (night)` if it exists — delete it or replace with the same content. The dark theme is hardcoded so we don't need a separate night version.

---

## Step 6 — strings.xml

**app → res → values → strings.xml**

```xml
<resources>
    <string name="app_name">Snokonoko</string>
    <string name="tagline">Your money, your story.</string>
    <string name="made_in_sa">Snokonoko · Made in South Africa 🇿🇦</string>
</resources>
```

---

---

# PHASE 4 — Drawable Resources

---

## Step 7 — Create Drawables

For each file below: **Right-click res/drawable → New → Drawable Resource File → name it → paste the XML**

---

### drawable/input_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/surface2"/>
    <stroke android:width="1dp" android:color="@color/border"/>
    <corners android:radius="12dp"/>
</shape>
```

---

### drawable/card_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/surface1"/>
    <stroke android:width="1dp" android:color="@color/border"/>
    <corners android:radius="18dp"/>
</shape>
```

---

### drawable/btn_primary.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape android:shape="rectangle">
            <solid android:color="#B83450"/>
            <corners android:radius="13dp"/>
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <solid android:color="@color/accent"/>
            <corners android:radius="13dp"/>
        </shape>
    </item>
</selector>
```

---

### drawable/btn_ghost.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/surface2"/>
    <stroke android:width="1dp" android:color="@color/border"/>
    <corners android:radius="13dp"/>
</shape>
```

---

### drawable/btn_danger.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/red_dim"/>
    <stroke android:width="1dp" android:color="#2AFF453A"/>
    <corners android:radius="13dp"/>
</shape>
```

---

### drawable/ic_nav_home.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M3,9l9,-7l9,7v11a2,2,0,0,1,-2,2H5a2,2,0,0,1,-2,-2z"/>
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M9,22V12h6v10"/>
</vector>
```

---

### drawable/ic_nav_reports.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M18,20V10"/>
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M12,20V4"/>
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M6,20V14"/>
</vector>
```

---

### drawable/ic_nav_budget.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M21,4H3C1.9,4,1,4.9,1,6v13c0,1.1,0.9,2,2,2h18c1.1,0,2,-0.9,2,-2V6C23,4.9,22.1,4,21,4z"/>
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M1,10h22"/>
</vector>
```

---

### drawable/ic_nav_account.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M20,21v-2a4,4,0,0,0,-4,-4H8a4,4,0,0,0,-4,4v2"/>
    <path
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="1.8"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M12,11m-4,0a4,4,0,1,0,8,0a4,4,0,1,0,-8,0"/>
</vector>
```

---

## Step 8 — Nav Item Color Selector

**Right-click res → New → Android Resource Directory → Resource type: color → OK**

Then right-click the new `color` folder: **New → Color Resource File → name: `nav_item_color`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="@color/accent" android:state_checked="true"/>
    <item android:color="@color/text3"/>
</selector>
```

---

## Step 9 — Bottom Nav Menu

**Right-click res → New → Android Resource Directory → Resource type: menu → OK**

Right-click the new `menu` folder: **New → Menu Resource File → name: `bottom_nav`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:icon="@drawable/ic_nav_home"
        android:title="Home"/>
    <item
        android:id="@+id/nav_reports"
        android:icon="@drawable/ic_nav_reports"
        android:title="Reports"/>
    <item
        android:id="@+id/nav_budget"
        android:icon="@drawable/ic_nav_budget"
        android:title="Budget"/>
    <item
        android:id="@+id/nav_account"
        android:icon="@drawable/ic_nav_account"
        android:title="Account"/>
</menu>
```

---

---

# PHASE 5 — Room Database

---

## Step 10 — Create Package Folders

In the Project panel: **app → java → com.snokonoko.app**

Right-click `com.snokonoko.app` → **New → Package** → type each name:

- `data`
- `repository`
- `viewmodel`
- `ui`

---

## Step 11 — User.kt

Right-click `data` → **New → Kotlin Class/File → Class → name: `User`**

```kotlin
package com.snokonoko.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val surname: String,
    val email: String,
    val password: String
)
```

---

## Step 12 — Transaction.kt

Right-click `data` → **New → Kotlin Class/File → Class → name: `Transaction`**

```kotlin
package com.snokonoko.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * type = "income" or "expense"
 * category = "food", "transport", "shopping", "entertainment",
 *             "fitness", "utilities", "medical", "education",
 *             "other", "income"
 * date = "YYYY-MM-DD" string format
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String
)
```

---

## Step 13 — Budget.kt

Right-click `data` → **New → Kotlin Class/File → Class → name: `Budget`**

```kotlin
package com.snokonoko.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val limitAmount: Double
)
```

---

## Step 14 — UserDao.kt

Right-click `data` → **New → Kotlin Class/File → Interface → name: `UserDao`**

```kotlin
package com.snokonoko.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int
}
```

---

## Step 15 — TransactionDao.kt

Right-click `data` → **New → Kotlin Class/File → Interface → name: `TransactionDao`**

```kotlin
package com.snokonoko.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    // Returns LiveData — the UI automatically updates when data changes
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}
```

---

## Step 16 — BudgetDao.kt

Right-click `data` → **New → Kotlin Class/File → Interface → name: `BudgetDao`**

```kotlin
package com.snokonoko.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY category ASC")
    fun getAllBudgets(): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    suspend fun getBudgetByCategory(category: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}
```

---

## Step 17 — AppDatabase.kt

Right-click `data` → **New → Kotlin Class/File → Class → name: `AppDatabase`**

```kotlin
package com.snokonoko.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Transaction::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "snokonoko_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

---

---

# PHASE 6 — Repositories

---

## Step 18 — UserRepository.kt

Right-click `repository` → **New → Kotlin Class/File → Class → name: `UserRepository`**

```kotlin
package com.snokonoko.app.repository

import android.content.Context
import com.snokonoko.app.data.AppDatabase
import com.snokonoko.app.data.User

class UserRepository(context: Context) {

    private val userDao = AppDatabase.getDatabase(context).userDao()

    suspend fun registerUser(
        firstName: String,
        surname: String,
        email: String,
        password: String
    ): Long {
        if (userDao.emailExists(email) > 0) return -1L
        return userDao.insertUser(
            User(firstName = firstName, surname = surname, email = email, password = password)
        )
    }

    suspend fun loginUser(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email) ?: return null
        return if (user.password == password) user else null
    }
}
```

---

## Step 19 — FinanceRepository.kt

Right-click `repository` → **New → Kotlin Class/File → Class → name: `FinanceRepository`**

```kotlin
package com.snokonoko.app.repository

import android.content.Context
import com.snokonoko.app.data.AppDatabase
import com.snokonoko.app.data.Budget
import com.snokonoko.app.data.Transaction

class FinanceRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val txDao = db.transactionDao()
    private val budgetDao = db.budgetDao()

    val allTransactions = txDao.getAllTransactions()
    val allBudgets = budgetDao.getAllBudgets()

    suspend fun insertTransaction(t: Transaction) = txDao.insert(t)
    suspend fun updateTransaction(t: Transaction) = txDao.update(t)
    suspend fun deleteTransaction(t: Transaction) = txDao.delete(t)

    suspend fun insertBudget(b: Budget) = budgetDao.insert(b)
    suspend fun updateBudget(b: Budget) = budgetDao.update(b)
    suspend fun deleteBudget(b: Budget) = budgetDao.delete(b)
    suspend fun getBudgetByCategory(cat: String) = budgetDao.getBudgetByCategory(cat)
}
```

---

---

# PHASE 7 — ViewModels

---

## Step 20 — UserViewModel.kt

Right-click `viewmodel` → **New → Kotlin Class/File → Class → name: `UserViewModel`**

```kotlin
package com.snokonoko.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.snokonoko.app.data.User
import com.snokonoko.app.repository.UserRepository
import kotlinx.coroutines.launch

sealed class SignupResult {
    object Success : SignupResult()
    object EmailTaken : SignupResult()
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object Failed : LoginResult()
}

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    val signupResult = MutableLiveData<SignupResult>()
    val loginResult = MutableLiveData<LoginResult>()

    fun signup(firstName: String, surname: String, email: String, password: String) {
        viewModelScope.launch {
            val id = repository.registerUser(firstName, surname, email, password)
            if (id > 0) signupResult.postValue(SignupResult.Success)
            else signupResult.postValue(SignupResult.EmailTaken)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.loginUser(email, password)
            if (user != null) loginResult.postValue(LoginResult.Success(user))
            else loginResult.postValue(LoginResult.Failed)
        }
    }
}
```

---

## Step 21 — MainViewModel.kt

Right-click `viewmodel` → **New → Kotlin Class/File → Class → name: `MainViewModel`**

```kotlin
package com.snokonoko.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.snokonoko.app.data.Budget
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.repository.FinanceRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FinanceRepository(application)

    // Observe these in your fragments — they update automatically
    val transactions = repo.allTransactions
    val budgets = repo.allBudgets

    fun addTransaction(t: Transaction) = viewModelScope.launch { repo.insertTransaction(t) }
    fun updateTransaction(t: Transaction) = viewModelScope.launch { repo.updateTransaction(t) }
    fun deleteTransaction(t: Transaction) = viewModelScope.launch { repo.deleteTransaction(t) }

    fun addBudget(b: Budget) = viewModelScope.launch { repo.insertBudget(b) }
    fun updateBudget(b: Budget) = viewModelScope.launch { repo.updateBudget(b) }
    fun deleteBudget(b: Budget) = viewModelScope.launch { repo.deleteBudget(b) }
}
```

---

---

# PHASE 8 — Layouts

---

## Step 22 — activity_login.xml

**app → res → layout → activity_login.xml** (Right-click layout → New → Layout Resource File → `activity_login`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center_horizontal"
        android:paddingHorizontal="32dp"
        android:paddingBottom="48dp">

        <!-- Logo placeholder -->
        <FrameLayout
            android:layout_width="72dp"
            android:layout_height="72dp"
            android:layout_marginTop="100dp"
            android:layout_marginBottom="20dp"
            android:background="@drawable/card_background">
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:text="S"
                android:textColor="@color/accent"
                android:textSize="28sp"
                android:textStyle="bold"/>
        </FrameLayout>

        <!-- App name -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Snokonoko"
            android:textColor="@color/text1"
            android:textSize="32sp"
            android:fontFamily="serif"
            android:letterSpacing="-0.02"
            android:layout_marginBottom="6dp"/>

        <!-- Tagline -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/tagline"
            android:textColor="@color/text3"
            android:textSize="13sp"
            android:layout_marginBottom="48dp"/>

        <!-- Email -->
        <EditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Email address"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textEmailAddress"
            android:layout_marginBottom="12dp"/>

        <!-- Password -->
        <EditText
            android:id="@+id/etPassword"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Password"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textPassword"
            android:layout_marginBottom="10dp"/>

        <!-- Error -->
        <TextView
            android:id="@+id/tvError"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/red"
            android:textSize="12sp"
            android:layout_marginBottom="12dp"
            android:visibility="gone"/>

        <!-- Login button -->
        <Button
            android:id="@+id/btnLogin"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:background="@drawable/btn_primary"
            android:text="Log In"
            android:textColor="@color/text1"
            android:textSize="15sp"
            android:textStyle="bold"
            android:layout_marginBottom="32dp"
            android:stateListAnimator="@null"/>

        <!-- Sign up link -->
        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Don't have an account?  "
                android:textColor="@color/text2"
                android:textSize="13sp"/>

            <TextView
                android:id="@+id/tvGoSignup"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Sign up"
                android:textColor="@color/accent"
                android:textSize="13sp"
                android:textStyle="bold"/>
        </LinearLayout>

        <!-- Footer -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/made_in_sa"
            android:textColor="@color/text3"
            android:textSize="10sp"
            android:layout_marginTop="48dp"/>

    </LinearLayout>
</ScrollView>
```

---

## Step 23 — activity_signup.xml

**Right-click layout → New → Layout Resource File → `activity_signup`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center_horizontal"
        android:paddingHorizontal="32dp"
        android:paddingBottom="48dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Create Account"
            android:textColor="@color/text1"
            android:textSize="30sp"
            android:fontFamily="serif"
            android:layout_marginTop="90dp"
            android:layout_marginBottom="6dp"/>

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Join Snokonoko today."
            android:textColor="@color/text3"
            android:textSize="13sp"
            android:layout_marginBottom="40dp"/>

        <EditText
            android:id="@+id/etFirstName"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="First name"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textPersonName"
            android:layout_marginBottom="12dp"/>

        <EditText
            android:id="@+id/etSurname"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Surname"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textPersonName"
            android:layout_marginBottom="12dp"/>

        <EditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Email address"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textEmailAddress"
            android:layout_marginBottom="12dp"/>

        <EditText
            android:id="@+id/etPassword"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Password"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textPassword"
            android:layout_marginBottom="12dp"/>

        <EditText
            android:id="@+id/etConfirmPassword"
            android:layout_width="match_parent"
            android:layout_height="56dp"
            android:background="@drawable/input_background"
            android:hint="Confirm password"
            android:textColorHint="@color/text3"
            android:textColor="@color/text1"
            android:paddingHorizontal="16dp"
            android:inputType="textPassword"
            android:layout_marginBottom="10dp"/>

        <TextView
            android:id="@+id/tvError"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/red"
            android:textSize="12sp"
            android:layout_marginBottom="12dp"
            android:visibility="gone"/>

        <Button
            android:id="@+id/btnSignup"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:background="@drawable/btn_primary"
            android:text="Create Account"
            android:textColor="@color/text1"
            android:textSize="15sp"
            android:textStyle="bold"
            android:layout_marginBottom="32dp"
            android:stateListAnimator="@null"/>

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Already have an account?  "
                android:textColor="@color/text2"
                android:textSize="13sp"/>

            <TextView
                android:id="@+id/tvGoLogin"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Log in"
                android:textColor="@color/accent"
                android:textSize="13sp"
                android:textStyle="bold"/>
        </LinearLayout>

    </LinearLayout>
</ScrollView>
```

---

## Step 24 — activity_main.xml

**Open the existing `activity_main.xml`. Delete everything. Paste:**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg">

    <!-- Fragment content area -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragmentContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="72dp"/>

    <!-- Bottom Nav -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNav"
        android:layout_width="match_parent"
        android:layout_height="72dp"
        android:layout_gravity="bottom"
        android:background="@color/surface1"
        app:itemIconTint="@color/nav_item_color"
        app:itemTextColor="@color/nav_item_color"
        app:itemTextAppearanceActive="@null"
        app:labelVisibilityMode="labeled"
        app:menu="@menu/bottom_nav"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

---

## Step 25 — fragment_home.xml

**Right-click layout → New → Layout Resource File → `fragment_home`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingHorizontal="16dp"
            android:paddingTop="56dp"
            android:paddingBottom="24dp">

            <!-- Header -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Home"
                android:textColor="@color/text1"
                android:textSize="26sp"
                android:fontFamily="serif"
                android:layout_marginBottom="20dp"/>

            <!-- Balance Card -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/card_background"
                android:orientation="vertical"
                android:padding="20dp"
                android:layout_marginBottom="12dp">

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="CURRENT BALANCE"
                    android:textColor="@color/text3"
                    android:textSize="10sp"
                    android:letterSpacing="0.08"
                    android:layout_marginBottom="8dp"/>

                <TextView
                    android:id="@+id/tvBalance"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="R 0.00"
                    android:textColor="@color/text1"
                    android:textSize="36sp"
                    android:fontFamily="serif"
                    android:layout_marginBottom="20dp"/>

                <!-- Income / Expense row -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:weightSum="2">

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:orientation="vertical">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="INCOME"
                            android:textColor="@color/text3"
                            android:textSize="9sp"
                            android:letterSpacing="0.08"
                            android:layout_marginBottom="4dp"/>
                        <TextView
                            android:id="@+id/tvIncome"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="R 0.00"
                            android:textColor="@color/green"
                            android:textSize="16sp"
                            android:fontFamily="serif"/>
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:orientation="vertical">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="EXPENSES"
                            android:textColor="@color/text3"
                            android:textSize="9sp"
                            android:letterSpacing="0.08"
                            android:layout_marginBottom="4dp"/>
                        <TextView
                            android:id="@+id/tvExpenses"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="R 0.00"
                            android:textColor="@color/text1"
                            android:textSize="16sp"
                            android:fontFamily="serif"/>
                    </LinearLayout>

                </LinearLayout>
            </LinearLayout>

            <!-- Recent Transactions header row -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_marginTop="8dp"
                android:layout_marginBottom="8dp">

                <TextView
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1"
                    android:text="RECENT"
                    android:textColor="@color/text3"
                    android:textSize="10sp"
                    android:letterSpacing="0.08"/>

                <TextView
                    android:id="@+id/tvTxCount"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="0 transactions"
                    android:textColor="@color/text3"
                    android:textSize="11sp"/>

            </LinearLayout>

            <!-- Transaction list container -->
            <LinearLayout
                android:id="@+id/txListContainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/card_background"
                android:orientation="vertical"
                android:layout_marginBottom="8dp"/>

        </LinearLayout>
    </ScrollView>

    <!-- FAB -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="20dp"
        android:layout_marginBottom="92dp"
        android:backgroundTint="@color/accent"
        android:contentDescription="Add transaction"
        android:src="@android:drawable/ic_input_add"/>

</FrameLayout>
```

---

## Step 26 — fragment_reports.xml

**Right-click layout → `fragment_reports`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="16dp"
        android:paddingTop="56dp"
        android:paddingBottom="24dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Reports"
            android:textColor="@color/text1"
            android:textSize="26sp"
            android:fontFamily="serif"
            android:layout_marginBottom="20dp"/>

        <!-- Summary row -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:weightSum="2"
            android:layout_marginBottom="12dp">

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="120dp"
                android:layout_weight="1"
                android:layout_marginEnd="6dp"
                android:background="@drawable/card_background"
                android:orientation="vertical"
                android:padding="16dp">
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="TOTAL INCOME"
                    android:textColor="@color/text3"
                    android:textSize="9sp"
                    android:letterSpacing="0.08"
                    android:layout_marginBottom="8dp"/>
                <TextView
                    android:id="@+id/tvTotalIncome"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="R 0"
                    android:textColor="@color/green"
                    android:textSize="20sp"
                    android:fontFamily="serif"/>
            </LinearLayout>

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="120dp"
                android:layout_weight="1"
                android:layout_marginStart="6dp"
                android:background="@drawable/card_background"
                android:orientation="vertical"
                android:padding="16dp">
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="TOTAL SPENT"
                    android:textColor="@color/text3"
                    android:textSize="9sp"
                    android:letterSpacing="0.08"
                    android:layout_marginBottom="8dp"/>
                <TextView
                    android:id="@+id/tvTotalSpent"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="R 0"
                    android:textColor="@color/text1"
                    android:textSize="20sp"
                    android:fontFamily="serif"/>
            </LinearLayout>

        </LinearLayout>

        <!-- By Category label -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="BY CATEGORY"
            android:textColor="@color/text3"
            android:textSize="10sp"
            android:letterSpacing="0.08"
            android:layout_marginBottom="8dp"/>

        <!-- Category breakdown container -->
        <LinearLayout
            android:id="@+id/categoryContainer"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@drawable/card_background"
            android:orientation="vertical"
            android:padding="16dp"
            android:layout_marginBottom="12dp"/>

    </LinearLayout>
</ScrollView>
```

---

## Step 27 — fragment_budget.xml

**Right-click layout → `fragment_budget`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg">

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingHorizontal="16dp"
            android:paddingTop="56dp"
            android:paddingBottom="24dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Budget"
                android:textColor="@color/text1"
                android:textSize="26sp"
                android:fontFamily="serif"
                android:layout_marginBottom="20dp"/>

            <!-- Budget list container -->
            <LinearLayout
                android:id="@+id/budgetListContainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"/>

        </LinearLayout>
    </ScrollView>

    <!-- FAB to add budget -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabBudget"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="20dp"
        android:layout_marginBottom="92dp"
        android:backgroundTint="@color/accent"
        android:contentDescription="Add budget"
        android:src="@android:drawable/ic_input_add"/>

</FrameLayout>
```

---

## Step 28 — fragment_account.xml

**Right-click layout → `fragment_account`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/bg">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:paddingHorizontal="16dp"
        android:paddingTop="56dp"
        android:paddingBottom="40dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Account"
            android:textColor="@color/text1"
            android:textSize="26sp"
            android:fontFamily="serif"
            android:layout_marginBottom="24dp"/>

        <!-- User info card -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@drawable/card_background"
            android:orientation="vertical"
            android:padding="20dp"
            android:layout_marginBottom="12dp">

            <TextView
                android:id="@+id/tvUserName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="User"
                android:textColor="@color/text1"
                android:textSize="22sp"
                android:fontFamily="serif"
                android:layout_marginBottom="4dp"/>

            <TextView
                android:id="@+id/tvUserEmail"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="email@example.com"
                android:textColor="@color/text2"
                android:textSize="13sp"/>

        </LinearLayout>

        <!-- Net balance card -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@drawable/card_background"
            android:orientation="vertical"
            android:padding="20dp"
            android:layout_marginBottom="12dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="NET BALANCE"
                android:textColor="@color/text3"
                android:textSize="10sp"
                android:letterSpacing="0.08"
                android:layout_marginBottom="6dp"/>

            <TextView
                android:id="@+id/tvNetBalance"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="R 0"
                android:textColor="@color/green"
                android:textSize="28sp"
                android:fontFamily="serif"/>

        </LinearLayout>

        <!-- Log out -->
        <Button
            android:id="@+id/btnLogout"
            android:layout_width="match_parent"
            android:layout_height="54dp"
            android:background="@drawable/btn_danger"
            android:text="Log Out"
            android:textColor="@color/red"
            android:textSize="15sp"
            android:textStyle="bold"
            android:stateListAnimator="@null"
            android:layout_marginTop="16dp"/>

    </LinearLayout>
</ScrollView>
```

---

## Step 29 — sheet_add_transaction.xml

**Right-click layout → `sheet_add_transaction`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@color/surface1"
    android:paddingHorizontal="20dp"
    android:paddingBottom="40dp">

    <!-- Handle bar -->
    <View
        android:layout_width="36dp"
        android:layout_height="4dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="12dp"
        android:layout_marginBottom="16dp"
        android:background="@color/surface3"/>

    <!-- Title -->
    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Add Transaction"
        android:textColor="@color/text1"
        android:textSize="20sp"
        android:fontFamily="serif"
        android:layout_marginBottom="20dp"/>

    <!-- Type toggle -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@drawable/input_background"
        android:orientation="horizontal"
        android:padding="4dp"
        android:layout_marginBottom="16dp">

        <Button
            android:id="@+id/btnExpense"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="Expense"
            android:textSize="13sp"
            android:textStyle="bold"
            android:backgroundTint="@color/accent"
            android:textColor="@color/text1"
            android:stateListAnimator="@null"/>

        <Button
            android:id="@+id/btnIncome"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:text="Income"
            android:textSize="13sp"
            android:textStyle="bold"
            android:backgroundTint="@android:color/transparent"
            android:textColor="@color/text2"
            android:stateListAnimator="@null"/>

    </LinearLayout>

    <!-- Category spinner -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="CATEGORY"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <Spinner
        android:id="@+id/spinnerCategory"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:paddingHorizontal="12dp"
        android:layout_marginBottom="14dp"/>

    <!-- Description -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DESCRIPTION"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <EditText
        android:id="@+id/etDescription"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:hint="e.g. Pick n Pay Groceries"
        android:textColorHint="@color/text3"
        android:textColor="@color/text1"
        android:paddingHorizontal="14dp"
        android:inputType="textCapSentences"
        android:layout_marginBottom="14dp"/>

    <!-- Amount -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="AMOUNT (R)"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <EditText
        android:id="@+id/etAmount"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:hint="0.00"
        android:textColorHint="@color/text3"
        android:textColor="@color/text1"
        android:paddingHorizontal="14dp"
        android:inputType="numberDecimal"
        android:layout_marginBottom="14dp"/>

    <!-- Date -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DATE"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <EditText
        android:id="@+id/etDate"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:hint="YYYY-MM-DD"
        android:textColorHint="@color/text3"
        android:textColor="@color/text1"
        android:paddingHorizontal="14dp"
        android:inputType="date"
        android:layout_marginBottom="20dp"/>

    <!-- Buttons -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="54dp"
        android:orientation="horizontal"
        android:weightSum="3">

        <Button
            android:id="@+id/btnCancel"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="@drawable/btn_ghost"
            android:text="Cancel"
            android:textColor="@color/text2"
            android:stateListAnimator="@null"
            android:layout_marginEnd="8dp"/>

        <Button
            android:id="@+id/btnSave"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:background="@drawable/btn_primary"
            android:text="Add"
            android:textColor="@color/text1"
            android:textStyle="bold"
            android:stateListAnimator="@null"/>

    </LinearLayout>

</LinearLayout>
```

---

## Step 30 — sheet_add_budget.xml

**Right-click layout → `sheet_add_budget`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:background="@color/surface1"
    android:paddingHorizontal="20dp"
    android:paddingBottom="40dp">

    <View
        android:layout_width="36dp"
        android:layout_height="4dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="12dp"
        android:layout_marginBottom="16dp"
        android:background="@color/surface3"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Set Budget"
        android:textColor="@color/text1"
        android:textSize="20sp"
        android:fontFamily="serif"
        android:layout_marginBottom="20dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="CATEGORY"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <Spinner
        android:id="@+id/spinnerCategory"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:paddingHorizontal="12dp"
        android:layout_marginBottom="16dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="MONTHLY LIMIT (R)"
        android:textColor="@color/text3"
        android:textSize="9sp"
        android:letterSpacing="0.08"
        android:layout_marginBottom="6dp"/>

    <EditText
        android:id="@+id/etLimit"
        android:layout_width="match_parent"
        android:layout_height="52dp"
        android:background="@drawable/input_background"
        android:hint="e.g. 3000"
        android:textColorHint="@color/text3"
        android:textColor="@color/text1"
        android:paddingHorizontal="14dp"
        android:inputType="numberDecimal"
        android:layout_marginBottom="20dp"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="54dp"
        android:orientation="horizontal"
        android:weightSum="3">

        <Button
            android:id="@+id/btnCancel"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="@drawable/btn_ghost"
            android:text="Cancel"
            android:textColor="@color/text2"
            android:stateListAnimator="@null"
            android:layout_marginEnd="8dp"/>

        <Button
            android:id="@+id/btnSave"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="2"
            android:background="@drawable/btn_primary"
            android:text="Set Budget"
            android:textColor="@color/text1"
            android:textStyle="bold"
            android:stateListAnimator="@null"/>

    </LinearLayout>

</LinearLayout>
```

---

---

# PHASE 9 — Kotlin Files

---

## Step 31 — LoginActivity.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `LoginActivity`**

```kotlin
package com.snokonoko.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.snokonoko.app.MainActivity
import com.snokonoko.app.databinding.ActivityLoginBinding
import com.snokonoko.app.viewmodel.LoginResult
import com.snokonoko.app.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ─── Check for existing session ───────────────────────────────────
        val prefs = getSharedPreferences("snokonoko_prefs", MODE_PRIVATE)
        val savedName = prefs.getString("user_name", null)
        val savedEmail = prefs.getString("user_email", null)
        if (savedName != null) {
            goToMain(savedName, savedEmail ?: "")
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            when {
                email.isEmpty() -> showError("Please enter your email.")
                password.isEmpty() -> showError("Please enter your password.")
                else -> {
                    hideError()
                    viewModel.login(email, password)
                }
            }
        }

        binding.tvGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    val name = "${result.user.firstName} ${result.user.surname}"
                    val email = result.user.email
                    // Save session
                    prefs.edit()
                        .putString("user_name", name)
                        .putString("user_email", email)
                        .apply()
                    goToMain(name, email)
                }
                is LoginResult.Failed -> showError("Incorrect email or password.")
            }
        }
    }

    private fun goToMain(name: String, email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME", name)
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
        finish()
    }

    private fun showError(msg: String) {
        binding.tvError.text = msg
        binding.tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }
}
```

---

## Step 32 — SignupActivity.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `SignupActivity`**

```kotlin
package com.snokonoko.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.snokonoko.app.MainActivity
import com.snokonoko.app.databinding.ActivitySignupBinding
import com.snokonoko.app.viewmodel.SignupResult
import com.snokonoko.app.viewmodel.UserViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val surname = binding.etSurname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()

            when {
                firstName.isEmpty() -> showError("Please enter your first name.")
                surname.isEmpty() -> showError("Please enter your surname.")
                email.isEmpty() -> showError("Please enter your email.")
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    showError("Please enter a valid email address.")
                password.length < 6 -> showError("Password must be at least 6 characters.")
                password != confirm -> showError("Passwords do not match.")
                else -> {
                    hideError()
                    viewModel.signup(firstName, surname, email, password)
                }
            }
        }

        binding.tvGoLogin.setOnClickListener { finish() }

        viewModel.signupResult.observe(this) { result ->
            when (result) {
                is SignupResult.Success -> {
                    // Note: In a real app, store session here too.
                    // For now, go back to Login so they log in with their new account.
                    android.widget.Toast.makeText(this, "Account created! Please log in.", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
                is SignupResult.EmailTaken ->
                    showError("An account with this email already exists.")
            }
        }
    }

    private fun showError(msg: String) {
        binding.tvError.text = msg
        binding.tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }
}
```

---

## Step 33 — MainActivity.kt

Open the existing `MainActivity.kt`. Delete everything. Paste:

```kotlin
package com.snokonoko.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.snokonoko.app.databinding.ActivityMainBinding
import com.snokonoko.app.ui.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Pass user info to fragments
    var userName: String = "User"
    var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("USER_NAME") ?: "User"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // Load home on first launch
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNav.selectedItemId = R.id.nav_home
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home     -> HomeFragment()
                R.id.nav_reports  -> ReportsFragment()
                R.id.nav_budget   -> BudgetFragment()
                R.id.nav_account  -> AccountFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
```

---

## Step 34 — HomeFragment.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `HomeFragment`**

```kotlin
package com.snokonoko.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.R
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.FragmentHomeBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateUI(transactions ?: emptyList())
        }

        binding.fab.setOnClickListener {
            AddTransactionSheet().show(parentFragmentManager, "AddTransaction")
        }
    }

    private fun updateUI(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val balance = income - expense

        binding.tvBalance.text = formatZAR(balance)
        binding.tvBalance.setTextColor(if (balance >= 0) Color.parseColor("#30D158") else Color.parseColor("#FF453A"))
        binding.tvIncome.text = formatZAR(income)
        binding.tvExpenses.text = formatZAR(expense)
        binding.tvTxCount.text = "${transactions.size} transactions"

        // Show recent 6 transactions
        binding.txListContainer.removeAllViews()
        val recent = transactions.take(6)
        if (recent.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "No transactions yet"
                setTextColor(Color.parseColor("#474747"))
                textSize = 13f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 40, 0, 40)
            }
            binding.txListContainer.addView(empty)
        } else {
            recent.forEachIndexed { index, tx ->
                val row = buildTransactionRow(tx)
                binding.txListContainer.addView(row)
                if (index < recent.size - 1) {
                    val divider = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                        ).also { it.setMargins(16.dp, 0, 16.dp, 0) }
                        setBackgroundColor(Color.parseColor("#1A1A1A"))
                    }
                    binding.txListContainer.addView(divider)
                }
            }
        }
    }

    private fun buildTransactionRow(tx: Transaction): View {
        val ctx = requireContext()
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16.dp, 14.dp, 16.dp, 14.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val catColor = categoryColor(tx.category)

        // Category icon circle
        val icon = TextView(ctx).apply {
            text = categoryEmoji(tx.category)
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor(catColor))
            setBackgroundColor(Color.parseColor(catColor + "22"))
            layoutParams = LinearLayout.LayoutParams(40.dp, 40.dp).also {
                it.marginEnd = 12.dp
            }
        }
        row.addView(icon)

        // Description + sub info
        val infoCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        infoCol.addView(TextView(ctx).apply {
            text = tx.description
            setTextColor(Color.WHITE)
            textSize = 13f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        infoCol.addView(TextView(ctx).apply {
            text = "${categoryLabel(tx.category)} · ${tx.date.substring(5).replace("-", " ")}"
            setTextColor(Color.parseColor("#474747"))
            textSize = 11f
        })
        row.addView(infoCol)

        // Amount
        row.addView(TextView(ctx).apply {
            val sign = if (tx.type == "income") "+" else "−"
            text = "$sign${formatZAR(tx.amount)}"
            setTextColor(if (tx.type == "income") Color.parseColor("#30D158") else Color.WHITE)
            textSize = 14f
        })

        return row
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun formatZAR(amount: Double): String {
        val nf = NumberFormat.getInstance(Locale("en", "ZA"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return "R ${nf.format(abs(amount))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ─── Shared helpers — used by multiple fragments ──────────────────────────────

fun categoryColor(cat: String): String = when (cat) {
    "food"          -> "#FF6B6B"
    "transport"     -> "#0A84FF"
    "shopping"      -> "#BF5AF2"
    "entertainment" -> "#FF9F0A"
    "fitness"       -> "#FF375F"
    "utilities"     -> "#636366"
    "medical"       -> "#34C759"
    "education"     -> "#5AC8FA"
    "income"        -> "#30D158"
    else            -> "#8E8E93"
}

fun categoryLabel(cat: String): String = when (cat) {
    "food"          -> "Food & Dining"
    "transport"     -> "Transport"
    "shopping"      -> "Shopping"
    "entertainment" -> "Entertainment"
    "fitness"       -> "Fitness"
    "utilities"     -> "Utilities"
    "medical"       -> "Medical"
    "education"     -> "Education"
    "income"        -> "Income"
    else            -> "Other"
}

fun categoryEmoji(cat: String): String = when (cat) {
    "food"          -> "🍴"
    "transport"     -> "🚗"
    "shopping"      -> "🛍"
    "entertainment" -> "▶"
    "fitness"       -> "💪"
    "utilities"     -> "⚡"
    "medical"       -> "💊"
    "education"     -> "📚"
    "income"        -> "↑"
    else            -> "•"
}

val EXPENSE_CATEGORIES = listOf(
    "food", "transport", "shopping", "entertainment",
    "fitness", "utilities", "medical", "education", "other"
)
```

---

## Step 35 — ReportsFragment.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `ReportsFragment`**

```kotlin
package com.snokonoko.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.databinding.FragmentReportsBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val list = transactions ?: emptyList()
            val income = list.filter { it.type == "income" }.sumOf { it.amount }
            val expense = list.filter { it.type == "expense" }.sumOf { it.amount }

            binding.tvTotalIncome.text = formatZAR(income)
            binding.tvTotalSpent.text = formatZAR(expense)

            // Group expenses by category
            val byCategory = list.filter { it.type == "expense" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .entries.sortedByDescending { it.value }

            val maxVal = byCategory.firstOrNull()?.value ?: 1.0

            binding.categoryContainer.removeAllViews()
            byCategory.forEach { (cat, amount) ->
                binding.categoryContainer.addView(buildCategoryRow(cat, amount, maxVal))
            }
        }
    }

    private fun buildCategoryRow(cat: String, amount: Double, maxVal: Double): View {
        val ctx = requireContext()
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 16.dp }
        }

        val catColor = categoryColor(cat)

        // Label row
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 6.dp }
        }
        row.addView(TextView(ctx).apply {
            text = categoryLabel(cat)
            setTextColor(Color.WHITE)
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(TextView(ctx).apply {
            text = formatZAR(amount)
            setTextColor(Color.WHITE)
            textSize = 13f
        })
        col.addView(row)

        // Progress bar
        val track = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4.dp)
            setBackgroundColor(Color.parseColor("#222222"))
        }
        val fill = View(ctx).apply {
            val pct = (amount / maxVal).coerceIn(0.0, 1.0)
            layoutParams = FrameLayout.LayoutParams((pct * resources.displayMetrics.widthPixels).toInt(), FrameLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor(catColor))
        }
        track.addView(fill)
        col.addView(track)

        return col
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun formatZAR(amount: Double): String {
        val nf = NumberFormat.getInstance(Locale("en", "ZA"))
        nf.minimumFractionDigits = 0; nf.maximumFractionDigits = 0
        return "R ${nf.format(abs(amount))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## Step 36 — BudgetFragment.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `BudgetFragment`**

```kotlin
package com.snokonoko.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.data.Budget
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.FragmentBudgetBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var latestTransactions: List<Transaction> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            latestTransactions = transactions ?: emptyList()
            rebuildBudgetList()
        }

        viewModel.budgets.observe(viewLifecycleOwner) { _ ->
            rebuildBudgetList()
        }

        binding.fabBudget.setOnClickListener {
            AddBudgetSheet().show(parentFragmentManager, "AddBudget")
        }
    }

    private fun rebuildBudgetList() {
        val budgets = viewModel.budgets.value ?: emptyList()
        val spending = latestTransactions.filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { e -> e.value.sumOf { it.amount } }

        binding.budgetListContainer.removeAllViews()

        if (budgets.isEmpty()) {
            binding.budgetListContainer.addView(TextView(requireContext()).apply {
                text = "No budgets yet. Tap + to add one."
                setTextColor(Color.parseColor("#474747"))
                textSize = 13f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 40, 0, 40)
            })
            return
        }

        budgets.forEach { budget ->
            binding.budgetListContainer.addView(buildBudgetCard(budget, spending[budget.category] ?: 0.0))
            val space = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10.dp)
            }
            binding.budgetListContainer.addView(space)
        }
    }

    private fun buildBudgetCard(budget: Budget, spent: Double): View {
        val ctx = requireContext()
        val pct = ((spent / budget.limitAmount) * 100).coerceIn(0.0, 100.0).toInt()
        val isOver = spent > budget.limitAmount
        val isWarn = pct >= 80 && !isOver

        val barColor = when {
            isOver -> "#FF453A"
            isWarn -> "#FF9F0A"
            else   -> "#30D158"
        }
        val chipText = if (isOver) "Over" else "$pct%"
        val chipBg = when {
            isOver -> "#1AFF453A"
            isWarn -> "#1AFF9F0A"
            else   -> "#1A30D158"
        }
        val chipTxt = when {
            isOver -> "#FF453A"
            isWarn -> "#FF9F0A"
            else   -> "#30D158"
        }

        val card = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16.dp, 16.dp, 16.dp, 16.dp)
            setBackgroundColor(Color.parseColor("#0F0F0F"))
            // Rounded corners via outline provider omitted for simplicity
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Header row
        val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 10.dp }
        }

        // Left: category name + amounts
        val left = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        left.addView(TextView(ctx).apply {
            text = categoryLabel(budget.category)
            setTextColor(Color.WHITE)
            textSize = 14f
        })
        left.addView(TextView(ctx).apply {
            text = "${formatZAR(spent)} of ${formatZAR(budget.limitAmount)}"
            setTextColor(Color.parseColor("#8C8C8C"))
            textSize = 11f
        })
        header.addView(left)

        // Right: chip
        header.addView(TextView(ctx).apply {
            text = chipText
            setTextColor(Color.parseColor(chipTxt))
            setBackgroundColor(Color.parseColor(chipBg))
            textSize = 11f
            setPadding(8.dp, 3.dp, 8.dp, 3.dp)
        })

        card.addView(header)

        // Progress bar
        val track = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4.dp)
            setBackgroundColor(Color.parseColor("#222222"))
        }
        val fill = View(ctx).apply {
            val w = (pct / 100f * resources.displayMetrics.widthPixels).toInt()
            layoutParams = FrameLayout.LayoutParams(w, FrameLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor(barColor))
        }
        track.addView(fill)
        card.addView(track)

        return card
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun formatZAR(amount: Double): String {
        val nf = NumberFormat.getInstance(Locale("en", "ZA"))
        nf.minimumFractionDigits = 0; nf.maximumFractionDigits = 0
        return "R ${nf.format(abs(amount))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## Step 37 — AccountFragment.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `AccountFragment`**

```kotlin
package com.snokonoko.app.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.databinding.FragmentAccountBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as com.snokonoko.app.MainActivity
        binding.tvUserName.text = activity.userName
        binding.tvUserEmail.text = activity.userEmail

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val list = transactions ?: emptyList()
            val income = list.filter { it.type == "income" }.sumOf { it.amount }
            val expense = list.filter { it.type == "expense" }.sumOf { it.amount }
            val net = income - expense

            val nf = NumberFormat.getInstance(Locale("en", "ZA"))
            nf.minimumFractionDigits = 0; nf.maximumFractionDigits = 0
            binding.tvNetBalance.text = "R ${nf.format(abs(net))}"
            binding.tvNetBalance.setTextColor(
                if (net >= 0) Color.parseColor("#30D158") else Color.parseColor("#FF453A")
            )
        }

        binding.btnLogout.setOnClickListener {
            // Clear session
            requireContext().getSharedPreferences("snokonoko_prefs", 0)
                .edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## Step 38 — AddTransactionSheet.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `AddTransactionSheet`**

```kotlin
package com.snokonoko.app.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snokonoko.app.R
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.SheetAddTransactionBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.time.LocalDate

class AddTransactionSheet : BottomSheetDialogFragment() {

    private var _binding: SheetAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var selectedType = "expense"

    override fun getTheme(): Int = R.style.BottomSheetStyle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set today's date as default
        binding.etDate.setText(LocalDate.now().toString())

        // Category spinner
        val expenseCats = EXPENSE_CATEGORIES.map { categoryLabel(it) }
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, expenseCats
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Type toggle
        binding.btnExpense.setOnClickListener {
            selectedType = "expense"
            binding.spinnerCategory.visibility = View.VISIBLE
        }
        binding.btnIncome.setOnClickListener {
            selectedType = "income"
            binding.spinnerCategory.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val desc = binding.etDescription.text.toString().trim()
            val amtStr = binding.etAmount.text.toString()
            val date = binding.etDate.text.toString().trim()
            val amt = amtStr.toDoubleOrNull()

            if (desc.isEmpty() || amt == null || amt <= 0 || date.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Please fill in all fields.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val catIdx = binding.spinnerCategory.selectedItemPosition
            val cat = if (selectedType == "income") "income" else EXPENSE_CATEGORIES[catIdx]

            viewModel.addTransaction(
                Transaction(type = selectedType, category = cat, description = desc, amount = amt, date = date)
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

## Step 39 — AddBudgetSheet.kt

Right-click `ui` → **New → Kotlin Class/File → Class → `AddBudgetSheet`**

```kotlin
package com.snokonoko.app.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snokonoko.app.R
import com.snokonoko.app.data.Budget
import com.snokonoko.app.databinding.SheetAddBudgetBinding
import com.snokonoko.app.viewmodel.MainViewModel

class AddBudgetSheet : BottomSheetDialogFragment() {

    private var _binding: SheetAddBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun getTheme(): Int = R.style.BottomSheetStyle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catLabels = EXPENSE_CATEGORIES.map { categoryLabel(it) }
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, catLabels
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val catIdx = binding.spinnerCategory.selectedItemPosition
            val cat = EXPENSE_CATEGORIES[catIdx]
            val limit = binding.etLimit.text.toString().toDoubleOrNull()

            if (limit == null || limit <= 0) {
                android.widget.Toast.makeText(requireContext(), "Please enter a valid limit.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addBudget(Budget(category = cat, limitAmount = limit))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

---

# PHASE 10 — AndroidManifest.xml

---

## Step 40 — Update AndroidManifest.xml

**app → manifests → AndroidManifest.xml**

Delete everything. Paste:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Snokonoko">

        <!-- Login is the entry point -->
        <activity
            android:name=".ui.LoginActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>

        <activity
            android:name=".ui.SignupActivity"
            android:windowSoftInputMode="adjustResize"/>

        <activity
            android:name=".MainActivity"
            android:windowSoftInputMode="adjustResize"/>

    </application>

</manifest>
```

---

---

# PHASE 11 — Run the App

---

## Step 41 — Connect a Device

**Option A — Real phone:**
1. Settings → About Phone → tap **Build Number** 7 times → unlocks Developer Options
2. Settings → Developer Options → turn on **USB Debugging**
3. Plug phone in via USB → tap **Allow** on the prompt

**Option B — Emulator:**
1. Tools → **Device Manager** → Create Device
2. Choose **Pixel 7** → Next
3. Download **API 33 (Tiramisu)** → Next → Finish
4. Press the Play button next to it

---

## Step 42 — Build and Run

1. At the top of Android Studio, confirm your device shows in the dropdown
2. Press **Shift + F10** or click the green ▶ button
3. Watch the status bar — it should say "Launching" then open your app

---

## Step 43 — Test the Full Flow

Run through this checklist:

- [ ] Login screen appears on launch ✓
- [ ] Tap "Sign up" → Signup screen opens ✓
- [ ] Fill in name, email, password → "Create Account" → back to Login ✓
- [ ] Log in with those credentials → Home screen opens ✓
- [ ] Tap the + FAB → Add Transaction sheet slides up ✓
- [ ] Fill in a transaction → saves to Room → appears in Home list ✓
- [ ] Navigate to Reports → category breakdown shows ✓
- [ ] Navigate to Budget → tap + → add a budget limit ✓
- [ ] Navigate to Account → your name shows ✓
- [ ] Tap Log Out → back to Login ✓
- [ ] Log in again → still sees all your transactions ✓ (Room persisted them)
- [ ] Close and reopen the app → still logged in, no login screen ✓ (SharedPreferences session)

---

---

# PHASE 12 — Troubleshooting

---

| Error | Cause | Fix |
|---|---|---|
| `Unresolved reference: ActivityLoginBinding` | ViewBinding not enabled | Check `buildFeatures { viewBinding true }` in build.gradle |
| `Cannot find symbol` on `@Dao`, `@Entity` | KAPT not set up | Check `id 'kotlin-kapt'` in plugins block, then sync |
| `NetworkOnMainThreadException` | DB on main thread | All Room calls must be `suspend` functions called from a coroutine |
| `Cannot find symbol: LoginActivity` | Package wrong | Make sure package at top of file is `com.snokonoko.app.ui` |
| App crashes on launch | Check Logcat | Bottom panel → Logcat tab → filter by "Error" — the message tells you exactly what's wrong |
| Red underlines everywhere after paste | Not synced | Build → Make Project (Ctrl+F9) |
| `nav_item_color` not found | color folder missing | Create `res/color/` directory then add the file |
| Bottom sheet doesn't open | `BottomSheetStyle` missing | Check themes.xml has the style block from Step 5 |

---

---

# What's Next

Once the app runs end-to-end, here's what to build next:

1. **Edit + Delete transactions** — add swipe-to-delete on the list rows using `ItemTouchHelper`
2. **Edit + Delete budgets** — long press or swipe on budget cards
3. **DM Sans font** — add it to `res/font/` and apply in themes.xml for pixel-perfect type
4. **Chart** — use `MPAndroidChart` library for the bar chart on Home and Reports
5. **Savings goal** — add a `Goal` entity to Room (target, current amount)
6. **Session with userId** — store `user_id` in SharedPreferences and filter transactions per user
7. **Date picker** — replace the raw text date field with a `DatePickerDialog`
8. **Splash screen** — add a `SplashActivity` that checks session and routes to Login or Main
9. **Export data** — allow users to export transactions as a CSV file

---

*Snokonoko · Android Build Guide v2.0 · Made in South Africa 🇿🇦*
