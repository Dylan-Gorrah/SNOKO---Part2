package com.snokonoko.app.repository

import android.content.Context
import com.snokonoko.app.data.AppDatabase
import com.snokonoko.app.data.Budget
import com.snokonoko.app.data.Category
import com.snokonoko.app.data.MonthlyGoal
import com.snokonoko.app.data.Transaction

class FinanceRepository(context: Context) {

    // Built-in default categories with their colors
    companion object {
        val DEFAULT_CATEGORIES = mapOf(
            "food" to "#FF6B6B",
            "groceries" to "#FF453A",
            "coffee" to "#A2845E",
            "alcohol" to "#FF375F",
            "transport" to "#0A84FF",
            "fuel" to "#007AFF",
            "shopping" to "#BF5AF2",
            "clothing" to "#AF52DE",
            "entertainment" to "#FF9F0A",
            "fitness" to "#FF375F",
            "utilities" to "#636366",
            "rent" to "#8E8E93",
            "medical" to "#34C759",
            "education" to "#5AC8FA",
            "pets" to "#FF9500",
            "travel" to "#5856D6",
            "gifts" to "#FF2D55",
            "subscriptions" to "#64D2FF"
        )
    }

    private val db = AppDatabase.getDatabase(context)
    private val txDao = db.transactionDao()
    private val budgetDao = db.budgetDao()
    private val categoryDao = db.categoryDao()
    private val monthlyGoalDao = db.monthlyGoalDao()
    private val userDao = db.userDao()

    private val prefs = context.getSharedPreferences("snokonoko_prefs", Context.MODE_PRIVATE)
    private val currentUserId: Int
        get() = prefs.getInt("user_id", 0)

    fun allTransactions() = txDao.getAllTransactions(currentUserId)
    fun allBudgets() = budgetDao.getAllBudgets(currentUserId)
    fun allCategories() = categoryDao.getAllCategories(currentUserId)
    fun allMonthlyGoals() = monthlyGoalDao.getAllGoals(currentUserId)

    suspend fun insertTransaction(t: Transaction) = txDao.insert(t.copy(userId = currentUserId))
    suspend fun updateTransaction(t: Transaction) = txDao.update(t)
    suspend fun deleteTransaction(t: Transaction) = txDao.delete(t)

    suspend fun insertBudget(b: Budget) = budgetDao.insert(b.copy(userId = currentUserId))
    suspend fun updateBudget(b: Budget) = budgetDao.update(b)
    suspend fun deleteBudget(b: Budget) = budgetDao.delete(b)
    suspend fun getBudgetByCategory(cat: String) = budgetDao.getBudgetByCategory(currentUserId, cat)

    suspend fun insertCategory(c: Category) = categoryDao.insertCategory(c.copy(userId = currentUserId))
    suspend fun deleteCategory(c: Category) = categoryDao.deleteCategory(c)

    suspend fun insertMonthlyGoal(goal: MonthlyGoal) = monthlyGoalDao.insertGoal(goal.copy(userId = currentUserId))
    suspend fun getMonthlyGoal(monthYear: String) = monthlyGoalDao.getGoalForMonth(currentUserId, monthYear)

    suspend fun getCategoryTotalsBetweenDates(startDate: String, endDate: String) =
        txDao.getCategoryTotalsBetweenDates(currentUserId, startDate, endDate)

    suspend fun getAllUsers() = userDao.getAllUsers()

    suspend fun updateUser(id: Int, firstName: String, surname: String) = userDao.updateUser(id, firstName, surname)

    suspend fun seedDefaultCategories() {
        val existing = allCategories().value
        if (existing.isNullOrEmpty()) {
            DEFAULT_CATEGORIES.forEach { (name, color) ->
                categoryDao.insertCategory(Category(userId = currentUserId, name = name, colour = color, isDefault = true))
            }
        }
    }
}
