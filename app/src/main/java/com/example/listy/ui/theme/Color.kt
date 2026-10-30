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

// Her yeni listeye sırayla verilen renkler
val ListColors = listOf(
    Color(0xFFC9506B), // pembe
    Color(0xFF4A78C9), // mavi
    Color(0xFF3E9C6E), // yeşil
    Color(0xFF8A63C9), // mor
    Color(0xFFD27C3A), // turuncu
    Color(0xFF2E9AA6), // turkuaz
)

fun listColor(index: Int): Color = ListColors[index.mod(ListColors.size)]
