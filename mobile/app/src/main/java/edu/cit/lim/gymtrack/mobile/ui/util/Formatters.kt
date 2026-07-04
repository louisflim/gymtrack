package edu.cit.lim.gymtrack.mobile.ui.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    return runCatching {
        LocalDateTime.parse(value).format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
    }.getOrDefault(value)
}

fun formatCurrency(amount: BigDecimal?): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
    return formatter.format(amount ?: BigDecimal.ZERO)
}
