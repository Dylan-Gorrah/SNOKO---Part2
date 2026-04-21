package com.snokonoko.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    // Returns LiveData - the UI automatically updates when data changes
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}
