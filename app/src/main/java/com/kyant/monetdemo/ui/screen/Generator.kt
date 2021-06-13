package com.kyant.monetdemo.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.kyant.materialyou.ui.StretchScrollableColumn
import com.kyant.monet.LocalMonetColors
import com.kyant.monet.contentColor
import com.kyant.monetdemo.MainActivityDataModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Generator(onPickButtonClick: () -> Unit) {
    StretchScrollableColumn(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(112.dp))
        Text(
            "Palette Generator",
            Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.h4
        )
        Spacer(Modifier.height(48.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(MainActivityDataModel.imageUri.value) {
                Image(
                    rememberCoilPainter(it), null,
                    Modifier
                        .padding(16.dp, 32.dp)
                        .width(128.dp)
                        .border(2.dp, LocalMonetColors.current.accent1[5])
                )
            }
            Column {
                Card(
                    Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = LocalMonetColors.current.accent3[4],
                    contentColor = LocalMonetColors.current.accent3[4].contentColor(),
                    elevation = 0.dp
                ) {
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable { onPickButtonClick() },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Photo, null,
                                tint = LocalMonetColors.current.accent3[0]
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "Select",
                                color = LocalMonetColors.current.accent3[0],
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                Card(
                    Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = LocalMonetColors.current.accent1[5],
                    contentColor = LocalMonetColors.current.accent1[5].contentColor(),
                    elevation = 0.dp
                ) {
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable { },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.BubbleChart, null,
                                tint = LocalMonetColors.current.accent1[0]
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "k = ${MainActivityDataModel.k}",
                                color = LocalMonetColors.current.accent1[0],
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
            }
        }

        Text(
            "RGB",
            Modifier.padding(16.dp, 32.dp),
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.h5
        )
        FlowRow(
            Modifier.fillMaxWidth(),
            mainAxisAlignment = MainAxisAlignment.Center
        ) {
            MainActivityDataModel.rgbCentroids.forEach { color ->
                AnimatedContent(color) {
                    ColorCircle(it)
                }
            }
        }

        Text(
            "CAM16",
            Modifier.padding(16.dp, 32.dp),
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.h5
        )
        FlowRow(
            Modifier.fillMaxWidth(),
            mainAxisAlignment = MainAxisAlignment.Center
        ) {
            MainActivityDataModel.cam16Centroids.forEach { color ->
                AnimatedContent(color) {
                    ColorCircle(it)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}