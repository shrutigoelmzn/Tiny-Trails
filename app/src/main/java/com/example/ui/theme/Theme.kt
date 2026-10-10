package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

data class CustomThemeTokens(
    val cardBorder: Color,
    val themeEmoji: String,
    val themeName: String
)

val LocalCustomThemeTokens = staticCompositionLocalOf {
    CustomThemeTokens(
        cardBorder = GardenCardBorder,
        themeEmoji = "🌸",
        themeName = "Happy Garden"
    )
}

private val HappyGardenColorScheme = lightColorScheme(
    primary = GardenPrimary,
    onPrimary = GardenOnPrimary,
    primaryContainer = GardenPrimaryContainer,
    onPrimaryContainer = GardenOnPrimaryContainer,
    secondary = GardenSecondary,
    secondaryContainer = GardenSecondaryContainer,
    tertiary = GardenTertiary,
    background = GardenBackground,
    surface = GardenSurface,
    onBackground = Color(0xFF1B2E1C),
    onSurface = Color(0xFF1B2E1C)
)

private val DinoWorldColorScheme = lightColorScheme(
    primary = DinoPrimary,
    onPrimary = DinoOnPrimary,
    primaryContainer = DinoPrimaryContainer,
    onPrimaryContainer = DinoOnPrimaryContainer,
    secondary = DinoSecondary,
    secondaryContainer = DinoSecondaryContainer,
    tertiary = DinoTertiary,
    background = DinoBackground,
    surface = DinoSurface,
    onBackground = Color(0xFF122824),
    onSurface = Color(0xFF122824)
)

private val SpaceAdventureColorScheme = lightColorScheme(
    primary = SpacePrimary,
    onPrimary = SpaceOnPrimary,
    primaryContainer = SpacePrimaryContainer,
    onPrimaryContainer = SpaceOnPrimaryContainer,
    secondary = SpaceSecondary,
    secondaryContainer = SpaceSecondaryContainer,
    tertiary = SpaceTertiary,
    background = SpaceBackground,
    surface = SpaceSurface,
    onBackground = Color(0xFF1F1A30),
    onSurface = Color(0xFF1F1A30)
)

@Composable
fun TinyTrailsTheme(
    themeMode: ThemeMode = ThemeMode.HAPPY_GARDEN,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme
    val customTokens: CustomThemeTokens

    when (themeMode) {
        ThemeMode.HAPPY_GARDEN -> {
            colorScheme = HappyGardenColorScheme
            customTokens = CustomThemeTokens(
                cardBorder = GardenCardBorder,
                themeEmoji = "🌸",
                themeName = "Happy Garden"
            )
        }
        ThemeMode.DINO_WORLD -> {
            colorScheme = DinoWorldColorScheme
            customTokens = CustomThemeTokens(
                cardBorder = DinoCardBorder,
                themeEmoji = "🦕",
                themeName = "Dino World"
            )
        }
        ThemeMode.SPACE_ADVENTURE -> {
            colorScheme = SpaceAdventureColorScheme
            customTokens = CustomThemeTokens(
                cardBorder = SpaceCardBorder,
                themeEmoji = "🚀",
                themeName = "Space Adventure"
            )
        }
    }

    CompositionLocalProvider(LocalCustomThemeTokens provides customTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
