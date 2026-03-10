package me.y9san9.lifetime.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import app.meetacy.di.android.di
import kotlinx.coroutines.flow.update
import me.y9san9.lifetime.android.foreground.CountdownForegroundService
import me.y9san9.lifetime.compose.AppTheme
import me.y9san9.lifetime.compose.UnlockPopupContent
import me.y9san9.lifetime.looper.looper

class UnlockPopupActivity : ComponentActivity() {

    // Dismiss the popup automatically if the user locks the phone before responding.
    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        setContent {
            AppTheme(isSystemInDarkTheme()) {
                UnlockPopupContent(
                    onYes = ::onYes,
                    onNo = ::finish
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOffReceiver)
    }

    private fun onYes() {
        di.looper.countdownState.update { true }
        // Start the foreground service so the countdown notification appears
        // while the user is outside the app.
        startForegroundService(CountdownForegroundService.moveToForegroundIntent(this))
        finish()
    }
}
