package com.example.listy.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Görünüme (koyu/açık) göre değişen renkler. */
data class ListyColors(
    val background: Color,
    val cardSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
)

// To Do benzeri koyu tema
val DarkColors = ListyColors(
    background = Color(0xFF0A0A0A),
    cardSurface = Color(0xFF252525),
    textPrimary = Color(0xFFE8E8E8),
    textSecondary = Color(0xFF9E9E9E),
    divider = Color(0xFF2A2A2A),
)

// Beyaz arka planlı açık tema
val LightColors = ListyColors(
    background = Color(0xFFFFFFFF),
    cardSurface = Color(0xFFF2F2F5),
    textPrimary = Color(0xFF1C1C1E),
    textSecondary = Color(0xFF6B6B72),
    divider = Color(0xFFE2E2E6),
)

val LocalListyColors = staticCompositionLocalOf { DarkColors }

val AppBackground: Color
    @Composable @ReadOnlyComposable get() = LocalListyColors.current.background
val CardSurface: Color
    @Composable @ReadOnlyComposable get() = LocalListyColors.current.cardSurface
val TextPrimary: Color
    @Composable @ReadOnlyComposable get() = LocalListyColors.current.textPrimary
val TextSecondary: Color
    @Composable @ReadOnlyComposable get() = LocalListyColors.current.textSecondary
val Divider: Color
    @Composable @ReadOnlyComposable get() = LocalListyColors.current.divider
val Accent = Color(0xFF7B8CDE)
val EditYellow = Color(0xFFF2C94C)
val DeleteRed = Color(0xFFE5534B)

// fab: liste içindeki artı butonunun rengi; arka plandan ayrışsın diye zıt tonda
data class ListTheme(val name: String, val color: Color, val fab: Color)

// Liste temaları: yeni listeye sırayla verilir, liste içinden değiştirilebilir.
// Kayıtlı listeler index tuttuğu için yeni renkler hep sona eklenmeli.
val ListThemes = listOf(
    ListTheme("Pembe", Color(0xFFC9506B), Color(0xFF26A69A)),
    ListTheme("Mavi", Color(0xFF4A78C9), Color(0xFFFFB300)),
    ListTheme("Yeşil", Color(0xFF3E9C6E), Color(0xFFEC407A)),
    ListTheme("Mor", Color(0xFF8A63C9), Color(0xFFFFCA28)),
    ListTheme("Turuncu", Color(0xFFD27C3A), Color(0xFF1E88E5)),
    ListTheme("Turkuaz", Color(0xFF2E9AA6), Color(0xFFFF7043)),
    ListTheme("Kırmızı", Color(0xFFC62828), Color(0xFF26C6DA)),
    ListTheme("Sarı", Color(0xFFB8901C), Color(0xFF5E35B1)),
    ListTheme("Lacivert", Color(0xFF2C3E8F), Color(0xFFFFB300)),
    ListTheme("Kahverengi", Color(0xFF795548), Color(0xFF4FC3F7)),
    ListTheme("Açık yeşil", Color(0xFF6E9A2E), Color(0xFF8E24AA)),
    ListTheme("Gri", Color(0xFF5F6B7A), Color(0xFFFF8A65)),
)

fun listColor(index: Int): Color = ListThemes[index.mod(ListThemes.size)].color

fun listFabColor(index: Int): Color = ListThemes[index.mod(ListThemes.size)].fab
