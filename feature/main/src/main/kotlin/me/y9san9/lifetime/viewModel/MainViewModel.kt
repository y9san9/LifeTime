package me.y9san9.lifetime.viewModel

import kotlinx.coroutines.flow.*
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.looper.TimeLooper
import me.y9san9.lifetime.core.stdlib.mapState
import me.y9san9.lifetime.core.type.*

class MainViewModel(
    private val storage: Storage,
    clock: Clock,
    private val looper: TimeLooper
) {
    val stashedTime: StateFlow<StashedTimeView> = looper.time.mapState(StashedTime::map)

    val secondStashedTime: StateFlow<SecondStashedTimeView> =
        looper.stash.time.mapState { time ->
            SecondStashedTimeView(
                string = time.map().string,
                progress = { time.secondProgress(clock).float },
                updateDelay = { looper.stash.updateDelay(time) }
            )
        }

    val countdown: StateFlow<Boolean> get() = looper.countdownState

    fun timerClick() {
        looper.countdownState.update { !it }
    }

    fun resume() { }

    fun pause() { storage.saveTime(looper.time.value) }

    interface Storage {
        fun saveTime(time: StashedTime)
        fun loadTime(): StashedTime?
    }
}

private fun StashedTime.map(): StashedTimeView {
    return StashedTimeView(
        string = TimeFormatter.format(this)
    )
}
