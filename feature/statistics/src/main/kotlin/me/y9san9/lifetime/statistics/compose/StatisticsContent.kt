package me.y9san9.lifetime.statistics.compose

import android.view.animation.Interpolator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout.Companion
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.type.Date
import me.y9san9.lifetime.core.type.format
import me.y9san9.lifetime.core.type.localDate
import me.y9san9.lifetime.core.type.toInstant
import me.y9san9.lifetime.feature.statistics.R
import me.y9san9.lifetime.statistics.type.AppStats
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

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
                .fillMaxSize()
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
            val dates = remember(stats) { stats.lastData.dates().asReversed() }

            val chartEntryModel = buildChartModel(dates, values)

            val yFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
                TimeFormatter.format(value.toLong())
            }

            val xFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                dates[value.toInt()].format("d MMMM")
            }

            Chart(
                chart = lineChart(
                    listOf(
                        lineSpec(
                            lineColor = MaterialTheme.colors.primary,
                            pointConnector = DefaultPointConnector(cubicStrength = 0.6f)
                        )
                    ),
                    spacing = 4.dp
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

                    itemPlacer = AxisItemPlacer.Horizontal.default(
                        spacing = 24,
                        offset = getChartOffset(dates)
                    )
                ),
                chartScrollSpec = rememberChartScrollSpec(initialScroll = InitialScroll.End),
                isZoomEnabled = true,
                horizontalLayout = HorizontalLayout.fullWidth(endPadding = 50.dp),
                marker = rememberMarker()
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

private fun getChartOffset(dates: List<Date>): Int {
    return dates.zipWithNext()
        .indexOfFirst { (date, next) -> date != next } + 1
}

private const val MILLIS_PER_HOUR = 3_600_000

private fun AppStats.LastData.dates(): List<Date> = buildList {
    var currentMillis = last.stashSavedAtMillis
    repeat(list.size) {
        add(Date.ofEpochMillis(currentMillis, ZoneId.systemDefault()))
        currentMillis -= MILLIS_PER_HOUR
    }
}

private fun buildChartModel(
    dates: List<Date>,
    values: List<Long>
): ChartEntryModel = dates.withIndex().zip(values) { x, y ->
    entryOf(
        x = x.index.toFloat(),
        y = y.toFloat()
    )
}.let {
    entryModelOf(it)
}
