package com.kyant.materialyou.component

import android.os.Build
import android.widget.ScrollView
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun StretchScrollableColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AndroidView(
            {
                ScrollView(it).apply {
                    addView(ComposeView(it).apply {
                        setContent {
                            Column(modifier, verticalArrangement, horizontalAlignment, content)
                        }
                    })
                }
            }, modifier
        )
    } else {
        Column(
            Modifier.verticalScroll(rememberScrollState()) then modifier,
            verticalArrangement, horizontalAlignment, content
        )
    }
}

@Composable
fun StretchScrollableRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AndroidView(
            {
                ScrollView(it).apply {
                    addView(ComposeView(it).apply {
                        setContent {
                            Row(modifier, horizontalArrangement, verticalAlignment, content)
                        }
                    })
                }
            }, modifier
        )
    } else {
        Row(
            Modifier.horizontalScroll(rememberScrollState()) then modifier,
            horizontalArrangement, verticalAlignment, content
        )
    }
}