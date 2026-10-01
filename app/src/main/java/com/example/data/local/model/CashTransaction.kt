package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_transactions")
data class CashTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val amount: Double, // positive value
    val isExpense: Boolean,
    val date: Long,
    val dueDate: Long,
    val type: TransactionType,
    val status: TransactionStatus,
    val category: String,
    val statementCategory: StatementCategory,
    val dfcActivity: DfcActivity,
    val bankAccountId: Long,
    val isReconciled: Boolean = false,
    val paymentMethod: String = "PIX",
    val documentNumber: String = "",
    val counterpartyName: String = "",
    val counterpartyDocument: String = ""
)
