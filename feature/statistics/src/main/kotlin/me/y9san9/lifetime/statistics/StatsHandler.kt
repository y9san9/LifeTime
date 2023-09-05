package me.y9san9.lifetime.statistics

import kotlinx.coroutines.flow.StateFlow
import me.y9san9.lifetime.core.stdlib.mapState
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.update.update

class StatsHandler(
    initial: AppStats,
    delegate: StateFlow<StashedTime>
) {
    val stats: StateFlow<AppStats> = delegate.mapState(initial::update)
}
