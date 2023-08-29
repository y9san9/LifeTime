@file:Suppress("OPT_IN_USAGE")

package me.y9san9.lifetime.looper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import me.y9san9.lifetime.type.Clock
import me.y9san9.lifetime.type.StashedTime

class TimeLooper(
    initialTime: StashedTime,
    scope: CoroutineScope,
    clock: Clock
) {
    private val mutex = Mutex()
    private val _time = MutableStateFlow(initialTime)

    val stash: StashTimeLooper = StashTimeLooper(scope, _time, mutex, clock)
    val countdown: CountdownTimeLooper = CountdownTimeLooper(scope, _time, mutex, clock)
    val countdownState: MutableStateFlow<Boolean> get() = countdown.countdown

    val time: StateFlow<StashedTime> = countdownState.transformLatest { countdown ->
        if (countdown) {
            this@TimeLooper.countdown.time.drop(1).collect(this)
        } else {
            stash.time.drop(1).collect(this)
        }
    }.stateIn(scope, SharingStarted.Eagerly, initialTime)

    fun pause() {
        stash.pause()
        countdown.pause()
    }
    fun resume() {
        stash.resume()
        countdown.resume()
    }
}
