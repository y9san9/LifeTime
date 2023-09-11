package me.y9san9.lifetime.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import app.meetacy.di.android.di
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.y9san9.lifetime.android.extensions.*
import me.y9san9.lifetime.android.foreground.CountdownForegroundService
import me.y9san9.lifetime.android.tile.CountdownTileService
import me.y9san9.lifetime.compose.AppTheme
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.screen.MainScreen
import me.y9san9.lifetime.screen.NavHost
import me.y9san9.lifetime.screen.screens

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, /* decorFitsSystemWindows = */ false)
        requestNotificationsPermission()

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

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) return
        requestPermissions(Manifest.permission.POST_NOTIFICATIONS, requestCode = 0)
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
        di.looper.onActivityResume(this)
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
        di.looper.onActivityPause(this)
    }

    companion object {
        var isForeground: Boolean = false
    }
}
