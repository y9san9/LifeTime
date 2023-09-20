package me.y9san9.lifetime.integration

import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.integration.main.looper.looper
import me.y9san9.lifetime.integration.main.viewModel.mainViewModel
import me.y9san9.lifetime.integration.settings.settings
import me.y9san9.lifetime.integration.statistics.statsHandler.statsHandler
import me.y9san9.lifetime.integration.statistics.viewModel.statisticsViewModel

fun DIBuilder.integration() {
    settings()
    looper()
    statsHandler()

    mainViewModel()
    statisticsViewModel()
}
