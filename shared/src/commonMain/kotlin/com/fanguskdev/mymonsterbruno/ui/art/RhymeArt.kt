package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.fanguskdev.mymonsterbruno.game.RhymeWord
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.Word
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.star
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.sin

/**
 * The picture of each word of the rhymes (ADR 0021), centred on [center] and [size] units across: the duck, the moon,
 * the sun and the strawberry the game already draws, and the rest drawn here. [time] bobs the duck, wags the fish's tail
 * and makes the rocket's flame flicker.
 */
internal fun DrawScope.drawRhymePicture(
    word: RhymeWord,
    center: Offset,
    size: Float,
    time: Float,
) {
    when (word) {
        RhymeWord.GATO -> inBox(center, size) { cat() }
        RhymeWord.PATO -> drawStartPicture(StartWord.PATO, center, size, time)
        RhymeWord.LUNA -> drawWord(Word.LUNA, center, size)
        RhymeWord.CUNA -> inBox(center, size) { crib() }
        RhymeWord.RATON -> inBox(center, size) { mouse() }
        RhymeWord.BOTON -> inBox(center, size) { button() }
        RhymeWord.RANA -> inBox(center, size) { frog() }
        RhymeWord.LANA -> inBox(center, size) { yarn() }
        RhymeWord.SOL -> drawWord(Word.SOL, center, size)
        RhymeWord.COL -> inBox(center, size) { cabbage() }
        RhymeWord.PEZ -> inBox(center, size) { fish(time) }
        RhymeWord.NUEZ -> inBox(center, size) { walnut() }
        RhymeWord.MESA -> inBox(center, size) { table() }
        RhymeWord.FRESA -> drawTalkPicture(TalkWord.FRESA, center, size, time)
        RhymeWord.FOCA -> inBox(center, size) { seal() }
        RhymeWord.BOCA -> inBox(center, size) { mouth() }
        RhymeWord.LLAVE -> inBox(center, size) { key() }
        RhymeWord.NAVE -> inBox(center, size) { rocket(time) }
        RhymeWord.SETA -> inBox(center, size) { mushroom() }
        RhymeWord.MALETA -> inBox(center, size) { suitcase() }
    }
}

private val Pink = Color(0xFFF59AB0)
private val Blush = Color(0xFFFFA3B5)

/** A round eye of ink with its shine, the one of the duck. */
private fun DrawScope.inkEye(at: Offset, radius: Float) {
    circle(PictureInk, at, radius)
    circle(Color.White, at + Offset(radius * 0.36f, -radius * 0.36f), radius * 0.36f)
}

/** A ginger cat sitting, seen from the front: pointed ears, whiskers, a pink nose and its tail curled up at one side. */
private fun DrawScope.cat() {
    val fur = Color(0xFFF5A04A)
    val stripe = Color(0xFFE07A2C)
    val cream = Color(0xFFFFF3E0)
    ellipse(Shadow, Offset(4f, 40f), 36f, 5f)
    stroke(
        path {
            moveTo(10f, 36f)
            cubicTo(30f, 40f, 44f, 32f, 42f, 16f)
            quadraticTo(41f, 6f, 34f, 4f)
        },
        fur,
        9f,
    )
    fill(
        path {
            moveTo(-14f, -6f)
            cubicTo(-30f, 4f, -32f, 40f, -16f, 40f)
            lineTo(16f, 40f)
            cubicTo(32f, 40f, 30f, 4f, 14f, -6f)
            close()
        },
        fur,
    )
    for (side in listOf(-1f, 1f)) {
        for (y in listOf(14f, 24f)) {
            stroke(
                path {
                    moveTo(side * 27f, y)
                    quadraticTo(side * 22f, y + 1f, side * 20f, y + 5f)
                },
                stripe,
                3.5f,
            )
        }
    }
    ellipse(cream, Offset(0f, 20f), 11f, 15f)
    for (side in listOf(-1f, 1f)) ellipse(cream, Offset(side * 9f, 37f), 8.5f, 5f)
    for (side in listOf(-1f, 1f)) {
        val ear = path {
            moveTo(side * 24f, -24f)
            lineTo(side * 21f, -45f)
            lineTo(side * 5f, -34f)
            close()
        }
        fill(ear, fur)
        stroke(ear, fur, 5f)
        fill(
            path {
                moveTo(side * 20f, -28f)
                lineTo(side * 19.5f, -40f)
                lineTo(side * 10f, -33f)
                close()
            },
            Pink,
        )
    }
    ellipse(fur, Offset(0f, -16f), 25f, 21f)
    line(stripe, Offset(0f, -35f), Offset(0f, -28f), 3.2f)
    line(stripe, Offset(-7f, -34f), Offset(-6f, -28.5f), 3.2f)
    line(stripe, Offset(7f, -34f), Offset(6f, -28.5f), 3.2f)
    for (side in listOf(-1f, 1f)) ellipse(cream, Offset(side * 5.5f, -7f), 7f, 5.5f)
    for (side in listOf(-1f, 1f)) inkEye(Offset(side * 10f, -19f), 4.2f)
    for (side in listOf(-1f, 1f)) {
        for ((dy, to) in listOf(-1f to Offset(34f, -13f), 1f to Offset(35f, -7f), 3f to Offset(33f, -1f))) {
            line(PictureInk, Offset(side * 12f, -8f + dy), Offset(side * to.x, to.y), 1.8f)
        }
    }
    val nose = path {
        moveTo(-4f, -12.5f)
        lineTo(4f, -12.5f)
        lineTo(0f, -8.5f)
        close()
    }
    fill(nose, Pink)
    stroke(nose, Pink, 2f)
    stroke(
        path {
            moveTo(-5.5f, -5f)
            quadraticTo(-2.5f, -2.5f, 0f, -6.5f)
            quadraticTo(2.5f, -2.5f, 5.5f, -5f)
        },
        PictureInk,
        2f,
    )
}

/**
 * A baby's wooden cot seen from its side, on its rocker: the posts with their knobs and the bars, the pillow and the
 * blanket seen between them, and the end of the blanket hanging over the rail.
 */
private fun DrawScope.crib() {
    val wood = Color(0xFFE3A869)
    val dark = Color(0xFFC98A4B)
    val blanket = Color(0xFFF59AB8)
    val dots = Color(0xFFFFE3EE)
    ellipse(Shadow, Offset(0f, 41f), 40f, 5f)
    drawRoundRect(Color(0xFFBFDDF7), Offset(-37f, 8f), Size(74f, 11f), CornerRadius(4f))
    drawRoundRect(Color.White, Offset(-35f, -4f), Size(22f, 13f), CornerRadius(6.5f))
    drawRoundRect(Color(0xFF9CC7EE), Offset(-35f, -4f), Size(22f, 13f), CornerRadius(6.5f), style = Stroke(2f))
    drawRoundRect(blanket, Offset(-16f, -9f), Size(40f, 19f), CornerRadius(8f))
    for ((x, y) in listOf(-5f to -3f, 5f to 4f)) circle(dots, Offset(x, y), 2.4f)
    for (i in 0 until 7) drawRoundRect(wood, Offset(-32.5f + 10f * i, -18f), Size(5f, 40f), CornerRadius(2.5f))
    drawRoundRect(dark, Offset(-40f, -22f), Size(80f, 7f), CornerRadius(3.5f))
    drawRoundRect(dark, Offset(-40f, 18f), Size(80f, 8f), CornerRadius(4f))
    for (side in listOf(-1f, 1f)) {
        drawRoundRect(wood, Offset(side * 41f - 5f, -28f), Size(10f, 58f), CornerRadius(5f))
        circle(wood, Offset(side * 41f, -31f), 6.5f)
    }
    // The rocker it rocks on: a bed has none.
    stroke(
        path {
            moveTo(-46f, 26f)
            quadraticTo(0f, 52f, 46f, 26f)
        },
        dark,
        6f,
    )
    drawRoundRect(blanket, Offset(13f, -26f), Size(22f, 31f), CornerRadius(6f))
    drawRect(Color(0xFFE97BA2), Offset(13f, -3f), Size(22f, 3f))
    for ((x, y) in listOf(19f to -19f, 29f to -15f, 20f to -9f)) circle(dots, Offset(x, y), 2.4f)
}

/** A grey mouse in profile, looking right: big round ears, a pink nose with whiskers, little feet and a long tail. */
private fun DrawScope.mouse() {
    val grey = Color(0xFFAFB8C9)
    val dark = Color(0xFF939DB2)
    val light = Color(0xFFD6DBE5)
    ellipse(Shadow, Offset(-2f, 37f), 40f, 5f)
    stroke(
        path {
            moveTo(-28f, 28f)
            cubicTo(-44f, 34f, -50f, 20f, -44f, 10f)
            cubicTo(-40f, 2f, -32f, 6f, -35f, 12f)
        },
        Pink,
        3.5f,
    )
    circle(dark, Offset(-6f, -18f), 12f)
    for (x in listOf(-14f, 16f)) ellipse(Pink, Offset(x, 35f), 7f, 3.5f)
    val body = path {
        moveTo(42f, 16f)
        cubicTo(34f, -4f, 14f, -14f, -6f, -13f)
        cubicTo(-28f, -12f, -38f, 6f, -35f, 20f)
        cubicTo(-33f, 30f, -22f, 34f, -8f, 34f)
        lineTo(20f, 34f)
        cubicTo(32f, 34f, 40f, 26f, 42f, 16f)
        close()
    }
    fill(body, grey)
    clipPath(body) { ellipse(light, Offset(6f, 36f), 30f, 12f) }
    circle(grey, Offset(8f, -20f), 14f)
    circle(Pink, Offset(8f, -20f), 9f)
    circle(Blush, Offset(31f, 11f), 3.5f)
    inkEye(Offset(25f, 3f), 4f)
    for (to in listOf(Offset(48f, 9f), Offset(50f, 16f), Offset(47f, 23f))) line(PictureInk, Offset(38f, 17f), to, 1.6f)
    circle(Pink, Offset(42f, 15f), 4.5f)
}

/** A big round button, blue, with its rim, four holes and a white thread crossed through them. */
private fun DrawScope.button() {
    val blue = Color(0xFF4A9BE8)
    val deep = Color(0xFF2C74D8)
    val radius = 36f
    circle(Shadow, Offset(0f, 5f), radius)
    circle(blue, Offset.Zero, radius)
    circle(deep, Offset.Zero, radius * 0.72f)
    val holes = listOf(Offset(-9f, -9f), Offset(9f, -9f), Offset(9f, 9f), Offset(-9f, 9f))
    for (hole in holes) circle(Color(0xFF1B4C96), hole, 5f)
    line(Color.White, holes[0], holes[2], 3.5f)
    line(Color.White, holes[1], holes[3], 3.5f)
    stroke(
        path {
            moveTo(-30f, -6f)
            quadraticTo(-28f, -26f, -8f, -31f)
        },
        Color.White,
        4f,
        alpha = 0.5f,
    )
}

/** A green frog sitting, seen from the front: big eyes on top of its head, a wide smile and its legs at the sides. */
private fun DrawScope.frog() {
    val green = Color(0xFF6CC062)
    val dark = Color(0xFF4FA64A)
    val belly = Color(0xFFCBEA8E)
    ellipse(Shadow, Offset(0f, 40f), 42f, 5f)
    for (side in listOf(-1f, 1f)) {
        ellipse(dark, Offset(side * 29f, 27f), 15f, 12f)
        ellipse(dark, Offset(side * 36f, 38f), 10f, 4f)
    }
    ellipse(green, Offset(0f, 19f), 26f, 21f)
    ellipse(belly, Offset(0f, 24f), 16f, 14f)
    for (side in listOf(-1f, 1f)) {
        drawRoundRect(green, Offset(side * 14f - 4.5f, 18f), Size(9f, 20f), CornerRadius(4.5f))
        for (k in -1..1) circle(green, Offset(side * 14f + 4f * k, 39f), 3.2f)
    }
    ellipse(green, Offset(0f, -4f), 36f, 22f)
    for (side in listOf(-1f, 1f)) {
        circle(green, Offset(side * 18f, -23f), 13f)
        circle(Color.White, Offset(side * 18f, -24f), 9.5f)
        circle(Blush, Offset(side * 25f, 2f), 4f)
        circle(dark, Offset(side * 4f, -9f), 1.6f)
    }
    for (side in listOf(-1f, 1f)) inkEye(Offset(side * 18f, -23f), 5f)
    stroke(
        path {
            moveTo(-21f, -1f)
            quadraticTo(0f, 12f, 21f, -1f)
        },
        PictureInk,
        3f,
    )
}

/** A ball of wool, purple, with its threads wound round it, two knitting needles stuck in it and a loose end. */
private fun DrawScope.yarn() {
    val wool = Color(0xFF9B6BD3)
    val dark = Color(0xFF7D4FBA)
    val light = Color(0xFFBFA0EA)
    val center = Offset(-6f, 2f)
    val radius = 32f
    ellipse(Shadow, Offset(0f, 36f), 40f, 5f)
    for ((from, to) in listOf(Offset(-26f, -6f) to Offset(4f, -44f), Offset(-8f, -10f) to Offset(32f, -38f))) {
        line(Color(0xFFB0B8C8), from, to, 4f)
        circle(Color(0xFFF4B623), to, 4.5f)
    }
    stroke(
        path {
            moveTo(18f, 26f)
            cubicTo(28f, 40f, 46f, 40f, 46f, 30f)
            cubicTo(46f, 22f, 36f, 22f, 38f, 30f)
        },
        wool,
        4f,
    )
    val ball = path { addOval(Rect(center, radius)) }
    fill(ball, wool)
    clipPath(ball) {
        // The strands wound round it, one way and then the other, like the meridians of a globe.
        for ((tilt, color) in listOf(35f to dark, -50f to light)) {
            rotate(tilt, center) {
                for (k in listOf(0.3f, 0.62f, 0.94f)) {
                    drawOval(color, center - Offset(radius, radius * k), Size(radius * 2, radius * 2 * k), style = Stroke(3f))
                }
            }
        }
    }
}

/** A green cabbage: its round pale heart, two leaves wrapped round it with their veins, and the big leaves open at its foot. */
private fun DrawScope.cabbage() {
    val dark = Color(0xFF4FA64A)
    val leaf = Color(0xFF7FC76B)
    val pale = Color(0xFFBCE291)
    val vein = Color(0xFFE2F3C8)
    ellipse(Shadow, Offset(0f, 39f), 44f, 5f)
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(side * 2f, 37f)
                cubicTo(side * 24f, 39f, side * 44f, 32f, side * 46f, 16f)
                cubicTo(side * 47f, 6f, side * 40f, 0f, side * 32f, 4f)
                cubicTo(side * 22f, 10f, side * 12f, 24f, side * 2f, 37f)
                close()
            },
            dark,
        )
        stroke(
            path {
                moveTo(side * 6f, 34f)
                quadraticTo(side * 30f, 26f, side * 40f, 10f)
            },
            Color(0xFF8ED07C),
            2.5f,
        )
    }
    ellipse(dark, Offset(0f, 31f), 20f, 7f)
    circle(pale, Offset(0f, -2f), 31f)
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(side * -2f, 29f)
                cubicTo(side * 22f, 31f, side * 35f, 10f, side * 31f, -10f)
                cubicTo(side * 28f, -27f, side * 14f, -35f, side * -4f, -33f)
                cubicTo(side * 12f, -25f, side * 16f, 6f, side * -2f, 29f)
                close()
            },
            leaf,
        )
        stroke(
            path {
                moveTo(side * 2f, 26f)
                quadraticTo(side * 24f, 8f, side * 16f, -26f)
            },
            vein,
            2.5f,
        )
        for ((from, to) in CABBAGE_VEINS) line(vein, Offset(side * from.x, from.y), Offset(side * to.x, to.y), 2f)
    }
}

/** The little veins that branch off the rib of each leaf wrapped round the cabbage, drawn on its right. */
private val CABBAGE_VEINS = listOf(
    Offset(12.5f, 13.8f) to Offset(24f, 12f),
    Offset(17.1f, 1.4f) to Offset(28f, -5f),
    Offset(18f, -13f) to Offset(25f, -20f),
    Offset(15f, 6f) to Offset(9f, -2f),
    Offset(18f, -8f) to Offset(12f, -17f),
)

/** An orange fish swimming to the right, with its tail, its fins, a big eye and a smile, and two bubbles. */
private fun DrawScope.fish(time: Float) {
    val orange = Color(0xFFF28B2E)
    val light = Color(0xFFFFC069)
    val fin = Color(0xFFE4683C)
    rotate(6f * sin(time * 4f), Offset(-20f, 0f)) {
        fill(
            path {
                moveTo(-18f, 0f)
                cubicTo(-28f, -6f, -38f, -22f, -46f, -22f)
                quadraticTo(-40f, 0f, -46f, 22f)
                cubicTo(-38f, 22f, -28f, 6f, -18f, 0f)
                close()
            },
            fin,
        )
    }
    fill(
        path {
            moveTo(-12f, -16f)
            quadraticTo(-2f, -34f, 18f, -20f)
            close()
        },
        fin,
    )
    val body = path {
        moveTo(40f, 2f)
        cubicTo(36f, -18f, 14f, -24f, 0f, -22f)
        cubicTo(-16f, -20f, -26f, -8f, -26f, 0f)
        cubicTo(-26f, 8f, -16f, 20f, 0f, 22f)
        cubicTo(14f, 24f, 36f, 18f, 40f, 2f)
        close()
    }
    fill(body, orange)
    clipPath(body) { ellipse(light, Offset(6f, 20f), 30f, 10f) }
    fill(
        path {
            moveTo(10f, 3f)
            quadraticTo(0f, 2f, -6f, 12f)
            quadraticTo(4f, 14f, 10f, 3f)
            close()
        },
        fin,
    )
    circle(Color.White, Offset(24f, -6f), 7f)
    circle(PictureInk, Offset(25.5f, -5.5f), 4f)
    circle(Color.White, Offset(27f, -7f), 1.4f)
    stroke(
        path {
            moveTo(31f, 8f)
            quadraticTo(35f, 11f, 38f, 7f)
        },
        PictureInk,
        2.2f,
    )
    ring(Color(0xFF8CCBF0), Offset(44f, -20f), 4f, 2f)
    ring(Color(0xFF8CCBF0), Offset(40f, -34f), 5.5f, 2f)
}

/** A walnut in its shell, light brown, with the ridge down its middle, its little tip and the wrinkles of each half. */
private fun DrawScope.walnut() {
    val shell = Color(0xFFD29A5C)
    val dark = Color(0xFFA8703C)
    val light = Color(0xFFE8BC84)
    ellipse(Shadow, Offset(0f, 39f), 32f, 5f)
    fill(
        path {
            moveTo(0f, -42f)
            quadraticTo(2f, -37f, 8f, -36f)
            cubicTo(26f, -33f, 35f, -14f, 35f, 3f)
            cubicTo(35f, 24f, 20f, 38f, 0f, 38f)
            cubicTo(-20f, 38f, -35f, 24f, -35f, 3f)
            cubicTo(-35f, -14f, -26f, -33f, -8f, -36f)
            quadraticTo(-2f, -37f, 0f, -42f)
            close()
        },
        shell,
    )
    for ((a, b, c, d) in WALNUT_WRINKLES) {
        stroke(
            path {
                moveTo(a.x, a.y)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            },
            dark,
            2.8f,
        )
    }
    for ((dx, color) in listOf(-1.5f to light, 1.5f to dark)) {
        stroke(
            path {
                moveTo(dx, -38f)
                quadraticTo(dx + 3f, 0f, dx, 37f)
            },
            color,
            3f,
        )
    }
    tiltedEllipse(light, Offset(-20f, -18f), 4f, 8f, 30f)
}

/** The wrinkles of the walnut's shell, each from its first point to its last, a little different on each half. */
private val WALNUT_WRINKLES = listOf(
    listOf(Offset(-9f, -29f), Offset(-13f, -33f), Offset(-19f, -30f), Offset(-21f, -25f)),
    listOf(Offset(-6f, -18f), Offset(-11f, -24f), Offset(-21f, -21f), Offset(-27f, -14f)),
    listOf(Offset(-8f, -6f), Offset(-13f, -10f), Offset(-20f, -4f), Offset(-30f, -4f)),
    listOf(Offset(-6f, 6f), Offset(-12f, 2f), Offset(-22f, 10f), Offset(-30f, 8f)),
    listOf(Offset(-8f, 18f), Offset(-14f, 14f), Offset(-22f, 22f), Offset(-27f, 20f)),
    listOf(Offset(-7f, 29f), Offset(-11f, 26f), Offset(-16f, 32f), Offset(-20f, 31f)),
    listOf(Offset(9f, -28f), Offset(13f, -32f), Offset(18f, -30f), Offset(21f, -24f)),
    listOf(Offset(6f, -16f), Offset(12f, -22f), Offset(22f, -18f), Offset(28f, -12f)),
    listOf(Offset(8f, -3f), Offset(14f, -8f), Offset(22f, -1f), Offset(31f, -2f)),
    listOf(Offset(6f, 9f), Offset(13f, 5f), Offset(21f, 13f), Offset(30f, 10f)),
    listOf(Offset(8f, 21f), Offset(14f, 17f), Offset(21f, 24f), Offset(26f, 21f)),
    listOf(Offset(7f, 31f), Offset(11f, 28f), Offset(15f, 33f), Offset(19f, 31f)),
)

/** A wooden table seen a little from above, with its four legs and a little vase with a flower on top. */
private fun DrawScope.table() {
    val top = Color(0xFFD99C5C)
    val edge = Color(0xFFB5773F)
    val front = Color(0xFFC98A4B)
    val back = Color(0xFFA86E37)
    ellipse(Shadow, Offset(0f, 40f), 46f, 5f)
    for (side in listOf(-1f, 1f)) drawRoundRect(back, Offset(side * 30f - 3f, -6f), Size(6f, 38f), CornerRadius(3f))
    for (side in listOf(-1f, 1f)) drawRoundRect(front, Offset(side * 40f - 4f, -4f), Size(8f, 44f), CornerRadius(4f))
    fill(
        path {
            moveTo(-36f, -16f)
            lineTo(36f, -16f)
            lineTo(46f, -6f)
            lineTo(-46f, -6f)
            close()
        },
        top,
    )
    drawRoundRect(edge, Offset(-46f, -7f), Size(92f, 7f), CornerRadius(3f))
    line(Color(0xFF2E9E5B), Offset(0f, -18f), Offset(0f, -34f), 2.5f)
    fill(
        path {
            moveTo(-7f, -12f)
            quadraticTo(-9f, -22f, -4f, -24f)
            lineTo(4f, -24f)
            quadraticTo(9f, -22f, 7f, -12f)
            close()
        },
        Color(0xFF6FA8E6),
    )
    for (i in 0 until 5) {
        val angle = i * 72f
        rotate(angle, Offset(0f, -38f)) { ellipse(Color(0xFFE4483C), Offset(0f, -43f), 3.5f, 5f) }
    }
    circle(Color(0xFFF4C423), Offset(0f, -38f), 3.5f)
}

/** A grey seal sitting up, looking up, with its whiskers and a striped ball on the tip of its nose. */
private fun DrawScope.seal() {
    val grey = Color(0xFF8E9BB2)
    val light = Color(0xFFBCC6D6)
    val dark = Color(0xFF6E7B94)
    ellipse(Shadow, Offset(-4f, 40f), 38f, 5f)
    fill(
        path {
            moveTo(-30f, 34f)
            lineTo(-46f, 26f)
            quadraticTo(-48f, 38f, -36f, 40f)
            close()
        },
        dark,
    )
    val body = path {
        moveTo(-42f, 30f)
        cubicTo(-30f, 26f, -14f, 14f, -6f, -2f)
        cubicTo(-2f, -14f, 2f, -28f, 14f, -28f)
        cubicTo(20f, -28f, 24f, -26f, 28f, -22f)
        cubicTo(30f, -18f, 26f, -12f, 20f, -12f)
        cubicTo(24f, 0f, 26f, 24f, 16f, 36f)
        cubicTo(8f, 40f, -20f, 40f, -30f, 36f)
        cubicTo(-34f, 34f, -38f, 32f, -42f, 30f)
        close()
    }
    fill(body, grey)
    clipPath(body) { ellipse(light, Offset(34f, 14f), 18f, 30f) }
    tiltedEllipse(dark, Offset(6f, 24f), 6f, 13f, -25f)
    inkEye(Offset(12f, -20f), 3.6f)
    ellipse(PictureInk, Offset(28f, -21f), 3f, 2.4f)
    for (dot in listOf(Offset(22f, -16f), Offset(25f, -14f))) circle(dark, dot, 1f)
    line(PictureInk, Offset(23f, -15f), Offset(34f, -10f), 1.4f)
    line(PictureInk, Offset(23f, -14f), Offset(32f, -6f), 1.4f)
    val ballCenter = Offset(32f, -35f)
    val ball = path { addOval(Rect(ballCenter, 10f)) }
    fill(ball, Color(0xFF2C74D8))
    clipPath(ball) {
        ellipse(Color.White, ballCenter, 6.5f, 11f)
        ellipse(Color(0xFFE4483C), ballCenter, 3f, 11f)
    }
}

/** A mouth smiling wide: red lips, the white teeth on top and the pink tongue. */
private fun DrawScope.mouth() {
    val lips = Color(0xFFE4483C)
    val outer = path {
        moveTo(-44f, -4f)
        cubicTo(-34f, -14f, -22f, -22f, -12f, -20f)
        cubicTo(-6f, -19f, -3f, -15f, 0f, -14f)
        cubicTo(3f, -15f, 6f, -19f, 12f, -20f)
        cubicTo(22f, -22f, 34f, -14f, 44f, -4f)
        cubicTo(36f, 22f, 16f, 30f, 0f, 30f)
        cubicTo(-16f, 30f, -36f, 22f, -44f, -4f)
        close()
    }
    val opening = path {
        moveTo(-38f, -4f)
        cubicTo(-24f, -8f, -10f, -6f, 0f, -6f)
        cubicTo(10f, -6f, 24f, -8f, 38f, -4f)
        cubicTo(28f, 14f, 14f, 18f, 0f, 18f)
        cubicTo(-14f, 18f, -28f, 14f, -38f, -4f)
        close()
    }
    fill(outer, lips)
    fill(opening, Color(0xFF7A1F32))
    clipPath(opening) {
        drawRoundRect(Color.White, Offset(-30f, -12f), Size(60f, 12f), CornerRadius(4f))
        ellipse(Color(0xFFFF7FA0), Offset(0f, 18f), 18f, 9f)
    }
    tiltedEllipse(Color(0x66FFFFFF), Offset(-16f, 22f), 7f, 3f, -10f)
}

/** A golden key lying across: its round bow with a hole, the shaft and the teeth. */
private fun DrawScope.key() {
    val gold = Color(0xFFF4B623)
    val dark = Color(0xFFD99A1A)
    val bow = Offset(-27f, 0f)
    val solid = Path().apply {
        addOval(Rect(bow, 20f))
        addRoundRect(RoundRect(Rect(-12f, -5f, 42f, 5f), CornerRadius(5f)))
        addRect(Rect(24f, 0f, 31f, 16f))
        addRect(Rect(35f, 0f, 42f, 11f))
    }
    val shape = Path.combine(PathOperation.Difference, solid, path { addOval(Rect(bow, 8.5f)) })
    translate(0f, 5f) { rotate(-30f, Offset.Zero) { fill(shape, Shadow) } }
    rotate(-30f, Offset.Zero) {
        fill(shape, gold)
        ring(dark, bow, 14f, 2.5f)
        drawRoundRect(dark, Offset(-9f, -8f), Size(5f, 16f), CornerRadius(2f))
        stroke(
            path {
                moveTo(bow.x - 16f, bow.y + 2f)
                quadraticTo(bow.x - 15f, bow.y - 14f, bow.x - 2f, bow.y - 16.5f)
            },
            Color.White,
            3f,
            alpha = 0.6f,
        )
    }
}

/** A friendly rocket flying up: white with a red nose and red fins, a round window, its flame and two stars. */
private fun DrawScope.rocket(time: Float) {
    val red = Color(0xFFE4483C)
    val gold = Color(0xFFF4B623)
    fill(star(Offset(-34f, -26f), 6f), gold)
    fill(sparkle(Offset(34f, 26f), 7f), gold)
    rotate(20f, Offset.Zero) {
        val flicker = 1f + 0.12f * sin(time * 18f)
        fill(
            path {
                moveTo(-9f, 24f)
                quadraticTo(0f, 24f + 26f * flicker, 9f, 24f)
                close()
            },
            Color(0xFFF28B2E),
        )
        fill(
            path {
                moveTo(-5f, 24f)
                quadraticTo(0f, 24f + 15f * flicker, 5f, 24f)
                close()
            },
            Color(0xFFFFD23F),
        )
        for (side in listOf(-1f, 1f)) {
            fill(
                path {
                    moveTo(side * 12f, 4f)
                    quadraticTo(side * 26f, 12f, side * 24f, 30f)
                    lineTo(side * 12f, 22f)
                    close()
                },
                red,
            )
        }
        val body = path {
            moveTo(0f, -44f)
            cubicTo(16f, -32f, 18f, -8f, 15f, 24f)
            lineTo(-15f, 24f)
            cubicTo(-18f, -8f, -16f, -32f, 0f, -44f)
            close()
        }
        fill(body, Color(0xFFF4F6FB))
        clipPath(body) { drawRect(red, Offset(-20f, -50f), Size(40f, 22f)) }
        stroke(body, PictureInk, 3f)
        circle(Color(0xFF8CCBF0), Offset(0f, -8f), 9f)
        ring(Color(0xFF2C74D8), Offset(0f, -8f), 9f, 3f)
        circle(Color.White, Offset(-3f, -11f), 2.5f, alpha = 0.8f)
    }
}

/** A mushroom with a red cap and white spots, on its pale stalk. */
private fun DrawScope.mushroom() {
    val red = Color(0xFFE4483C)
    val stalk = Color(0xFFF6E3C2)
    val shade = Color(0xFFE2C496)
    ellipse(Shadow, Offset(0f, 40f), 30f, 5f)
    fill(
        path {
            moveTo(-13f, 0f)
            cubicTo(-14f, 16f, -20f, 30f, -18f, 36f)
            quadraticTo(-17f, 41f, 0f, 41f)
            quadraticTo(17f, 41f, 18f, 36f)
            cubicTo(20f, 30f, 14f, 16f, 13f, 0f)
            close()
        },
        stalk,
    )
    ellipse(shade, Offset(0f, 2f), 38f, 7f)
    val cap = path {
        moveTo(-44f, 4f)
        cubicTo(-46f, -24f, -24f, -40f, 0f, -40f)
        cubicTo(24f, -40f, 46f, -24f, 44f, 4f)
        cubicTo(30f, -2f, -30f, -2f, -44f, 4f)
        close()
    }
    fill(cap, red)
    for ((at, radius) in MUSHROOM_SPOTS) circle(Color.White, at, radius)
}

private val MUSHROOM_SPOTS = listOf(
    Offset(-24f, -16f) to 6f,
    Offset(0f, -30f) to 5f,
    Offset(18f, -18f) to 7f,
    Offset(-4f, -10f) to 4f,
    Offset(34f, -6f) to 3.5f,
    Offset(-36f, -4f) to 3.5f,
)

/** A suitcase, teal, with its handle on top, two straps with gold buckles and a couple of stickers from its trips. */
private fun DrawScope.suitcase() {
    val body = Color(0xFF2BB3A3)
    val strap = Color(0xFF1E8C80)
    val gold = Color(0xFFF4B623)
    ellipse(Shadow, Offset(0f, 41f), 44f, 5f)
    stroke(
        path {
            moveTo(-12f, -24f)
            lineTo(-12f, -33f)
            quadraticTo(-12f, -38f, -7f, -38f)
            lineTo(7f, -38f)
            quadraticTo(12f, -38f, 12f, -33f)
            lineTo(12f, -24f)
        },
        PictureInk,
        6f,
    )
    drawRoundRect(body, Offset(-44f, -26f), Size(88f, 64f), CornerRadius(10f))
    for (x in listOf(-24f, 24f)) {
        drawRect(strap, Offset(x - 4f, -26f), Size(8f, 64f))
        drawRoundRect(gold, Offset(x - 5.5f, -18f), Size(11f, 9f), CornerRadius(2f))
    }
    fill(star(Offset(0f, 6f), 11f), gold)
    circle(Color(0xFFE4483C), Offset(-34f, 22f), 6f)
    drawRoundRect(Color(0xFFFFF3E0), Offset(28f, 14f), Size(12f, 14f), CornerRadius(2f))
}
