package com.example.saku.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saku.app.core.database.entity.LoanApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Query("SELECT * FROM loan_applications ORDER BY createdDate DESC")
    fun getAllLoansFlow(): Flow<List<LoanApplicationEntity>>

    @Query("SELECT * FROM loan_applications ORDER BY createdDate DESC")
    suspend fun getAllLoans(): List<LoanApplicationEntity>

    @Query("SELECT * FROM loan_applications WHERE id = :id LIMIT 1")
    fun getLoanByIdFlow(id: String): Flow<LoanApplicationEntity?>

    @Query("SELECT * FROM loan_applications WHERE id = :id LIMIT 1")
    suspend fun getLoanById(id: String): LoanApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanApplicationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanApplicationEntity)

    @Query("DELETE FROM loan_applications")
    suspend fun clearLoans()
}
