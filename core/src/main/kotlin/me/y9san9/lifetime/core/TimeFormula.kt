package me.y9san9.lifetime.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.y9san9.lifetime.core.type.StashedTime

object TimeFormula {
    private val _timeChangeDetected = MutableSharedFlow<Unit>()
    val timeChangeDetected: SharedFlow<Unit> = _timeChangeDetected.asSharedFlow()

    /**
     * How many milliseconds being stashed per one millisecond
     * Or how many seconds being stashed per one second
     * Or how many minutes being stashed per one minute
     * And so on
     */
    const val millisPerMillisecond: Double = 0.08333333333

    fun calculate(
        currentTimeMillis: Long,
        time: StashedTime
    ): StashedTime {
        val stashedTime = calculateStashed(currentTimeMillis, time)
        return calculateCountdown(currentTimeMillis, stashedTime)
    }

    fun calculateStashed(
        currentTimeMillis: Long,
        time: StashedTime
    ): StashedTime {
        val delta = (currentTimeMillis - time.stashSavedAtMillis).coerceAtLeast(minimumValue = 0)

        val newMillis = (delta * millisPerMillisecond).toLong() / 1_000 * 1_000

        return time.copy(
            millis = time.millis + newMillis,
            stashSavedAtMillis = time.stashSavedAtMillis + (newMillis / millisPerMillisecond).toLong()
        )
    }

    fun calculateCountdown(
        currentTimeMillis: Long,
        time: StashedTime
    ): StashedTime {
        time.countdownSavedAtMillis ?: return time

        val delta = (currentTimeMillis - time.countdownSavedAtMillis)
            .coerceAtLeast(minimumValue = 0) / 1000 * 1000

        return time.copy(
            millis = (time.millis - delta).coerceAtLeast(minimumValue = 0L),
            countdownSavedAtMillis = time.countdownSavedAtMillis + delta
        )
    }
}
