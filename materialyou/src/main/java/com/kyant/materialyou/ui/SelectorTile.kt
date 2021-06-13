package com.kyant.materialyou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyant.monet.contentColor

@Composable
fun SelectorTile(
    text: String,
    icon: ImageVector,
    tint: Color,
    label: String,
    labelColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon, text,
                Modifier
                    .background(tint, CircleShape)
                    .padding(16.dp),
                tint = tint.contentColor()
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.h6
            )
        }
        Text(
            label,
            Modifier
                .background(labelColor, CircleShape)
                .padding(16.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.body2
        )
    }
}