package com.snokonoko.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val colour: String, // hex color like "#FF6B6B"
    val isDefault: Boolean = false // system categories vs user-created
)
