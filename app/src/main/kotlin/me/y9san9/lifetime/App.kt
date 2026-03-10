package me.y9san9.lifetime

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import app.meetacy.di.android.AndroidDI
import app.meetacy.di.android.annotation.AndroidGlobalApi
import app.meetacy.di.android.di
import app.meetacy.di.builder.di
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.y9san9.lifetime.android.debug.DebugActivity
import me.y9san9.lifetime.android.settings.settings
import me.y9san9.lifetime.android.tile.CountdownTileService
import me.y9san9.lifetime.android.widget.MainWidget
import me.y9san9.lifetime.integration.integration
import me.y9san9.lifetime.looper.looper
import me.y9san9.lifetime.statistics.statsHandler
import kotlin.system.exitProcess

class App : Application() {
    @OptIn(DelicateCoroutinesApi::class)
    private val backgroundScope = GlobalScope + CoroutineName("Application Background")
    private val looper get() = di.looper
    private val statsHandler get() = di.statsHandler
    private val settings get() = di.settings

    @OptIn(AndroidGlobalApi::class)
    override fun onCreate() {
        super.onCreate()

        AndroidDI.init(
            application = this,
            di = di(checkDependencies = false) {
                integration()
            }
        )
        settings.init()

        setDebugActivity()
        notifyServicesAboutState()
        saveWorkersData()
        registerScreenOffReceiver()
    }

    private fun setDebugActivity() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            startActivity(DebugActivity.intent(applicationContext, throwable))
            exitProcess(0)
        }
    }

    private fun notifyServicesAboutState() {
        looper.countdownState.onEach {
            CountdownTileService.requestListeningState(this)
            MainWidget.update(this)
        }.launchIn(backgroundScope)
        looper.countdown.time.onEach {
            CountdownTileService.requestListeningState(this)
            MainWidget.update(this)
        }.launchIn(backgroundScope)
    }

    private fun saveWorkersData() {
        looper.time
            .onEach(settings::saveTime)
            .launchIn(backgroundScope)
        statsHandler.stats
            .onEach(settings::saveStats)
            .launchIn(backgroundScope)
    }

    /**
     * Stops the countdown whenever the screen turns off (phone locked).
     *
     * ACTION_SCREEN_OFF cannot be received by manifest-declared receivers —
     * it must be registered dynamically. The Application context lives for
     * the entire process lifetime, so this receiver is always active while
     * the app process is alive (which is guaranteed when countdown is running,
     * because CountdownForegroundService keeps the process alive).
     */
    private fun registerScreenOffReceiver() {
        registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    looper.countdownState.update { false }
                }
            },
            IntentFilter(Intent.ACTION_SCREEN_OFF)
        )
    }
}
