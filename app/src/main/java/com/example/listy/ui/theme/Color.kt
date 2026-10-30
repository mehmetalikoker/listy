package com.example.listy.ui.theme

import androidx.compose.ui.graphics.Color

// To Do benzeri koyu tema
val AppBackground = Color(0xFF0A0A0A)
val CardSurface = Color(0xFF252525)
val TextPrimary = Color(0xFFE8E8E8)
val TextSecondary = Color(0xFF9E9E9E)
val Divider = Color(0xFF2A2A2A)
val Accent = Color(0xFF7B8CDE)
val EditYellow = Color(0xFFF2C94C)
val DeleteRed = Color(0xFFE5534B)

data class ListTheme(val name: String, val color: Color)

// Liste temaları: yeni listeye sırayla verilir, liste içinden değiştirilebilir.
// Kayıtlı listeler index tuttuğu için yeni renkler hep sona eklenmeli.
val ListThemes = listOf(
    ListTheme("Pembe", Color(0xFFC9506B)),
    ListTheme("Mavi", Color(0xFF4A78C9)),
    ListTheme("Yeşil", Color(0xFF3E9C6E)),
    ListTheme("Mor", Color(0xFF8A63C9)),
    ListTheme("Turuncu", Color(0xFFD27C3A)),
    ListTheme("Turkuaz", Color(0xFF2E9AA6)),
    ListTheme("Kırmızı", Color(0xFFC62828)),
    ListTheme("Sarı", Color(0xFFB8901C)),
    ListTheme("Lacivert", Color(0xFF2C3E8F)),
    ListTheme("Kahverengi", Color(0xFF795548)),
    ListTheme("Açık yeşil", Color(0xFF6E9A2E)),
    ListTheme("Gri", Color(0xFF5F6B7A)),
)

fun listColor(index: Int): Color = ListThemes[index.mod(ListThemes.size)].color
