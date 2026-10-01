package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.BankAccount
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.TransactionStatus
import com.example.ui.AppScreen
import com.example.ui.FinControlUiState
import com.example.ui.FinControlViewModel
import com.example.ui.TimePeriod
import com.example.ui.components.CashFlowBarPoint
import com.example.ui.components.CashFlowProjectionChart
import com.example.ui.components.ExpenseBreakdownSection
import com.example.ui.components.ExpenseCategoryItem
import com.example.ui.components.FinancialKpiCard
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatPercent
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRose
import com.example.ui.theme.CashInflowGreen
import com.example.ui.theme.CashOutflowRed
import com.example.ui.theme.PrimaryAccentBlue
import com.example.ui.theme.SecondaryTeal

@Composable
fun DashboardScreen(
    state: FinControlUiState,
    viewModel: FinControlViewModel,
    modifier: Modifier = Modifier
) {
    val totalCash = state.accounts.sumOf { it.currentBalance }
    val dre = state.dre
    val bp = state.balanceSheet
    val kpis = state.kpis

    // Mock/derived daily points for Cash Flow chart
    val chartPoints = listOf(
        CashFlowBarPoint("D-6", 62000.0, 18200.0, 43800.0),
        CashFlowBarPoint("D-5", 88500.0, 76000.0, 12500.0),
        CashFlowBarPoint("D-4", 15000.0, 34800.0, -19800.0),
        CashFlowBarPoint("D-3", 22000.0, 14500.0, 7500.0),
        CashFlowBarPoint("D-2", 145000.0, 26500.0, 118500.0),
        CashFlowBarPoint("D-1", 7650.0, 6200.0, 1450.0),
        CashFlowBarPoint("Hoje", 115000.0, 3850.0, 111150.0)
    )

    val expenseItems = listOf(
        ExpenseCategoryItem("Custos Mercadoria (CPV/CMV)", dre?.cpvCmv ?: 63200.0, Color(0xFFEF4444)),
        ExpenseCategoryItem("Despesas Admin & Folha", dre?.despesasAdministrativas ?: 102300.0, Color(0xFFF59E0B)),
        ExpenseCategoryItem("Vendas & Marketing", dre?.despesasVendas ?: 32700.0, Color(0xFF3B82F6)),
        ExpenseCategoryItem("Tributos & Deduções", (dre?.deducoesImpostos ?: 26500.0) + (dre?.impostosLucro ?: 14800.0), Color(0xFF8B5CF6)),
        ExpenseCategoryItem("Despesas Financeiras", dre?.despesasFinanceiras ?: 3850.0, Color(0xFF10B981))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Period Filter Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimePeriod.values().forEach { period ->
                    FilterChip(
                        selected = state.selectedPeriod == period,
                        onClick = { viewModel.setPeriod(period) },
                        label = { Text(period.label) },
                        leadingIcon = if (state.selectedPeriod == period) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Hero KPI Cards Grid (2x2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinancialKpiCard(
                        title = "Caixa Consolidado",
                        value = formatCurrency(totalCash),
                        subtitle = "4 Contas Integradas",
                        icon = Icons.Default.AccountBalance,
                        iconBgColor = PrimaryAccentBlue.copy(alpha = 0.15f),
                        iconTint = PrimaryAccentBlue,
                        isPositiveTrend = true,
                        trendLabel = "+18.4%",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_cash_balance"
                    )
                    FinancialKpiCard(
                        title = "Lucro Líquido DRE",
                        value = formatCurrency(dre?.lucroLiquido ?: 0.0),
                        subtitle = "Margem ${formatPercent(dre?.margemLiquidaPct ?: 0.0)}",
                        icon = Icons.Default.MonetizationOn,
                        iconBgColor = AccentEmerald.copy(alpha = 0.15f),
                        iconTint = AccentEmerald,
                        isPositiveTrend = (dre?.lucroLiquido ?: 0.0) >= 0,
                        trendLabel = if ((dre?.lucroLiquido ?: 0.0) >= 0) "Superávit" else "Déficit",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_net_profit"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinancialKpiCard(
                        title = "EBITDA Operacional",
                        value = formatCurrency(dre?.ebitda ?: 0.0),
                        subtitle = "Margem ${formatPercent(dre?.margemEbitdaPct ?: 0.0)}",
                        icon = Icons.Default.TrendingUp,
                        iconBgColor = AccentAmber.copy(alpha = 0.15f),
                        iconTint = AccentAmber,
                        isPositiveTrend = true,
                        trendLabel = "Forte",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_ebitda"
                    )
                    FinancialKpiCard(
                        title = "Nec. Capital Giro",
                        value = formatCurrency(kpis?.necessidadeCapitalGiro ?: 0.0),
                        subtitle = "Ciclo Fin: ${kpis?.cicloFinanceiroDias ?: 32}d",
                        icon = Icons.Default.HourglassEmpty,
                        iconBgColor = AccentPurple.copy(alpha = 0.15f),
                        iconTint = AccentPurple,
                        isPositiveTrend = true,
                        trendLabel = "Cob. OK",
                        modifier = Modifier.weight(1f),
                        testTag = "kpi_working_capital"
                    )
                }
            }
        }

        // Quick Action Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Ações Rápidas de Gestão",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.runAutoReconciliation() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                            modifier = Modifier.testTag("quick_action_auto_reconcile")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Conciliação Inteligente", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.syncAllBankApis() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccentBlue),
                            modifier = Modifier.testTag("quick_action_sync_banks")
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (state.isSyncingBankApis) "Sincronizando..." else "Open Finance Sync", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.showAddTransactionDialog(true) },
                            modifier = Modifier.testTag("quick_action_new_tx")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Novo Lançamento", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.CONTROLADORIA) }
                        ) {
                            Icon(imageVector = Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ver DRE & BP", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Bank Accounts Carousel
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contas Bancárias (Tesouraria em Tempo Real)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ver Todas",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryAccentBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.TESOURARIA) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.accounts.forEach { account ->
                        BankSummaryCard(account = account)
                    }
                }
            }
        }

        // Cash Flow Realtime Projection Chart
        item {
            CashFlowProjectionChart(points = chartPoints)
        }

        // Expense Structure Breakdown
        item {
            ExpenseBreakdownSection(items = expenseItems)
        }

        // Recent Movements Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimos Lançamentos Auditados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Extrato Completo",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryAccentBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.TESOURARIA) }
                )
            }
        }

        // Recent items slice
        items(state.transactions.take(5)) { tx ->
            TransactionRowItem(transaction = tx)
        }
    }
}

@Composable
fun BankSummaryCard(account: BankAccount) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("bank_card_${account.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val bankColor = try {
                        Color(android.graphics.Color.parseColor(account.colorHex))
                    } catch (_: Exception) {
                        PrimaryAccentBlue
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(bankColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = account.bankName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = AccentEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "API ON",
                        color = AccentEmerald,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ag ${account.agency} • CC ${account.accountNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatCurrency(account.currentBalance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = account.accountType,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TransactionRowItem(transaction: CashTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconColor = if (transaction.isExpense) CashOutflowRed else CashInflowGreen
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (transaction.isExpense) "−" else "+",
                        color = iconColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        if (transaction.isReconciled) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentEmerald.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Conciliado",
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Conciliado",
                                        color = AccentEmerald,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val amountColor = if (transaction.isExpense) CashOutflowRed else CashInflowGreen
                val prefix = if (transaction.isExpense) "- " else "+ "
                Text(
                    text = prefix + formatCurrency(transaction.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                Text(
                    text = transaction.paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}
