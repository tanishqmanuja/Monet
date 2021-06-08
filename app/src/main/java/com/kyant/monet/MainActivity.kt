package com.kyant.monet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

        MonetTheme(primary = primaryColor, secondary = secondaryColor) {
            Surface(
                Modifier.fillMaxSize(),
                color = backgroundColor
            ) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(56.dp))
                    Text(
                        "Monet Color",
                        Modifier.padding(horizontal = 24.dp),
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.h4
                    )
                    ColorTextField(text) { text = it }
                    Spacer(Modifier.height(24.dp))
                    ColorScheme(
                        "Accent 1" to a1,
                        "Accent 2" to a2,
                        "Accent 3" to a3,
                        "Neutral 1" to n1,
                        "Neutral 2" to n2
                    )
                    ButtonGroup()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorTextField(text: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    TextField(
        text,
        onValueChange,
        Modifier
            .fillMaxWidth()
            .padding(16.dp, 32.dp),
        textStyle = MaterialTheme.typography.h6.copy(textAlign = TextAlign.Center),
        label = {
            Text(
                "Enter color here",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            softwareKeyboardController?.hide()
        }),
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.primary,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ButtonGroup() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            Modifier
                .padding(32.dp)
                .size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.secondary,
            contentColor = if (MaterialTheme.colors.secondary.luminance() <= 0.5f) Color.White else Color.Black
        ) {
            Box(Modifier.fillMaxSize()) {
                Icon(
                    Icons.Outlined.Info,
                    "Info",
                    Modifier.align(Alignment.Center)
                )
            }
        }
        Surface(
            Modifier.padding(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.primary,
            contentColor = if (MaterialTheme.colors.primary.luminance() <= 0.5f) Color.White else Color.Black
        ) {
            Box(
                Modifier
                    .height(80.dp)
                    .padding(horizontal = 48.dp)
            ) {
                Text(
                    "Customize",
                    Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColorScheme(vararg shades: Pair<String, List<Color>>) {
    val activatedList = remember { mutableStateListOf<Boolean>() }
    shades.forEachIndexed { i, (name, shade) ->
        if (activatedList.getOrNull(i) == null) {
            activatedList += false
        }
        var activated by remember { mutableStateOf(false) }
        LaunchedEffect(activatedList[i]) {
            activated = activatedList[i]
        }
        val previousActivated = activatedList.getOrElse(i - 1) { false }
        val nextActivated = activatedList.getOrElse(i + 1) { false }

        val verticalPadding = animateDpAsState(if (activated) 8.dp else 0.dp).value

        val cornerSize = animateDpAsState(if (activated) 40.dp else 0.dp).value
        val topCornerSize = animateDpAsState(
            when {
                previousActivated -> 40.dp
                i == 0 -> 40.dp
                i == shades.lastIndex -> cornerSize
                else -> cornerSize
            }
        ).value
        val bottomCornerSize = animateDpAsState(
            when {
                nextActivated -> 40.dp
                i == 0 -> cornerSize
                i == shades.lastIndex -> 40.dp
                else -> cornerSize
            }
        ).value

        Card(
            when (i) {
                0 -> Modifier.padding(16.dp, 0.dp, 16.dp, verticalPadding)
                shades.lastIndex -> Modifier.padding(16.dp, verticalPadding, 16.dp, 0.dp)
                else -> Modifier.padding(16.dp, verticalPadding)
            },
            shape = RoundedCornerShape(
                topCornerSize,
                topCornerSize,
                bottomCornerSize,
                bottomCornerSize
            ),
            backgroundColor = Color.White,
            elevation = 0.dp
        ) {
            Column(Modifier.pointerInput(Unit) {
                detectTapGestures { activatedList[i] = !activated }
            }) {
                Box(Modifier.height(80.dp)) {
                    Text(
                        name,
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h5
                    )
                }
                Divider(
                    color = animateColorAsState(
                        if (if (activated) true else if (i != shades.lastIndex) !nextActivated else false)
                            MaterialTheme.colors.onSurface.copy(0.12f)
                        else Color.Transparent
                    ).value
                )
                AnimatedVisibility(visible = activated) {
                    FlowRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        mainAxisAlignment = MainAxisAlignment.Center
                    ) {
                        ColorButton("0")
                        shade.forEachIndexed { i, color ->
                            ColorButton((if (i == 0) 50 else i * 100).toString(), color)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorButton(
    text: String,
    color: Color = Color.White
) {
    Box(
        Modifier
            .padding(4.dp)
            .size(94.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { }
    ) {
        Text(
            text,
            Modifier.align(Alignment.Center),
            color = if (color.luminance() <= 0.5f) Color.White else Color.Black,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.body1
        )
    }
}