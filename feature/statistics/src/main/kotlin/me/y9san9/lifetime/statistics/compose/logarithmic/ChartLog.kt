package me.y9san9.lifetime.statistics.compose.logarithmic

import kotlin.math.log
import kotlin.math.pow

private const val BASE = 2f
private const val TWO_HOURS_IN_MILLIS = 2 * 3_600_000

fun chartTransform(x: Float) = when {
    x < TWO_HOURS_IN_MILLIS -> x / TWO_HOURS_IN_MILLIS
    else -> log(x / TWO_HOURS_IN_MILLIS, BASE) / 2 + 1
}

fun chartInverse(x: Float) = when {
    x < 1 -> x * TWO_HOURS_IN_MILLIS
    else -> BASE.pow((x - 1) * 2) * TWO_HOURS_IN_MILLIS
}
