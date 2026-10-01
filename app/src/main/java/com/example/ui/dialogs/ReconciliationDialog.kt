package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionType
import com.example.ui.components.formatCurrency
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.CashInflowGreen
import com.example.ui.theme.CashOutflowRed
import com.example.ui.theme.PrimaryAccentBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun ReconciliationDialog(
    bankItem: BankStatementItem,
    candidateTransactions: List<CashTransaction>,
    onDismiss: () -> Unit,
    onLinkTransaction: (bankItemId: Long, txId: Long) -> Unit,
    onCreateAndReconcile: (
        item: BankStatementItem,
        type: TransactionType,
        statementCategory: StatementCategory,
        dfcActivity: DfcActivity,
        category: String
    ) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
    val isDebit = bankItem.amount < 0
    val absAmount = abs(bankItem.amount)

    // Find suggested match
    val matchingCandidates = remember(candidateTransactions, bankItem) {
        candidateTransactions.filter { tx ->
            !tx.isReconciled &&
                    tx.isExpense == isDebit &&
                    abs(tx.amount - absAmount) < 0.01
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Conciliação Bancária",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Bank Item Detail Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item do Extrato Bancário",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val amountColor = if (isDebit) CashOutflowRed else CashInflowGreen
                            val prefix = if (isDebit) "- " else "+ "
                            Text(
                                text = prefix + formatCurrency(absAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = amountColor
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bankItem.description,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Data: ${dateFormat.format(Date(bankItem.date))} • ${bankItem.transactionId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Suggestion section
                if (matchingCandidates.isNotEmpty()) {
                    Text(
                        text = "Correspondência Encontrada no ERP (${matchingCandidates.size})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = AccentEmerald
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    matchingCandidates.forEach { candidate ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLinkTransaction(bankItem.id, candidate.id) }
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = AccentEmerald.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Match 100%",
                                                color = AccentEmerald,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                fontSize = 9.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = candidate.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = "${candidate.category} • Venc: ${dateFormat.format(Date(candidate.dueDate))}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }

                                Button(
                                    onClick = { onLinkTransaction(bankItem.id, candidate.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Vincular", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Option to create transaction directly from bank statement
                Text(
                    text = "Ou Criar Lançamento a partir do Extrato",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Gera a contrapartida contábil no ERP e liquida imediatamente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                val suggestedType = if (isDebit) {
                    if (bankItem.description.contains("TARIFA", true) || bankItem.description.contains("IOF", true)) {
                        TransactionType.DESPESA_OPERACIONAL
                    } else if (bankItem.description.contains("IMPOSTO", true) || bankItem.description.contains("DARF", true)) {
                        TransactionType.IMPOSTO_TRIBUTO
                    } else {
                        TransactionType.DESPESA_ADMINISTRATIVA
                    }
                } else {
                    TransactionType.RECEITA
                }

                val suggestedCategory = if (isDebit) {
                    if (bankItem.description.contains("TARIFA", true)) "Tarifas Bancárias" else "Despesas Gerais"
                } else {
                    "Receitas Comerciais / Spot"
                }

                val suggestedStatementCat = if (isDebit) {
                    if (bankItem.description.contains("TARIFA", true)) StatementCategory.DESPESAS_FINANCEIRAS
                    else StatementCategory.DESPESAS_ADMINISTRATIVAS
                } else {
                    StatementCategory.RECEITA_BRUTA
                }

                Button(
                    onClick = {
                        onCreateAndReconcile(
                            bankItem,
                            suggestedType,
                            suggestedStatementCat,
                            DfcActivity.OPERACIONAL,
                            suggestedCategory
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_create_and_reconcile"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccentBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Criar e Conciliar Automaticamente")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
