package me.y9san9.lifetime.android

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import app.meetacy.di.android.di
import kotlinx.coroutines.flow.updateAndGet
import me.y9san9.lifetime.R
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.looper.looper

class CountdownTileService : TileService() {
    private val looper = di.looper
    private val tile: Tile get() = qsTile

    override fun onStartListening() {
        super.onStartListening()
        val enabled = looper.countdownState.value
        tile.state = if (looper.countdownState.value) STATE_ACTIVE else STATE_INACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (enabled) {
                val time = TimeFormatter.format(looper.countdown.time.value)
                getString(R.string.tile_countdown_message, time)
            } else null
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val countdown = looper.countdownState.updateAndGet { !it }
        if (countdown && !MainActivity.isForeground) startMainActivity()
        requestListeningState(this)
    }

    private fun startMainActivity() {
        unlockAndRun {
            val intent = Intent(this, MainActivity::class.java)

            if (Build.VERSION.SDK_INT >= 34) {
                val pendingIntent = PendingIntent.getActivity(
                    /* context = */ this,
                    /* requestCode = */ 0,
                    /* intent = */ intent,
                    /* flags = */ PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                startActivity(intent)
            }
        }
    }

    companion object {
        fun requestListeningState(context: Context) {
            requestListeningState(context, ComponentName(context, CountdownTileService::class.java))
        }
    }
}
