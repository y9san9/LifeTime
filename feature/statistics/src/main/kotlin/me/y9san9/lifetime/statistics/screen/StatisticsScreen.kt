package me.y9san9.lifetime.statistics.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import app.meetacy.di.android.compose.viewmodel.viewModel
import app.meetacy.di.android.di
import me.y9san9.lifetime.core.android.OnPause
import me.y9san9.lifetime.core.android.OnResume
import me.y9san9.lifetime.core.screen.Screen
import me.y9san9.lifetime.statistics.compose.StatisticsContent
import me.y9san9.lifetime.statistics.viewModel.StatisticsViewModel

object StatisticsScreen : Screen {
    @Composable
    override fun Content(
        controller: NavController,
        entry: NavBackStackEntry
    ) {
        val viewModel = di.viewModel<StatisticsViewModel>()

        val stats by viewModel.stats.collectAsState()
        val maxStashedTime by viewModel.maxStashedTime.collectAsState()
        val maxStashedDate by viewModel.maxStashedDate.collectAsState()
        val installDate by viewModel.installedDate.collectAsState()

        OnResume(viewModel::resume)
        OnPause(viewModel::pause)

        StatisticsContent(stats, maxStashedTime, maxStashedDate, installDate)
    }
}
