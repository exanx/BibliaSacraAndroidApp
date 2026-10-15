package com.example.bibliasacra.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemePreset(
    val title: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val text: Color,
    val primary: Color,
    val secondary: Color
) {
    AMOLED_BLACK(
        title = "AMOLED Black",
        isDark = true,
        background = Color(0xFF000000),
        surface = Color(0xFF121214),
        text = Color(0xFFE4E4E7),
        primary = Color(0xFF6366F1),
        secondary = Color(0xFF818CF8)
    ),
    PURE_WHITE(
        title = "Pure White",
        isDark = false,
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF4F4F5),
        text = Color(0xFF18181B),
        primary = Color(0xFF4F46E5),
        secondary = Color(0xFF6366F1)
    ),
    GOLDEN_PARCHMENT(
        title = "Golden Parchment",
        isDark = false,
        background = Color(0xFFFAEDD0),
        surface = Color(0xFFF3E4C0),
        text = Color(0xFF3F2D18),
        primary = Color(0xFFD97706),
        secondary = Color(0xFFB45309)
    ),
    ANCIENT_SCROLL(
        title = "Ancient Scroll",
        isDark = false,
        background = Color(0xFFF4EBD9),
        surface = Color(0xFFEADBCE),
        text = Color(0xFF382B1D),
        primary = Color(0xFFC2410C),
        secondary = Color(0xFF9A3412)
    ),
    EDEN_SAGE(
        title = "Eden Sage",
        isDark = false,
        background = Color(0xFFEAF4EE),
        surface = Color(0xFFD8EBE0),
        text = Color(0xFF123924),
        primary = Color(0xFF059669),
        secondary = Color(0xFF047857)
    ),
    NIGHT_SKY(
        title = "Night Sky",
        isDark = true,
        background = Color(0xFF070E1C),
        surface = Color(0xFF0F1B33),
        text = Color(0xFFE2E8F0),
        primary = Color(0xFF3B82F6),
        secondary = Color(0xFF60A5FA)
    ),
    ROYAL_AMETHYST(
        title = "Royal Amethyst",
        isDark = true,
        background = Color(0xFF13091F),
        surface = Color(0xFF221136),
        text = Color(0xFFF3E8FF),
        primary = Color(0xFFA855F7),
        secondary = Color(0xFFC084FC)
    )
}

@Composable
fun BibliaSacraTheme(
    preset: ThemePreset = ThemePreset.AMOLED_BLACK,
    content: @Composable () -> Unit
) {
    val colorScheme = if (preset.isDark) {
        darkColorScheme(
            primary = preset.primary,
            secondary = preset.secondary,
            background = preset.background,
            surface = preset.surface,
            onBackground = preset.text,
            onSurface = preset.text,
            onPrimary = Color.White
        )
    } else {
        lightColorScheme(
            primary = preset.primary,
            secondary = preset.secondary,
            background = preset.background,
            surface = preset.surface,
            onBackground = preset.text,
            onSurface = preset.text,
            onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
