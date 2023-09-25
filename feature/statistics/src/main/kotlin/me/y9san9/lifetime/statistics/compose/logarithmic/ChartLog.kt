package me.y9san9.lifetime.statistics.compose.logarithmic

import kotlin.math.log2
import kotlin.math.pow

private const val HOUR_IN_MILLIS = 3_600_000f
private const val THREE_HOURS_IN_MILLIS = 3f * HOUR_IN_MILLIS

/**
 * val threshold = latestX + 2 * HOUR_IN_MILLIS
 * return log2(~~threshold~~ * 1.5f / ~~threshold~~)
 */
private val minMaxLog: Float = log2(1.5f)

fun chartMaxLog(
    latestX: Float,
    x: Float
): Float {
    val threshold = latestX + 2 * HOUR_IN_MILLIS
    return log2(x / threshold)
}

fun chartTransform(
    maxLog: Float,
    latestX: Float,
    x: Float
): Float {
    val threshold = latestX + 2 * HOUR_IN_MILLIS
    return when {
        x < threshold -> x / threshold * 2
        maxLog < minMaxLog -> log2(x / threshold) + 2
        else -> log2(x / threshold) / maxLog + 2
    }
}

fun chartInverse(
    maxLog: Float,
    latestX: Float,
    y: Float
): Float {
    val threshold = latestX + 2 * HOUR_IN_MILLIS
    return when {
        y < 2 -> y * threshold / 2
        maxLog < minMaxLog -> 2f.pow(y - 2) * threshold
        else -> 2f.pow((y - 2) * maxLog) * threshold
    }
}
