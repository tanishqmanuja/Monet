package com.kyant.monetdemo.color

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kyant.monet.ColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val LocalMonetColors = compositionLocalOf { monetColorsOf(Color(0xFF1B6EF3), false) }

@Composable
fun ProvideMonetColors(
    color: Color,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    var monetColors by remember { mutableStateOf(monetColorsOf(Color(0xFF1B6EF3), darkTheme)) }
    LaunchedEffect(color, darkTheme) {
        withContext(Dispatchers.IO) {
            monetColors = monetColorsOf(color, darkTheme)
        }
    }
    CompositionLocalProvider(LocalMonetColors provides monetColors) {
        content()
    }
}

data class MonetColors(
    val accent1: List<Color>,
    val accent2: List<Color>,
    val accent3: List<Color>,
    val neutral1: List<Color>,
    val neutral2: List<Color>
)

fun monetColorsOf(color: Color, darkTheme: Boolean = false): MonetColors {
    val scheme = ColorScheme(color.toArgb(), darkTheme)
    return MonetColors(
        scheme.accent1.map { Color(it) },
        scheme.accent2.map { Color(it) },
        scheme.accent3.map { Color(it) },
        scheme.neutral1.map { Color(it) },
        scheme.neutral2.map { Color(it) }
    )
}