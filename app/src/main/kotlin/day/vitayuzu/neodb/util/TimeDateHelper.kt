package day.vitayuzu.neodb.util

import android.icu.text.RelativeDateTimeFormatter
import android.icu.text.RelativeDateTimeFormatter.Direction
import android.icu.text.RelativeDateTimeFormatter.RelativeUnit
import android.icu.util.ULocale
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.todayIn
import java.util.Locale
import kotlin.time.Clock

/**
 * Convert [LocalDate] to a localized relative string, like "3 days ago"/"3天前".
 * Dates of today or in the future fall back to ISO-8601.
 */
fun LocalDate.toReadableString(locale: Locale = Locale.getDefault()): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val period = this.periodUntil(today)
    val [amount, unit] = when {
        period.years >= 1 -> period.years to RelativeUnit.YEARS
        period.months >= 1 -> period.months to RelativeUnit.MONTHS
        period.days >= 7 -> period.days / 7 to RelativeUnit.WEEKS
        period.days >= 1 -> period.days to RelativeUnit.DAYS
        else -> return this.toString()
    }
    return RelativeDateTimeFormatter
        .getInstance(ULocale.forLocale(locale))
        .format(amount.toDouble(), Direction.LAST, unit)
}

@Composable
fun LocalDate.toLocalizedReadableString(): String {
    val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
    return toReadableString(locale)
}
