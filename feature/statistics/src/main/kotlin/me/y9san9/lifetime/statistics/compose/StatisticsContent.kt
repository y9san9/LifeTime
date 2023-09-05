package me.y9san9.lifetime.statistics.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.type.format
import me.y9san9.lifetime.core.type.localDate
import me.y9san9.lifetime.feature.statistics.R
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.type.dates
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsContent(
    stats: AppStats,
    maxStashedTime: String,
    maxStashedDate: String,
    installDate: String
) {
    BoxWithConstraints {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(
                modifier = Modifier.height(this@BoxWithConstraints.maxHeight * 0.05f)
            )

            Text(
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.onBackground,
            )

            Spacer(modifier = Modifier.height(20.dp))

            val values = stats.lastData.list.asReversed()
            val dates = stats.lastData.dates.asReversed()

            val chartEntryModel = dates.withIndex().zip(values) { x, y ->
                entryOf(
                    x = x.index.toFloat(),
                    y = y.toFloat()
                )
            }.let { entryModelOf(it) }

            val yFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
                TimeFormatter.format(value.toLong())
            }

            val xFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                dates[value.toInt()].format("d MMMM")
            }

            Chart(
                chart = lineChart(
                    listOf(
                        lineSpec(MaterialTheme.colors.primary)
                    ),
                    spacing = 100.dp
                ),
                model = chartEntryModel,
                startAxis = rememberStartAxis(
                    valueFormatter = yFormatter,
                    itemPlacer = AxisItemPlacer.Vertical.default(
                        maxItemCount = 7
                    )
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = xFormatter,
                ),
                chartScrollSpec = rememberChartScrollSpec(initialScroll = InitialScroll.End),
                isZoomEnabled = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.max_time))
                    }
                    append(maxStashedTime)
                    append(" ($maxStashedDate)")
                },
                color = MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.install_date))
                    }
                    append(installDate)
                },
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}
