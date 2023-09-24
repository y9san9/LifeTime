package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter

fun logLabelFormatter(
    maxLog: Float,
    format: (x: Float, y: Float) -> String
) = MarkerLabelFormatter { markedEntries, _ ->
    val x = markedEntries.first().entry.x
    val y = markedEntries.first().entry.y
    println("LOGLABEL: $x $y $maxLog")
    format(x, chartInverse(maxLog, y).also { println("INVERTED $it") })
}
