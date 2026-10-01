package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.engine.BalanceSheetResult
import com.example.domain.engine.DfcResult
import com.example.domain.engine.DreResult
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryAccentBlue

@Composable
fun DreTableView(
    dre: DreResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Demonstração do Resultado (DRE)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Regime de Competência • Padrão CPC / IFRS",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Margem Líquida ${formatPercent(dre.margemLiquidaPct)}",
                        color = AccentEmerald,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StatementLineItem(label = "Receita Operacional Bruta", amount = dre.receitaBruta, isPositive = true)
            StatementLineItem(label = "(-) Deduções e Impostos sobre Vendas", amount = -dre.deducoesImpostos, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(label = "(=) RECEITA OPERACIONAL LÍQUIDA", amount = dre.receitaLiquida)

            Spacer(modifier = Modifier.height(8.dp))
            StatementLineItem(label = "(-) Custos dos Produtos/Serviços (CPV/CMV)", amount = -dre.cpvCmv, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(
                label = "(=) LUCRO BRUTO",
                amount = dre.lucroBruto,
                highlight = true,
                badge = formatPercent(dre.margemBrutaPct)
            )

            Spacer(modifier = Modifier.height(8.dp))
            StatementLineItem(label = "(-) Despesas Comerciais e Vendas", amount = -dre.despesasVendas, isIndent = true)
            StatementLineItem(label = "(-) Despesas Administrativas e Gerais", amount = -dre.despesasAdministrativas, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(
                label = "(=) EBITDA / LAJIDA",
                amount = dre.ebitda,
                highlight = true,
                badge = formatPercent(dre.margemEbitdaPct)
            )

            Spacer(modifier = Modifier.height(8.dp))
            StatementLineItem(label = "(-) Depreciação e Amortização", amount = -dre.depreciacaoAmortizacao, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(label = "(=) EBIT / LAJIR (Resultado Operacional)", amount = dre.ebit)

            Spacer(modifier = Modifier.height(8.dp))
            StatementLineItem(label = "(+) Receitas Financeiras", amount = dre.receitasFinanceiras, isPositive = true, isIndent = true)
            StatementLineItem(label = "(-) Despesas Financeiras e Juros", amount = -dre.despesasFinanceiras, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(label = "(=) LAIR (Lucro Antes do IR)", amount = dre.lair)

            Spacer(modifier = Modifier.height(8.dp))
            StatementLineItem(label = "(-) Provisão IRPJ e CSLL", amount = -dre.impostosLucro, isIndent = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 2.dp)

            // Final Profit line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (dre.lucroLiquido >= 0) AccentEmerald.copy(alpha = 0.12f)
                        else AccentRose.copy(alpha = 0.12f)
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "(=) LUCRO LÍQUIDO DO EXERCÍCIO",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Disponível para reinvestimento ou distribuição",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = formatCurrency(dre.lucroLiquido),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (dre.lucroLiquido >= 0) AccentEmerald else AccentRose
                )
            }
        }
    }
}

@Composable
fun BalanceSheetTableView(
    bp: BalanceSheetResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Balanço Patrimonial (BP)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Posição Patrimonial Consolidada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentEmerald.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Equilíbrio",
                            tint = AccentEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Balanço Equilibrado",
                            color = AccentEmerald,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ATIVO
            Text(
                text = "ATIVO TOTAL",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryAccentBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            StatementSubtotalItem(label = "1. ATIVO CIRCULANTE (Curto Prazo)", amount = bp.totalAtivoCirculante)
            StatementLineItem(label = "• Caixa e Bancos (Disponibilidades)", amount = bp.caixaBancos, isPositive = true, isIndent = true)
            StatementLineItem(label = "• Clientes / Contas a Receber", amount = bp.contasReceber, isPositive = true, isIndent = true)
            StatementLineItem(label = "• Estoques de Mercadorias/Insumos", amount = bp.estoques, isPositive = true, isIndent = true)
            StatementLineItem(label = "• Outros Créditos e Tributos a Compensar", amount = bp.outrosCreditos, isPositive = true, isIndent = true)

            Spacer(modifier = Modifier.height(10.dp))
            StatementSubtotalItem(label = "2. ATIVO NÃO CIRCULANTE (Longo Prazo)", amount = bp.totalAtivoNaoCirculante)
            StatementLineItem(label = "• Ativo Imobilizado Bruto (Maquinário/TI)", amount = bp.ativoImobilizado, isPositive = true, isIndent = true)
            StatementLineItem(label = "• (-) Depreciação Acumulada", amount = -bp.depreciacaoAcumulada, isIndent = true)
            StatementLineItem(label = "• Ativos Intangíveis (Software/Marcas)", amount = bp.intangivel, isPositive = true, isIndent = true)

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), thickness = 2.dp)
            StatementSubtotalItem(label = "TOTAL DO ATIVO", amount = bp.totalAtivo, highlight = true)

            Spacer(modifier = Modifier.height(18.dp))

            // PASSIVO E PL
            Text(
                text = "PASSIVO E PATRIMÔNIO LÍQUIDO",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = AccentAmber
            )
            Spacer(modifier = Modifier.height(6.dp))
            StatementSubtotalItem(label = "1. PASSIVO CIRCULANTE (Curto Prazo)", amount = bp.totalPassivoCirculante)
            StatementLineItem(label = "• Fornecedores e Contas a Pagar", amount = bp.fornecedoresContasPagar, isIndent = true)
            StatementLineItem(label = "• Empréstimos Bancários C/P", amount = bp.emprestimosCurtoPrazo, isIndent = true)
            StatementLineItem(label = "• Obrigações Fiscais e Trabalhistas", amount = bp.obrigacoesFiscaisTrabalhistas, isIndent = true)

            Spacer(modifier = Modifier.height(10.dp))
            StatementSubtotalItem(label = "2. PASSIVO NÃO CIRCULANTE (Longo Prazo)", amount = bp.totalPassivoNaoCirculante)
            StatementLineItem(label = "• Financiamentos Longo Prazo (BNDES)", amount = bp.financiamentosLongoPrazo, isIndent = true)
            StatementLineItem(label = "• Provisões para Contingências", amount = bp.provisoesContingencia, isIndent = true)

            Spacer(modifier = Modifier.height(10.dp))
            StatementSubtotalItem(label = "3. PATRIMÔNIO LÍQUIDO (PL)", amount = bp.totalPatrimonioLiquido)
            StatementLineItem(label = "• Capital Social Subscrito", amount = bp.capitalSocial, isPositive = true, isIndent = true)
            StatementLineItem(label = "• Reservas de Lucro / Capital", amount = bp.reservasLucros, isPositive = true, isIndent = true)
            StatementLineItem(label = "• Lucro Líquido Acumulado do Período", amount = bp.lucroLiquidoPeriodo, isPositive = true, isIndent = true)

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), thickness = 2.dp)
            StatementSubtotalItem(label = "TOTAL DO PASSIVO + PATRIMÔNIO LÍQUIDO", amount = bp.totalPassivoEPl, highlight = true)
        }
    }
}

@Composable
fun DfcTableView(
    dfc: DfcResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Demonstração dos Fluxos de Caixa (DFC)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Método Direto Integrado com a Tesouraria em Tempo Real",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. OPERACIONAL
            StatementSubtotalItem(
                label = "1. FLUXO DE CAIXA OPERACIONAL",
                amount = dfc.fluxoOperacionalLiquido,
                highlight = true
            )
            StatementLineItem(label = "(+) Recebimentos de Clientes e Serviços", amount = dfc.recebimentosClientes, isPositive = true, isIndent = true)
            StatementLineItem(label = "(-) Pagamentos a Fornecedores de Insumos", amount = -dfc.pagamentosFornecedores, isIndent = true)
            StatementLineItem(label = "(-) Pagamentos de Folha e Despesas Gerais", amount = -dfc.pagamentosFolhaDespesas, isIndent = true)
            StatementLineItem(label = "(-) Pagamentos de Tributos e Guias Fiscais", amount = -dfc.pagamentosTributos, isIndent = true)

            Spacer(modifier = Modifier.height(14.dp))

            // 2. INVESTIMENTO
            StatementSubtotalItem(
                label = "2. FLUXO DE CAIXA DE INVESTIMENTO",
                amount = dfc.fluxoInvestimentoLiquido,
                highlight = true
            )
            StatementLineItem(label = "(-) Aquisições de Ativo Imobilizado (CAPEX)", amount = -dfc.capexAquisicoes, isIndent = true)
            StatementLineItem(label = "(+) Resgates / Vendas de Investimentos", amount = dfc.resgatesInvestimentos, isPositive = true, isIndent = true)

            Spacer(modifier = Modifier.height(14.dp))

            // 3. FINANCIAMENTO
            StatementSubtotalItem(
                label = "3. FLUXO DE CAIXA DE FINANCIAMENTO",
                amount = dfc.fluxoFinanciamentoLiquido,
                highlight = true
            )
            StatementLineItem(label = "(+) Captação de Novos Financiamentos", amount = dfc.captacaoFinanciamentos, isPositive = true, isIndent = true)
            StatementLineItem(label = "(-) Amortização de Financiamentos Bancários", amount = -dfc.amortizacaoEmprestimos, isIndent = true)
            StatementLineItem(label = "(-) Distribuição de Dividendos e Sócios", amount = -dfc.distribuicaoDividendos, isIndent = true)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp)

            // RESUMO
            StatementSubtotalItem(label = "VARIAÇÃO LÍQUIDA DE CAIXA NO PERÍODO", amount = dfc.variacaoLiquidaCaixa)
            StatementLineItem(label = "(+) Saldo Inicial de Caixa e Equivalentes", amount = dfc.saldoInicialCaixa, isPositive = true)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            StatementSubtotalItem(
                label = "(=) SALDO FINAL DE CAIXA E EQUIVALENTES",
                amount = dfc.saldoFinalCaixa,
                highlight = true
            )
        }
    }
}

@Composable
private fun StatementLineItem(
    label: String,
    amount: Double,
    isPositive: Boolean = false,
    isIndent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isIndent) 12.dp else 0.dp, top = 3.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (amount < 0) AccentRose else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatementSubtotalItem(
    label: String,
    amount: Double,
    highlight: Boolean = false,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
                color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = badge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
        Text(
            text = formatCurrency(amount),
            style = if (highlight) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (amount < 0) AccentRose else MaterialTheme.colorScheme.onSurface
        )
    }
}
