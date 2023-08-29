package me.y9san9.lifetime.looper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.y9san9.lifetime.type.Clock
import me.y9san9.lifetime.type.StashedTime
import me.y9san9.lifetime.looper.TimeFormula.millisPerMillisecond

class StashTimeLooper(
    scope: CoroutineScope,
    base: MutableStateFlow<StashedTime>,
    mutex: Mutex,
    private val clock: Clock
) {
    private val _time = MutableStateFlow(base.value)
    val time: StateFlow<StashedTime> = _time.asStateFlow()

    val isRunning = MutableStateFlow(false)

    init {
        var currentTime by base::value

        scope.launch {
            while (true) {
                val delayTime: Long

                mutex.withLock {
                    currentTime = TimeFormula.calculateStashed(
                        clock.currentTimeMillis(),
                        currentTime
                    )
                    _time.value = currentTime
                    delayTime = updateDelay(currentTime)
                }

                delay(delayTime)
                isRunning.first { it }
            }
        }
    }

    fun updateDelay(time: StashedTime): Long {
        val secondDelay = 1.0 / millisPerMillisecond * 1_000
        val targetTime = (time.stashSavedAtMillis + secondDelay).toLong()
        return (targetTime - clock.currentTimeMillis()).coerceAtLeast(0)
    }

    fun resume() {
        isRunning.value = true
    }
    fun pause() {
        isRunning.value = false
    }
}
