package me.y9san9.lifetime.android

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import me.y9san9.lifetime.type.StashedTime

class MainSettings(
    private val application: Application
) {
    private val preferences = application.getSharedPreferences(
        /* name = */ "main-settings",
        /* mode = */ Context.MODE_PRIVATE
    )

    fun saveTime(time: StashedTime) {
        preferences.edit {
            putLong(MILLIS_KEY, time.millis)
            putLong(STASH_SAVED_AT_KEY, time.stashSavedAtMillis)
            putLong(COUNTDOWN_SAVED_AT_KET, time.countdownSavedAtMillis ?: -1)
        }
    }

    fun loadTime(): StashedTime? {
        val millis = preferences
            .getLong(MILLIS_KEY, -1)
            .takeIf { it >= 0 } ?: return null

        val stashSavedAt = preferences
            .getLong(STASH_SAVED_AT_KEY, -1)
            .takeIf { it >= 0 } ?: return null

        val countdownSavedAt = preferences
            .getLong(COUNTDOWN_SAVED_AT_KET, -1)
            .takeIf { it >= 0 }

        return StashedTime(millis, stashSavedAt, countdownSavedAt)
    }

    companion object {
        private const val MILLIS_KEY = "millis"
        private const val STASH_SAVED_AT_KEY = "stash_saved_at"
        private const val COUNTDOWN_SAVED_AT_KET = "countdown_saved_at"
    }
}
