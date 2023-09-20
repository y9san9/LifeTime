package me.y9san9.lifetime.core.type

import java.time.ZoneId
import java.lang.System as nativeSystem

interface Clock {
    fun currentTimeMillis(): Long

    object System : Clock {
        override fun currentTimeMillis(): Long =
            nativeSystem.currentTimeMillis()
    }
}

fun Clock.currentDate(zoneId: ZoneId): Date = Date.ofEpochMillis(currentTimeMillis(), zoneId)
