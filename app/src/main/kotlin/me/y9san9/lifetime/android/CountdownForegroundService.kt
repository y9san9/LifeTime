package me.y9san9.lifetime.android

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import app.meetacy.di.android.di
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import me.y9san9.lifetime.R
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.core.type.countdown
import me.y9san9.lifetime.looper.looper

class CountdownForegroundService : Service() {
    @OptIn(DelicateCoroutinesApi::class)
    private val scope = GlobalScope + CoroutineName("Foreground Service")
    private val looper = di.looper

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
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        createNotificationsChannel()

        if (intent == null) {
            if (looper.countdownState.value) {
                moveToForeground()
            } else {
                moveToBackground()
            }
        }

        when (intent?.getStringExtra(SERVICE_ACTION)) {
            MOVE_TO_FOREGROUND -> moveToForeground()
            MOVE_TO_BACKGROUND -> moveToBackground()
            else -> error("Unknown action")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        moveToBackground()
    }

    private var serviceJob: Job? = null

    private fun moveToForeground() {
        startForeground(1, buildNotification())

        serviceJob?.cancel()
        serviceJob = looper.countdown.time
            .takeWhile { it.countdown }
            .onEach(::updateNotification)
            .onEach { CountdownTileService.requestListeningState(this) }
            .onCompletion { moveToBackground() }
            .launchIn(scope)
    }

    private fun moveToBackground() {
        serviceJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationsChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.setSound(null, null)
        notificationChannel.setShowBadge(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun updateNotification(time: StashedTime) {
        notificationManager.notify(
            1,
            buildNotification(time)
        )
    }

    private fun buildNotification(time: StashedTime = looper.time.value): Notification {
        require(looper.countdownState.value) { "Cannot build notification for countdown while not in the state" }

        val title = getString(R.string.notification_countdown_message)

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
            .setAutoCancel(true)
            .setContentText(TimeFormatter.format(time))
            .setColorized(true)
            .setColor(getColor(R.color.color_accent))
            .setSmallIcon(R.drawable.stopwatch_icon)
            .setContentIntent(contentIntent)
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
            return Intent(context, CountdownForegroundService::class.java).apply {
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
