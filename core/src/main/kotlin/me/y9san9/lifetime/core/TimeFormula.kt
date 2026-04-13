package me.y9san9.lifetime.core

import me.y9san9.lifetime.core.type.StashGain
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.y9san9.lifetime.core.type.StashedTime

object TimeFormula {
    private val _timeChangeDetected = MutableSharedFlow<Unit>()
    val timeChangeDetected: SharedFlow<Unit> = _timeChangeDetected.asSharedFlow()

    fun calculate(
        currentTimeMillis: Long,
        time: StashedTime,
        gain: StashGain,
    ): StashedTime {
        val stashedTime = calculateStashed(currentTimeMillis, time, gain)
        return calculateCountdown(currentTimeMillis, stashedTime)
    }

    fun calculateStashed(
        currentTimeMillis: Long,
        time: StashedTime,
        gain: StashGain,
    ): StashedTime {
        val delta = (currentTimeMillis - time.stashSavedAtMillis).coerceAtLeast(minimumValue = 0)

        val newMillis = (delta * gain.millisPerMillisecond).toLong() / 1_000 * 1_000

        return time.copy(
            millis = time.millis + newMillis,
            stashSavedAtMillis = time.stashSavedAtMillis + (newMillis / gain.millisPerMillisecond).toLong()
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
