package me.y9san9.lifetime.android

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowCompat
import me.y9san9.lifetime.android.integration.mainViewModel
import me.y9san9.lifetime.compose.AppTheme
import me.y9san9.lifetime.compose.integration.MainContent


class MainActivity : ComponentActivity() {
    private val viewModel by mainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, /* decorFitsSystemWindows = */ false)

        setContent {
            AppTheme(isSystemInDarkTheme()) {
                MainContent(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resume()
        ForegroundService
            .moveToBackgroundIntent(this)
            .apply(applicationContext::startService)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
        if (viewModel.countdown.value) {
            ForegroundService
                .moveToForegroundIntent(this)
                .apply(applicationContext::startForegroundServiceCompat)
        }
    }
}

private fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}
