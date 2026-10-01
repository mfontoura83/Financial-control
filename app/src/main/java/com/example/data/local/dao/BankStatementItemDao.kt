package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.ReconciliationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BankStatementItemDao {
    @Query("SELECT * FROM bank_statement_items ORDER BY date DESC, id DESC")
    fun getAllStatementItems(): Flow<List<BankStatementItem>>

    @Query("SELECT * FROM bank_statement_items WHERE bankAccountId = :accountId ORDER BY date DESC")
    fun getItemsByAccount(accountId: Long): Flow<List<BankStatementItem>>

    @Query("SELECT * FROM bank_statement_items WHERE isReconciled = 0 ORDER BY date DESC")
    fun getUnreconciledItems(): Flow<List<BankStatementItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: BankStatementItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<BankStatementItem>)

    @Update
    suspend fun updateItem(item: BankStatementItem)

    @Query("UPDATE bank_statement_items SET isReconciled = :reconciled, matchedCashTransactionId = :matchedTxId, matchConfidence = :confidence, reconciliationStatus = :status WHERE id = :id")
    suspend fun setReconciled(id: Long, reconciled: Boolean, matchedTxId: Long?, confidence: Float, status: ReconciliationStatus)

    @Delete
    suspend fun deleteItem(item: BankStatementItem)

    @Query("SELECT COUNT(*) FROM bank_statement_items")
    suspend fun getCount(): Int
}
