package com.kyant.monetdemo.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.materialyou.ui.StretchScrollableColumn
import com.kyant.monet.LocalMonetColors
import com.kyant.monet.MonetColors
import com.kyant.monet.contentColor

fun colorToHex(x: Color): String {
    val intColor = x.toArgb()
    return java.lang.String.format("#%06X", 0xFFFFFF and intColor)
}

@Composable
fun Palette(
    text: TextFieldValue,
    colors: MonetColors,
    onValueChange: (TextFieldValue) -> Unit
) {
    val (a1, a2, a3, n1, n2) = colors
//    Log.d("COLORS-A1", a1.map{ colorToHex(it) }.toString())
//    Log.d("COLORS-A2", a2.map{ colorToHex(it) }.toString())
//    Log.d("COLORS-A3", a3.map{ colorToHex(it) }.toString())
//    Log.d("COLORS-N1", n1.map{ colorToHex(it) }.toString())
//    Log.d("COLORS-N2", n2.map{ colorToHex(it) }.toString())
    val arr = arrayOf(a1,a2,a3,n1,n2).map { list -> list.map {colorToHex(it) } }
    Log.d("COLORS", arr.toString())

    StretchScrollableColumn(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(112.dp))
        Text(
            "Monet Palette",
            Modifier.padding(horizontal = 24.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.h4
        )
        ColorTextField(text, onValueChange)
        ColorSchemePalette(
            "A-1" to a1,
            "A-2" to a2,
            "A-3" to a3,
            "N-1" to n1,
            "N-2" to n2
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
        placeholder = {
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
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = LocalMonetColors.current.accent1[5],
            backgroundColor = LocalMonetColors.current.neutral1[0],
            cursorColor = LocalMonetColors.current.accent1[5],
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ColorSchemePalette(vararg shades: Pair<String, List<Color>>) {
    Column(
        Modifier
            .padding(8.dp)
            .background(LocalMonetColors.current.neutral1[0], RoundedCornerShape(32.dp))
            .horizontalScroll(rememberScrollState())
    ) {
        Row(Modifier.padding(40.dp, 16.dp)) {
            for (i in 0..12) {
                Box(
                    Modifier
                        .padding(4.dp)
                        .width(64.dp)
                ) {
                    Text(
                        (when (i) {
                            0 -> 0
                            1 -> 10
                            2 -> 50
                            else -> (i - 2) * 100
                        }).toString(),
                        Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
        shades.forEach { (name, shade) ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        name,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.body1
                    )
                    (shade + Color.Black).forEach { color ->
                        Box(
                            Modifier
                                .padding(4.dp)
                                .size(64.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)

                        ){
                            Text(colorToHex(color), fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ColorCell(color: Color = Color.White, text: String = "") {
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
            color = color.contentColor(),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.body1
        )
    }
}