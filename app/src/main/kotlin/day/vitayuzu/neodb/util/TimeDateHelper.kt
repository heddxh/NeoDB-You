package day.vitayuzu.neodb.util

import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.RelativeDateTimeFormatter.Direction
import android.icu.text.RelativeDateTimeFormatter.RelativeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Convert [LocalDate] to a localized relative string, like "3 days ago"/"3天前".
 * Dates of today or in the future fall back to ISO-8601.
 */
fun LocalDate.toReadableString(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val period = this.periodUntil(today)
    val [amount, unit] = when {
        period.years >= 1 -> period.years to RelativeUnit.YEARS
        period.months >= 1 -> period.months to RelativeUnit.MONTHS
        period.days >= 7 -> period.days / 7 to RelativeUnit.WEEKS
        period.days >= 1 -> period.days to RelativeUnit.DAYS
        else -> return this.toString()
    }
    return RelativeDateTimeFormatter.getInstance().format(amount.toDouble(), Direction.LAST, unit)
}
