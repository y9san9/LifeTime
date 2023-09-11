package me.y9san9.lifetime.integration.settings

import app.meetacy.di.android.application
import app.meetacy.di.builder.DIBuilder
import me.y9san9.lifetime.android.settings.MainSettings

fun DIBuilder.settings() {
    val settings by singleton {
        MainSettings(application)
    }
}
