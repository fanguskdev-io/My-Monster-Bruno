package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Happening
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.sin

/**
 * The colour of [feeling], the one of the corner of feelings of the classroom (ADR 0010): joy yellow, sadness blue, anger
 * red, fear purple and calm green.
 */
internal fun Feeling.paint(): Color = when (this) {
    Feeling.JOY -> Color(0xFFF4C423)
    Feeling.SADNESS -> Color(0xFF4A8FE0)
    Feeling.ANGER -> Color(0xFFE4483C)
    Feeling.FEAR -> Color(0xFF7E57C2)
    Feeling.CALM -> Color(0xFF3DB46D)
}

/** How Bruno looks with [feeling]: his eyes and his mouth. */
internal fun Feeling.pose(time: Float): Pose = when (this) {
    Feeling.JOY -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 6f * sin(time * 5f).coerceAtLeast(0f))
    Feeling.SADNESS -> Pose(Mouth.FROWN, Eyes.SAD, Arms.DOWN, lookY = 1f)
    Feeling.ANGER -> Pose(Mouth.FROWN, Eyes.ANGRY, Arms.DOWN)
    Feeling.FEAR -> Pose(Mouth.O, Eyes.WIDE, Arms.UP, lookX = 0.6f * sin(time * 9f))
    Feeling.CALM -> Pose(Mouth.SMILE, Eyes.CLOSED, Arms.DOWN)
}

private val FaceInk = Color(0xFF1D2B53)

/** A little face of [feeling], [radius] units: a circle of its colour with its eyes and its mouth, to choose from. */
internal fun DrawScope.drawFeelingFace(feeling: Feeling, center: Offset, radius: Float) {
    circle(Shadow, center + Offset(0f, 4f), radius)
    circle(feeling.paint(), center, radius)
    val k = radius / 40f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        when (feeling) {
            Feeling.JOY -> {
                for (x in listOf(-13f, 13f)) {
                    stroke(
                        path {
                            moveTo(x - 7f, -6f)
                            quadraticTo(x, -15f, x + 7f, -6f)
                        },
                        FaceInk,
                        3.5f,
                    )
                }
                fill(
                    path {
                        moveTo(-16f, 6f)
                        quadraticTo(0f, 28f, 16f, 6f)
                        close()
                    },
                    FaceInk,
                )
            }
            Feeling.SADNESS -> {
                for (x in listOf(-13f, 13f)) circle(FaceInk, Offset(x, -4f), 4f)
                line(FaceInk, Offset(-22f, -12f), Offset(-8f, -18f), 3f)
                line(FaceInk, Offset(22f, -12f), Offset(8f, -18f), 3f)
                stroke(
                    path {
                        moveTo(-12f, 18f)
                        quadraticTo(0f, 7f, 12f, 18f)
                    },
                    FaceInk,
                    3.5f,
                )
                ellipse(Color(0xFFBFE6FF), Offset(-17f, 7f), 3f, 4.5f)
            }
            Feeling.ANGER -> {
                for (x in listOf(-13f, 13f)) circle(FaceInk, Offset(x, -3f), 4f)
                line(FaceInk, Offset(-22f, -17f), Offset(-7f, -10f), 3.5f)
                line(FaceInk, Offset(22f, -17f), Offset(7f, -10f), 3.5f)
                stroke(
                    path {
                        moveTo(-12f, 17f)
                        quadraticTo(0f, 10f, 12f, 17f)
                    },
                    FaceInk,
                    3.5f,
                )
            }
            Feeling.FEAR -> {
                for (x in listOf(-13f, 13f)) {
                    circle(Color.White, Offset(x, -6f), 9f)
                    circle(FaceInk, Offset(x, -5f), 3.5f)
                }
                ellipse(FaceInk, Offset(0f, 14f), 6f, 7.5f)
            }
            Feeling.CALM -> {
                for (x in listOf(-13f, 13f)) {
                    stroke(
                        path {
                            moveTo(x - 7f, -5f)
                            quadraticTo(x, 1f, x + 7f, -5f)
                        },
                        FaceInk,
                        3.5f,
                    )
                }
                stroke(
                    path {
                        moveTo(-10f, 10f)
                        quadraticTo(0f, 18f, 10f, 10f)
                    },
                    FaceInk,
                    3.5f,
                )
            }
        }
    }
}

/** The picture of what happens to Bruno, [size] units across, the one of his bubble. */
internal fun DrawScope.drawHappening(
    happening: Happening,
    center: Offset,
    size: Float,
    time: Float,
) {
    val k = size / 100f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        when (happening) {
            Happening.BIRTHDAY -> cake(time)
            Happening.ICE_CREAM -> fallenIceCream()
            Happening.TOWER -> fallenTower()
            Happening.STORM -> storm(time)
            Happening.LULLABY -> lullaby(time)
        }
    }
}

private fun DrawScope.cake(time: Float) {
    ellipse(Color(0xFFDCE3EF), Offset(0f, 34f), 44f, 9f)
    drawRect(Color(0xFFF7C6D9), Offset(-34f, 0f), Size(68f, 34f))
    drawRect(Color(0xFFFF8FB1), Offset(-34f, -6f), Size(68f, 12f))
    for (x in listOf(-24f, -8f, 8f, 24f)) circle(Color(0xFFFF8FB1), Offset(x, 6f), 5f)
    for ((i, x) in listOf(-14f, 0f, 14f).withIndex()) {
        drawRect(Color(0xFF8FD0FF), Offset(x - 3f, -26f), Size(6f, 20f))
        val flicker = 1f + 0.15f * sin(time * 12f + i)
        ellipse(Color(0xFFFFB13D), Offset(x, -32f), 4f * flicker, 7f * flicker)
    }
}

private fun DrawScope.fallenIceCream() {
    ellipse(Color(0xFFFFD9E5), Offset(0f, 30f), 40f, 10f)
    // The scoop, flat on the floor, and the cone upside down on it.
    fill(
        path {
            moveTo(-30f, 30f)
            quadraticTo(-34f, 10f, -14f, 12f)
            quadraticTo(0f, 0f, 14f, 12f)
            quadraticTo(34f, 10f, 30f, 30f)
            close()
        },
        Color(0xFFFF8FB1),
    )
    fill(
        path {
            moveTo(-14f, 14f)
            lineTo(14f, 14f)
            lineTo(0f, -38f)
            close()
        },
        Color(0xFFE9B36A),
    )
    line(Color(0xFFC98E45), Offset(-7f, 0f), Offset(7f, -14f), 2f)
    line(Color(0xFFC98E45), Offset(7f, 0f), Offset(-5f, -18f), 2f)
}

private fun DrawScope.fallenTower() {
    ellipse(Color(0xFFDCE3EF), Offset(0f, 36f), 46f, 8f)
    drawRect(Color(0xFF2C74D8), Offset(-36f, 8f), Size(26f, 26f))
    withTransform({ rotate(28f, Offset(10f, 20f)) }) { drawRect(Color(0xFFF4C423), Offset(-3f, 8f), Size(26f, 26f)) }
    withTransform({ rotate(-20f, Offset(30f, 0f)) }) { drawRect(Color(0xFF2E9E5B), Offset(18f, -12f), Size(26f, 26f)) }
    withTransform({ rotate(40f, Offset(-14f, -14f)) }) { drawRect(Color(0xFFE4483C), Offset(-26f, -28f), Size(24f, 24f)) }
}

private fun DrawScope.storm(time: Float) {
    for ((x, r) in listOf(-22f to 18f, 0f to 24f, 22f to 18f)) circle(Color(0xFF6E7591), Offset(x, -18f), r)
    ellipse(Color(0xFF6E7591), Offset(0f, -6f), 40f, 12f)
    val flash = (time % 1.6f) < 0.25f
    fill(
        path {
            moveTo(4f, 0f)
            lineTo(-10f, 22f)
            lineTo(0f, 22f)
            lineTo(-6f, 42f)
            lineTo(12f, 14f)
            lineTo(2f, 14f)
            close()
        },
        if (flash) Color(0xFFFFF3B0) else Color(0xFFF4C423),
    )
    for (x in listOf(-28f, -18f, 20f, 30f)) line(Color(0xFF8FD0FF), Offset(x, 6f), Offset(x - 4f, 18f), 3f)
}

private fun DrawScope.lullaby(time: Float) {
    fill(
        path {
            moveTo(-6f, -36f)
            cubicTo(-40f, -30f, -40f, 24f, -6f, 30f)
            cubicTo(-22f, 16f, -22f, -22f, -6f, -36f)
            close()
        },
        Color(0xFFF4C423),
    )
    for ((i, base) in listOf(Offset(14f, 6f), Offset(32f, -14f)).withIndex()) {
        val at = base + Offset(0f, 4f * sin(time * 2f + i))
        tiltedEllipse(Color(0xFF5B5F97), at, 6f, 4.5f, -20f)
        line(Color(0xFF5B5F97), at + Offset(5f, -1f), at + Offset(5f, -24f), 2.5f)
        line(Color(0xFF5B5F97), at + Offset(5f, -24f), at + Offset(13f, -18f), 2.5f)
    }
}

/** A table lamp standing on the floor at [base]: with [on], its light falls round it. */
internal fun DrawScope.drawLamp(base: Offset, on: Boolean) {
    if (on) circle(Color(0xFFFFF3B0), base + Offset(0f, -96f), 90f, alpha = 0.45f)
    ellipse(Shadow, base + Offset(0f, 4f), 30f, 6f)
    ellipse(Color(0xFF8A5A3C), base, 26f, 7f)
    line(Color(0xFF8A5A3C), base, base + Offset(0f, -86f), 6f)
    fill(
        path {
            moveTo(base.x - 20f, base.y - 120f)
            lineTo(base.x + 20f, base.y - 120f)
            lineTo(base.x + 36f, base.y - 82f)
            lineTo(base.x - 36f, base.y - 82f)
            close()
        },
        if (on) Color(0xFFFFE27A) else Color(0xFFCFC6E8),
    )
}

/** Hearts that float up from [from] after [since] seconds, fading as they go. */
internal fun DrawScope.drawHearts(from: Offset, since: Float) {
    for (i in 0 until 4) {
        val t = since - i * 0.2f
        if (t < 0f || t > 1.6f) continue
        val at = from + Offset(-36f + 24f * i, -70f * t)
        val alpha = (1f - t / 1.6f).coerceIn(0f, 1f)
        val r = 8f
        circle(Color(0xFFFF6F91), at + Offset(-r * 0.55f, 0f), r * 0.62f, alpha)
        circle(Color(0xFFFF6F91), at + Offset(r * 0.55f, 0f), r * 0.62f, alpha)
        fill(
            path {
                moveTo(at.x - r * 1.12f, at.y + 1f)
                lineTo(at.x + r * 1.12f, at.y + 1f)
                lineTo(at.x, at.y + r * 1.3f)
                close()
            },
            Color(0xFFFF6F91),
            alpha,
        )
    }
}
