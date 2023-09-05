package me.y9san9.lifetime.integration.main.looper

import app.meetacy.di.builder.DIBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.y9san9.lifetime.android.MainSettings
import me.y9san9.lifetime.android.settings
import me.y9san9.lifetime.core.TimeFormula
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.looper.TimeLooper

fun DIBuilder.looper() {
    val looper by singleton {
        TimeLooper(settings)
    }
}

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
