package com.kyant.materialyou.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationRail(
    items: Map<String, ImageVector>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    selectedColor: Color = MaterialTheme.colors.primary.copy(ContentAlpha.disabled),
    elevation: Dp = BottomNavigationDefaults.Elevation
) {
    var selectedItem by remember { mutableStateOf(0) }
    BottomNavigation(
        Modifier.height(80.dp) then modifier,
        backgroundColor,
        contentColor,
        elevation
    ) {
        items.entries.forEachIndexed { i, (label, icon) ->
            val selected = i == selectedItem
            BottomNavigationItem(
                selected,
                onClick = { selectedItem = i },
                icon = {
                    Icon(
                        icon,
                        label,
                        Modifier
                            .padding(bottom = 8.dp)
                            .background(
                                if (selected) selectedColor else Color.Transparent,
                                CircleShape
                            )
                            .padding(animateDpAsState(if (selected) 16.dp else 4.dp).value, 4.dp)
                            .height(24.dp)
                    )
                },
                modifier = Modifier.align(Alignment.CenterVertically),
                label = {
                    Text(
                        label,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            )
        }
    }
}