package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.entry.FloatEntry

fun List<FloatEntry>.logarithmic(): List<FloatEntry> {
    val maxLog = chartMaxLog(maxOf { it.y })
    return map { entry ->
        entry.copy(
            y = chartTransform(maxLog, entry.y)
        )
    }
}
