package me.y9san9.lifetime.android

import android.content.Intent
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
        sendServiceAction(ForegroundService.MOVE_TO_BACKGROUND)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
        println(viewModel.countdown.value)
        if (viewModel.countdown.value) {
            sendServiceAction(ForegroundService.MOVE_TO_FOREGROUND)
        }
    }

    private fun sendServiceAction(action: String) {
        val service = Intent(this, ForegroundService::class.java)
        service.putExtra(
            ForegroundService.SERVICE_ACTION,
            action
        )
        startService(service)
    }
}
