package com.kyant.materialyou.ui

import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kyant.monet.LocalMonetColors
import com.kyant.monet.contentColor

@Composable
fun BottomNavigationRail(
    items: Map<String, ImageVector>,
    selectedItem: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        LaunchedEffect(selectedItem) {
            (context.getSystemService(VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        }
    }
    val backgroundColor = LocalMonetColors.current.accent1[0]
    Card(
        modifier.height(80.dp),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = backgroundColor,
        contentColor = backgroundColor.contentColor()
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
                                animateColorAsState(if (selected) LocalMonetColors.current.accent1[2] else backgroundColor).value,
                                CircleShape
                            )
                            .padding(animateDpAsState(if (selected) 16.dp else 4.dp).value, 4.dp)
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