package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Tale
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.star
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A page of a story for bedtime (ADR 0045), at night: [page] of [tale], [since] seconds after it started, so that what
 * happens in it moves slowly, as it is told.
 */
internal fun DrawScope.drawTalePage(
    tale: Tale,
    page: Int,
    since: Float,
    time: Float,
    look: BrunoLook,
    stage: Stage,
) {
    val p = easeInOut(clamp01(since / PAGE_MOVE_SECONDS))
    when (tale) {
        Tale.STAR -> starPage(page, p, time, look, stage)
        Tale.FISH -> fishPage(page, p, time, look, stage)
        Tale.MOON -> moonPage(page, p, time, look, stage)
    }
}

/** How long what happens in a page takes: slow, for bedtime. */
private const val PAGE_MOVE_SECONDS = 4f

private val NIGHT_TOP = Color(0xFF141A44)
private val NIGHT_LOW = Color(0xFF2E3A7A)
private val SEA = Color(0xFF233269)
private val STAR = Color(0xFFFFE27A)

private fun DrawScope.sky(stage: Stage, time: Float, moon: Boolean = true) {
    drawRect(
        Brush.verticalGradient(listOf(NIGHT_TOP, NIGHT_LOW), startY = stage.top, endY = stage.bottom),
        Offset(stage.left, stage.top),
        Size(stage.right - stage.left, stage.bottom - stage.top),
    )
    val stars = listOf(Offset(90f, 50f), Offset(210f, 110f), Offset(330f, 40f), Offset(470f, 90f), Offset(610f, 50f), Offset(720f, 120f))
    stars.forEachIndexed { i, at -> fill(sparkle(at, 5f + 2.5f * sin(time * 1.6f + i)), Color.White, alpha = 0.85f) }
    if (moon) {
        circle(Color(0xFFFFF3C4), Offset(690f, 70f), 30f)
        circle(NIGHT_TOP, Offset(704f, 60f), 27f)
    }
}

private fun DrawScope.sea(stage: Stage, time: Float, top: Float = 260f) {
    drawRect(SEA, Offset(stage.left, top), Size(stage.right - stage.left, stage.bottom - top))
    for (k in 0 until 7) {
        val x = stage.left + 60f + k * 120f + 10f * sin(time * 0.8f + k)
        val y = top + 30f + (k % 3) * 34f
        stroke(
            path {
                moveTo(x - 18f, y)
                quadraticTo(x - 9f, y - 7f, x, y)
                quadraticTo(x + 9f, y + 7f, x + 18f, y)
            },
            Color(0xFF6F82C9),
            3f,
            alpha = 0.7f,
        )
    }
}

private fun DrawScope.islet(center: Offset, rx: Float, ry: Float) {
    ellipse(Color(0xFFB59A62), center, rx, ry)
    ellipse(Color(0xFF3F7A4B), center + Offset(0f, -6f), rx - 18f, ry - 10f)
}

/** The little star of the first story, with a face: sad, happy or, at the end, winking. */
private fun DrawScope.littleStar(
    center: Offset,
    size: Float,
    mood: Int,
    time: Float,
) {
    circle(STAR, center, size * 1.25f, alpha = 0.18f + 0.06f * sin(time * 3f))
    fill(star(center, size, size * 0.48f), STAR)
    val ink = Color(0xFF5A4300)
    val eye = size * 0.12f
    if (mood == WINK) {
        line(ink, center + Offset(-size * 0.32f, -size * 0.06f), center + Offset(-size * 0.12f, -size * 0.06f), eye * 0.8f)
    } else {
        circle(ink, center + Offset(-size * 0.22f, -size * 0.06f), eye)
    }
    circle(ink, center + Offset(size * 0.22f, -size * 0.06f), eye)
    val smile = if (mood == SAD) -1f else 1f
    stroke(
        path {
            moveTo(center.x - size * 0.2f, center.y + size * 0.18f)
            quadraticTo(center.x, center.y + size * (0.18f + 0.16f * smile), center.x + size * 0.2f, center.y + size * 0.18f)
        },
        ink,
        eye * 0.8f,
    )
}

private const val SAD = 0
private const val HAPPY = 1
private const val WINK = 2

private fun DrawScope.starPage(
    page: Int,
    p: Float,
    time: Float,
    look: BrunoLook,
    stage: Stage,
) {
    sky(stage, time)
    sea(stage, time)
    val island = Offset(590f, 282f)
    when (page) {
        0 -> drawBoatWith(look, Offset(260f + 120f * p, 330f + 4f * sin(time * 1.5f)), time)
        1 -> {
            islet(island, 120f, 34f)
            val fall = lerp(Offset(180f, 40f), island + Offset(0f, -40f), p)
            for (k in 1..4) circle(STAR, lerp(Offset(180f, 40f), fall, 1f - k * 0.08f), 6f - k, alpha = 0.6f)
            littleStar(fall, 26f, SAD, time)
            drawBoatWith(look, Offset(220f, 334f + 4f * sin(time * 1.5f)), time)
        }
        2 -> {
            islet(island, 120f, 34f)
            littleStar(island + Offset(30f, -40f), 30f, SAD, time)
            drawBoatWith(look, Offset(lerp(Offset(200f, 0f), Offset(400f, 0f), p).x, 334f + 4f * sin(time * 1.5f)), time)
        }
        3 -> {
            islet(island, 120f, 34f)
            val at = island + Offset(30f, -40f)
            balloonsOver(at, p, time)
            littleStar(at, 30f, HAPPY, time)
            drawBruno(look, island + Offset(-60f, -70f), 110f, Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP), time)
        }
        4 -> {
            islet(island, 120f, 34f)
            val at = lerp(island + Offset(30f, -40f), Offset(560f, 70f), p)
            balloonsOver(at, 1f, time)
            littleStar(at, 30f, HAPPY, time)
            drawBruno(look, island + Offset(-60f, -70f), 110f, Pose(Mouth.OPEN, Eyes.WIDE, Arms.UP, lookX = 0.4f, lookY = -1f), time)
        }
        else -> {
            littleStar(Offset(560f, 90f), 30f, if (sin(time * 2f) > 0.3f) WINK else HAPPY, time)
            drawBoatWith(look, Offset(400f - 160f * p, 334f + 4f * sin(time * 1.5f)), time)
        }
    }
}

private fun DrawScope.balloonsOver(at: Offset, shown: Float, time: Float) {
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFFF4B623))
    for ((k, color) in colors.withIndex()) {
        if (shown < k / 3f) continue
        val top = at + Offset(-34f + 34f * k, -78f + 4f * sin(time * 2f + k))
        line(Color.White, at, top + Offset(0f, 20f), 1.5f, alpha = 0.8f)
        drawBalloon(top, 40f, color)
    }
}

/** The orange fish of the second story, with its eyes open, half closed or closed, as sleepy as [sleep], 0 to 1. */
private fun DrawScope.seaFish(
    center: Offset,
    size: Float,
    sleep: Float,
    facing: Float = 1f,
) {
    val orange = Color(0xFFF28B2E)
    fill(
        path {
            moveTo(center.x - facing * size * 0.7f, center.y)
            lineTo(center.x - facing * size * 1.15f, center.y - size * 0.38f)
            lineTo(center.x - facing * size * 1.15f, center.y + size * 0.38f)
            close()
        },
        orange,
    )
    ellipse(orange, center, size * 0.8f, size * 0.5f)
    ellipse(Color(0xFFFFB066), center + Offset(facing * size * 0.05f, size * 0.12f), size * 0.45f, size * 0.22f)
    val eye = center + Offset(facing * size * 0.42f, -size * 0.12f)
    circle(Color.White, eye, size * 0.16f)
    if (sleep >= 1f) {
        line(Color(0xFF2A2238), eye + Offset(-size * 0.1f, 0f), eye + Offset(size * 0.1f, 0f), size * 0.05f)
    } else {
        circle(Color(0xFF2A2238), eye, size * 0.09f)
        if (sleep > 0f) drawRect(orange, eye - Offset(size * 0.17f, size * 0.17f), Size(size * 0.34f, size * 0.17f * sleep * 1.6f))
    }
}

private fun DrawScope.fishPage(
    page: Int,
    p: Float,
    time: Float,
    look: BrunoLook,
    stage: Stage,
) {
    sky(stage, time)
    sea(stage, time, top = 180f)
    val boat = Offset(330f, 190f + 4f * sin(time * 1.5f))
    when (page) {
        0 -> seaFish(Offset(420f, 300f + 6f * sin(time * 2f)), 50f, 0f)
        1 -> {
            val a = time * 1.6f
            seaFish(Offset(420f + 90f * cos(a), 300f + 40f * sin(a)), 50f, 0f, if (sin(a) > 0f) -1f else 1f)
        }
        2 -> {
            drawBoatWith(look, boat, time)
            seaFish(Offset(470f, 310f), 50f, 0f, -1f)
        }
        3 -> {
            drawBoatWith(look, boat, time)
            notes(boat + Offset(40f, -120f), time)
            seaFish(Offset(470f, 310f), 50f, 0.3f, -1f)
        }
        4 -> {
            drawBoatWith(look, boat, time)
            for (k in 0 until 3) {
                val lit = p * 3f > k
                val crest = Offset(520f + k * 80f, 230f)
                stroke(
                    path {
                        moveTo(crest.x - 26f, crest.y)
                        quadraticTo(crest.x - 13f, crest.y - 12f, crest.x, crest.y)
                        quadraticTo(crest.x + 13f, crest.y + 12f, crest.x + 26f, crest.y)
                    },
                    if (lit) Color.White else Color(0xFF6F82C9),
                    5f,
                )
            }
            seaFish(Offset(470f, 320f), 50f, 0.3f + 0.6f * p, -1f)
        }
        else -> {
            drawBoatWith(look, boat, time)
            seaFish(Offset(470f, 320f + 4f * sin(time)), 50f, 1f, -1f)
            sleepyZs(Offset(530f, 280f), time)
        }
    }
}

/** Little notes going up, as Bruno sings very quietly. */
private fun DrawScope.notes(from: Offset, time: Float) {
    for (k in 0 until 3) {
        val phase = (time * 0.4f + k / 3f) % 1f
        val at = from + Offset(20f * sin(time + k) + 30f * phase, -60f * phase)
        val alpha = 1f - phase
        circle(Color.White, at, 6f, alpha = alpha)
        line(Color.White, at + Offset(5f, 0f), at + Offset(5f, -18f), 2.5f, alpha = alpha)
        line(Color.White, at + Offset(5f, -18f), at + Offset(13f, -14f), 2.5f, alpha = alpha)
    }
}

/** Three Zs going up: sleeping. */
private fun DrawScope.sleepyZs(from: Offset, time: Float) {
    for (k in 0 until 3) {
        val phase = (time * 0.35f + k / 3f) % 1f
        val at = from + Offset(24f * phase, -50f * phase)
        val s = 7f + 6f * phase
        stroke(
            path {
                moveTo(at.x - s, at.y - s)
                lineTo(at.x + s, at.y - s)
                lineTo(at.x - s, at.y + s)
                lineTo(at.x + s, at.y + s)
            },
            Color.White,
            2.5f,
            alpha = 1f - phase,
        )
    }
}

/** The moon of the third story, round and with a face: shivering, or warm in Bruno's woolly hat. */
private fun DrawScope.moonFace(
    center: Offset,
    radius: Float,
    cold: Boolean,
    hat: Boolean,
    time: Float,
) {
    val at = if (cold) center + Offset(3f * sin(time * 40f), 0f) else center
    if (!cold) {
        for (k in 0 until 12) {
            val a = k * PI.toFloat() / 6f + time * 0.2f
            line(
                Color(0xFFFFF3C4),
                at + Offset(cos(a), sin(a)) * (radius * 1.2f),
                at + Offset(cos(a), sin(a)) * (radius * 1.45f),
                4f,
                alpha = 0.6f,
            )
        }
    }
    circle(Color(0xFFFFF3C4), at, radius)
    circle(Color(0xFFEFDFA0), at + Offset(-radius * 0.35f, radius * 0.3f), radius * 0.12f)
    val ink = Color(0xFF6B5A2E)
    circle(ink, at + Offset(-radius * 0.3f, -radius * 0.1f), radius * 0.08f)
    circle(ink, at + Offset(radius * 0.3f, -radius * 0.1f), radius * 0.08f)
    if (cold) {
        line(ink, at + Offset(-radius * 0.22f, radius * 0.32f), at + Offset(radius * 0.22f, radius * 0.32f), radius * 0.06f)
        for (side in listOf(-1f, 1f)) {
            for (k in 0 until 2) {
                val x = at.x + side * radius * (1.25f + k * 0.18f)
                stroke(
                    path {
                        moveTo(x, at.y - radius * 0.25f)
                        quadraticTo(x + side * 6f, at.y, x, at.y + radius * 0.25f)
                    },
                    Color.White,
                    3f,
                    alpha = 0.7f,
                )
            }
        }
    } else {
        stroke(
            path {
                moveTo(at.x - radius * 0.25f, at.y + radius * 0.28f)
                quadraticTo(at.x, at.y + radius * 0.48f, at.x + radius * 0.25f, at.y + radius * 0.28f)
            },
            ink,
            radius * 0.06f,
        )
    }
    if (hat) drawWoollyHat(at + Offset(0f, -radius * 0.55f), radius / 34f)
}

private fun DrawScope.hill(stage: Stage) {
    fill(
        path {
            moveTo(stage.left, 330f)
            quadraticTo(220f, 230f, 460f, 300f)
            quadraticTo(640f, 350f, stage.right, 320f)
            lineTo(stage.right, stage.bottom)
            lineTo(stage.left, stage.bottom)
            close()
        },
        Color(0xFF2E5A4A),
    )
}

private fun DrawScope.snow(time: Float) {
    for (k in 0 until 18) {
        val x = (k * 47f + 30f * sin(time * 0.6f + k)) % 800f
        val y = (time * 25f + k * 53f) % 400f
        circle(Color.White, Offset(x, y), 2.5f + (k % 3), alpha = 0.85f)
    }
}

/** Bruno's house on the hill, its window lit. */
private fun DrawScope.house(center: Offset, brunoInside: BrunoLook?, time: Float) {
    drawRect(Color(0xFFE8D9B5), center + Offset(-60f, -40f), Size(120f, 80f))
    fill(
        path {
            moveTo(center.x - 74f, center.y - 38f)
            lineTo(center.x, center.y - 94f)
            lineTo(center.x + 74f, center.y - 38f)
            close()
        },
        Color(0xFF9C3B33),
    )
    drawRect(Color(0xFFFFD36E), center + Offset(-40f, -26f), Size(46f, 40f))
    if (brunoInside !=
        null
    ) {
        drawBruno(brunoInside, center + Offset(-17f, -2f), 46f, Pose(Mouth.O, Eyes.OPEN, lookX = 0.6f, lookY = -0.8f), time)
    }
    drawRect(Color(0xFF5B3A2A), center + Offset(16f, -6f), Size(26f, 46f))
}

private fun DrawScope.moonPage(
    page: Int,
    p: Float,
    time: Float,
    look: BrunoLook,
    stage: Stage,
) {
    sky(stage, time, moon = false)
    val moon = Offset(590f, 100f)
    when (page) {
        0 -> {
            hill(stage)
            house(Offset(180f, 290f), null, time)
            snow(time)
        }
        1 -> {
            moonFace(moon, 56f, cold = true, hat = false, time = time)
            hill(stage)
            snow(time)
        }
        2 -> {
            moonFace(moon, 56f, cold = true, hat = false, time = time)
            hill(stage)
            house(Offset(180f, 290f), look, time)
            snow(time)
        }
        3 -> {
            moonFace(moon, 56f, cold = true, hat = false, time = time)
            hill(stage)
            drawBruno(look, Offset(400f, 196f), 110f, Pose(Mouth.SMILE, Eyes.OPEN, Arms.UP, lookX = 1f, lookY = -1f), time)
            drawWoollyHat(lerp(Offset(420f, 120f), moon + Offset(0f, -30f), p), 1.4f)
            snow(time)
        }
        4 -> {
            moonFace(moon, 56f, cold = false, hat = true, time = time)
            hill(stage)
            drawBruno(look, Offset(400f, 196f), 110f, Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP), time)
        }
        else -> {
            moonFace(moon, 56f, cold = false, hat = true, time = time)
            hill(stage)
            fill(
                path {
                    moveTo(moon.x - 30f, moon.y + 50f)
                    lineTo(160f, 300f)
                    lineTo(300f, 330f)
                    close()
                },
                Color(0x33FFF3C4),
            )
            house(Offset(180f, 290f), null, time)
            drawBruno(look, lerp(Offset(400f, 236f), Offset(250f, 296f), p), 90f, Pose(Mouth.SMILE, Eyes.OPEN, lookX = -1f), time)
        }
    }
}
