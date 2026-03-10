package me.y9san9.lifetime.android.unlock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import me.y9san9.lifetime.R
import me.y9san9.lifetime.android.UnlockPopupActivity

/**
 * Short-lived foreground service whose only job is to launch UnlockPopupActivity.
 *
 * On Android 10+, background apps cannot freely start activities.
 * A running foreground service is one of the explicitly allowed exceptions.
 * This service starts, launches the popup, then immediately stops itself —
 * the notification is removed before the user ever sees it.
 */
class UnlockLauncherService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // startForeground must be called before anything else to avoid ANR.
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
            )
        } else {
            @Suppress("DEPRECATION")
            startForeground(NOTIFICATION_ID, buildNotification())
        }

        // Allowed because a foreground service is currently running.
        // FLAG_ACTIVITY_SINGLE_TOP prevents stacking a second popup if one is already showing.
        startActivity(
            Intent(this, UnlockPopupActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )

        // Remove the notification and stop immediately.
        // The notification appears for only a few milliseconds.
        // Note: we call stopForeground before stopSelf so the notification is removed
        // synchronously on the main thread, before the OS considers killing the process.
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val manager = requireNotNull(
            ContextCompat.getSystemService(this, NotificationManager::class.java)
        ) { "NotificationManager system service is unavailable" }

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Unlock launcher",
            NotificationManager.IMPORTANCE_MIN
        ).apply { setShowBadge(false) }

        manager.createNotificationChannel(channel)
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.drawable.stopwatch_icon)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()

    companion object {
        private const val CHANNEL_ID = "LifeTime_Unlock_Launcher"
        private const val NOTIFICATION_ID = 3
    }
}
