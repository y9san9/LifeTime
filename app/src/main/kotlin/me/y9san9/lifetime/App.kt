package me.y9san9.lifetime

import android.app.Application
import app.meetacy.di.android.AndroidDI
import app.meetacy.di.android.annotation.AndroidGlobalApi
import app.meetacy.di.builder.di
import me.y9san9.lifetime.integration.integration

class App : Application() {
    @OptIn(AndroidGlobalApi::class)
    override fun onCreate() {
        super.onCreate()
        AndroidDI.init(
            application = this,
            di = di(checkDependencies = false) {
                integration()
            }
        )
    }
}
