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
