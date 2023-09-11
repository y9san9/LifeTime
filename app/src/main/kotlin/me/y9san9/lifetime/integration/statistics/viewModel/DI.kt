package me.y9san9.lifetime.integration.statistics.viewModel

import app.meetacy.di.android.compose.viewmodel.viewModelScope
import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.android.settings
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.statistics.type.AppStats
import me.y9san9.lifetime.statistics.viewModel.StatisticsViewModel

fun DIBuilder.statisticsViewModel() {
    val statisticsViewModel by factory {
        val delegate = settings

        val storage = object : StatisticsViewModel.Storage {
            override fun save(stats: AppStats) = delegate.saveStats(stats)
            override fun load(): AppStats? = delegate.loadStats()
        }

        StatisticsViewModel(
            storage = storage,
            time = looper.time,
            clock = Clock.System,
            scope = viewModelScope
        )
    }
}
