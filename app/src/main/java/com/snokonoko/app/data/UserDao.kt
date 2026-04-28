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

    @Query("SELECT * FROM users ORDER BY firstName ASC")
    suspend fun getAllUsers(): List<User>

    @Query("UPDATE users SET firstName = :firstName, surname = :surname WHERE id = :id")
    suspend fun updateUser(id: Int, firstName: String, surname: String)
}
