package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.entry.FloatEntry

fun List<FloatEntry>.logarithmic(): List<FloatEntry> {
    if (this.isEmpty()) return this

    val latest = last().y
    val maxLog = chartMaxLog(latest, maxOf { it.y })

    return map { entry ->
        entry.copy(
            y = chartTransform(maxLog, latest, entry.y)
        )
    }
}
