package me.y9san9.lifetime.statistics.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.stdlib.mapState
import me.y9san9.lifetime.core.type.*
import me.y9san9.lifetime.statistics.StatsHandler
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.type.date
import java.time.ZoneId
import me.y9san9.lifetime.core.type.StashGain
import kotlinx.coroutines.CoroutineScope

class StatisticsViewModel(
    handler: StatsHandler,
    clock: Clock,
    gain: StashGain,
    private val setGain: (StashGain) -> Unit,
) {
    val stats: StateFlow<AppStats> = handler.stats

    val maxStashedTime: StateFlow<String> = stats.mapState { stats ->
        TimeFormatter.format(stats.maxStashed.millis)
    }
    val maxStashedDate: StateFlow<String> = stats.mapState { stats ->
        stats.maxStashed.date.formatWithYear(clock.currentDate(ZoneId.systemDefault()))
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

    private val _hoursPerDay = MutableStateFlow(gain.toHoursPerDayString())
    val hoursPerDay: StateFlow<String> = _hoursPerDay

    private val _showHoursPerDayError = MutableStateFlow(false)
    val showHoursPerDayError: StateFlow<Boolean> = _showHoursPerDayError

    fun setHoursPerDay(value: String) {
        _hoursPerDay.value = value
        val double = value.toDoubleOrNull()
        if (double == null || double <= 0 || double > 24) {
            _showHoursPerDayError.value = true
            return
        }
        _showHoursPerDayError.value = false
        val gain = StashGain.ofHoursPerDay(double)
        setGain(gain)
    }

    private fun StashGain.toHoursPerDayString(): String {
        return "%.1f".format(toHoursPerDay())
    }
}

private fun Date.formatWithYear(currentDate: Date): String {
    return if (currentDate.year == year) {
        format("d MMMM")
    } else {
        format("d MMMM, yyyy")
    }
}
