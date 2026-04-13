package me.y9san9.lifetime.statistics

import app.meetacy.di.DI
import app.meetacy.di.dependency.Dependency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.update.update
import me.y9san9.lifetime.core.type.StashGain

val DI.statsHandler: StatsHandler by Dependency

class StatsHandler(
    initial: AppStats,
    delegate: StateFlow<StashedTime>,
    gain: StateFlow<StashGain>,
    scope: CoroutineScope,
) {
    private val _stats = MutableStateFlow(initial)
    val stats: StateFlow<AppStats> = _stats.asStateFlow()

    init {
        delegate.onEach { time ->
            _stats.update { stats ->
                stats.update(time, gain.value)
            }
        }.launchIn(scope)
    }
}
