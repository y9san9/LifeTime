package me.y9san9.lifetime.core.type

/**
 * How many milliseconds being stashed per one millisecond
 * Or how many seconds being stashed per one second
 * Or how many minutes being stashed per one minute
 * And so on
 */
data class StashGain(
    val millisPerMillisecond: Double,
) {
    fun toHoursPerDay(): Double {
        return millisPerMillisecond * 24
    }

    companion object {
        // 2 hours per day
        val Default: StashGain = StashGain(0.08333333333)

        fun ofHoursPerDay(hoursPerDay: Double): StashGain {
            return StashGain(hoursPerDay / 24)
        }
    }
}
