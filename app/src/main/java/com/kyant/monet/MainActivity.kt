package com.kyant.monet

import android.app.Activity
import android.content.Intent
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kyant.materialyou.component.BottomNavigationRail
import com.kyant.monet.color.MonetColors
import com.kyant.monet.color.monetColors
import com.kyant.monet.ui.screen.Generator
import com.kyant.monet.ui.screen.Palette
import com.kyant.monet.ui.screen.Settings
import com.kyant.monet.ui.theme.MonetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MainActivityDataModel {
    val imageUri: MutableState<Uri?> = mutableStateOf(null)
    var centroids by mutableStateOf<List<Color>>(emptyList())
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
    val color = Color("ff${text.text}".toLongOrNull(16) ?: 0xFFFFFFFF)
    var monetColors by remember { mutableStateOf(MonetColors()) }
    LaunchedEffect(text) {
        withContext(Dispatchers.IO) {
            monetColors = monetColors(color)
        }
    }

    val (a1, a2, a3, n1, n2) = monetColors
    val primaryColor = a1.getOrElse(5) { MaterialTheme.colors.primary }
    val secondaryColor = a3.getOrElse(5) { MaterialTheme.colors.secondary }
    val backgroundColor = n2.getOrElse(1) { MaterialTheme.colors.background }
    val surfaceColor = n2.getOrElse(0) { MaterialTheme.colors.surface }
    SideEffect {
        systemUiController.setSystemBarsColor(backgroundColor)
    }

    MonetTheme(
        lightColors(
            primary = primaryColor,
            secondary = secondaryColor,
            background = backgroundColor,
            surface = surfaceColor,
            onPrimary = if (MaterialTheme.colors.primary.luminance() <= 0.5f) Color.White else Color.Black,
            onSecondary = if (MaterialTheme.colors.secondary.luminance() <= 0.5f) Color.White else Color.Black,
            onBackground = if (MaterialTheme.colors.background.luminance() <= 0.5f) Color.White else Color.Black,
            onSurface = if (MaterialTheme.colors.surface.luminance() <= 0.5f) Color.White else Color.Black
        ),
        darkColors(
            primary = primaryColor,
            secondary = secondaryColor,
            background = backgroundColor,
            surface = surfaceColor,
            onPrimary = if (MaterialTheme.colors.primary.luminance() <= 0.5f) Color.White else Color.Black,
            onSecondary = if (MaterialTheme.colors.secondary.luminance() <= 0.5f) Color.White else Color.Black,
            onBackground = if (MaterialTheme.colors.background.luminance() <= 0.5f) Color.White else Color.Black,
            onSurface = if (MaterialTheme.colors.surface.luminance() <= 0.5f) Color.White else Color.Black
        )
    ) {
        Surface(
            Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Box {
                var screen by remember { mutableStateOf(0) }
                AnimatedContent(
                    screen,
                    Modifier.padding(bottom = 80.dp)
                ) { currentScreen ->
                    when (currentScreen) {
                        0 -> Palette(text, monetColors) { text = it }
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
                    Modifier.align(Alignment.BottomCenter),
                    selectedColor = a1.getOrElse(2) { MaterialTheme.colors.background }
                ) { screen = it }
            }
        }
    }
}