package com.kyant.monetdemo.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.materialyou.component.Selector
import com.kyant.materialyou.component.StretchScrollableColumn
import com.kyant.monetdemo.color.LocalMonetColors

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
        Spacer(Modifier.height(56.dp))
        Selector(
            "monet impl.",
            Icons.Outlined.Landscape,
            LocalMonetColors.current.accent1[2],
            "A12 Beta 2",
            LocalMonetColors.current.accent3[2]
        ) { }
        Selector(
            "Precision level",
            Icons.Outlined.Stars,
            LocalMonetColors.current.accent1[2],
            "Common",
            LocalMonetColors.current.accent3[1]
        ) { }
        Selector(
            "Clustering",
            Icons.Outlined.BubbleChart,
            LocalMonetColors.current.accent1[2],
            "KMeans (Smile ML)",
            LocalMonetColors.current.accent3[2]
        ) { }
        Spacer(Modifier.height(24.dp))
    }
}