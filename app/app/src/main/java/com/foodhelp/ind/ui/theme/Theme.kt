package com.foodhelp.ind.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EmeraldGreen = Color(0xFF047857)
val EmeraldLight = Color(0xFFA7F3D0)
val PureWhite = Color(0xFFFFFFFF)
val DarkText = Color(0xFF111827)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = PureWhite,
    secondary = EmeraldLight,
    background = PureWhite,
    surface = PureWhite,
    onBackground = DarkText,
    onSurface = DarkText
)

@Composable
fun FoodHelpINDTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
