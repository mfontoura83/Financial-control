package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRose
import com.example.ui.theme.CashInflowGreen
import com.example.ui.theme.CashOutflowRed
import com.example.ui.theme.PrimaryAccentBlue
import com.example.ui.theme.SecondaryTeal

data class CashFlowBarPoint(
    val label: String,
    val inflow: Double,
    val outflow: Double,
    val net: Double
)

@Composable
fun CashFlowProjectionChart(
    points: List<CashFlowBarPoint>,
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
                Text(
                    text = "Fluxo de Caixa em Tempo Real (Previsto vs Realizado)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartLegendItem(color = CashInflowGreen, label = "Entradas")
                Spacer(modifier = Modifier.width(16.dp))
                ChartLegendItem(color = CashOutflowRed, label = "Saídas")
                Spacer(modifier = Modifier.width(16.dp))
                ChartLegendItem(color = PrimaryAccentBlue, label = "Saldo Líquido")
            }

            Spacer(modifier = Modifier.height(14.dp))

            val maxVal = points.maxOfOrNull { maxOf(it.inflow, it.outflow) }?.coerceAtLeast(1000.0) ?: 1000.0

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bottomPadding = 30f
                val topPadding = 10f
                val chartHeight = canvasHeight - bottomPadding - topPadding

                val n = points.size
                if (n == 0) return@Canvas
                val groupWidth = canvasWidth / n
                val barWidth = (groupWidth * 0.3f).coerceAtMost(28f)
                val spacing = 4f

                // Draw horizontal guide lines
                val steps = 3
                for (i in 0..steps) {
                    val y = topPadding + (chartHeight / steps) * i
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                }

                val linePath = Path()

                points.forEachIndexed { index, point ->
                    val groupCenterX = (index * groupWidth) + (groupWidth / 2)

                    val inflowH = (point.inflow / maxVal).toFloat() * chartHeight
                    val outflowH = (point.outflow / maxVal).toFloat() * chartHeight

                    val inflowX = groupCenterX - barWidth - (spacing / 2)
                    val outflowX = groupCenterX + (spacing / 2)

                    val baselineY = topPadding + chartHeight

                    // Inflow bar
                    if (inflowH > 0) {
                        drawRoundRect(
                            color = CashInflowGreen,
                            topLeft = Offset(inflowX, baselineY - inflowH),
                            size = Size(barWidth, inflowH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // Outflow bar
                    if (outflowH > 0) {
                        drawRoundRect(
                            color = CashOutflowRed,
                            topLeft = Offset(outflowX, baselineY - outflowH),
                            size = Size(barWidth, outflowH),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // Net line calculation
                    val netRatio = ((point.net + maxVal) / (maxVal * 2)).coerceIn(0.0, 1.0).toFloat()
                    val netY = baselineY - (netRatio * chartHeight)

                    if (index == 0) {
                        linePath.moveTo(groupCenterX, netY)
                    } else {
                        linePath.lineTo(groupCenterX, netY)
                    }

                    drawCircle(
                        color = PrimaryAccentBlue,
                        radius = 4f,
                        center = Offset(groupCenterX, netY)
                    )
                }

                drawPath(
                    path = linePath,
                    color = PrimaryAccentBlue,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )
            }

            // Labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                points.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class ExpenseCategoryItem(
    val categoryName: String,
    val amount: Double,
    val color: Color
)

@Composable
fun ExpenseBreakdownSection(
    items: List<ExpenseCategoryItem>,
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
                text = "Estrutura e Composição de Custos / Despesas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            val total = items.sumOf { it.amount }.coerceAtLeast(1.0)

            // Segmented colored bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
            ) {
                items.forEach { item ->
                    val weight = (item.amount / total).toFloat().coerceAtLeast(0.01f)
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .height(14.dp)
                            .background(item.color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown list
            items.forEach { item ->
                val pct = (item.amount / total) * 100
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(item.color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.categoryName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatCurrency(item.amount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatPercent(pct),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidityGaugeBar(
    label: String,
    ratio: Double,
    benchmarkGood: Double = 1.5,
    benchmarkMedium: Double = 1.0,
    modifier: Modifier = Modifier
) {
    val statusColor = when {
        ratio >= benchmarkGood -> AccentEmerald
        ratio >= benchmarkMedium -> AccentAmber
        else -> AccentRose
    }

    val statusText = when {
        ratio >= benchmarkGood -> "Excelente / Saudável"
        ratio >= benchmarkMedium -> "Adequado / Moderado"
        else -> "Atenção / Déficit"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(java.util.Locale.US, "%.2f", ratio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($statusText)",
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress track up to 3.0 scale
        val progress = (ratio / 3.0).coerceIn(0.0, 1.0).toFloat()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor)
            )
        }
    }
}
