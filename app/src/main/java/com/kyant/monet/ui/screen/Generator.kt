package com.kyant.monet.ui.screen

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.kyant.materialyou.component.StretchScrollableColumn
import com.kyant.monet.MainActivityDataModel
import com.kyant.monet.color.LocalMonetColors
import com.kyant.monet.color.findCentroids
import com.kyant.monet.ui.util.contentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Generator(onPickButtonClick: () -> Unit) {
    val context = LocalContext.current
    StretchScrollableColumn(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(56.dp))
        Text(
            "Palette Generator",
            Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.h4
        )
        Spacer(Modifier.height(32.dp))

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
                        .clickable {
                            onPickButtonClick()
                        }
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(LocalContentColor provides LocalMonetColors.current.accent3[4].contentColor()) {
                        Icon(Icons.Outlined.Photo, "Photo")
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Select an image",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            }
        }

        MainActivityDataModel.imageUri.value?.let { uri ->
            AnimatedContent(uri) {
                Image(
                    rememberCoilPainter(it), null,
                    Modifier
                        .padding(32.dp)
                        .size(128.dp)
                )
            }

            LaunchedEffect(uri) {
                withContext(Dispatchers.IO) {
                    val colors = mutableListOf<Color>()
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(uri)
                        .allowHardware(false)
                        .build()
                    val result = (loader.execute(request) as SuccessResult).drawable
                    val bitmap = (result as BitmapDrawable).bitmap
                    val scaledBitmap = bitmap.scale(200, 200)
                    for (i in 0 until scaledBitmap.width) {
                        for (j in 0 until scaledBitmap.height) {
                            colors.add(Color(scaledBitmap[i, j]))
                        }
                    }
                    MainActivityDataModel.centroids = colors.findCentroids(8)
                }
            }
            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                mainAxisAlignment = MainAxisAlignment.Center
            ) {
                MainActivityDataModel.centroids.forEach { color ->
                    AnimatedContent(color) {
                        ColorButton(it)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}