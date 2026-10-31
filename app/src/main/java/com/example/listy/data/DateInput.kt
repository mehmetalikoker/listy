package com.example.listy.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Hatırlatıcı tarihleri: Türkçe gün.ay.yıl yazımı. */
object DateInput {

    private val longFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("tr"))

    /**
     * "31.10.2026", "31/10/2026", "31-10-26" ya da yıl yazılmazsa bu yıl ("31.10").
     * Geçersizse null.
     */
    fun parse(input: String, today: LocalDate = LocalDate.now()): LocalDate? {
        val parts = input.trim().split('.', '/', '-', ' ').filter { it.isNotEmpty() }
        if (parts.size !in 2..3) return null
        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val year = when {
            parts.size == 2 -> today.year
            parts[2].length == 2 -> 2000 + (parts[2].toIntOrNull() ?: return null)
            else -> parts[2].toIntOrNull() ?: return null
        }
        return runCatching { LocalDate.of(year, month, day) }.getOrNull()
    }

    /** Alana geri yazmak için "31.10.2026". */
    fun formatShort(date: LocalDate): String =
        "%02d.%02d.%d".format(date.dayOfMonth, date.monthValue, date.year)

    /** Kartta gösterim: "Bugün", "Yarın", "Dün" ya da "31 Ekim 2026". */
    fun formatLong(date: LocalDate, today: LocalDate = LocalDate.now()): String =
        when (ChronoUnit.DAYS.between(today, date)) {
            0L -> "Bugün"
            1L -> "Yarın"
            -1L -> "Dün"
            else -> date.format(longFormat)
        }
}
