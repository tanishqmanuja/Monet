package com.kyant.monetdemo

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.get
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kyant.materialyou.ui.BottomNavigationRail
import com.kyant.monet.LocalMonetColors
import com.kyant.monet.cam16Centroids
import com.kyant.monet.rgbCentroids
import com.kyant.monetdemo.ui.screen.Generator
import com.kyant.monetdemo.ui.screen.Palette
import com.kyant.monetdemo.ui.screen.Settings
import com.kyant.monetdemo.ui.theme.MonetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MainActivityDataModel {
    val imageUri: MutableState<Uri?> = mutableStateOf(null)
    var k by mutableStateOf(3)
    var rgbCentroids by mutableStateOf<List<Color>>(emptyList())
    var cam16Centroids by mutableStateOf<List<Color>>(emptyList())
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startForImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val resultCode = it.resultCode
                val data = it.data
                if (resultCode == Activity.RESULT_OK) {
                    MainActivityDataModel.imageUri.value = data?.data
                }
            }

        setContent {
            with(MainActivityDataModel) {
                imageUri.value?.let {
                    LaunchedEffect(it, k) {
                        withContext(Dispatchers.IO) {
                            rgbCentroids = emptyList()
                            cam16Centroids = emptyList()
                            val colors = mutableListOf<Color>()
                            val loader = ImageLoader(this@MainActivity)
                            val request = ImageRequest.Builder(this@MainActivity)
                                .data(it)
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
                            CoroutineScope(Dispatchers.IO).launch {
                                rgbCentroids = colors.rgbCentroids(k)
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                cam16Centroids = colors.cam16Centroids(k)
                            }
                        }
                    }
                }
            }
            Content(startForImageResult)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Content(startForImageResult: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current as MainActivity
    val systemUiController = rememberSystemUiController()

    var text by remember { mutableStateOf(TextFieldValue("")) }
    val color = Color("ff${text.text}".toLongOrNull(16) ?: 0xFF1B6EF3)
    MonetTheme(color) {
        val colors = LocalMonetColors.current
        SideEffect {
            systemUiController.setSystemBarsColor(colors.neutral1[1])
            systemUiController.setNavigationBarColor(colors.accent1[0])
        }

        Surface(
            Modifier.fillMaxSize(),
            color = colors.neutral1[1]
        ) {
            Box {
                var screen by remember { mutableStateOf(0) }
                AnimatedContent(
                    screen,
                    Modifier.padding(bottom = 80.dp)
                ) { currentScreen ->
                    when (currentScreen) {
                        0 -> Palette(text, colors) { text = it }
                        1 -> Generator {
                            CoroutineScope(Dispatchers.IO).launch {
                                ImagePicker
                                    .with(context)
                                    .galleryOnly()
                                    .createIntent {
                                        startForImageResult.launch(it)
                                    }
                            }
                        }
                        2 -> Settings()
                    }
                }
                BottomNavigationRail(
                    mapOf(
                        "Palette" to Icons.Outlined.Palette,
                        "Generator" to Icons.Outlined.Brush,
                        "Settings" to Icons.Outlined.Settings
                    ),
                    selectedItem = screen,
                    Modifier.align(Alignment.BottomCenter)
                ) { screen = it }
            }
        }
    }
}