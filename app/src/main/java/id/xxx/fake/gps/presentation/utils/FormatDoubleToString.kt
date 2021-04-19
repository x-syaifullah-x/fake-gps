package id.xxx.fake.gps.presentation.utils

import java.util.*

private val formatDouble: (Double, Locale, String) -> String =
    { double, locale, format -> String.format(locale, format, double) }

fun formatDouble(double: Double, format: String = "%.3f"): String {
    return String.format(Locale.getDefault(), format, double)
}