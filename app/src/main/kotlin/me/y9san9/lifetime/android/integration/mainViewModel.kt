package me.y9san9.lifetime.android.integration

import androidx.activity.ComponentActivity
import androidx.lifecycle.viewModelScope
import me.y9san9.lifetime.App
import me.y9san9.lifetime.android.MainViewModel
import me.y9san9.lifetime.android.viewModel
import me.y9san9.lifetime.type.Clock
import me.y9san9.lifetime.type.StashedTime
import me.y9san9.lifetime.viewModel.MainViewModel as MainViewModelImpl

fun ComponentActivity.mainViewModel(): Lazy<MainViewModel> {
    return viewModel {
        val mainSettings = App.settings

        val settings = object : MainViewModelImpl.Settings {
            override fun saveTime(time: StashedTime) {
                mainSettings.saveTime(time)
            }
            override fun loadTime(): StashedTime? {
                return mainSettings.loadTime()
            }
        }

        val impl = MainViewModelImpl(
            settings = settings,
            clock = Clock.System,
            looper = App.looper
        )

        object : MainViewModel {
            override val stashedTime = impl.stashedTime
            override val secondAddedTime = impl.secondStashedTime
            override val countdown = impl.countdown
            override fun timerClick() = impl.timerClick()
            override fun resume() = impl.resume()
            override fun pause() = impl.pause()
        }
    }
}
