package me.y9san9.lifetime.type

data class StashedTimeView(val string: String)

data class SecondStashedTimeView(
    val string: String,
    val progress: () -> Float,
    val updateDelay: () -> Long
)
