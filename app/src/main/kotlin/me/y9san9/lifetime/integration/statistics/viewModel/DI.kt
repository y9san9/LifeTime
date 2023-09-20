package me.y9san9.lifetime.integration.statistics.viewModel

import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.statistics.statsHandler
import me.y9san9.lifetime.statistics.viewModel.StatisticsViewModel

fun DIBuilder.statisticsViewModel() {
    val statisticsViewModel by factory {
        StatisticsViewModel(
            handler = statsHandler,
            clock = Clock.System,
        )
    }
}
