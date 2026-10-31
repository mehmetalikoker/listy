package com.example.listy.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Hatırlatıcı tarihlerinin gösterimi; yıl yazılmaz ("23 Eylül"). */
object DateInput {

    private val dayMonth = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("tr"))

    /** "23 Eylül". */
    fun format(date: LocalDate): String = date.format(dayMonth)

    /** Kartta gösterim: "Bugün", "Yarın", "Dün" ya da "23 Eylül". */
    fun formatRelative(date: LocalDate, today: LocalDate = LocalDate.now()): String =
        when (ChronoUnit.DAYS.between(today, date)) {
            0L -> "Bugün"
            1L -> "Yarın"
            -1L -> "Dün"
            else -> format(date)
        }
}
