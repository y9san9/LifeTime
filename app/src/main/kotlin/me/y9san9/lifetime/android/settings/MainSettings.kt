package me.y9san9.lifetime.android.settings

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import app.meetacy.di.DI
import app.meetacy.di.dependency.Dependency
import me.y9san9.lifetime.core.annotation.UnsafeConstructor
import me.y9san9.lifetime.core.type.Date
import me.y9san9.lifetime.core.type.StashedTime
import me.y9san9.lifetime.statistics.type.AppStats

val DI.settings: MainSettings by Dependency

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
            putLong(COUNTDOWN_SAVED_AT_KEY, time.countdownSavedAtMillis ?: -1)
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
            .getLong(COUNTDOWN_SAVED_AT_KEY, -1)
            .takeIf { it >= 0 }

        return StashedTime(millis, stashSavedAt, countdownSavedAt)
    }

    fun saveStats(appStats: AppStats) {
        preferences.edit {
            with(appStats.lastData.last) {
                putLong(STATS_LAST_MILLIS_KEY, millis)
                putLong(STATS_LAST_STASH_SAVED_AT_KEY, stashSavedAtMillis)
                putLong(STATS_LAST_COUNTDOWN_SAVED_AT_KEY, countdownSavedAtMillis ?: -1)
            }
            for ((i, dayMillis) in appStats.lastData.list.withIndex()) {
                putLong(statsDayKey(i), dayMillis)
            }
            with(appStats.maxStashed) {
                putLong(MAX_STASHED_MILLIS_KEY, millis)
                putString(MAX_STASHED_DATE_KEY, date.iso8601)
            }
            putString(INSTALLED_MILLIS_KEY, appStats.installedDate.iso8601)
        }
    }

    @OptIn(UnsafeConstructor::class)
    fun loadStats(): AppStats? {
        val millis = preferences
            .getLong(STATS_LAST_MILLIS_KEY, -1)
            .takeIf { it >= 0 } ?: return null

        val stashSavedAt = preferences
            .getLong(STATS_LAST_STASH_SAVED_AT_KEY, -1)
            .takeIf { it >= 0 } ?: return null

        val countdownSavedAt = preferences
            .getLong(STATS_LAST_COUNTDOWN_SAVED_AT_KEY, -1)
            .takeIf { it >= 0 }

        val last = StashedTime(millis, stashSavedAt, countdownSavedAt)

        val list = buildList {
            val indexes = generateSequence(seed = 0) { it + 1 }

            for (day in indexes) {
                val dayMillis = preferences
                    .getLong(statsDayKey(day), -1)
                    .takeIf { it >= 0 } ?: break
                add(dayMillis)
            }
        }

        val lastData = AppStats.LastData(list, last)

        val maxStashedMillis = preferences
            .getLong(MAX_STASHED_MILLIS_KEY, -1)
            .takeIf { it >= 0 } ?: return null

        val maxStashedDate = preferences
            .getString(MAX_STASHED_DATE_KEY, null)
            ?: return null

        val maxStashed = AppStats.Max(maxStashedMillis, Date(maxStashedDate))

        val installedMillis = preferences
            .getString(INSTALLED_MILLIS_KEY, null)
            ?.let(::Date)
            ?: return null

        return AppStats(lastData, maxStashed, installedMillis)
    }

    companion object {
        private const val MILLIS_KEY = "millis"
        private const val STASH_SAVED_AT_KEY = "stash_saved_at"
        private const val COUNTDOWN_SAVED_AT_KEY = "countdown_saved_at"

        // stats
        private const val STATS_LAST_MILLIS_KEY = "stats_last_millis"
        private const val STATS_LAST_STASH_SAVED_AT_KEY = "stats_last_stash_saved_at"
        private const val STATS_LAST_COUNTDOWN_SAVED_AT_KEY = "stats_last_countdown_saved_at"
        private const val STATS_DAY_X_KEY = "stats_day_"
        private const val MAX_STASHED_MILLIS_KEY = "max_stashed_millis"
        private const val MAX_STASHED_DATE_KEY = "max_stashed_date"
        private const val INSTALLED_MILLIS_KEY = "installed_millis"

        fun statsDayKey(x: Int) = STATS_DAY_X_KEY + x
    }
}
