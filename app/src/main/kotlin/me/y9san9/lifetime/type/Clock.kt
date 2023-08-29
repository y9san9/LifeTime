package me.y9san9.lifetime.type

import java.lang.System as nativeSystem

interface Clock {
    fun currentTimeMillis(): Long

    object System : Clock {
        override fun currentTimeMillis(): Long =
            nativeSystem.currentTimeMillis()
    }
}
