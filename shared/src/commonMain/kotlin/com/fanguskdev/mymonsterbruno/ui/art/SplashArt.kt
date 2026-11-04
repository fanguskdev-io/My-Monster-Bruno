package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Where the sky meets the sea on the start screen (ADR 0018). */
internal const val HORIZON = 250f

/** The sky of the start screen: a day sky, blue above and pale at the horizon, with the sun and clouds drifting. */
internal fun DrawScope.drawSplashSky(stage: Stage, time: Float) {
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF8FC9F4), Color(0xFFDCD6FA)), startY = stage.top, endY = HORIZON),
        topLeft = Offset(stage.left, stage.top),
        size = Size(stage.right - stage.left, HORIZON - stage.top),
    )
    val sun = Offset(96f, 76f)
    rotate(time * 6f, sun) {
        for (i in 0 until 10) {
            val angle = i * PI / 5
            val from = sun + Offset((36 * cos(angle)).toFloat(), (36 * sin(angle)).toFloat())
            val to = sun + Offset((50 * cos(angle)).toFloat(), (50 * sin(angle)).toFloat())
            line(Color(0xFFFFC94D), from, to, 6f)
        }
    }
    circle(Color(0xFFFFD54A), sun, 30f)
    val width = stage.right - stage.left + 160f
    for ((i, cloud) in CLOUDS.withIndex()) {
        val x = stage.left - 80f + (cloud.x - time * (8f + 4f * i)).mod(width)
        drawCloud(Offset(x, cloud.y), 0.8f + 0.15f * i)
    }
}

private val CLOUDS = listOf(Offset(260f, 62f), Offset(520f, 40f), Offset(760f, 96f), Offset(380f, 128f))

private fun DrawScope.drawCloud(center: Offset, scale: Float) {
    for ((dx, r) in listOf(-26f to 16f, 0f to 22f, 26f to 15f)) {
        circle(Color.White, center + Offset(dx * scale, 0f), r * scale, alpha = 0.92f)
    }
    ellipse(Color.White, center + Offset(0f, 8f * scale), 40f * scale, 11f * scale, alpha = 0.92f)
}

/** The sea under the horizon, lilac as on the map, with rows of little waves going by at their own speed. */
internal fun DrawScope.drawSplashSea(stage: Stage, time: Float) {
    drawRect(
        Brush.verticalGradient(listOf(Color(0xFF8C96F2), Color(0xFF5967D8)), startY = HORIZON, endY = stage.bottom),
        topLeft = Offset(stage.left, HORIZON),
        size = Size(stage.right - stage.left, stage.bottom - HORIZON),
    )
    val width = stage.right - stage.left + 80f
    for (row in 0 until 4) {
        val y = HORIZON + 24f + 38f * row
        val speed = 10f + 8f * row
        for (k in 0 until 7) {
            val x = stage.left - 40f + (k * 140f + row * 55f - time * speed).mod(width)
            stroke(
                path {
                    moveTo(x, y)
                    quadraticTo(x + 10f, y - 7f, x + 20f, y)
                    quadraticTo(x + 30f, y + 7f, x + 40f, y)
                },
                Color.White,
                3f,
                alpha = 0.35f,
            )
        }
    }
}

/**
 * The islands of the game on the horizon, small and far, sliding slowly to the left one after the other, each with its
 * sign: the letters, the numbers, the balloons, the shapes, the faces and the book.
 */
internal fun DrawScope.drawHorizonIslands(stage: Stage, time: Float) {
    val width = stage.right - stage.left + 240f
    for (i in 0 until ISLAND_SIGNS) {
        val x = stage.left - 120f + (i * width / ISLAND_SIGNS - time * 9f).mod(width)
        val at = Offset(x, HORIZON + 6f)
        ellipse(Color(0xFFF6DFA6), at, 52f, 12f)
        ellipse(Color(0xFF93D46E), at + Offset(0f, -4f), 38f, 7f)
        val top = at + Offset(0f, -16f)
        when (i) {
            0 -> for ((k, hue) in listOf(Hue.RED, Hue.BLUE, Hue.YELLOW).withIndex()) {
                circle(Color.White, top + Offset(-16f + 16f * k, 2f), 7f)
                circle(hue.paint(), top + Offset(-16f + 16f * k, 2f), 3.5f)
            }
            1 -> for (k in 0 until 3) drawBalloon(top + Offset(-14f + 14f * k, -10f), 20f, Hue.entries[k + 2].paint())
            2 -> for ((k, shape) in Shape.entries.take(3).withIndex()) {
                drawShapePiece(shape, pieceBox(shape, top + Offset(-18f + 18f * k, 0f), 14f), Hue.entries[k].paint())
            }
            3 -> for ((k, feeling) in Feeling.entries.take(3).withIndex()) drawFeelingFace(feeling, top + Offset(-16f + 16f * k, 0f), 7f)
            else -> drawOpenBook(top, 30f)
        }
    }
}

private const val ISLAND_SIGNS = 5

/**
 * Bruno's boat on the waves at [at], rocking, with Bruno in it looking ahead; or, before he hatches ([look] null), his
 * egg, which wobbles.
 */
internal fun DrawScope.drawBoatWith(look: BrunoLook?, at: Offset, time: Float) {
    val rock = 3f * sin(time * 1.6f)
    rotate(rock, at) {
        if (look != null) {
            drawBruno(look, at + Offset(0f, -46f), 112f, Pose(lookX = 0.5f, hop = 2f * sin(time * 3f)), time)
        } else {
            drawEgg(at + Offset(0f, -40f), 60f, 74f, cracks = 0, tilt = 5f * sin(time * 2.4f))
        }
        val hull = path {
            moveTo(at.x - 74f, at.y - 12f)
            lineTo(at.x + 74f, at.y - 12f)
            lineTo(at.x + 52f, at.y + 26f)
            lineTo(at.x - 52f, at.y + 26f)
            close()
        }
        fill(hull, Color(0xFFB5643A))
        line(Color(0xFF8A4B2E), Offset(at.x - 66f, at.y - 2f), Offset(at.x + 66f, at.y - 2f), 3f)
    }
    stroke(
        path {
            moveTo(at.x - 96f, at.y + 28f)
            quadraticTo(at.x - 84f, at.y + 20f, at.x - 72f, at.y + 28f)
            moveTo(at.x + 72f, at.y + 28f)
            quadraticTo(at.x + 84f, at.y + 20f, at.x + 96f, at.y + 28f)
        },
        Color.White,
        3f,
        alpha = 0.6f,
    )
}
