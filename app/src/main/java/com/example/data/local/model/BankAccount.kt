package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankName: String,
    val bankCode: String,
    val agency: String,
    val accountNumber: String,
    val currentBalance: Double,
    val accountType: String = "Conta Corrente PJ",
    val apiConnected: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val colorHex: String = "#1E40AF"
)
