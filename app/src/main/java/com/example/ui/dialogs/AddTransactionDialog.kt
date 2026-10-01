package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.BankAccount
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionType
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryAccentBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    accounts: List<BankAccount>,
    onDismiss: () -> Unit,
    onConfirm: (
        description: String,
        amount: Double,
        isExpense: Boolean,
        type: TransactionType,
        category: String,
        statementCategory: StatementCategory,
        dfcActivity: DfcActivity,
        bankAccountId: Long,
        paymentMethod: String,
        counterparty: String,
        docNumber: String
    ) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf(TransactionType.DESPESA_ADMINISTRATIVA) }
    var category by remember { mutableStateOf("Despesas Administrativas") }
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull() ?: BankAccount(id = 1, bankName = "Itaú", bankCode = "341", agency = "0001", accountNumber = "1234", currentBalance = 0.0)) }
    var paymentMethod by remember { mutableStateOf("PIX") }
    var counterparty by remember { mutableStateOf("") }
    var docNumber by remember { mutableStateOf("") }

    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var accountDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Novo Lançamento Financeiro",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Inflow or Outflow Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isExpense,
                        onClick = {
                            isExpense = false
                            selectedType = TransactionType.RECEITA
                            category = "Receitas de Serviços B2B"
                        },
                        label = { Text("Entrada (+)", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isExpense,
                        onClick = {
                            isExpense = true
                            selectedType = TransactionType.DESPESA_ADMINISTRATIVA
                            category = "Despesas Administrativas"
                        },
                        label = { Text("Saída (−)", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Lançamento") },
                    placeholder = { Text("Ex: Faturamento Contrato Alpha") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_description")
                )

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$)") },
                    placeholder = { Text("Ex: 15400.00") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tx_amount")
                )

                // Contraparte / Beneficiário / Cliente
                OutlinedTextField(
                    value = counterparty,
                    onValueChange = { counterparty = it },
                    label = { Text("Contraparte (Cliente / Fornecedor)") },
                    placeholder = { Text("Ex: IBM Brasil Indústria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Bank Account Selection
                ExposedDropdownMenuBox(
                    expanded = accountDropdownExpanded,
                    onExpandedChange = { accountDropdownExpanded = !accountDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAccount.bankName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Conta Bancária de Liquidação") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = accountDropdownExpanded,
                        onDismissRequest = { accountDropdownExpanded = false }
                    ) {
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("${acc.bankName} (${acc.accountNumber})") },
                                onClick = {
                                    selectedAccount = acc
                                    accountDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Transaction Type / Natureza Contábil
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Classificação DRE / BP") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        TransactionType.values().forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.label) },
                                onClick = {
                                    selectedType = t
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Payment Method
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("Forma de Pagamento (PIX, TED, Boleto)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank() && amount > 0) {
                        val statementCat = when (selectedType) {
                            TransactionType.RECEITA -> StatementCategory.RECEITA_BRUTA
                            TransactionType.CUSTO_MERCADORIA -> StatementCategory.CPV_CMV
                            TransactionType.DESPESA_OPERACIONAL -> StatementCategory.DESPESAS_ADMINISTRATIVAS
                            TransactionType.DESPESA_ADMINISTRATIVA -> StatementCategory.DESPESAS_ADMINISTRATIVAS
                            TransactionType.DESPESA_VENDAS -> StatementCategory.DESPESAS_COMERCIAIS
                            TransactionType.INVESTIMENTO_CAPEX -> StatementCategory.ATIVO_IMOBILIZADO
                            TransactionType.FINANCIAMENTO -> StatementCategory.DIVIDAS_BANCARIAS
                            TransactionType.IMPOSTO_TRIBUTO -> StatementCategory.DEDUCOES_IMPOSTOS
                            TransactionType.TRANSFERENCIA -> StatementCategory.DESPESAS_ADMINISTRATIVAS
                        }

                        val dfcAct = when (selectedType) {
                            TransactionType.INVESTIMENTO_CAPEX -> DfcActivity.INVESTIMENTO
                            TransactionType.FINANCIAMENTO -> DfcActivity.FINANCIAMENTO
                            else -> DfcActivity.OPERACIONAL
                        }

                        onConfirm(
                            description,
                            amount,
                            isExpense,
                            selectedType,
                            category,
                            statementCat,
                            dfcAct,
                            selectedAccount.id,
                            paymentMethod,
                            counterparty,
                            docNumber
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccentBlue),
                modifier = Modifier.testTag("button_confirm_add_tx")
            ) {
                Text("Confirmar Lançamento")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
