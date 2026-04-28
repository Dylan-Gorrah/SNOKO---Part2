package com.snokonoko.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    // Returns LiveData - the UI automatically updates when data changes
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getAllTransactions(userId: Int): LiveData<List<Transaction>>

    // Get transactions between two dates (inclusive)
    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, id DESC")
    fun getTransactionsBetweenDates(userId: Int, startDate: String, endDate: String): LiveData<List<Transaction>>

    // Get total spent per category between two dates
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE userId = :userId AND type = 'expense' AND date BETWEEN :startDate AND :endDate GROUP BY category")
    suspend fun getCategoryTotalsBetweenDates(userId: Int, startDate: String, endDate: String): List<CategoryTotal>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

// Helper class for category totals query
data class CategoryTotal(
    val category: String,
    val total: Double
)
