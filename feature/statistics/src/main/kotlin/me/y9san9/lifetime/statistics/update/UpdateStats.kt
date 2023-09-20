package me.y9san9.lifetime.statistics.update

import me.y9san9.lifetime.core.TimeFormula
import me.y9san9.lifetime.core.type.*
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.type.AppStats.Companion.MAX_AMOUNT_DAYS
import me.y9san9.lifetime.statistics.type.AppStats.Companion.MAX_AMOUNT_HOURS

fun AppStats.update(time: StashedTime): AppStats {
    // Do nothing if invalid date provided
    if (time.stashSavedAtMillis < lastData.last.stashSavedAtMillis) return this

    val lastData = lastData.update(time)
    val maxStashed = maxStashed.update(time.millis, time.date)

    return copy(
        lastData = lastData,
        maxStashed = maxStashed
    )
}

private const val MILLIS_PER_HOUR = 3_600_000

private tailrec fun AppStats.LastData.update(
    time: StashedTime,
    fromHour: Long = this.last.stashSavedAtMillis / MILLIS_PER_HOUR
): AppStats.LastData {
    val currentHour = time.stashSavedAtMillis / MILLIS_PER_HOUR

    if (currentHour == fromHour) {
        return AppStats.LastData(
            list = listOf(time.millis) + this.list.drop(n = 1),
            last = time
        )
    }

    val recalculatedLast = TimeFormula.calculate(
        currentTimeMillis = (currentHour + 1) * MILLIS_PER_HOUR,
        time = this.last
    )

    val resultList = listOf(recalculatedLast.millis, recalculatedLast.millis) + this.list.drop(n = 1)

    val lastData = AppStats.LastData(
        list = if (resultList.size > MAX_AMOUNT_HOURS) resultList.dropLast(n = 1) else resultList,
        last = recalculatedLast
    )

    return lastData.update(time, fromHour = fromHour + 1)
}

/**
 * For now this function does not calculate time at the end of the day,
 * however it seems useless since when user will enter the app,
 * this method will be triggered anyway
 */
private fun AppStats.Max.update(
    millis: Long,
    date: Date
): AppStats.Max {
    if (millis <= this.millis) return this
    return AppStats.Max(millis, date)
}
