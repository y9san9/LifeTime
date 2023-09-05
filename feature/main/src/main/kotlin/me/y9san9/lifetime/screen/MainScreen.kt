package me.y9san9.lifetime.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import app.meetacy.di.android.compose.viewmodel.viewModel
import app.meetacy.di.android.di
import me.y9san9.lifetime.compose.MainContent
import me.y9san9.lifetime.core.android.OnPause
import me.y9san9.lifetime.core.android.OnResume
import me.y9san9.lifetime.core.android.viewModel
import me.y9san9.lifetime.core.screen.Screen
import me.y9san9.lifetime.core.screen.navigateStatistics
import me.y9san9.lifetime.viewModel.MainViewModel

object MainScreen : Screen {
    @Composable
    override fun Content(
        controller: NavController,
        entry: NavBackStackEntry
    ) {
        val viewModel = di.viewModel<MainViewModel>()

        val time by viewModel.stashedTime.collectAsState()
        val secondAddedTime = viewModel.secondStashedTime.collectAsState()
        val countdown by viewModel.countdown.collectAsState()

        OnResume(viewModel::resume)
        OnPause(viewModel::pause)

        MainContent(
            time = time,
            secondAddedTime = secondAddedTime,
            countdown = countdown,
            onTimerClick = viewModel::timerClick,
            onNavigateStatistics = controller::navigateStatistics
        )
    }
}
