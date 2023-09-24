package me.y9san9.lifetime.statistics.compose.logarithmic

import kotlin.math.log2
import kotlin.math.pow

private const val HOUR_IN_MILLIS = 3_600_000f
private const val THREE_HOURS_IN_MILLIS = 3f * HOUR_IN_MILLIS

private val minMaxLog = chartMaxLog(5f * HOUR_IN_MILLIS)

fun chartMaxLog(x: Float) = safeLog(x)

fun chartTransform(
    maxLog: Float,
    x: Float
) = when {
    x < THREE_HOURS_IN_MILLIS -> x / THREE_HOURS_IN_MILLIS * 2
    maxLog < minMaxLog -> safeLog(x) + 2
    else -> safeLog(x) / maxLog + 2
}

fun chartInverse(
    maxLog: Float,
    x: Float
): Float {
    println("MAX $maxLog, x $x")
    return when {
        x < 2 -> x * THREE_HOURS_IN_MILLIS / 2
        maxLog < minMaxLog -> safeLogInverse(x - 2)
        else -> safeLogInverse((x - 2) * maxLog)
    }
}

private fun safeLog(x: Float) = log2(x / THREE_HOURS_IN_MILLIS)
private fun safeLogInverse(x: Float) = (2f.pow(x).also {
    println("POWEER OF TWO: $it")
}).also {
    println("INVERSE LOG $it")
} * THREE_HOURS_IN_MILLIS
