package me.y9san9.lifetime.android

import kotlinx.coroutines.flow.StateFlow
import me.y9san9.lifetime.type.SecondStashedTimeView
import me.y9san9.lifetime.type.StashedTimeView

interface MainViewModel {
    val stashedTime: StateFlow<StashedTimeView>
    val secondAddedTime: StateFlow<SecondStashedTimeView>

    val countdown: StateFlow<Boolean>
    fun timerClick()

    fun resume()
    fun pause()
}
