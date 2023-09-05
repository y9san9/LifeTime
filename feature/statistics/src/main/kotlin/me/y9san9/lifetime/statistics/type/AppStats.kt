package me.y9san9.lifetime.statistics.type

import me.y9san9.lifetime.core.type.Date
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.core.type.date
import me.y9san9.lifetime.core.type.yesterday

data class AppStats(
    val lastData: LastData,
    val maxStashed: Max,
    val installedDate: Date
) {
    data class LastData(
        val list: List<Long>,
        val last: StashedTime
    ) {
        init {
            require(list.size in 2..31)
        }
    }
    
    data class Max(
        val millis: Long,
        val date: Date
    )

    companion object {
        fun initial(time: StashedTime): AppStats {
            return AppStats(
                lastData = LastData(
                    list = listOf(time.millis, 0),
                    last = time
                ),
                maxStashed = Max(
                    millis = time.millis,
                    date = time.date
                ),
                installedDate = time.date
            )
        }
    }
}

val AppStats.LastData.date: Date get() = last.date

val AppStats.LastData.dates: List<Date>
    get() = list.runningFold(date) { date, _ -> date.yesterday }.dropLast(n = 1)
