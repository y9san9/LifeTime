package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

fun logLabelFormatter(
    format: (x: Float, y: Float) -> String
) = MarkerLabelFormatter { markedEntries, _ ->
    val x = markedEntries.first().entry.x
    val y = markedEntries.first().entry.y
    format(x, chartInverse(y))
}
