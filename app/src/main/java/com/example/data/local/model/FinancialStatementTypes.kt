package com.example.data.local.model

enum class TransactionType(val label: String, val isExpense: Boolean) {
    RECEITA("Receita Operacional", false),
    CUSTO_MERCADORIA("Custos (CPV/CMV)", true),
    DESPESA_OPERACIONAL("Despesa Operacional", true),
    DESPESA_ADMINISTRATIVA("Despesa Administrativa", true),
    DESPESA_VENDAS("Despesa Comercial/Vendas", true),
    INVESTIMENTO_CAPEX("Investimento (CAPEX)", true),
    FINANCIAMENTO("Financiamento / Empréstimo", false),
    IMPOSTO_TRIBUTO("Impostos e Tributos", true),
    TRANSFERENCIA("Transferência Interna", false)
}

enum class TransactionStatus(val label: String) {
    REALIZADO("Realizado / Liquidado"),
    PENDENTE("A Vencer / Pendente"),
    ATRASADO("Em Atraso")
}

enum class StatementCategory(val label: String) {
    RECEITA_BRUTA("Receita Operacional Bruta"),
    DEDUCOES_IMPOSTOS("(-) Deduções e Impostos s/ Vendas"),
    CPV_CMV("(-) Custos das Vendas (CMV/CPV)"),
    DESPESAS_COMERCIAIS("(-) Despesas com Vendas"),
    DESPESAS_ADMINISTRATIVAS("(-) Despesas Administrativas"),
    DEPRECIACAO_AMORTIZACAO("(-) Depreciação e Amortização"),
    RECEITAS_FINANCEIRAS("(+) Receitas Financeiras"),
    DESPESAS_FINANCEIRAS("(-) Despesas Financeiras"),
    IMPOSTOS_LUCRO("(-) IRPJ e CSLL"),
    ATIVO_IMOBILIZADO("Ativo Imobilizado"),
    DIVIDAS_BANCARIAS("Passivo Financeiro"),
    CAPITAL_SOCIAL("Patrimônio Líquido")
}

enum class DfcActivity(val label: String) {
    OPERACIONAL("Atividades Operacionais"),
    INVESTIMENTO("Atividades de Investimento"),
    FINANCIAMENTO("Atividades de Financiamento")
}

enum class ReconciliationStatus(val label: String) {
    NAO_CONCILIADO("Pendente"),
    CONCILIADO_AUTOMATICO("Auto Conciliado"),
    CONCILIADO_MANUAL("Conciliado Manual"),
    DIVERGENCIA("Divergência")
}
