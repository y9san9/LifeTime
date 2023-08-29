package me.y9san9.lifetime.type

import me.y9san9.lifetime.looper.TimeFormula.millisPerMillisecond

data class StashedTime(
    val millis: Long,
    val stashSavedAtMillis: Long,
    // null if there is no countdown
    val countdownSavedAtMillis: Long?
) {
    init {
        require(millis >= 0)
        require(stashSavedAtMillis >= 0)
        require(countdownSavedAtMillis == null || countdownSavedAtMillis >= 0)
    }

    companion object {
        fun zero(currentTimeMillis: Long): StashedTime {
            return StashedTime(
                millis = 1_000_000,
                stashSavedAtMillis = currentTimeMillis,
                countdownSavedAtMillis = null
            )
        }
    }
}

fun StashedTime.withCountdown(clock: Clock) = copy(countdownSavedAtMillis = clock.currentTimeMillis())
fun StashedTime.withoutCountdown() = copy(
    millis = millis / 1_000 * 1_000,
    countdownSavedAtMillis = null
)

val StashedTime.countdown: Boolean get() = countdownSavedAtMillis != null

val StashedTime.seconds: Long get() = millis / 1_000
val StashedTime.minutes: Long get() = seconds / 60
val StashedTime.hours: Long get() = minutes / 60

val StashedTime.secondMillis: Int get() = (millis % 1000).toInt()
val StashedTime.minuteSeconds: Int get() = (seconds % 60).toInt()
val StashedTime.hourMinutes: Int get() = (minutes % 60).toInt()

fun StashedTime.secondProgress(clock: Clock): Progress {
    val secondDelay = 1.0 / millisPerMillisecond * 1_000
    val targetTime = stashSavedAtMillis + secondDelay
    val progress = 100 - ((targetTime - clock.currentTimeMillis()).coerceAtLeast(0.0) / secondDelay * 100).toInt()
    if (progress < 10) return Progress.Min
    return Progress(progress)
}
