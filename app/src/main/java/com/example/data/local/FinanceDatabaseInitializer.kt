package com.example.data.local

import com.example.data.local.model.BankAccount
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.CashTransaction
import com.example.data.local.model.DfcActivity
import com.example.data.local.model.ReconciliationStatus
import com.example.data.local.model.StatementCategory
import com.example.data.local.model.TransactionStatus
import com.example.data.local.model.TransactionType
import java.util.Calendar

object FinanceDatabaseInitializer {

    fun getInitialAccounts(): List<BankAccount> {
        val now = System.currentTimeMillis()
        return listOf(
            BankAccount(
                id = 1,
                bankName = "Itaú Empresas PJ",
                bankCode = "341",
                agency = "0842",
                accountNumber = "18492-3",
                currentBalance = 342850.00,
                accountType = "Conta Movimento Principal",
                apiConnected = true,
                lastSyncTime = now,
                colorHex = "#EA580C" // Itaú Orange
            ),
            BankAccount(
                id = 2,
                bankName = "Bradesco Corporate",
                bankCode = "237",
                agency = "2390",
                accountNumber = "45210-9",
                currentBalance = 189420.00,
                accountType = "Conta Pagamentos / Folha",
                apiConnected = true,
                lastSyncTime = now,
                colorHex = "#DC2626" // Bradesco Red
            ),
            BankAccount(
                id = 3,
                bankName = "BTG Pactual Tesouraria",
                bankCode = "208",
                agency = "0001",
                accountNumber = "99312-1",
                currentBalance = 750000.00,
                accountType = "CDB Liquidez Diária (104% CDI)",
                apiConnected = true,
                lastSyncTime = now,
                colorHex = "#1E293B" // BTG Dark Slate
            ),
            BankAccount(
                id = 4,
                bankName = "Banco do Brasil PJ",
                bankCode = "001",
                agency = "1250",
                accountNumber = "33902-8",
                currentBalance = 94130.00,
                accountType = "Conta Operacional Tributos",
                apiConnected = true,
                lastSyncTime = now,
                colorHex = "#FBBF24" // BB Yellow
            )
        )
    }

    fun getInitialTransactions(): List<CashTransaction> {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis

        // Helper date offsets (days)
        fun daysAgo(days: Int): Long {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -days)
            return c.timeInMillis
        }

        fun daysAhead(days: Int): Long {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, days)
            return c.timeInMillis
        }

        return listOf(
            // --- RECEITAS BRUTAS (DRE / DFC OPERACIONAL) ---
            CashTransaction(
                id = 1,
                description = "Faturamento Contrato Enterprise - Votorantim Corp",
                amount = 145000.00,
                isExpense = false,
                date = daysAgo(2),
                dueDate = daysAgo(2),
                type = TransactionType.RECEITA,
                status = TransactionStatus.REALIZADO,
                category = "Receita de Serviços B2B",
                statementCategory = StatementCategory.RECEITA_BRUTA,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "PIX",
                documentNumber = "NF-e 8941",
                counterpartyName = "Votorantim Participações S/A",
                counterpartyDocument = "01.007.456/0001-90"
            ),
            CashTransaction(
                id = 2,
                description = "Mensalidade Assinatura SaaS - Grupo Ambev",
                amount = 88500.00,
                isExpense = false,
                date = daysAgo(5),
                dueDate = daysAgo(5),
                type = TransactionType.RECEITA,
                status = TransactionStatus.REALIZADO,
                category = "Licenciamento de Software",
                statementCategory = StatementCategory.RECEITA_BRUTA,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "TED",
                documentNumber = "NF-e 8935",
                counterpartyName = "Ambev Brasil Bebidas S.A.",
                counterpartyDocument = "07.526.557/0001-00"
            ),
            CashTransaction(
                id = 3,
                description = "Consultoria de Implantação e Treinamento - Magalu",
                amount = 62000.00,
                isExpense = false,
                date = daysAgo(8),
                dueDate = daysAgo(8),
                type = TransactionType.RECEITA,
                status = TransactionStatus.REALIZADO,
                category = "Consultoria Técnica",
                statementCategory = StatementCategory.RECEITA_BRUTA,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 2,
                isReconciled = false,
                paymentMethod = "Boleto",
                documentNumber = "NF-e 8912",
                counterpartyName = "Magazine Luiza S.A.",
                counterpartyDocument = "47.960.950/0001-21"
            ),
            CashTransaction(
                id = 4,
                description = "Contrato de Suporte Anual - Natura Cosméticos",
                amount = 115000.00,
                isExpense = false,
                date = daysAhead(4),
                dueDate = daysAhead(4),
                type = TransactionType.RECEITA,
                status = TransactionStatus.PENDENTE,
                category = "Receita Recorrente Anual",
                statementCategory = StatementCategory.RECEITA_BRUTA,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = false,
                paymentMethod = "PIX",
                documentNumber = "NF-e 8960",
                counterpartyName = "Natura Cosméticos S/A",
                counterpartyDocument = "71.673.990/0001-77"
            ),

            // --- DEDUÇÕES E TRIBUTOS SOBRE VENDAS ---
            CashTransaction(
                id = 5,
                description = "Guia DAS / Simples Nacional e ISS s/ Faturamento",
                amount = 26500.00,
                isExpense = true,
                date = daysAgo(10),
                dueDate = daysAgo(10),
                type = TransactionType.IMPOSTO_TRIBUTO,
                status = TransactionStatus.REALIZADO,
                category = "Impostos sobre Faturamento",
                statementCategory = StatementCategory.DEDUCOES_IMPOSTOS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 4,
                isReconciled = true,
                paymentMethod = "Guia Débito Conta",
                documentNumber = "DARF-98214",
                counterpartyName = "Receita Federal do Brasil",
                counterpartyDocument = "00.394.460/0058-87"
            ),

            // --- CUSTOS DAS VENDAS (CMV / CPV) ---
            CashTransaction(
                id = 6,
                description = "Infraestrutura Cloud & Servidores AWS - Amazon Web Services",
                amount = 34800.00,
                isExpense = true,
                date = daysAgo(4),
                dueDate = daysAgo(4),
                type = TransactionType.CUSTO_MERCADORIA,
                status = TransactionStatus.REALIZADO,
                category = "Cloud & Hospedagem Dedicada",
                statementCategory = StatementCategory.CPV_CMV,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "Cartão Corporativo",
                documentNumber = "INV-AWS-8831",
                counterpartyName = "Amazon Web Services Brasil Ltda",
                counterpartyDocument = "23.412.247/0001-10"
            ),
            CashTransaction(
                id = 7,
                description = "Fornecedor de Hardware & Sensores IoT - Intelbras",
                amount = 28400.00,
                isExpense = true,
                date = daysAgo(7),
                dueDate = daysAgo(7),
                type = TransactionType.CUSTO_MERCADORIA,
                status = TransactionStatus.REALIZADO,
                category = "Insumos e Componentes",
                statementCategory = StatementCategory.CPV_CMV,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 2,
                isReconciled = true,
                paymentMethod = "Boleto",
                documentNumber = "NF-e 44321",
                counterpartyName = "Intelbras S/A Indústria",
                counterpartyDocument = "82.901.000/0001-27"
            ),

            // --- DESPESAS COM VENDAS / COMERCIAIS ---
            CashTransaction(
                id = 8,
                description = "Comissão Equipe Comercial - Fechamento Mês",
                amount = 18200.00,
                isExpense = true,
                date = daysAgo(6),
                dueDate = daysAgo(6),
                type = TransactionType.DESPESA_VENDAS,
                status = TransactionStatus.REALIZADO,
                category = "Comissões de Vendas",
                statementCategory = StatementCategory.DESPESAS_COMERCIAIS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 2,
                isReconciled = true,
                paymentMethod = "PIX",
                documentNumber = "COM-0926",
                counterpartyName = "Representantes Comerciais Associados",
                counterpartyDocument = "14.281.993/0001-44"
            ),
            CashTransaction(
                id = 9,
                description = "Marketing Digital & Performance Google / LinkedIn Ads",
                amount = 14500.00,
                isExpense = true,
                date = daysAgo(3),
                dueDate = daysAgo(3),
                type = TransactionType.DESPESA_VENDAS,
                status = TransactionStatus.REALIZADO,
                category = "Marketing e Aquisição (CAC)",
                statementCategory = StatementCategory.DESPESAS_COMERCIAIS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = false,
                paymentMethod = "Boleto",
                documentNumber = "FAT-GOOGLE-9912",
                counterpartyName = "Google Brasil Internet Ltda",
                counterpartyDocument = "06.990.590/0001-23"
            ),

            // --- DESPESAS ADMINISTRATIVAS & GERAIS ---
            CashTransaction(
                id = 10,
                description = "Folha de Pagamento Salarial e Encargos CLT",
                amount = 76000.00,
                isExpense = true,
                date = daysAgo(5),
                dueDate = daysAgo(5),
                type = TransactionType.DESPESA_ADMINISTRATIVA,
                status = TransactionStatus.REALIZADO,
                category = "Folha de Pagamento e Encargos",
                statementCategory = StatementCategory.DESPESAS_ADMINISTRATIVAS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 2,
                isReconciled = true,
                paymentMethod = "Folha Bradesco",
                documentNumber = "FOLHA-0926",
                counterpartyName = "Colaboradores FinControl S/A",
                counterpartyDocument = "33.109.845/0001-30"
            ),
            CashTransaction(
                id = 11,
                description = "Locação Comercial Escritório Sede - Edifício Faria Lima Tower",
                amount = 16800.00,
                isExpense = true,
                date = daysAgo(12),
                dueDate = daysAgo(12),
                type = TransactionType.DESPESA_ADMINISTRATIVA,
                status = TransactionStatus.REALIZADO,
                category = "Aluguel & Condomínio",
                statementCategory = StatementCategory.DESPESAS_ADMINISTRATIVAS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 2,
                isReconciled = true,
                paymentMethod = "Boleto",
                documentNumber = "REC-LOC-882",
                counterpartyName = "Faria Lima Prime Real Estate FII",
                counterpartyDocument = "19.382.711/0001-92"
            ),
            CashTransaction(
                id = 12,
                description = "Honorários de Auditoria e Controladoria Externa - PwC",
                amount = 9500.00,
                isExpense = true,
                date = daysAhead(7),
                dueDate = daysAhead(7),
                type = TransactionType.DESPESA_ADMINISTRATIVA,
                status = TransactionStatus.PENDENTE,
                category = "Serviços Contábeis & Auditoria",
                statementCategory = StatementCategory.DESPESAS_ADMINISTRATIVAS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = false,
                paymentMethod = "Boleto",
                documentNumber = "NF-e 38102",
                counterpartyName = "PricewaterhouseCoopers Auditores",
                counterpartyDocument = "61.565.107/0001-58"
            ),

            // --- DEPRECIAÇÃO & AMORTIZAÇÃO ---
            CashTransaction(
                id = 13,
                description = "Provisão de Depreciação - Maquinário e TI (Não Afeta Caixa)",
                amount = 6200.00,
                isExpense = true,
                date = daysAgo(1),
                dueDate = daysAgo(1),
                type = TransactionType.DESPESA_OPERACIONAL,
                status = TransactionStatus.REALIZADO,
                category = "Depreciação de Ativos",
                statementCategory = StatementCategory.DEPRECIACAO_AMORTIZACAO,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "Lançamento Contábil",
                documentNumber = "LCTO-DEP-0926",
                counterpartyName = "Departamento de Patrimônio",
                counterpartyDocument = "-"
            ),

            // --- RESULTADO FINANCEIRO (RECEITAS & DESPESAS) ---
            CashTransaction(
                id = 14,
                description = "Rendimento de Aplicação Financeira CDB Liquidez Diária BTG",
                amount = 7650.00,
                isExpense = false,
                date = daysAgo(1),
                dueDate = daysAgo(1),
                type = TransactionType.RECEITA,
                status = TransactionStatus.REALIZADO,
                category = "Receitas Financeiras / CDI",
                statementCategory = StatementCategory.RECEITAS_FINANCEIRAS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 3,
                isReconciled = true,
                paymentMethod = "Crédito em Conta",
                documentNumber = "EXT-BTG-REND-09",
                counterpartyName = "Banco BTG Pactual S.A.",
                counterpartyDocument = "30.306.294/0001-45"
            ),
            CashTransaction(
                id = 15,
                description = "Juros s/ Financiamento Bancário Capital de Giro Itaú",
                amount = 3850.00,
                isExpense = true,
                date = daysAgo(9),
                dueDate = daysAgo(9),
                type = TransactionType.DESPESA_OPERACIONAL,
                status = TransactionStatus.REALIZADO,
                category = "Despesas Financeiras e Juros",
                statementCategory = StatementCategory.DESPESAS_FINANCEIRAS,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "Débito Automático",
                documentNumber = "DEB-JUR-3392",
                counterpartyName = "Itaú Unibanco S.A.",
                counterpartyDocument = "60.701.190/0001-04"
            ),

            // --- TRIBUTOS S/ LUCRO (IRPJ / CSLL) ---
            CashTransaction(
                id = 16,
                description = "Provisão IRPJ e CSLL - Apuração Trimestral",
                amount = 14800.00,
                isExpense = true,
                date = daysAhead(15),
                dueDate = daysAhead(15),
                type = TransactionType.IMPOSTO_TRIBUTO,
                status = TransactionStatus.PENDENTE,
                category = "Impostos sobre Lucro",
                statementCategory = StatementCategory.IMPOSTOS_LUCRO,
                dfcActivity = DfcActivity.OPERACIONAL,
                bankAccountId = 4,
                isReconciled = false,
                paymentMethod = "DARF",
                documentNumber = "DARF-LUCRO-09",
                counterpartyName = "Secretaria da Receita Federal",
                counterpartyDocument = "00.394.460/0058-87"
            ),

            // --- FLUXO DE INVESTIMENTO (CAPEX) ---
            CashTransaction(
                id = 17,
                description = "Aquisição de 6 Servidores Dell PowerEdge (CAPEX)",
                amount = 45000.00,
                isExpense = true,
                date = daysAgo(11),
                dueDate = daysAgo(11),
                type = TransactionType.INVESTIMENTO_CAPEX,
                status = TransactionStatus.REALIZADO,
                category = "Investimento em Ativo Imobilizado",
                statementCategory = StatementCategory.ATIVO_IMOBILIZADO,
                dfcActivity = DfcActivity.INVESTIMENTO,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "TED",
                documentNumber = "NF-e 119842",
                counterpartyName = "Dell Computadores do Brasil Ltda",
                counterpartyDocument = "72.381.189/0001-10"
            ),

            // --- FLUXO DE FINANCIAMENTO ---
            CashTransaction(
                id = 18,
                description = "Amortização Parcela Financiamento BNDES Inovação",
                amount = 22000.00,
                isExpense = true,
                date = daysAgo(14),
                dueDate = daysAgo(14),
                type = TransactionType.FINANCIAMENTO,
                status = TransactionStatus.REALIZADO,
                category = "Amortização de Empréstimos",
                statementCategory = StatementCategory.DIVIDAS_BANCARIAS,
                dfcActivity = DfcActivity.FINANCIAMENTO,
                bankAccountId = 1,
                isReconciled = true,
                paymentMethod = "Débito Automático",
                documentNumber = "BNDES-PARC-24",
                counterpartyName = "BNDES / Itaú Agente",
                counterpartyDocument = "33.657.248/0001-89"
            ),
            CashTransaction(
                id = 19,
                description = "Distribuição de Lucros e Dividendos aos Sócios",
                amount = 35000.00,
                isExpense = true,
                date = daysAgo(16),
                dueDate = daysAgo(16),
                type = TransactionType.FINANCIAMENTO,
                status = TransactionStatus.REALIZADO,
                category = "Dividendos Pagos",
                statementCategory = StatementCategory.CAPITAL_SOCIAL,
                dfcActivity = DfcActivity.FINANCIAMENTO,
                bankAccountId = 2,
                isReconciled = true,
                paymentMethod = "PIX",
                documentNumber = "DIV-0926",
                counterpartyName = "Acionistas Controladores",
                counterpartyDocument = "000.000.000-00"
            )
        )
    }

    fun getInitialBankFeed(): List<BankStatementItem> {
        val cal = Calendar.getInstance()
        fun daysAgo(days: Int): Long {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -days)
            return c.timeInMillis
        }

        return listOf(
            BankStatementItem(
                id = 1,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260928-001",
                date = daysAgo(2),
                amount = 145000.00,
                description = "PIX RECEBIDO VOTORANTIM PARTICIPACOES",
                counterparty = "Votorantim Participações S/A",
                isReconciled = true,
                matchedCashTransactionId = 1,
                matchConfidence = 1.0f,
                reconciliationStatus = ReconciliationStatus.CONCILIADO_AUTOMATICO
            ),
            BankStatementItem(
                id = 2,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260925-002",
                date = daysAgo(5),
                amount = 88500.00,
                description = "TED RECEBIDA AMBEV S/A FL 001",
                counterparty = "Ambev Brasil Bebidas S.A.",
                isReconciled = true,
                matchedCashTransactionId = 2,
                matchConfidence = 0.98f,
                reconciliationStatus = ReconciliationStatus.CONCILIADO_AUTOMATICO
            ),
            BankStatementItem(
                id = 3,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260926-003",
                date = daysAgo(4),
                amount = -34800.00,
                description = "COMPRA CARTAO CORP AWS CLOUD SP",
                counterparty = "Amazon Web Services Brasil Ltda",
                isReconciled = true,
                matchedCashTransactionId = 6,
                matchConfidence = 1.0f,
                reconciliationStatus = ReconciliationStatus.CONCILIADO_AUTOMATICO
            ),
            BankStatementItem(
                id = 4,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260927-004",
                date = daysAgo(3),
                amount = -14500.00,
                description = "PAGAMENTO TITULO GOOGLE ADS BRASIL",
                counterparty = "Google Brasil Internet Ltda",
                isReconciled = false,
                matchedCashTransactionId = 9,
                matchConfidence = 0.95f,
                reconciliationStatus = ReconciliationStatus.NAO_CONCILIADO // Ready for 1-click reconciliation!
            ),
            BankStatementItem(
                id = 5,
                bankAccountId = 2,
                transactionId = "FITID-BRAD-20260924-005",
                date = daysAgo(6),
                amount = -18200.00,
                description = "PIX ENVIADO COMISSOES VENDAS",
                counterparty = "Representantes Comerciais Associados",
                isReconciled = true,
                matchedCashTransactionId = 8,
                matchConfidence = 1.0f,
                reconciliationStatus = ReconciliationStatus.CONCILIADO_AUTOMATICO
            ),
            BankStatementItem(
                id = 6,
                bankAccountId = 2,
                transactionId = "FITID-BRAD-20260922-006",
                date = daysAgo(8),
                amount = 62000.00,
                description = "CREDITO COBRANCA LIQ TITULO MAGALU",
                counterparty = "Magazine Luiza S.A.",
                isReconciled = false,
                matchedCashTransactionId = 3,
                matchConfidence = 0.96f,
                reconciliationStatus = ReconciliationStatus.NAO_CONCILIADO // Ready for 1-click reconciliation!
            ),
            BankStatementItem(
                id = 7,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260929-007",
                date = daysAgo(1),
                amount = 4500.00,
                description = "PIX RECEBIDO CLIENTE SPOT - TECHLAB",
                counterparty = "Techlab Soluções Digitais",
                isReconciled = false,
                matchedCashTransactionId = null,
                matchConfidence = 0.0f,
                reconciliationStatus = ReconciliationStatus.NAO_CONCILIADO // Unmatched bank credit!
            ),
            BankStatementItem(
                id = 8,
                bankAccountId = 1,
                transactionId = "FITID-ITAU-20260929-008",
                date = daysAgo(1),
                amount = -1250.00,
                description = "TARIFA PACOTE SERV BANCARIOS PJ ITAU",
                counterparty = "Itaú Unibanco S.A.",
                isReconciled = false,
                matchedCashTransactionId = null,
                matchConfidence = 0.0f,
                reconciliationStatus = ReconciliationStatus.NAO_CONCILIADO // Bank fee needing quick creation
            )
        )
    }
}
