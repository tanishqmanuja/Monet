package com.kyant.monet.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.kyant.materialyou.component.StretchScrollableColumn
import com.kyant.monet.color.MonetColors

@Composable
fun Palette(
    text: TextFieldValue,
    monetColors: MonetColors,
    onValueChange: (TextFieldValue) -> Unit
) {
    val (a1, a2, a3, n1, n2) = monetColors
    StretchScrollableColumn(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(56.dp))
        Text(
            "Monet Color",
            Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.h4
        )
        Box {
            ColorTextField(text, onValueChange)
            Icon(
                Icons.Outlined.Palette,
                "Pick a color",
                Modifier
                    .padding(end = 20.dp)
                    .clip(CircleShape)
                    .clickable { }
                    .background(MaterialTheme.colors.primary)
                    .padding(16.dp)
                    .align(Alignment.CenterEnd),
                tint = MaterialTheme.colors.onPrimary
            )
        }
        Spacer(Modifier.height(24.dp))
        ColorScheme(
            "Accent 1" to a1,
            "Accent 2" to a2,
            "Accent 3" to a3,
            "Neutral 1" to n1,
            "Neutral 2" to n2
        )
        Spacer(Modifier.height(24.dp))
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
            .padding(16.dp, 32.dp)
            .height(60.dp),
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
            backgroundColor = MaterialTheme.colors.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ColorScheme(vararg shades: Pair<String, List<Color>>) {
    val expandedItems = remember { mutableStateListOf<Boolean>() }
    shades.forEachIndexed { i, (name, shade) ->
        if (expandedItems.getOrNull(i) == null) {
            expandedItems += i == 0
        }
        var expanded by remember { mutableStateOf(false) }
        LaunchedEffect(expandedItems[i]) {
            expanded = expandedItems[i]
        }
        val previousActivated = expandedItems.getOrElse(i - 1) { false }
        val nextActivated = expandedItems.getOrElse(i + 1) { false }

        val roundedCornerSize = 28.dp
        val cornerSize = animateDpAsState(if (expanded) roundedCornerSize else 4.dp).value
        val topCornerSize = animateDpAsState(
            when {
                previousActivated -> roundedCornerSize
                i == 0 -> roundedCornerSize
                i == shades.lastIndex -> cornerSize
                else -> cornerSize
            }
        ).value
        val bottomCornerSize = animateDpAsState(
            when {
                nextActivated -> roundedCornerSize
                i == 0 -> cornerSize
                i == shades.lastIndex -> roundedCornerSize
                else -> cornerSize
            }
        ).value

        Card(
            Modifier.padding(horizontal = 16.dp),
            shape = RoundedCornerShape(
                topCornerSize,
                topCornerSize,
                bottomCornerSize,
                bottomCornerSize
            ),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { expandedItems[i] = !expanded }
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.h6
                    )
                    Icon(
                        if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        if (expanded) "Collapse" else "Expand"
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Divider()
                    FlowRow(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        mainAxisAlignment = MainAxisAlignment.Center
                    ) {
                        ColorButton(text = "0")
                        shade.forEachIndexed { i, color ->
                            ColorButton(
                                color,
                                (if (i == 0) 10 else if (i == 1) 50 else (i - 1) * 100).toString()
                            )
                        }
                    }
                }
            }
        }
        if (i != shades.lastIndex) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colors.background)
            )
        }
    }
}

@Composable
fun ColorButton(color: Color = Color.White, text: String = "") {
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