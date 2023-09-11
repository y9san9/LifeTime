package me.y9san9.lifetime

import android.app.Application
import app.meetacy.di.android.AndroidDI
import app.meetacy.di.android.annotation.AndroidGlobalApi
import app.meetacy.di.android.di
import app.meetacy.di.builder.di
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.y9san9.lifetime.android.CountdownTileService
import me.y9san9.lifetime.android.DebugActivity
import me.y9san9.lifetime.integration.integration
import me.y9san9.lifetime.looper.looper
import kotlin.system.exitProcess

class App : Application() {
    @OptIn(DelicateCoroutinesApi::class)
    private val backgroundScope = GlobalScope + CoroutineName("Application Background")

    @OptIn(AndroidGlobalApi::class)
    override fun onCreate() {
        super.onCreate()
        setDebugActivity()

        AndroidDI.init(
            application = this,
            di = di(checkDependencies = false) {
                integration()
            }
        )
    }

    private fun setDebugActivity() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            startActivity(DebugActivity.intent(applicationContext, throwable))
            exitProcess(0)
        }
    }
}
