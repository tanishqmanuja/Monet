package com.kyant.monet.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.materialyou.component.StretchScrollableColumn

@Composable
fun Settings() {
    StretchScrollableColumn(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(56.dp))
        Text(
            "Settings",
            Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.h4
        )
    }
}