@file:OptIn(DelicateCoroutinesApi::class)

package me.y9san9.lifetime.integration.statistics.statsHandler

import app.meetacy.di.builder.DIBuilder
import kotlinx.coroutines.*
import me.y9san9.lifetime.android.settings.MainSettings
import me.y9san9.lifetime.android.settings.settings
import me.y9san9.lifetime.looper.TimeLooper
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.statistics.StatsHandler
import me.y9san9.lifetime.statistics.type.AppStats

fun DIBuilder.statsHandler() {
    val statsHandler by singleton {
        StatsHandler(settings, looper)
    }
}

fun StatsHandler(
    settings: MainSettings,
    looper: TimeLooper,
    scope: CoroutineScope = GlobalScope + CoroutineName("Stats Handler Worker")
): StatsHandler {
    val initial = settings.loadStats() ?: AppStats.initial(looper.time.value)
    return StatsHandler(initial, looper.time, scope)
}
