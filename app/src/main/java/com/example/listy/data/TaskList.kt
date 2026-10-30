package com.example.listy.data

import java.time.LocalDate

enum class ListType { TODO, PAYMENT }

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
     * Günlük ödeme: [amount] aylık toplamdır ve ay boyunca her gün eşit parça kendiliğinden düşer.
     * Elle tamamlanmaz; her ayın 1'inde tekrar baştan başlar.
     */
    val daily: Boolean = false,
)

/** Ayın gün sayısına bölünmüş günlük pay (kuruş). */
fun Task.dailyShare(today: LocalDate = LocalDate.now()): Long =
    (amount ?: 0) / today.lengthOfMonth()

/** Ödenmiş sayılan kısım. Günlükte bugün dahil geçen günlerin payı, diğerlerinde tamamlandıysa tamamı. */
fun Task.paidAmount(today: LocalDate = LocalDate.now()): Long {
    val total = amount ?: return 0
    return when {
        daily -> total * today.dayOfMonth / today.lengthOfMonth()
        done -> total
        else -> 0
    }
}

fun Task.remainingAmount(today: LocalDate = LocalDate.now()): Long = (amount ?: 0) - paidAmount(today)

/** Günlük ödeme ayın son günü tamamlanmış sayılır; diğerleri elle işaretlenir. */
fun Task.isCompleted(today: LocalDate = LocalDate.now()): Boolean =
    if (daily) remainingAmount(today) == 0L else done
