package me.y9san9.lifetime.statistics.compose.logarithmic

import kotlin.math.pow

private const val BASE = 2f

fun chartTransform(x: Float) = x.pow(1 / BASE)
fun chartInverse(x: Float) = x.pow(BASE)
