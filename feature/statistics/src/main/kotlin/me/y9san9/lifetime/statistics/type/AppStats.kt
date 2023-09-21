package me.y9san9.lifetime.statistics.type

import me.y9san9.lifetime.core.type.*

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
            require(list.size in 2..MAX_AMOUNT_HOURS) { list }
        }
    }
    
    data class Max(
        val millis: Long,
        val date: Date
    )

    companion object {
        private const val MAX_AMOUNT_DAYS = 365
        const val MAX_AMOUNT_HOURS = MAX_AMOUNT_DAYS * 24

        fun initial(time: StashedTime): AppStats {
            return AppStats(
                lastData = LastData(
                    list = buildInitial(time),
                    last = time
                ),
                maxStashed = Max(
                    millis = time.millis,
                    date = time.date
                ),
                installedDate = time.date
            )
        }

        private const val MILLIS_PER_HOUR = 3_600_000

        private fun buildInitial(time: StashedTime): List<Long> {
            val yesterdayMillis = time.date.yesterday.epochMillis

            return buildList {
                add(time.millis)
                var currentTimeMillis = time.stashSavedAtMillis
                while (true) {
                    add(0)
                    currentTimeMillis -= MILLIS_PER_HOUR
                    if (currentTimeMillis < yesterdayMillis) break
                }
            }
        }
    }
}

@Deprecated(
    message = "Should be used only to migrate day stats to hour stats"
)
fun AppStats.upgradeFromDaysToHours(): AppStats {
    val lastData = AppStats.LastData(
        list = lastData.list.windowed(2) { (current, next) ->
            buildList {
                val delta = (next - current) / 24
                for (i in 0..<24) {
                    add(current + delta * i)
                }
            }
        }.flatten() + lastData.list.last(),
        last = lastData.last
    )
    return AppStats(
        lastData, maxStashed, installedDate
    )
}

val AppStats.LastData.date: Date get() = last.date
