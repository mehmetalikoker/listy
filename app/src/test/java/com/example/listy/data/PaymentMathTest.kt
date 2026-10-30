package com.example.listy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class PaymentMathTest {

    private val ev = Task(id = 1, listId = 1, title = "Ev", createdAt = 0, amount = 6_000_000, daily = true)

    @Test
    fun `dönem önceki ayın son gününden bu ayın son gününe`() {
        val p = DailyPeriod.of(LocalDate.of(2026, 10, 30))
        assertEquals(LocalDate.of(2026, 9, 30), p.start)
        assertEquals(LocalDate.of(2026, 10, 31), p.end)
        assertEquals(31, p.totalDays)
        assertEquals(30, p.daysPassed)
        assertEquals(1, p.daysLeft)
    }

    @Test
    fun `ayın son günü yeni dönem başlar ve tutar dolar`() {
        val today = LocalDate.of(2026, 10, 31)
        val p = DailyPeriod.of(today)
        assertEquals(LocalDate.of(2026, 11, 30), p.end)
        assertEquals(30, p.totalDays)
        assertEquals(6_000_000, ev.remainingAmount(today))
    }

    @Test
    fun `şubat geçişleri`() {
        // 31 Ocak – 28 Şubat: 28 gün
        assertEquals(28, DailyPeriod.of(LocalDate.of(2026, 1, 31)).totalDays)
        // 28 Şubat – 31 Mart: 31 gün
        assertEquals(31, DailyPeriod.of(LocalDate.of(2026, 2, 28)).totalDays)
        // Artık yıl: 29 Şubat 2028 ay sonudur
        assertEquals(LocalDate.of(2028, 2, 29), DailyPeriod.of(LocalDate.of(2028, 2, 29)).start)
    }

    @Test
    fun `günlük ödeme her gün eşit azalır`() {
        val nov15 = LocalDate.of(2026, 11, 15) // 31 Ekim'den bu yana 15 gün
        assertEquals(200_000, ev.dailyShare(nov15))
        assertEquals(3_000_000, ev.paidAmount(nov15))
        assertEquals(3_000_000, ev.remainingAmount(nov15))
    }

    @Test
    fun `normal ödeme sadece tamamlanınca düşer`() {
        val kira = Task(id = 2, listId = 1, title = "Kira", createdAt = 0, amount = 1_500_000)
        val today = LocalDate.of(2026, 10, 30)
        assertEquals(1_500_000, kira.remainingAmount(today))
        assertEquals(0, kira.copy(done = true).remainingAmount(today))
    }

    @Test
    fun `tutar okuma ve yazma`() {
        assertEquals(6_000_000L, Money.parse("60.000"))
        assertEquals(125_050L, Money.parse("1.250,50"))
        assertEquals(45_000L, Money.parse("450"))
        assertEquals(1_250L, Money.parse("12.5"))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("1,234"))
        assertEquals("1.250,50 TL", Money.format(125_050))
        assertEquals("60.000 TL", Money.format(6_000_000))
    }
}
