package com.example.listy.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class ListType { TODO, PAYMENT, REMINDER }

data class TaskList(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val colorIndex: Int = 0,
    val type: ListType = ListType.TODO,
)

data class Task(
    val id: Long,
    val listId: Long,
    val title: String,
    val createdAt: Long,
    val done: Boolean = false,
    val important: Boolean = false,
    /** Ödeme listelerinde tutar, kuruş cinsinden. Yapılacaklar listesinde null. */
    val amount: Long? = null,
    /**
     * Günlük ödeme: [amount] dönemlik toplamdır ve her gün eşit parça kendiliğinden düşer.
     * Dönem, ne zaman eklenirse eklensin, ayın son günü başlar ve sonraki ayın son günü biter
     * (bkz. [DailyPeriod]). Elle tamamlanmaz; her ay sonunda tutar yeniden dolar.
     */
    val daily: Boolean = false,
    /** Hatırlatıcı listelerinde görevin tarihi. Diğer listelerde null. */
    val date: LocalDate? = null,
)

/**
 * Ay sonundan ay sonuna dönem. Örn. 30 Ekim'de dönem 30 Eylül – 31 Ekim (31 gün),
 * 31 Ekim'de ise yeni dönem 31 Ekim – 30 Kasım (30 gün) başlar.
 */
data class DailyPeriod(val start: LocalDate, val end: LocalDate, val today: LocalDate) {
    val totalDays: Long = ChronoUnit.DAYS.between(start, end)
    val daysPassed: Long = ChronoUnit.DAYS.between(start, today)
    val daysLeft: Long = totalDays - daysPassed

    companion object {
        fun of(today: LocalDate): DailyPeriod {
            val start = if (today == today.endOfMonth()) today else today.minusMonths(1).endOfMonth()
            return DailyPeriod(start, start.plusMonths(1).endOfMonth(), today)
        }

        private fun LocalDate.endOfMonth(): LocalDate = withDayOfMonth(lengthOfMonth())
    }
}

/** Dönemin gün sayısına bölünmüş günlük pay (kuruş). */
fun Task.dailyShare(today: LocalDate = LocalDate.now()): Long =
    (amount ?: 0) / DailyPeriod.of(today).totalDays

/** Ödenmiş sayılan kısım. Günlükte dönem başından bu yana geçen günlerin payı, diğerlerinde tamamlandıysa tamamı. */
fun Task.paidAmount(today: LocalDate = LocalDate.now()): Long {
    val total = amount ?: return 0
    return when {
        daily -> DailyPeriod.of(today).let { total * it.daysPassed / it.totalDays }
        done -> total
        else -> 0
    }
}

fun Task.remainingAmount(today: LocalDate = LocalDate.now()): Long = (amount ?: 0) - paidAmount(today)

/**
 * Günlük ödeme (her ay sonu yeniden dolar) ve hatırlatıcı hiç tamamlanmaz;
 * diğerleri elle işaretlenir.
 */
fun Task.isCompleted(today: LocalDate = LocalDate.now()): Boolean =
    if (daily || date != null) false else done
