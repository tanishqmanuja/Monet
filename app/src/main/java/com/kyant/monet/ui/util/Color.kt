package com.kyant.monet.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun Color.contentColor(): Color = if (this.luminance() <= 0.5f) Color.White else Color.Black