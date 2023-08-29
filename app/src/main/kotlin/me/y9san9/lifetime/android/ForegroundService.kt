package me.y9san9.lifetime.android

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.y9san9.lifetime.App
import me.y9san9.lifetime.R
import me.y9san9.lifetime.looper.TimeFormatter
import me.y9san9.lifetime.type.StashedTime

class ForegroundService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(context = Dispatchers.IO + job)

    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        createNotificationsChannel()

        when (intent.getStringExtra(SERVICE_ACTION)) {
            MOVE_TO_FOREGROUND -> moveToForeground()
            MOVE_TO_BACKGROUND -> moveToBackground()
            else -> error("Unknown action")
        }

        return START_STICKY
    }

    private var serviceJob: Job? = null

    private fun moveToForeground() {

        startForeground(1, buildNotification())

        serviceJob = App.looper.countdown.time
            .onEach(::updateNotification)
            .launchIn(scope)
    }

    private fun moveToBackground() {
        serviceJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun updateNotification(time: StashedTime) {
        notificationManager.notify(
            1,
            buildNotification(time)
        )
    }

    private fun buildNotification(time: StashedTime = App.looper.time.value): Notification {
        require(App.looper.countdownState.value) { "Cannot build notification for countdown while not in the state" }

        val title = getString(R.string.countdown_message)

        val intent = Intent(this, MainActivity::class.java)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setOngoing(true)
            .setContentText(TimeFormatter.format(time))
            .setColorized(true)
            .setColor(getColor(R.color.color_accent))
            .setSmallIcon(R.drawable.notification_icon)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "LifeTime_Foreground"

        // Actions
        const val MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND"
        const val MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND"

        // Intent Extras
        const val SERVICE_ACTION = "SERVICE_ACTION"

        fun intent(context: Context, action: String): Intent {
            return Intent(context, ForegroundService::class.java).apply {
                putExtra(
                    SERVICE_ACTION,
                    action
                )
            }
        }

        fun moveToForegroundIntent(context: Context) = intent(
            context = context,
            action = MOVE_TO_FOREGROUND
        )

        fun moveToBackgroundIntent(context: Context) = intent(
            context = context,
            action = MOVE_TO_BACKGROUND
        )
    }
}
