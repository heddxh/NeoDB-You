package day.vitayuzu.neodb.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDateTime

/**
 * Get date string from epoch milliseconds long number
 */
fun Long.toDateString() = Instant.fromEpochMilliseconds(this).toDateString()

fun Instant.toDateString() = this.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

/**
 * Convert [Instant] to a human-readable string,
 * like: 1d ago, 2m ago, 3y ago, etc.
 * WIP
 */
fun Instant.toReadableString(): String {
    val duration = this.periodUntil(Clock.System.now(), TimeZone.UTC)
    return if (duration.days <= 7) {
        "${duration.days}d ago"
    } else if (duration.months <= 1) {
        "${duration.months}m ago"
    } else if (duration.years <= 1) {
        "${duration.years}y ago"
    } else {
        this.toString()
    }
}
