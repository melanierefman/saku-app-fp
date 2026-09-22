package com.example.saku.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saku.app.core.database.entity.CustomerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer_profile LIMIT 1")
    fun getProfileFlow(): Flow<CustomerProfileEntity?>

    @Query("SELECT * FROM customer_profile LIMIT 1")
    suspend fun getProfile(): CustomerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CustomerProfileEntity)

    @Query("DELETE FROM customer_profile")
    suspend fun clearProfile()
}