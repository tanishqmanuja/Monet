package com.kyant.materialyou.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationRail(
    items: Map<String, ImageVector>,
    selectedItem: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    selectedColor: Color = MaterialTheme.colors.primary.copy(ContentAlpha.disabled),
    elevation: Dp = BottomNavigationDefaults.Elevation,
    onClick: (Int) -> Unit
) {
    Card(
        modifier.height(80.dp),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            items.entries.forEachIndexed { i, (label, icon) ->
                val selected = i == selectedItem
                Column(
                    Modifier
                        .width(128.dp)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable { (onClick(i)) },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))
                    Icon(
                        icon,
                        label,
                        Modifier
                            .background(
                                if (selected) selectedColor else Color.Transparent,
                                CircleShape
                            )
                            .padding(
                                animateDpAsState(if (selected) 16.dp else 4.dp).value,
                                4.dp
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        label,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }
        }
    }
}