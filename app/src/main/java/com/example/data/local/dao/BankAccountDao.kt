package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts ORDER BY id ASC")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    fun getBankAccountById(id: Long): Flow<BankAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccount): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBankAccounts(accounts: List<BankAccount>)

    @Update
    suspend fun updateBankAccount(account: BankAccount)

    @Query("UPDATE bank_accounts SET currentBalance = :newBalance WHERE id = :id")
    suspend fun updateBalance(id: Long, newBalance: Double)

    @Delete
    suspend fun deleteBankAccount(account: BankAccount)

    @Query("SELECT COUNT(*) FROM bank_accounts")
    suspend fun getCount(): Int
}
