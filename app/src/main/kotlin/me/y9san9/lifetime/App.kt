package me.y9san9.lifetime

import android.app.Application
import kotlinx.coroutines.MainScope
import me.y9san9.lifetime.android.MainSettings
import me.y9san9.lifetime.looper.TimeLooper
import me.y9san9.lifetime.looper.integration.TimeLooper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        init(app = this)
    }

    companion object {
        lateinit var settings: MainSettings
        lateinit var looper: TimeLooper

        private fun init(app: App) {
            settings = MainSettings(app)
            looper = TimeLooper(settings).apply(TimeLooper::resume)
        }
    }
}
