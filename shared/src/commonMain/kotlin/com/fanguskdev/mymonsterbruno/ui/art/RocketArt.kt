package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.sin

private val RocketRed = Color(0xFFE4483C)
private val RocketWhite = Color(0xFFF7F8FC)
private val RocketEdge = Color(0xFFC9D1E0)
private val Steel = Color(0xFF8A94A8)

/** How tall the rocket is, from the bottom of its nozzle to its tip, and how wide its body. */
internal const val ROCKET_HEIGHT = 250f
private const val BODY_WIDTH = 96f

/**
 * Bruno's rocket (ADR 0023), standing on [base], the middle of the bottom of its nozzle: white, with a red nose, red fins
 * and a red band, and Bruno looking out of its round window with [pose]. [firing], from 0 to 1, lights its flame.
 */
internal fun DrawScope.drawRocket(
    look: BrunoLook,
    base: Offset,
    pose: Pose,
    firing: Float,
    time: Float,
) {
    val x = base.x
    val bottom = base.y - 16f
    val top = base.y - ROCKET_HEIGHT
    val half = BODY_WIDTH / 2
    if (firing > 0f) drawFlame(base, firing, time)
    // The fins, behind the body.
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(x + side * half * 0.8f, bottom - 70f)
                quadraticTo(x + side * (half + 34f), bottom - 30f, x + side * (half + 30f), bottom + 4f)
                lineTo(x + side * half * 0.6f, bottom - 6f)
                close()
            },
            RocketRed,
        )
    }
    drawRoundRect(Steel, Offset(x - 22f, bottom - 4f), Size(44f, 20f), CornerRadius(6f))
    val body = path {
        moveTo(x - half, bottom)
        lineTo(x - half, top + 92f)
        cubicTo(x - half, top + 40f, x - 14f, top + 8f, x, top)
        cubicTo(x + 14f, top + 8f, x + half, top + 40f, x + half, top + 92f)
        lineTo(x + half, bottom)
        quadraticTo(x, bottom + 10f, x - half, bottom)
        close()
    }
    fill(body, RocketWhite)
    clipPath(body) {
        // The red nose and the red band near the bottom.
        drawRect(RocketRed, Offset(x - half, top - 4f), Size(BODY_WIDTH, 62f))
        drawRect(RocketRed, Offset(x - half, bottom - 40f), Size(BODY_WIDTH, 14f))
        // A shine down its left side.
        drawRect(Color.White, Offset(x - half + 10f, top + 70f), Size(8f, ROCKET_HEIGHT - 140f), alpha = 0.8f)
    }
    stroke(body, RocketEdge, 2.5f)
    // The window, with Bruno in it.
    val window = Offset(x, top + 118f)
    val radius = 36f
    circle(Color(0xFFBFE6F5), window, radius)
    clipPath(Path().apply { addOval(Rect(window - Offset(radius, radius), Size(2 * radius, 2 * radius))) }) {
        drawBruno(look, window + Offset(0f, 22f), 84f / look.size, pose, time)
    }
    ring(Steel, window, radius, 6f)
    stroke(
        path {
            moveTo(window.x - 22f, window.y - 14f)
            quadraticTo(window.x - 18f, window.y - 26f, window.x - 6f, window.y - 30f)
        },
        Color.White,
        4f,
        alpha = 0.7f,
    )
}

/** The flame under the nozzle at [base]: an orange drop round a yellow one, flickering, as long as [strength]. */
private fun DrawScope.drawFlame(base: Offset, strength: Float, time: Float) {
    val flicker = 1f + 0.12f * sin(time * 40f) + 0.06f * sin(time * 23f)
    val length = 90f * strength * flicker
    for ((color, width, k) in listOf(Triple(Color(0xFFFF9A3C), 22f, 1f), Triple(Color(0xFFFFE27A), 13f, 0.7f))) {
        fill(
            path {
                moveTo(base.x - width, base.y)
                quadraticTo(base.x - width * 0.6f, base.y + length * k * 0.6f, base.x, base.y + length * k)
                quadraticTo(base.x + width * 0.6f, base.y + length * k * 0.6f, base.x + width, base.y)
                close()
            },
            color,
        )
    }
}

/** The launch pad on the grass, under the rocket at [center]: a grey platform with its yellow and black edge. */
internal fun DrawScope.drawLaunchPad(center: Offset) {
    ellipse(Shadow, center + Offset(0f, 10f), 120f, 12f)
    drawRoundRect(Color(0xFFB9C1CF), Offset(center.x - 110f, center.y - 6f), Size(220f, 16f), CornerRadius(6f))
    for (i in 0 until 11) {
        val color = if (i % 2 == 0) Color(0xFFF4B623) else Color(0xFF2B2F3A)
        drawRect(color, Offset(center.x - 110f + 20f * i, center.y + 6f), Size(20f, 5f))
    }
}

/**
 * The launch tower beside the rocket, at [x] from [top] to [bottom], with ten lights from ten at the top to one at the
 * bottom: those already counted ([counted]) lit, the next one blinking.
 */
internal fun DrawScope.drawLaunchTower(
    x: Float,
    top: Float,
    bottom: Float,
    counted: Int,
    time: Float,
) {
    val red = Color(0xFFD9534A)
    line(red, Offset(x - 12f, top), Offset(x - 12f, bottom), 5f)
    line(red, Offset(x + 12f, top), Offset(x + 12f, bottom), 5f)
    val rungs = 10
    val step = (bottom - top) / rungs
    for (i in 0 until rungs) {
        val y = top + step * i
        line(red, Offset(x - 12f, y), Offset(x + 12f, y + step), 3f)
        line(red, Offset(x + 12f, y), Offset(x - 12f, y + step), 3f)
    }
    for (i in 0 until rungs) {
        val y = top + step * (i + 0.5f)
        val lit = i < counted
        val next = i == counted
        val glow = if (next) 0.5f + 0.5f * sin(time * 6f) else 0f
        circle(Color(0xFF2B2F3A), Offset(x, y), 10f)
        circle(if (lit) Color(0xFFFFD54A) else Color(0xFF5E6678), Offset(x, y), 7f)
        if (lit) circle(Color(0xFFFFF3B0), Offset(x - 2f, y - 2f), 2.5f)
        if (glow > 0f) ring(Color(0xFFFFD54A), Offset(x, y), 11f, 3f, alpha = glow)
    }
}

/** The smoke of the take-off at [base], [since] seconds after the zero: white puffs that grow and fade at both sides. */
internal fun DrawScope.drawSmoke(base: Offset, since: Float) {
    val k = clamp01(since / 2.2f)
    if (k >= 1f) return
    for (i in 0 until 6) {
        val side = if (i % 2 == 0) -1f else 1f
        val spread = 30f + 110f * k * (1f + 0.25f * (i / 2))
        val center = base + Offset(side * spread, -6f - 10f * (i / 2) - 20f * k)
        circle(Color.White, center, 18f + 34f * k, alpha = 0.85f * (1f - k))
    }
}
