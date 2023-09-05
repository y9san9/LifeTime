package me.y9san9.lifetime.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import app.meetacy.di.android.di
import me.y9san9.lifetime.compose.AppTheme
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.screen.MainScreen
import me.y9san9.lifetime.screen.NavHost
import me.y9san9.lifetime.screen.screens


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, /* decorFitsSystemWindows = */ false)

        setContent {
            Box(Modifier.safeDrawingPadding()) {
                AppTheme(isSystemInDarkTheme()) {
                    NavHost(
                        startScreen = MainScreen,
                        other = screens
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ForegroundService
            .moveToBackgroundIntent(this)
            .apply(applicationContext::startService)
    }

    override fun onPause() {
        super.onPause()
        if (di.looper.countdown.countdown.value) {
            ForegroundService
                .moveToForegroundIntent(this)
                .apply(applicationContext::startForegroundService)
        } else {
            di.looper.pause()
        }
    }
}
