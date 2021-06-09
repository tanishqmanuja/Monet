package com.kyant.monet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.kyant.monet.R

@Composable
fun MonetTheme(
    lightkColors: Colors = lightColors(),
    darkColors: Colors = darkColors(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightkColors
    val typography = Typography(
        defaultFontFamily = FontFamily(
            Font(
                resId = R.font.google_sans_text_regular,
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            ),
            Font(
                resId = R.font.google_sans_text_medium,
                weight = FontWeight.Medium,
                style = FontStyle.Normal
            ),
            Font(
                resId = R.font.google_sans_text_bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        )
    )
    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )
}