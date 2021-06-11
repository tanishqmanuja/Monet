package com.kyant.monet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.insets.ProvideWindowInsets
import com.kyant.monet.R
import com.kyant.monet.color.ProvideLocalMonetColors

@Composable
fun MonetTheme(
    color: Color = Color(0xFF1B6EF3),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
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
        typography = typography,
        content = {
            ProvideLocalMonetColors(color, darkTheme) {
                ProvideWindowInsets {
                    content()
                }
            }
        }
    )
}