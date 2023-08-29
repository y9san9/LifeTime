package me.y9san9.lifetime.compose.integration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import me.y9san9.lifetime.android.MainViewModel
import me.y9san9.lifetime.compose.MainContent

@Composable
fun MainContent(viewModel: MainViewModel) {
    val stashedTime by viewModel.stashedTime.collectAsState()
    val secondAddedTime = viewModel.secondAddedTime.collectAsState()
    val countdown by viewModel.countdown.collectAsState()

    MainContent(
        time = stashedTime,
        countdown = countdown,
        secondAddedTime = secondAddedTime,
        onTimerClick = { viewModel.timerClick() }
    )
}
