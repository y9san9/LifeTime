package me.y9san9.lifetime.statistics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.update.update

class StatsHandler(
    initial: AppStats,
    delegate: StateFlow<StashedTime>,
    scope: CoroutineScope
) {
    private val _stats = MutableStateFlow(initial)
    val stats: StateFlow<AppStats> = _stats.asStateFlow()

    init {
        delegate.onEach { time ->
            _stats.update { stats ->
                stats.update(time)
            }
        }.launchIn(scope)
    }
}
