package com.accend.app.ui.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

private class GoldParticle(
    val normX: Float,
    val normY: Float,
    val radiusDp: Float,
    val baseAlpha: Float,
    val driftX: Float,
    val driftY: Float,
    val twinklePhase: Float,
)

/**
 * Cinematic golden splash screen shown on app launch.
 * Modeled after the "ACCEND cinematic golden loading" reference:
 * floating gold particles, serif ACCEND title with a gold-gradient
 * bottom-up reveal, shimmering progress line, "RISE BEYOND LIMITS"
 * tagline and hairline gold corner brackets.
 *
 * Auto-dismisses after the intro completes; tapping anywhere also skips.
 */
@Composable
fun CinematicSplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val particles = remember {
        List(34) {
            GoldParticle(
                normX = Random.nextFloat(),
                normY = Random.nextFloat(),
                radiusDp = 0.5f + Random.nextFloat() * 1.0f,
                baseAlpha = (0.04f + Random.nextFloat() * 0.18f).coerceIn(0f, 1f),
                driftX = (Random.nextFloat() - 0.5f) * 0.08f,
                driftY = (Random.nextFloat() - 0.5f) * 0.08f,
                twinklePhase = Random.nextFloat() * (2f * Math.PI.toFloat()),
            )
        }
    }

    // Wall-clock time in seconds used to drive particles + progress
    var timeSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = System.nanoTime()
        while (true) {
            timeSeconds = ((System.nanoTime() - start) / 1_000_000_000f)
            delay(16L)
        }
    }

    val t = (timeSeconds / 5f).coerceIn(0f, 1f)

    // Gold fill ease-out curve (fills fast then slows at the end)
    val progress = if (t < 0.8f) {
        1f - (1f - t / 0.8f).pow(2.2f) * 0.8f
    } else {
        0.8f + (t - 0.8f)
    }.coerceIn(0f, 1f)

    val introDone = t >= 1f

    val titleIn by animateFloatAsState(
        targetValue = if (t >= 0.10f) 1f else 0f,
        animationSpec = tween(1400, easing = EaseOutCubic),
        label = "title_in",
    )
    val goldReveal by animateFloatAsState(
        targetValue = if (t >= 0.16f) ((t - 0.16f) / 0.84f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "gold_reveal",
    )
    val taglineIn by animateFloatAsState(
        targetValue = if (t >= 0.18f) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "tagline_in",
    )
    val enterIn by animateFloatAsState(
        targetValue = if (introDone) 1f else 0f,
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "enter_in",
    )

    // Exit transition: slight scale-up + fade toward the app
    val exitScale by animateFloatAsState(
        targetValue = if (introDone) 1.08f else 1f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "exit_scale",
    )
    val exitAlpha by animateFloatAsState(
        targetValue = if (introDone) 0f else 1f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "exit_alpha",
    )

    // Auto-dismiss shortly after the intro completes
    LaunchedEffect(introDone) {
        if (introDone) {
            delay(2600L)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                scaleX = exitScale
                scaleY = exitScale
                alpha = exitAlpha
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onDismiss() },
    ) {
        // Floating gold particles
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            for (p in particles) {
                val x = (((p.normX + p.driftX * timeSeconds) % 1f) + 1f) % 1f * w
                val y = (((p.normY + p.driftY * timeSeconds) % 1f) + 1f) % 1f * h
                val twinkle = 0.7f + 0.3f * sin(p.twinklePhase + timeSeconds * 2f)
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = (p.baseAlpha * twinkle).coerceIn(0f, 1f)),
                    radius = p.radiusDp.dp.toPx(),
                    center = Offset(x, y),
                )
            }
        }

        val titleTextStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 44.sp,
            letterSpacing = 12.sp,
            textAlign = TextAlign.Center,
        )
        val goldGradient = Brush.verticalGradient(
            listOf(
                Color(0xFFFFF9D6),
                Color(0xFFF9E7A0),
                Color(0xFFE8C86A),
                Color(0xFFD4AF37),
                Color(0xFFB8921F),
                Color(0xFF8C6A1A),
                Color(0xFF5A4210),
            ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(titleIn),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // ACCEND — dark embossed base
            Box {
                Text(
                    text = "ACCEND",
                    style = titleTextStyle.copy(color = Color(0xFF0A0A0A)),
                )
                // Gold gradient layer revealed bottom-up
                Box(
                    modifier = Modifier
                        .drawWithContent {
                            clipRect(
                                left = 0f,
                                top = size.height * (1f - goldReveal),
                                right = size.width,
                                bottom = size.height,
                            ) {
                                this@drawWithContent.drawContent()
                            }
                        },
                ) {
                    Text(
                        text = "ACCEND",
                        style = titleTextStyle.copy(brush = goldGradient),
                    )
                }
            }

            // Progress line with shimmering tip
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .width(148.dp)
                    .height(1.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.06f)),
                )
                Box(
                    Modifier
                        .width((148f * progress).dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF5A4210),
                                    Color(0xFF8C6A1A),
                                    Color(0xFFD4AF37),
                                    Color(0xFFF9E7A0),
                                    Color(0xFFFFF7CC),
                                ),
                            ),
                        ),
                )
                if (progress > 0f) {
                    Box(
                        Modifier
                            .offset(x = (148f * progress - 2f).dp, y = (-1.5f).dp)
                            .size(4.dp)
                            .background(Color(0xFFFFF8CC), shape = RoundedCornerShape(2.dp)),
                    )
                }
            }

            // ENTER button (appears once intro completes)
            if (enterIn > 0f) {
                Box(
                    modifier = Modifier
                        .padding(top = 84.dp)
                        .alpha(enterIn)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                        .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.32f), RoundedCornerShape(20.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onDismiss() }
                        .width(120.dp)
                        .height(40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "ENTER",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 10.sp,
                        letterSpacing = 4.sp,
                        color = Color(0xFFF0DD9C),
                    )
                }
            }
        }

        // RISE BEYOND LIMITS tagline
        Text(
            text = "RISE BEYOND LIMITS",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = 8.sp,
            letterSpacing = 8.sp,
            color = Color.White.copy(alpha = 0.28f * taglineIn),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp),
        )

        // Gold hairline corner brackets
        val bracketColor = Color(0xFFD4AF37).copy(alpha = 0.14f)
        val off = 24.dp
        val len = 10.dp
        Canvas(Modifier.fillMaxSize()) {
            val l = len.toPx()
            val o = off.toPx()
            val w = size.width
            val h = size.height
            // top-left
            drawRect(bracketColor, Offset(o, o), androidx.compose.ui.geometry.Size(l, 1.dp.toPx()))
            drawRect(bracketColor, Offset(o, o), androidx.compose.ui.geometry.Size(1.dp.toPx(), l))
            // top-right
            drawRect(bracketColor, Offset(w - o - l, o), androidx.compose.ui.geometry.Size(l, 1.dp.toPx()))
            drawRect(bracketColor, Offset(w - o - 1.dp.toPx(), o), androidx.compose.ui.geometry.Size(1.dp.toPx(), l))
            // bottom-left
            drawRect(bracketColor, Offset(o, h - o - 1.dp.toPx()), androidx.compose.ui.geometry.Size(l, 1.dp.toPx()))
            drawRect(bracketColor, Offset(o, h - o - l), androidx.compose.ui.geometry.Size(1.dp.toPx(), l))
            // bottom-right
            drawRect(bracketColor, Offset(w - o - l, h - o - 1.dp.toPx()), androidx.compose.ui.geometry.Size(l, 1.dp.toPx()))
            drawRect(bracketColor, Offset(w - o - 1.dp.toPx(), h - o - l), androidx.compose.ui.geometry.Size(1.dp.toPx(), l))
        }
    }
}
