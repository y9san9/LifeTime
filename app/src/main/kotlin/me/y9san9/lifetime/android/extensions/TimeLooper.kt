package me.y9san9.lifetime.android.extensions

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.service.quicksettings.TileService
import kotlinx.coroutines.flow.update
import me.y9san9.lifetime.android.MainActivity
import me.y9san9.lifetime.android.foreground.CountdownForegroundService
import me.y9san9.lifetime.looper.TimeLooper

fun TimeLooper.toggleCountdownFromWidget(context: Context) {
    countdownState.update { !it }
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

fun TimeLooper.toggleCountdownFromTile(service: TileService) {
    countdownState.update { !it }
    if (shouldGoForeground()) startMainActivityFromTile(service)
}

fun TimeLooper.onActivityResume(activity: Activity) {
    resume()
    CountdownForegroundService
        .moveToBackgroundIntent(activity)
        .apply(activity::startServiceOnResume)
}

fun TimeLooper.onActivityPause(activity: Activity) {
    if (countdownState.value) {
        CountdownForegroundService
            .moveToForegroundIntent(activity)
            .apply(activity::startForegroundService)
    } else {
        pause()
    }
}

private fun TimeLooper.shouldGoForeground() = countdownState.value && !MainActivity.isForeground

private fun startMainActivityFromTile(service: TileService) {
    service.unlockAndRun {
        val intent = Intent(service, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
        }

        if (Build.VERSION.SDK_INT >= 34) {
            val pendingIntent = PendingIntent.getActivity(
                /* context = */ service,
                /* requestCode = */ 0,
                /* intent = */ intent,
                /* flags = */ PendingIntent.FLAG_IMMUTABLE
            )
            service.startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            service.startActivityAndCollapse(intent)
        }
    }
}
