package com.kyant.monet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kyant.materialyou.component.BottomNavigationRail
import com.kyant.monet.ui.screen.Home
import com.kyant.monet.ui.screen.Settings
import com.kyant.monet.ui.theme.MonetTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Content() {
    CompositionLocalProvider(LocalMonetParameters provides MonetParameters()) {
        val systemUiController = rememberSystemUiController()
        val monetParameters = LocalMonetParameters.current

        var text by remember { mutableStateOf(TextFieldValue("ffffff")) }
        val color = Color("ff${text.text}".toLongOrNull(16) ?: 0xFFFFFFFF)
        var monetColors by remember { mutableStateOf(MonetColors()) }
        LaunchedEffect(text) {
            withContext(Dispatchers.IO) {
                monetColors = monetColors(color, monetParameters)
            }
        }

        val (a1, a2, a3, n1, n2) = monetColors
        val primaryColor = a1.getOrElse(4) { MaterialTheme.colors.primary }
        val secondaryColor = a3.getOrElse(3) { MaterialTheme.colors.primary }
        val backgroundColor = n2.getOrElse(0) { MaterialTheme.colors.background }
        SideEffect {
            systemUiController.setSystemBarsColor(backgroundColor)
        }

        MonetTheme(
            lightColors(
                primary = primaryColor,
                secondary = secondaryColor,
                background = backgroundColor
            ),
            darkColors(
                primary = primaryColor,
                secondary = secondaryColor,
                background = backgroundColor
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
                            0 -> Home(text, monetColors) { text = it }
                            1 -> Settings()
                        }
                    }
                    BottomNavigationRail(
                        mapOf(
                            "Home" to Icons.Outlined.Home,
                            "Settings" to Icons.Outlined.Settings
                        ),
                        selectedItem = screen,
                        Modifier.align(Alignment.BottomCenter),
                        selectedColor = a1.getOrElse(0) { MaterialTheme.colors.background }
                    ) { screen = it }
                }
            }
        }
    }
}