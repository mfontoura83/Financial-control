package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CashTransactionDao {
    @Query("SELECT * FROM cash_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<CashTransaction>>

    @Query("SELECT * FROM cash_transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsBetween(startDate: Long, endDate: Long): Flow<List<CashTransaction>>

    @Query("SELECT * FROM cash_transactions WHERE status = :status ORDER BY dueDate ASC")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<CashTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CashTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<CashTransaction>)

    @Update
    suspend fun updateTransaction(transaction: CashTransaction)

    @Query("UPDATE cash_transactions SET isReconciled = :isReconciled WHERE id = :id")
    suspend fun setReconciled(id: Long, isReconciled: Boolean)

    @Query("UPDATE cash_transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: TransactionStatus)

    @Delete
    suspend fun deleteTransaction(transaction: CashTransaction)

    @Query("SELECT COUNT(*) FROM cash_transactions")
    suspend fun getCount(): Int
}
