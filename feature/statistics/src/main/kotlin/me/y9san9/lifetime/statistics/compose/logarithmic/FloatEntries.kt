package me.y9san9.lifetime.statistics.compose.logarithmic

import com.patrykandpatrick.vico.core.entry.FloatEntry

fun List<FloatEntry>.logarithmic(): List<FloatEntry> =
    map { entry ->
        println()
        entry.copy(
            y = chartTransform(entry.y)
        )
    }
