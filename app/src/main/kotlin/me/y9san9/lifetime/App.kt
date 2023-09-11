package me.y9san9.lifetime

import android.app.Application
import android.os.Handler
import app.meetacy.di.android.AndroidDI
import app.meetacy.di.android.annotation.AndroidGlobalApi
import app.meetacy.di.builder.di
import me.y9san9.lifetime.android.DebugActivity
import me.y9san9.lifetime.integration.integration
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class App : Application() {
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
