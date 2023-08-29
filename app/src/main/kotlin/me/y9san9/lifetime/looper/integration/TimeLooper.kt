package me.y9san9.lifetime.looper.integration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.y9san9.lifetime.android.MainSettings
import me.y9san9.lifetime.looper.TimeFormula
import me.y9san9.lifetime.looper.TimeLooper
import me.y9san9.lifetime.type.Clock
import me.y9san9.lifetime.type.StashedTime

fun TimeLooper(
    settings: MainSettings,
    scope: CoroutineScope = MainScope(),
    clock: Clock = Clock.System
): TimeLooper {
    val loadedTime = settings.loadTime() ?: StashedTime.zero(clock.currentTimeMillis())
    val actualTime = TimeFormula.calculate(clock.currentTimeMillis(), loadedTime)

    return TimeLooper(
        initialTime = actualTime,
        scope = scope,
        clock = clock
    )
}
