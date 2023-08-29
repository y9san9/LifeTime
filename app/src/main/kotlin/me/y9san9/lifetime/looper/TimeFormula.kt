package me.y9san9.lifetime.looper

import me.y9san9.lifetime.type.StashedTime

object TimeFormula {
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
        val delta = currentTimeMillis - time.stashSavedAtMillis
        require(delta >= 0) { "Cannot calculate back in time" }

        val newMillis = (delta * millisPerMillisecond).toLong() / 1000 * 1000

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
        val delta = (currentTimeMillis - time.countdownSavedAtMillis) / 1000 * 1000

        require(delta >= 0) { "Cannot calculate back in time" }
        return time.copy(
            millis = (time.millis - delta).coerceAtLeast(minimumValue = 0L),
            countdownSavedAtMillis = time.countdownSavedAtMillis + delta
        )
    }
}
