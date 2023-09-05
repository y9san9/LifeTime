package me.y9san9.lifetime.core

import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.core.type.hourMinutes
import me.y9san9.lifetime.core.type.hours
import me.y9san9.lifetime.core.type.minuteSeconds
import kotlin.math.max

object TimeFormatter {
    fun format(time: StashedTime) = buildString {
        val hours = time.hours.toString()
        val minutes = time.hourMinutes.toString()
        val seconds = time.minuteSeconds.toString()

        if (time.hours != 0L) {
            append(
                hours.padStart(
                    length = max(2, hours.length),
                    padChar = '0'
                )
            )
            append(':')
        }

        append(
            minutes.padStart(
                length = 2,
                padChar = '0'
            )
        )
        append(':')

        append(
            seconds.padStart(
                length = 2,
                padChar = '0'
            )
        )
    }

    fun format(milliseconds: Long) = format(
        StashedTime(milliseconds, 0, null)
    )
}
