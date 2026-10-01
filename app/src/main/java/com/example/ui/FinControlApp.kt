package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.ReconciliationDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IndicatorsScreen
import com.example.ui.screens.ReconciliationScreen
import com.example.ui.screens.StatementsScreen
import com.example.ui.screens.TreasuryScreen
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryAccentBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinControlApp(
    viewModel: FinControlViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button: if not on DASHBOARD, return to DASHBOARD
    BackHandler(enabled = state.currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    LaunchedEffect(state.userNotification) {
        state.userNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    val unreconciledCount = state.bankStatementItems.count { !it.isReconciled }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FinControl",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PrimaryAccentBlue
                            ) {
                                Text(
                                    text = "PRO",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Controladoria • Tesouraria • Open Finance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.isSyncingBankApis) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryAccentBlue
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentEmerald)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "APIs Online",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = state.currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Visão Geral", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = state.currentScreen == AppScreen.CONTROLADORIA,
                    onClick = { viewModel.navigateTo(AppScreen.CONTROLADORIA) },
                    icon = { Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Controladoria") },
                    label = { Text("DRE • BP", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_controladoria")
                )

                NavigationBarItem(
                    selected = state.currentScreen == AppScreen.TESOURARIA,
                    onClick = { viewModel.navigateTo(AppScreen.TESOURARIA) },
                    icon = { Icon(imageVector = Icons.Default.Payments, contentDescription = "Tesouraria") },
                    label = { Text("Tesouraria", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_tesouraria")
                )

                NavigationBarItem(
                    selected = state.currentScreen == AppScreen.CONCILIACAO,
                    onClick = { viewModel.navigateTo(AppScreen.CONCILIACAO) },
                    icon = {
                        if (unreconciledCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = AccentRose) {
                                        Text("$unreconciledCount")
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Automação")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Automação")
                        }
                    },
                    label = { Text("Automação", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_conciliacao")
                )

                NavigationBarItem(
                    selected = state.currentScreen == AppScreen.INDICADORES,
                    onClick = { viewModel.navigateTo(AppScreen.INDICADORES) },
                    icon = { Icon(imageVector = Icons.Default.InsertChart, contentDescription = "Indicadores") },
                    label = { Text("Indicadores", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_indicadores")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(state = state, viewModel = viewModel)
                AppScreen.CONTROLADORIA -> StatementsScreen(state = state, viewModel = viewModel)
                AppScreen.TESOURARIA -> TreasuryScreen(state = state, viewModel = viewModel)
                AppScreen.CONCILIACAO -> ReconciliationScreen(state = state, viewModel = viewModel)
                AppScreen.INDICADORES -> IndicatorsScreen(state = state, viewModel = viewModel)
            }
        }

        // Add Transaction Dialog
        if (state.showAddTransactionDialog) {
            AddTransactionDialog(
                accounts = state.accounts,
                onDismiss = { viewModel.showAddTransactionDialog(false) },
                onConfirm = { desc, amount, isExp, type, cat, stmtCat, dfcAct, bankId, payMethod, party, doc ->
                    viewModel.createTransaction(
                        description = desc,
                        amount = amount,
                        isExpense = isExp,
                        type = type,
                        category = cat,
                        statementCategory = stmtCat,
                        dfcActivity = dfcAct,
                        bankAccountId = bankId,
                        paymentMethod = payMethod,
                        counterpartyName = party,
                        documentNumber = doc
                    )
                }
            )
        }

        // Bank Item Reconciliation Dialog
        state.selectedBankItemForReconcile?.let { bankItem ->
            if (state.showReconcileDialog) {
                ReconciliationDialog(
                    bankItem = bankItem,
                    candidateTransactions = state.transactions,
                    onDismiss = { viewModel.openReconciliationForBankItem(null) },
                    onLinkTransaction = { bId, txId ->
                        viewModel.reconcileBankItemWithTransaction(bId, txId)
                    },
                    onCreateAndReconcile = { item, type, stmtCat, dfcAct, cat ->
                        viewModel.createAndReconcileFromBankFeed(item, type, stmtCat, dfcAct, cat)
                    }
                )
            }
        }
    }
}
