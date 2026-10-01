package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.FinanceRepository
import com.example.data.local.model.BankAccount
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.ReconciliationStatus
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionStatus
import com.example.data.local.model.TransactionType
import com.example.domain.engine.BalanceSheetResult
import com.example.domain.engine.DfcResult
import com.example.domain.engine.DreResult
import com.example.domain.engine.FinancialEngine
import com.example.domain.engine.FinancialKpis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val label: String) {
    DASHBOARD("Visão Geral"),
    CONTROLADORIA("DRE • BP • DFC"),
    TESOURARIA("Tesouraria & Caixa"),
    CONCILIACAO("Automação Bancária"),
    INDICADORES("Indicadores & Ciclo")
}

enum class StatementSubTab(val label: String) {
    DRE("DRE"),
    BP("Balanço Patrimonial"),
    DFC("Fluxo de Caixa (DFC)")
}

enum class TimePeriod(val label: String) {
    MES_ATUAL("Mês Atual (Set/26)"),
    TRIMESTRE("3º Trimestre (3T26)"),
    ANO("Exercício 2026")
}

data class FinControlUiState(
    val accounts: List<BankAccount> = emptyList(),
    val transactions: List<CashTransaction> = emptyList(),
    val bankStatementItems: List<BankStatementItem> = emptyList(),
    val dre: DreResult? = null,
    val balanceSheet: BalanceSheetResult? = null,
    val dfc: DfcResult? = null,
    val kpis: FinancialKpis? = null,
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val currentStatementTab: StatementSubTab = StatementSubTab.DRE,
    val selectedPeriod: TimePeriod = TimePeriod.MES_ATUAL,
    val showAddTransactionDialog: Boolean = false,
    val showReconcileDialog: Boolean = false,
    val selectedBankItemForReconcile: BankStatementItem? = null,
    val userNotification: String? = null,
    val isSyncingBankApis: Boolean = false
)

class FinControlViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(
            db.bankAccountDao(),
            db.cashTransactionDao(),
            db.bankStatementItemDao()
        )

        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    private val _currentStatementTab = MutableStateFlow(StatementSubTab.DRE)
    private val _selectedPeriod = MutableStateFlow(TimePeriod.MES_ATUAL)
    private val _showAddTransactionDialog = MutableStateFlow(false)
    private val _showReconcileDialog = MutableStateFlow(false)
    private val _selectedBankItem = MutableStateFlow<BankStatementItem?>(null)
    private val _userNotification = MutableStateFlow<String?>(null)
    private val _isSyncingBankApis = MutableStateFlow(false)

    val uiState: StateFlow<FinControlUiState> = combine(
        repository.allAccounts,
        repository.allTransactions,
        repository.allStatementItems,
        _currentScreen,
        _currentStatementTab,
        _selectedPeriod,
        _showAddTransactionDialog,
        _showReconcileDialog,
        _selectedBankItem,
        _userNotification,
        _isSyncingBankApis
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val accounts = params[0] as List<BankAccount>
        val transactions = params[1] as List<CashTransaction>
        val statementItems = params[2] as List<BankStatementItem>
        val currentScreen = params[3] as AppScreen
        val statementTab = params[4] as StatementSubTab
        val period = params[5] as TimePeriod
        val showAdd = params[6] as Boolean
        val showRec = params[7] as Boolean
        val selectedBankItem = params[8] as BankStatementItem?
        val notification = params[9] as String?
        val isSyncing = params[10] as Boolean

        val dre = FinancialEngine.calculateDre(transactions)
        val bp = FinancialEngine.calculateBalanceSheet(accounts, transactions, dre)
        val dfc = FinancialEngine.calculateDfc(accounts, transactions)
        val kpis = FinancialEngine.calculateKpis(dre, bp)

        FinControlUiState(
            accounts = accounts,
            transactions = transactions,
            bankStatementItems = statementItems,
            dre = dre,
            balanceSheet = bp,
            dfc = dfc,
            kpis = kpis,
            currentScreen = currentScreen,
            currentStatementTab = statementTab,
            selectedPeriod = period,
            showAddTransactionDialog = showAdd,
            showReconcileDialog = showRec,
            selectedBankItemForReconcile = selectedBankItem,
            userNotification = notification,
            isSyncingBankApis = isSyncing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinControlUiState()
    )

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setStatementTab(tab: StatementSubTab) {
        _currentStatementTab.value = tab
    }

    fun setPeriod(period: TimePeriod) {
        _selectedPeriod.value = period
    }

    fun showAddTransactionDialog(show: Boolean) {
        _showAddTransactionDialog.value = show
    }

    fun openReconciliationForBankItem(item: BankStatementItem?) {
        _selectedBankItem.value = item
        _showReconcileDialog.value = item != null
    }

    fun clearNotification() {
        _userNotification.value = null
    }

    fun createTransaction(
        description: String,
        amount: Double,
        isExpense: Boolean,
        type: TransactionType,
        category: String,
        statementCategory: StatementCategory,
        dfcActivity: DfcActivity,
        bankAccountId: Long,
        paymentMethod: String,
        counterpartyName: String,
        documentNumber: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val tx = CashTransaction(
                description = description,
                amount = amount,
                isExpense = isExpense,
                date = now,
                dueDate = now,
                type = type,
                status = TransactionStatus.REALIZADO,
                category = category,
                statementCategory = statementCategory,
                dfcActivity = dfcActivity,
                bankAccountId = bankAccountId,
                isReconciled = false,
                paymentMethod = paymentMethod,
                counterpartyName = counterpartyName,
                documentNumber = documentNumber
            )
            repository.insertTransaction(tx)
            _showAddTransactionDialog.value = false
            _userNotification.value = "Lançamento de R$ ${String.format("%.2f", amount)} registrado com sucesso!"
        }
    }

    fun runAutoReconciliation() {
        viewModelScope.launch {
            val count = repository.runAutomatedReconciliation()
            _userNotification.value = if (count > 0) {
                "$count transações foram conciliadas automaticamente por valor, data e contraparte!"
            } else {
                "Conciliação automática executada: todas as transações elegíveis já estão conciliadas."
            }
        }
    }

    fun reconcileBankItemWithTransaction(bankItemId: Long, transactionId: Long) {
        viewModelScope.launch {
            repository.reconcile(bankItemId, transactionId, isAuto = false)
            _showReconcileDialog.value = false
            _selectedBankItem.value = null
            _userNotification.value = "Conciliação bancária confirmada com sucesso!"
        }
    }

    fun unmatchReconciliation(bankItemId: Long, matchedTxId: Long?) {
        viewModelScope.launch {
            repository.unmatchReconciliation(bankItemId, matchedTxId)
            _userNotification.value = "Vínculo de conciliação desfeito."
        }
    }

    fun createAndReconcileFromBankFeed(
        item: BankStatementItem,
        type: TransactionType,
        statementCat: StatementCategory,
        dfcAct: DfcActivity,
        category: String
    ) {
        viewModelScope.launch {
            repository.createTransactionFromBankItem(
                bankItem = item,
                type = type,
                statementCategory = statementCat,
                dfcActivity = dfcAct,
                category = category
            )
            _showReconcileDialog.value = false
            _selectedBankItem.value = null
            _userNotification.value = "Lançamento criado no ERP e conciliado com o extrato bancário!"
        }
    }

    fun syncAllBankApis() {
        viewModelScope.launch {
            _isSyncingBankApis.value = true
            val currentAccounts = uiState.value.accounts
            for (acc in currentAccounts) {
                repository.syncOpenFinance(acc.id)
            }
            kotlinx.coroutines.delay(1200) // Realistic banking latency
            _isSyncingBankApis.value = false
            _userNotification.value = "APIs Bancárias Open Finance sincronizadas em tempo real (4 bancos atualizados)."
        }
    }
}
