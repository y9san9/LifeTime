package me.y9san9.lifetime.statistics.update

import me.y9san9.lifetime.core.TimeFormula
import me.y9san9.lifetime.core.type.*
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.type.AppStats.Companion.MAX_AMOUNT
import me.y9san9.lifetime.statistics.type.date

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

private tailrec fun AppStats.LastData.update(
    time: StashedTime,
    date: Date = this.date
): AppStats.LastData {
    if (time.date == date) {
        return AppStats.LastData(
            list = listOf(time.millis) + this.list.drop(n = 1),
            last = time
        )
    }

    val recalculatedLast = TimeFormula.calculate(
        currentTimeMillis = date.tomorrow.epochMillis,
        time = this.last
    )

    val resultList = listOf(recalculatedLast.millis, recalculatedLast.millis) + this.list.drop(n = 1)

    val lastData = AppStats.LastData(
        list = if (resultList.size > MAX_AMOUNT) resultList.dropLast(n = 1) else resultList,
        last = recalculatedLast
    )

    return lastData.update(time, date.tomorrow)
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
