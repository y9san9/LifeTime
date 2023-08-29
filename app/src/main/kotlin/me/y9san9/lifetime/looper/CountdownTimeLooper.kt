package me.y9san9.lifetime.looper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.y9san9.lifetime.type.*

class CountdownTimeLooper(
    scope: CoroutineScope,
    private val base: MutableStateFlow<StashedTime>,
    private val mutex: Mutex,
    private val clock: Clock
) {
    private val _time = MutableStateFlow(base.value)
    val time: StateFlow<StashedTime> = _time.asStateFlow()

    val countdown = MutableStateFlow(base.value.countdown)

    private val isRunning = MutableStateFlow(false)

    init {
        scope.launch {
            while (true) {
                val delayTime: Long

                mutex.withLock {
                    updateTime()
                    delayTime = updateDelay(base.value)
                }

                delay(delayTime)
                isRunning.first { it }
                base.first { it.countdown }
            }
        }

        countdown.onEach { countdown ->
            mutex.withLock {
                if (countdown) {
                    setTime { it.withCountdown(clock) }
                } else {
                    setTime(StashedTime::withoutCountdown)
                }
            }
        }.launchIn(scope)
    }

    private inline fun updateTime(transform: (StashedTime) -> StashedTime = { it }) {
        setTime { time ->
            TimeFormula.calculateCountdown(
                clock.currentTimeMillis(),
                time
            ).let(transform)
        }
    }

    private inline fun setTime(transform: (StashedTime) -> StashedTime) {
        base.value = transform(base.value)
        _time.value = base.value
    }

    private fun updateDelay(time: StashedTime): Long {
        time.countdownSavedAtMillis ?: return 1_000
        val targetTime = time.countdownSavedAtMillis + 1_000
        return (targetTime - clock.currentTimeMillis()).coerceAtLeast(0)
    }

    fun resume() {
        isRunning.value = true
    }
    fun pause() {
        isRunning.value = false
    }
}
