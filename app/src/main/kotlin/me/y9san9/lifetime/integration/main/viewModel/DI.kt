package me.y9san9.lifetime.integration.main.viewModel

import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.core.type.Clock
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.viewModel.MainViewModel
import me.y9san9.lifetime.android.settings.settings

fun DIBuilder.mainViewModel() {
    val mainViewModel by factory {
        MainViewModel(
            clock = Clock.System,
            looper = looper,
            gain = settings.stashGain,
        )
    }
}
