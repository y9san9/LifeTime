package me.y9san9.lifetime.integration.main.viewModel

import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.android.settings
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.viewModel.MainViewModel

fun DIBuilder.mainViewModel() {
    val mainViewModel by factory {
        val delegate = settings

        val storage = object : MainViewModel.Storage {
            override fun saveTime(time: StashedTime) = delegate.saveTime(time)
            override fun loadTime(): StashedTime? = delegate.loadTime()
        }

        MainViewModel(
            storage = storage,
            clock = Clock.System,
            looper = looper
        )
    }
}
