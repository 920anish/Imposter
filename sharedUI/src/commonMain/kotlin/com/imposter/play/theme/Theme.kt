package com.imposter.play.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.bebas_neue_regular
import imposter.sharedui.generated.resources.dm_sans_medium
import imposter.sharedui.generated.resources.dm_sans_regular
import imposter.sharedui.generated.resources.ibm_plex_mono_regular
import imposter.sharedui.generated.resources.ibm_plex_mono_semibold
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily

// ── Font families ────────────────────────────────────────────
val BebasNeue: FontFamily
    @Composable get() = FontFamily(Font(Res.font.bebas_neue_regular, FontWeight.Normal))

val IBMPlexMono: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.ibm_plex_mono_regular, FontWeight.Normal),
        Font(Res.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
    )

val DMSans: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.dm_sans_regular, FontWeight.Normal),
        Font(Res.font.dm_sans_medium, FontWeight.Medium),
    )

// ── Color scheme — always dark ───────────────────────────────
private val ImposterColorScheme = darkColorScheme(
    background        = ColorBg,
    surface           = ColorSurface,
    onBackground      = ColorText,
    onSurface         = ColorText,
    primary           = ColorCrew,
    error             = ColorImp,
    outline           = ColorBorder,
    outlineVariant    = ColorBorder2,
    // unused slots — set to bg so nothing bleeds through accidentally
    secondary         = ColorMuted,
    tertiary          = ColorWin,
    scrim             = Color.Black,
    inverseSurface    = ColorText,
    inverseOnSurface  = ColorBg,
    inversePrimary    = ColorCrewDim,
)

// ── Typography ───────────────────────────────────────────────
@Composable
private fun imposterTypography() = Typography(
    // DM Sans for body/labels
    bodyLarge  = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal,  fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    bodySmall  = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Normal,  fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Medium,  fontSize = 14.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = DMSans, fontWeight = FontWeight.Medium,  fontSize = 11.sp, letterSpacing = 1.sp),
    // Bebas Neue for display
    displayLarge  = TextStyle(fontFamily = BebasNeue, fontSize = 96.sp, letterSpacing = 6.sp),
    displayMedium = TextStyle(fontFamily = BebasNeue, fontSize = 64.sp, letterSpacing = 5.sp),
    displaySmall  = TextStyle(fontFamily = BebasNeue, fontSize = 48.sp, letterSpacing = 4.sp),
    headlineLarge  = TextStyle(fontFamily = BebasNeue, fontSize = 36.sp, letterSpacing = 4.sp),
    headlineMedium = TextStyle(fontFamily = BebasNeue, fontSize = 28.sp, letterSpacing = 3.sp),
    // IBM Plex Mono for codes/badges — override per usage in screens
    titleLarge  = TextStyle(fontFamily = IBMPlexMono, fontSize = 14.sp, letterSpacing = 3.sp),
    titleMedium = TextStyle(fontFamily = IBMPlexMono, fontSize = 11.sp, letterSpacing = 4.sp),
    titleSmall  = TextStyle(fontFamily = IBMPlexMono, fontSize = 9.sp,  letterSpacing = 5.sp),
)

// ── App theme entry point ────────────────────────────────────
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val typography = imposterTypography()

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = ColorCrew,
            backgroundColor = ColorCrewDim,
        )
    ) {
        MaterialTheme(
            colorScheme = ImposterColorScheme,
            typography  = typography,
            content     = content,
        )
    }
}