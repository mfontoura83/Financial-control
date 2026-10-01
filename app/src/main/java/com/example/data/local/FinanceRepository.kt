package com.example.data.local

import com.example.data.local.dao.BankAccountDao
import com.example.data.local.dao.BankStatementItemDao
import com.example.data.local.dao.CashTransactionDao
import com.example.data.local.model.BankAccount
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.ReconciliationStatus
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionStatus
import com.example.data.local.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.math.abs

class FinanceRepository(
    private val bankAccountDao: BankAccountDao,
    private val cashTransactionDao: CashTransactionDao,
    private val bankStatementItemDao: BankStatementItemDao
) {
    val allAccounts: Flow<List<BankAccount>> = bankAccountDao.getAllBankAccounts()
    val allTransactions: Flow<List<CashTransaction>> = cashTransactionDao.getAllTransactions()
    val allStatementItems: Flow<List<BankStatementItem>> = bankStatementItemDao.getAllStatementItems()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val accountCount = bankAccountDao.getCount()
        if (accountCount == 0) {
            bankAccountDao.insertAllBankAccounts(FinanceDatabaseInitializer.getInitialAccounts())
            cashTransactionDao.insertAllTransactions(FinanceDatabaseInitializer.getInitialTransactions())
            bankStatementItemDao.insertAllItems(FinanceDatabaseInitializer.getInitialBankFeed())
        }
    }

    suspend fun insertTransaction(transaction: CashTransaction): Long = withContext(Dispatchers.IO) {
        val id = cashTransactionDao.insertTransaction(transaction)
        // If transaction is REALIZADO, update the account balance
        if (transaction.status == TransactionStatus.REALIZADO) {
            updateAccountBalanceForTransaction(transaction.bankAccountId, transaction.amount, transaction.isExpense)
        }
        id
    }

    suspend fun updateTransaction(transaction: CashTransaction) = withContext(Dispatchers.IO) {
        cashTransactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: CashTransaction) = withContext(Dispatchers.IO) {
        cashTransactionDao.deleteTransaction(transaction)
    }

    suspend fun insertBankAccount(account: BankAccount): Long = withContext(Dispatchers.IO) {
        bankAccountDao.insertBankAccount(account)
    }

    suspend fun updateBankAccount(account: BankAccount) = withContext(Dispatchers.IO) {
        bankAccountDao.updateBankAccount(account)
    }

    suspend fun reconcile(statementItemId: Long, transactionId: Long, isAuto: Boolean) = withContext(Dispatchers.IO) {
        val status = if (isAuto) ReconciliationStatus.CONCILIADO_AUTOMATICO else ReconciliationStatus.CONCILIADO_MANUAL
        bankStatementItemDao.setReconciled(
            id = statementItemId,
            reconciled = true,
            matchedTxId = transactionId,
            confidence = 1.0f,
            status = status
        )
        cashTransactionDao.setReconciled(transactionId, true)
    }

    suspend fun unmatchReconciliation(statementItemId: Long, transactionId: Long?) = withContext(Dispatchers.IO) {
        bankStatementItemDao.setReconciled(
            id = statementItemId,
            reconciled = false,
            matchedTxId = null,
            confidence = 0f,
            status = ReconciliationStatus.NAO_CONCILIADO
        )
        if (transactionId != null) {
            cashTransactionDao.setReconciled(transactionId, false)
        }
    }

    suspend fun createTransactionFromBankItem(
        bankItem: BankStatementItem,
        type: TransactionType,
        statementCategory: StatementCategory,
        dfcActivity: DfcActivity,
        category: String
    ): Long = withContext(Dispatchers.IO) {
        val isExpense = bankItem.amount < 0
        val absAmount = abs(bankItem.amount)

        val newTx = CashTransaction(
            description = bankItem.description,
            amount = absAmount,
            isExpense = isExpense,
            date = bankItem.date,
            dueDate = bankItem.date,
            type = type,
            status = TransactionStatus.REALIZADO,
            category = category,
            statementCategory = statementCategory,
            dfcActivity = dfcActivity,
            bankAccountId = bankItem.bankAccountId,
            isReconciled = true,
            paymentMethod = if (bankItem.description.contains("PIX", true)) "PIX" else "Bancário",
            counterpartyName = bankItem.counterparty.ifBlank { "Identificado via Extrato" }
        )

        val newTxId = cashTransactionDao.insertTransaction(newTx)
        bankStatementItemDao.setReconciled(
            id = bankItem.id,
            reconciled = true,
            matchedTxId = newTxId,
            confidence = 1.0f,
            status = ReconciliationStatus.CONCILIADO_MANUAL
        )
        updateAccountBalanceForTransaction(bankItem.bankAccountId, absAmount, isExpense)
        newTxId
    }

    suspend fun runAutomatedReconciliation(): Int = withContext(Dispatchers.IO) {
        val unreconciledBankItems = bankStatementItemDao.getUnreconciledItems().first()
        val allTx = cashTransactionDao.getAllTransactions().first()
        val unreconciledTx = allTx.filter { !it.isReconciled }

        var matchedCount = 0

        for (bankItem in unreconciledBankItems) {
            val bankAbsAmount = abs(bankItem.amount)
            val bankIsExpense = bankItem.amount < 0

            // Try exact amount + same direction + close date (within 7 days)
            val candidate = unreconciledTx.firstOrNull { tx ->
                !tx.isReconciled &&
                        tx.isExpense == bankIsExpense &&
                        abs(tx.amount - bankAbsAmount) < 0.01 &&
                        abs(tx.date - bankItem.date) <= (7L * 24 * 60 * 60 * 1000)
            }

            if (candidate != null) {
                reconcile(bankItem.id, candidate.id, isAuto = true)
                matchedCount++
            }
        }

        matchedCount
    }

    suspend fun syncOpenFinance(accountId: Long): Boolean = withContext(Dispatchers.IO) {
        val account = bankAccountDao.getBankAccountById(accountId).first() ?: return@withContext false
        val updatedAccount = account.copy(
            lastSyncTime = System.currentTimeMillis(),
            apiConnected = true
        )
        bankAccountDao.updateBankAccount(updatedAccount)
        true
    }

    private suspend fun updateAccountBalanceForTransaction(accountId: Long, amount: Double, isExpense: Boolean) {
        val account = bankAccountDao.getBankAccountById(accountId).first() ?: return
        val delta = if (isExpense) -amount else amount
        val newBalance = account.currentBalance + delta
        bankAccountDao.updateBalance(accountId, newBalance)
    }
}
