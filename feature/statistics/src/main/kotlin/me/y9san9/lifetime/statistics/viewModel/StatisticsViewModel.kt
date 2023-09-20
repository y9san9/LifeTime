package me.y9san9.lifetime.statistics.viewModel

import kotlinx.coroutines.flow.StateFlow
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.stdlib.mapState
import me.y9san9.lifetime.core.type.*
import me.y9san9.lifetime.statistics.StatsHandler
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.type.date

class StatisticsViewModel(
    handler: StatsHandler,
    clock: Clock
) {
    val stats: StateFlow<AppStats> = handler.stats

    val maxStashedTime: StateFlow<String> = stats.mapState { stats ->
        TimeFormatter.format(stats.maxStashed.millis)
    }
    val maxStashedDate: StateFlow<String> = stats.mapState { stats ->
        stats.maxStashed.date.formatWithYear(clock.currentDate())
    }
    val installedDate: StateFlow<String> = stats.mapState { stats ->
        val currentDate = stats.lastData.date

        buildString {
            val formatted = stats.installedDate.formatWithYear(currentDate)
            append(formatted)
            val diff = currentDate.epochDay - stats.installedDate.epochDay
            append(" ($diff days ago)")
        }
    }
}

private fun Date.formatWithYear(currentDate: Date): String {
    return if (currentDate.year == year) {
        format("d MMMM")
    } else {
        format("d MMMM, yyyy")
    }
}
