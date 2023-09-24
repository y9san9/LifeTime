package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter

inline fun <T : AxisPosition.Vertical> logarithmicAxisFormatter(
    maxLog: Float,
    crossinline format: (value: Float) -> String
) = AxisValueFormatter<T> { value, _ ->
    format(chartInverse(maxLog, value))
}
