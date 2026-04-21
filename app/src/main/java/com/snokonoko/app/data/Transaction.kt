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
