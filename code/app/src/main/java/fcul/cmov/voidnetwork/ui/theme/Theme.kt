package fcul.cmov.voidnetwork.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue

private val darkColorScheme = darkColorScheme(
    primary = BloodRed,
    secondary = DeepCrimson,
    tertiary = DarkMaroon,
    background = AlmostBlack,
    surface = Darkest,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = LightGray,
    onSurface = LightGray
)

private val lightColorScheme = lightColorScheme(
    primary = LightBloodRed,
    secondary = SoftCrimson,
    tertiary = PaleMaroon,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = DarkGray,
    onSurface = DarkGray
)

@Composable
fun VoidNetworkTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = rememberDynamicColorScheme(darkTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun rememberDynamicColorScheme(darkTheme: Boolean): ColorScheme {
    val targetColors = if (darkTheme) darkColorScheme else lightColorScheme
    val animatedColors = with(targetColors) {
        mapOf(
            "primary" to primary,
            "secondary" to secondary,
            "tertiary" to tertiary,
            "background" to background,
            "surface" to surface,
            "onPrimary" to onPrimary,
            "onSecondary" to onSecondary,
            "onTertiary" to onTertiary,
            "onBackground" to onBackground,
            "onSurface" to onSurface
        ).mapValues { (_, color) ->
            animateColorAsState(color, tween(durationMillis = 1000), label = "").value
        }
    }
    return targetColors.copy(
        primary = animatedColors["primary"]!!,
        secondary = animatedColors["secondary"]!!,
        tertiary = animatedColors["tertiary"]!!,
        background = animatedColors["background"]!!,
        surface = animatedColors["surface"]!!,
        onPrimary = animatedColors["onPrimary"]!!,
        onSecondary = animatedColors["onSecondary"]!!,
        onTertiary = animatedColors["onTertiary"]!!,
        onBackground = animatedColors["onBackground"]!!,
        onSurface = animatedColors["onSurface"]!!
    )
}