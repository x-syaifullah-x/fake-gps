package id.xxx.fake.gps.history.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

object FormatDate {
    val formatHourAndDateNowWithLocale: (Locale) -> String = { locale ->
        SimpleDateFormat("HH:mm:ss  EEE, dd MMM yyyy", locale).format(System.currentTimeMillis())
    }

    val formatHourAndDateNowWithPatternAndLocale: (pattern: String, Locale) -> String =
        { pattern, locale ->
            SimpleDateFormat(pattern, locale).format(System.currentTimeMillis())
        }

    fun formatHourAndDateNow(
        pattern: String = "HH:mm:ss  EEE, dd MMM yyyy",
        locale: Locale = Locale.getDefault()
    ): String {
        return SimpleDateFormat(pattern, locale).format(System.currentTimeMillis())
    }

    fun formatDate(currentTimeMillis: Long): String? {
        return SimpleDateFormat("HH:mm:ss  EEE, dd MMM yyyy", Locale.getDefault())
            .format(currentTimeMillis)
    }
}