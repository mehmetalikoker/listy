package com.example.listy.data

/**
 * Tutarlar kuruş cinsinden Long olarak tutulur; toplamada küsurat hatası olmaz.
 * Türkçe yazımı esas alır: "60.000" = altmış bin, "1.250,50" = bin iki yüz elli lira elli kuruş.
 */
object Money {

    /** Kullanıcının yazdığı tutarı kuruşa çevirir. Geçersizse null. */
    fun parse(input: String): Long? {
        val s = input.replace(" ", "").replace("TL", "", ignoreCase = true).replace("₺", "")
        if (s.isEmpty()) return null

        val normalized = when {
            // Hem nokta hem virgül varsa: nokta binlik, virgül ondalık
            ',' in s -> s.replace(".", "").replace(',', '.')
            // Sadece nokta varsa ve son noktadan sonra tam 3 hane varsa binlik ayırıcıdır
            '.' in s && s.substringAfterLast('.').length == 3 -> s.replace(".", "")
            else -> s
        }

        val value = normalized.toBigDecimalOrNull() ?: return null
        if (value.signum() < 0 || value.scale() > 2) return null
        return value.movePointRight(2).toLong()
    }

    /** Kuruşu "60.000 TL" ya da "1.250,50 TL" şeklinde yazar. */
    fun format(kurus: Long): String {
        val lira = kurus / 100
        val rest = kurus % 100
        val grouped = lira.toString().reversed().chunked(3).joinToString(".").reversed()
        return if (rest == 0L) "$grouped TL" else "$grouped,${rest.toString().padStart(2, '0')} TL"
    }
}
