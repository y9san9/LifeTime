package me.y9san9.lifetime.android.tile

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import app.meetacy.di.android.di
import me.y9san9.lifetime.R
import me.y9san9.lifetime.android.extensions.toggleCountdownFromTile
import me.y9san9.lifetime.core.TimeFormatter
import me.y9san9.lifetime.looper.looper

class CountdownTileService : TileService() {
    private val looper = di.looper
    private val tile: Tile get() = qsTile

    override fun onStartListening() {
        super.onStartListening()
        val countdown = looper.countdownState.value
        tile.state = if (countdown) STATE_ACTIVE else STATE_INACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (countdown) {
                val time = TimeFormatter.format(looper.countdown.time.value)
                getString(R.string.tile_countdown_message, time)
            } else null
        } else {
            tile.label = if (countdown) {
                TimeFormatter.format(looper.countdown.time.value)
            } else getString(R.string.app_name)
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        looper.toggleCountdownFromTile(this)
        requestListeningState(this)
    }

    companion object {
        fun requestListeningState(context: Context) {
            requestListeningState(context, ComponentName(context, CountdownTileService::class.java))
        }
    }
}
