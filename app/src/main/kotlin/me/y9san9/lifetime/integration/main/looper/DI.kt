package me.y9san9.lifetime.integration.main.looper

import app.meetacy.di.builder.DIBuilder
import kotlinx.coroutines.*
import me.y9san9.lifetime.android.settings.MainSettings
import me.y9san9.lifetime.android.settings.settings
import me.y9san9.lifetime.core.TimeFormula
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.looper.TimeLooper

fun DIBuilder.looper() {
    val looper by singleton {
        TimeLooper(settings)
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun TimeLooper(
    settings: MainSettings,
    scope: CoroutineScope = GlobalScope + CoroutineName("Time Looper Worker"),
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
