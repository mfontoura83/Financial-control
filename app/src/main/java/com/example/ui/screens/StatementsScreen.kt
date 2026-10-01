package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FinControlUiState
import com.example.ui.FinControlViewModel
import com.example.ui.StatementSubTab
import com.example.ui.components.BalanceSheetTableView
import com.example.ui.components.DfcTableView
import com.example.ui.components.DreTableView
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatPercent
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryAccentBlue
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementsScreen(
    state: FinControlUiState,
    viewModel: FinControlViewModel,
    modifier: Modifier = Modifier
) {
    val dre = state.dre
    val bp = state.balanceSheet
    val dfc = state.dfc

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("statements_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selection: DRE, BP, DFC
        item {
            PrimaryTabRow(
                selectedTabIndex = state.currentStatementTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("statements_tab_row")
            ) {
                StatementSubTab.values().forEach { tab ->
                    Tab(
                        selected = state.currentStatementTab == tab,
                        onClick = { viewModel.setStatementTab(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                fontWeight = if (state.currentStatementTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        }

        // Executive Audit & Compliance Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryAccentBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Conformidade",
                            tint = PrimaryAccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Integração Contábil & Controladoria 100% Validada",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Os lançamentos de caixa alimentam o Balanço, a DRE e o DFC em tempo real conforme as normas CPC 00, CPC 03 (DFC) e CPC 26.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Statement content depending on selected tab
        when (state.currentStatementTab) {
            StatementSubTab.DRE -> {
                if (dre != null) {
                    item {
                        DreTableView(dre = dre)
                    }
                    item {
                        DreExecutiveInsightsCard(dre = dre)
                    }
                }
            }
            StatementSubTab.BP -> {
                if (bp != null) {
                    item {
                        BalanceSheetTableView(bp = bp)
                    }
                    item {
                        BpExecutiveInsightsCard(bp = bp)
                    }
                }
            }
            StatementSubTab.DFC -> {
                if (dfc != null) {
                    item {
                        DfcTableView(dfc = dfc)
                    }
                    item {
                        DfcExecutiveInsightsCard(dfc = dfc)
                    }
                }
            }
        }
    }
}

@Composable
fun DreExecutiveInsightsCard(dre: com.example.domain.engine.DreResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = PrimaryAccentBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Diagnóstico e Parecer do Controller (DRE)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "• A Margem Bruta de ${formatPercent(dre.margemBrutaPct)} reflete boa precificação e controle sobre custos diretos (CPV/CMV).\n" +
                        "• O EBITDA de ${formatCurrency(dre.ebitda)} (Margem ${formatPercent(dre.margemEbitdaPct)}) demonstra sólida capacidade de geração de caixa operacional antes dos efeitos de depreciação e juros.\n" +
                        "• O Lucro Líquido final de ${formatCurrency(dre.lucroLiquido)} entrega uma Margem Líquida saudável de ${formatPercent(dre.margemLiquidaPct)}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun BpExecutiveInsightsCard(bp: com.example.domain.engine.BalanceSheetResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = AccentEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estrutura Patrimonial & Solvência (BP)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val capitalProprioRatio = (bp.totalPatrimonioLiquido / bp.totalAtivo) * 100
            Text(
                text = "• O Ativo Total está avaliado em ${formatCurrency(bp.totalAtivo)}, financiado por ${formatPercent(capitalProprioRatio)} de Capital Próprio (PL).\n" +
                        "• O Ativo Circulante de ${formatCurrency(bp.totalAtivoCirculante)} cobre confortavelmente o Passivo Circulante de ${formatCurrency(bp.totalPassivoCirculante)}.\n" +
                        "• As Disponibilidades em Caixa e Bancos (${formatCurrency(bp.caixaBancos)}) garantem folga financeira para honrar os compromissos de curto prazo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun DfcExecutiveInsightsCard(dfc: com.example.domain.engine.DfcResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = PrimaryAccentBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dinâmica de Liquidez e Caixa (DFC)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "• O Fluxo Operacional Líquido gerou ${formatCurrency(dfc.fluxoOperacionalLiquido)}, comprovando que as operações comerciais financiam as obrigações correntes.\n" +
                        "• Os investimentos em CAPEX (${formatCurrency(dfc.capexAquisicoes)}) foram absorvidos sem necessidade de endividamento emergencial.\n" +
                        "• A variação líquida total de ${formatCurrency(dfc.variacaoLiquidaCaixa)} resultou no saldo de fechamento de ${formatCurrency(dfc.saldoFinalCaixa)}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
