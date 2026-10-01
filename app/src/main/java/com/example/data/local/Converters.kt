package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.ReconciliationStatus
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionStatus
import com.example.data.local.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = try {
        TransactionType.valueOf(value)
    } catch (_: Exception) {
        TransactionType.DESPESA_OPERACIONAL
    }

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String = value.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = try {
        TransactionStatus.valueOf(value)
    } catch (_: Exception) {
        TransactionStatus.PENDENTE
    }

    @TypeConverter
    fun fromStatementCategory(value: StatementCategory): String = value.name

    @TypeConverter
    fun toStatementCategory(value: String): StatementCategory = try {
        StatementCategory.valueOf(value)
    } catch (_: Exception) {
        StatementCategory.DESPESAS_ADMINISTRATIVAS
    }

    @TypeConverter
    fun fromDfcActivity(value: DfcActivity): String = value.name

    @TypeConverter
    fun toDfcActivity(value: String): DfcActivity = try {
        DfcActivity.valueOf(value)
    } catch (_: Exception) {
        DfcActivity.OPERACIONAL
    }

    @TypeConverter
    fun fromReconciliationStatus(value: ReconciliationStatus): String = value.name

    @TypeConverter
    fun toReconciliationStatus(value: String): ReconciliationStatus = try {
        ReconciliationStatus.valueOf(value)
    } catch (_: Exception) {
        ReconciliationStatus.NAO_CONCILIADO
    }
}
