package me.y9san9.lifetime.statistics.viewModel

import kotlinx.coroutines.flow.StateFlow
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.stdlib.mapState
import me.y9san9.lifetime.core.type.*
import me.y9san9.lifetime.statistics.StatsHandler
import me.y9san9.lifetime.statistics.type.AppStats

class StatisticsViewModel(
    private val storage: Storage,
    time: StateFlow<StashedTime>,
    clock: Clock
) {
    private val handler: StatsHandler

    val stats: StateFlow<AppStats> get() = handler.stats

    init {
        val initial = storage.load() ?: AppStats.initial(time.value)
        handler = StatsHandler(initial, time)
    }

    val maxStashedTime: StateFlow<String> = stats.mapState { stats ->
        TimeFormatter.format(stats.maxStashed.millis)
    }
    val maxStashedDate: StateFlow<String> = stats.mapState { stats ->
        stats.maxStashed.date.formatWithYear(clock.currentDate())
    }
    val installedDate: StateFlow<String> = stats.mapState { stats ->
        val currentDate = clock.currentDate()

        buildString {
            val formatted = stats.installedDate.formatWithYear(currentDate)
            append(formatted)
            val diff = currentDate.epochDay - stats.installedDate.epochDay
            append(" ($diff days ago)")
        }
    }

    fun resume() {}

    fun pause() {
        storage.save(handler.stats.value)
    }

    interface Storage {
        fun save(stats: AppStats)
        fun load(): AppStats?
    }
}

private fun Date.formatWithYear(currentDate: Date): String {
    return if (currentDate.year == year) {
        format("d MMMM")
    } else {
        format("d MMMM, yyyy")
    }
}
