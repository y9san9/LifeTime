@file:OptIn(UnsafeConstructor::class)

package me.y9san9.lifetime.core.type

import me.y9san9.lifetime.core.annotation.UnsafeConstructor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@JvmInline
value class Date @UnsafeConstructor constructor(val iso8601: String) {
    companion object {
        internal const val MILLIS_PER_DAY = 24 * 60 * 60 * 1_000

        fun now(zone: ZoneId): Date = ofEpochMillis(System.currentTimeMillis(), zone)
        fun ofEpochDay(day: Long) = LocalDate.ofEpochDay(day).toString().let(::Date)
        fun ofEpochMillis(
            millis: Long,
            zone: ZoneId
        ): Date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate().toString().let(::Date)

        fun parse(iso8601: String): Date {
            LocalDate.parse(iso8601)
            return Date(iso8601)
        }
    }
}

val Date.epochMillis: Long
    get() = epochDay * Date.MILLIS_PER_DAY

val Date.epochDay: Long
    get() = localDate.toEpochDay()

val Date.year: Int
    get() = localDate.year

val Date.localDate: LocalDate get() = LocalDate.parse(iso8601)
val LocalDate.domainDate: Date get() = Date(toString())

fun Date.toInstant(zone: ZoneId): Instant = localDate.atStartOfDay(zone).toInstant()

val Date.yesterday: Date get() = localDate.minusDays(1).domainDate
val Date.tomorrow: Date get() = localDate.plusDays(1).domainDate

operator fun Date.compareTo(other: Date): Int {
    return epochDay.compareTo(other.epochDay)
}

fun Date.format(pattern: String) = localDate.format(DateTimeFormatter.ofPattern(pattern))
