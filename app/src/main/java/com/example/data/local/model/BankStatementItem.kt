package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_statement_items")
data class BankStatementItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankAccountId: Long,
    val transactionId: String, // FITID from bank Open Finance
    val date: Long,
    val amount: Double, // positive = inflow, negative = outflow
    val description: String,
    val counterparty: String = "",
    val isReconciled: Boolean = false,
    val matchedCashTransactionId: Long? = null,
    val matchConfidence: Float = 0f,
    val reconciliationStatus: ReconciliationStatus = ReconciliationStatus.NAO_CONCILIADO
)
