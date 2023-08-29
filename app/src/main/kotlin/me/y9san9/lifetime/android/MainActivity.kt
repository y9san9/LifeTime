package me.y9san9.lifetime.android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
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
        checkBatteryOptimizations()

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
            .apply(::startService)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
        if (viewModel.countdown.value) {
            ForegroundService
                .moveToForegroundIntent(this)
                .apply(::startForegroundServiceCompat)
        }
    }

    /**
     * This really sucks, but Huawei kills foreground services LoL
     */
    private fun checkBatteryOptimizations() {
        val packageName = applicationContext.packageName

        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.setAction(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }
}

private fun Activity.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}
