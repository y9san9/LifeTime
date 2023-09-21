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
import me.y9san9.lifetime.statistics.type.upgradeFromDaysToHours

val DI.settings: MainSettings by Dependency

class MainSettings(
    application: Application
) {
    private val preferences = application.getSharedPreferences(
        /* name = */ "main-settings",
        /* mode = */ Context.MODE_PRIVATE
    )

    @Suppress("DEPRECATION")
    fun init() {
        if (preferences.getLong(statsDayKey(0), -1) != -1L) {
            val stats = loadStatsOld()
            if (stats != null) {
                saveStats(stats.upgradeFromDaysToHours())
            }
            saveVersion(0)
            clearStatsOld()
        }
    }

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
            for ((i, hourMillis) in appStats.lastData.list.withIndex()) {
                putLong(statsHourKey(i), hourMillis)
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
                    .getLong(statsHourKey(day), -1)
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

    fun loadVersion(): Int = preferences.getInt(VERSION_KEY, LAST_VERSION)
    fun saveVersion(version: Int) = preferences.edit {
        putInt(VERSION_KEY, version)
    }

    @Suppress("DEPRECATION")
    @Deprecated(message = "loadStats() should be used instead")
    @OptIn(UnsafeConstructor::class)
    fun loadStatsOld(): AppStats? {
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

    @Suppress("DEPRECATION")
    @Deprecated(
        message = "May be used only for migration"
    )
    private fun clearStatsOld() {
        val indexes = generateSequence(seed = 0) { it + 1 }

        for (day in indexes) {
            preferences
                .getLong(statsDayKey(day), -1)
                .takeIf { it >= 0 } ?: break
            preferences.edit {
                remove(statsDayKey(day))
            }
        }
    }

    companion object {
        private const val MILLIS_KEY = "millis"
        private const val STASH_SAVED_AT_KEY = "stash_saved_at"
        private const val COUNTDOWN_SAVED_AT_KEY = "countdown_saved_at"

        // stats
        private const val STATS_LAST_MILLIS_KEY = "stats_last_millis"
        private const val STATS_LAST_STASH_SAVED_AT_KEY = "stats_last_stash_saved_at"
        private const val STATS_LAST_COUNTDOWN_SAVED_AT_KEY = "stats_last_countdown_saved_at"
        @Deprecated(message = "STATS_HOUR_X_KEY should be used instead")
        private const val STATS_DAY_X_KEY = "stats_day_"
        private const val STATS_HOUR_X_KEY = "stats_hour_"
        private const val MAX_STASHED_MILLIS_KEY = "max_stashed_millis"
        private const val MAX_STASHED_DATE_KEY = "max_stashed_date"
        private const val INSTALLED_MILLIS_KEY = "installed_millis"

        @Suppress("DEPRECATION")
        @Deprecated(message = "statsHourKey() should be used instead")
        fun statsDayKey(x: Int) = STATS_DAY_X_KEY + x

        fun statsHourKey(x: Int) = STATS_HOUR_X_KEY + x

        // versioning
        private const val VERSION_KEY = "version"
        private const val LAST_VERSION = 0
    }
}
