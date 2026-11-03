package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.Slot
import com.fanguskdev.mymonsterbruno.game.Word
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.star
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** The things of the game, drawn with the shapes of the mockups: each one centred on a point of the stage. */

internal val Shadow = Color(0x291D2B53)

/** The egg Bruno hatches from: [cracks] from 0 to 5 draw its cracks, from the first line to the big one. */
internal fun DrawScope.drawEgg(
    center: Offset,
    width: Float,
    height: Float,
    cracks: Int,
    tilt: Float,
) {
    rotate(tilt, Offset(center.x, center.y + height / 2)) {
        val egg = path {
            moveTo(center.x, center.y - height / 2)
            cubicTo(
                center.x + width * 0.55f,
                center.y - height / 2,
                center.x + width / 2,
                center.y + height / 2,
                center.x,
                center.y + height / 2,
            )
            cubicTo(
                center.x - width / 2,
                center.y + height / 2,
                center.x - width * 0.55f,
                center.y - height / 2,
                center.x,
                center.y - height / 2,
            )
            close()
        }
        fill(egg, Color(0xFFFFF6E5))
        for ((dx, dy, r) in listOf(
            Triple(-0.22f, 0.1f, 0.07f),
            Triple(0.2f, 0.25f, 0.05f),
            Triple(0.12f, -0.18f, 0.045f),
            Triple(-0.1f, 0.33f, 0.035f),
        )) {
            circle(Color(0xFFF7C9D6), Offset(center.x + dx * width, center.y + dy * height), r * width)
        }
        val crackColor = Color(0xFFB9A58A)
        val lines = listOf(
            listOf(Offset(-0.05f, -0.42f), Offset(0.02f, -0.32f), Offset(-0.04f, -0.24f)),
            listOf(Offset(0.18f, -0.3f), Offset(0.12f, -0.2f), Offset(0.2f, -0.12f)),
            listOf(Offset(-0.25f, -0.18f), Offset(-0.16f, -0.12f), Offset(-0.24f, -0.04f)),
            listOf(Offset(-0.04f, -0.24f), Offset(0.06f, -0.16f), Offset(0.12f, -0.2f)),
            listOf(
                Offset(-0.38f, -0.02f),
                Offset(-0.26f, -0.1f),
                Offset(-0.14f, 0f),
                Offset(-0.02f, -0.1f),
                Offset(0.1f, 0f),
                Offset(0.22f, -0.1f),
                Offset(0.36f, -0.02f),
            ),
        )
        for (crack in lines.take(cracks)) {
            stroke(
                path {
                    crack.forEachIndexed { i, p ->
                        val x = center.x + p.x * width
                        val y = center.y + p.y * height
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                },
                crackColor,
                3.5f,
            )
        }
    }
}

/** The bottom half of the hatched egg, with its zigzag edge: Bruno stands in it while she chooses how he looks. */
internal fun DrawScope.drawEggBottom(center: Offset, width: Float, height: Float) {
    val half = width / 2
    val top = center.y - height * 0.02f
    val bottom = center.y + height / 2
    val shell = path {
        moveTo(center.x - half * 0.98f, top)
        val teeth = 8
        for (i in 1..teeth) {
            val x = center.x - half * 0.98f + (half * 1.96f) * i / teeth
            val y = if (i % 2 == 1) top - height * 0.09f else top
            lineTo(x, y)
        }
        cubicTo(center.x + half, center.y + height * 0.3f, center.x + half * 0.6f, bottom, center.x, bottom)
        cubicTo(center.x - half * 0.6f, bottom, center.x - half, center.y + height * 0.3f, center.x - half * 0.98f, top)
        close()
    }
    fill(shell, Color(0xFFFFF6E5))
    circle(Color(0xFFF7C9D6), Offset(center.x - width * 0.22f, center.y + height * 0.2f), width * 0.06f)
    circle(Color(0xFFF7C9D6), Offset(center.x + width * 0.2f, center.y + height * 0.32f), width * 0.045f)
}

/** A cookie with a capital [letter] on it, [radius] units. */
internal fun DrawScope.drawCookie(
    center: Offset,
    radius: Float,
    letter: Char,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    circle(Shadow, center + Offset(0f, 5f), radius)
    circle(Color(0xFFE9B36A), center, radius)
    ring(Color(0xFFD49A4E), center, radius * 0.93f, radius * 0.1f)
    for ((dx, dy) in listOf(-0.55f to -0.35f, 0.58f to 0.3f, -0.38f to 0.55f, 0.35f to -0.55f)) {
        circle(Color(0xFFA7693A), center + Offset(dx * radius, dy * radius), radius * 0.075f)
    }
    label(measurer, letter.uppercaseChar().toString(), center + Offset(0f, radius * 0.06f), radius * 1.15f, Color(0xFF6B3E26), font)
}

/** A strawberry, [radius] units. */
internal fun DrawScope.drawStrawberry(center: Offset, radius: Float) {
    val berry = path {
        moveTo(center.x, center.y + radius * 1.1f)
        cubicTo(
            center.x - radius * 1.3f,
            center.y + radius * 0.2f,
            center.x - radius * 1.1f,
            center.y - radius * 0.9f,
            center.x,
            center.y - radius * 0.7f,
        )
        cubicTo(
            center.x + radius * 1.1f,
            center.y - radius * 0.9f,
            center.x + radius * 1.3f,
            center.y + radius * 0.2f,
            center.x,
            center.y + radius * 1.1f,
        )
        close()
    }
    fill(berry, Color(0xFFE4483C))
    for ((dx, dy) in listOf(-0.35f to -0.2f, 0.3f to -0.25f, 0f to 0.1f, -0.4f to 0.25f, 0.38f to 0.22f, 0f to 0.55f)) {
        ellipse(Color(0xFFFFE08A), center + Offset(dx * radius, dy * radius), radius * 0.06f, radius * 0.09f)
    }
    val leaf = Color(0xFF2E9E5B)
    for (i in 0 until 5) {
        val angle = PI + PI * i / 4
        val tip = center + Offset((radius * 0.75f * cos(angle)).toFloat(), (-radius * 0.65f + radius * 0.35f * sin(angle)).toFloat())
        stroke(
            path {
                moveTo(center.x, center.y - radius * 0.7f)
                lineTo(tip.x, tip.y)
            },
            leaf,
            radius * 0.22f,
        )
    }
    circle(leaf, center + Offset(0f, -radius * 0.78f), radius * 0.16f)
}

/** A plate seen from the side, [width] units wide. */
internal fun DrawScope.drawPlate(center: Offset, width: Float) {
    ellipse(Shadow, center + Offset(0f, 6f), width / 2, width * 0.085f)
    ellipse(Color.White, center, width / 2, width * 0.085f)
    ellipse(Color(0xFFF1F2F8), center + Offset(0f, -2f), width * 0.42f, width * 0.05f)
}

/** The strawberry bush, [width] units wide, round and green. */
internal fun DrawScope.drawBush(center: Offset, width: Float) {
    val dark = Color(0xFF4FA64A)
    val light = Color(0xFF6CC062)
    val r = width / 4.4f
    for ((dx, dy) in listOf(-1.25f to 0.45f, 1.25f to 0.45f, 0f to 0.5f)) circle(dark, center + Offset(dx * r, dy * r), r * 1.1f)
    for ((dx, dy) in listOf(-0.75f to -0.35f, 0.75f to -0.35f, 0f to -0.7f, -1.4f to 0.25f, 1.4f to 0.25f)) {
        circle(light, center + Offset(dx * r, dy * r), r)
    }
    circle(light, center + Offset(0f, 0.2f * r), r * 1.2f)
}

/** The picture of each word, the one in Bruno's thought bubble, [size] units across. */
internal fun DrawScope.drawWord(word: Word, center: Offset, size: Float) {
    val k = size / 100f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        when (word) {
            Word.AVION -> plane()
            Word.ELEFANTE -> elephant()
            Word.IGLU -> igloo()
            Word.OSO -> bear()
            Word.UVA -> grapes()
            Word.MANZANA -> apple()
            Word.PELOTA -> ball()
            Word.LUNA -> moon()
            Word.SOL -> sun()
        }
    }
}

private fun DrawScope.plane() {
    fill(
        path {
            moveTo(-8f, -6f)
            lineTo(14f, -38f)
            lineTo(25f, -38f)
            lineTo(14f, -6f)
            close()
        },
        Color(0xFF2C74D8),
    )
    fill(
        path {
            moveTo(-45f, -4f)
            lineTo(-55f, -28f)
            lineTo(-44f, -28f)
            lineTo(-34f, -6f)
            close()
        },
        Color(0xFFE4483C),
    )
    val body = path {
        moveTo(-50f, 2f)
        quadraticTo(-50f, -10f, -38f, -10f)
        lineTo(38f, -10f)
        quadraticTo(54f, -10f, 54f, 0f)
        quadraticTo(54f, 10f, 38f, 10f)
        lineTo(-38f, 10f)
        quadraticTo(-50f, 10f, -50f, 2f)
        close()
    }
    fill(body, Color(0xFFF4F6FB))
    stroke(body, Color(0xFF1D2B53), 3f)
    fill(
        path {
            moveTo(-8f, 6f)
            lineTo(14f, 34f)
            lineTo(25f, 34f)
            lineTo(14f, 6f)
            close()
        },
        Color(0xFF2C74D8),
    )
    for (x in listOf(-26f, -14f, -2f, 28f)) circle(Color(0xFF8CCBF0), Offset(x, 0f), 3.5f)
}

private fun DrawScope.elephant() {
    val grey = Color(0xFFA9B4C8)
    val dark = Color(0xFF8C97AD)
    ellipse(grey, Offset(6f, 12f), 34f, 24f)
    for (x in listOf(-16f, 0f, 16f, 28f)) drawRoundRect(dark, Offset(x - 6f, 22f), Size(12f, 18f), CornerRadius(4f))
    circle(grey, Offset(-26f, -10f), 20f)
    ellipse(dark, Offset(-14f, -10f), 12f, 16f)
    stroke(
        path {
            moveTo(-40f, -4f)
            quadraticTo(-52f, 14f, -44f, 28f)
        },
        grey,
        9f,
    )
    circle(Color(0xFF2A2238), Offset(-32f, -14f), 2.6f)
    stroke(
        path {
            moveTo(38f, 6f)
            quadraticTo(46f, 2f, 44f, -6f)
        },
        dark,
        3f,
    )
}

private fun DrawScope.igloo() {
    val ice = Color(0xFFEAF6FF)
    val line = Color(0xFFB9D7F0)
    fill(
        path {
            moveTo(-46f, 30f)
            quadraticTo(-46f, -34f, 0f, -34f)
            quadraticTo(46f, -34f, 46f, 30f)
            close()
        },
        ice,
    )
    for (y in listOf(-14f, 6f)) line(line, Offset(-42f, y), Offset(42f, y), 3f)
    for ((x, y) in listOf(-20f to -24f, 14f to -24f, -30f to -4f, 2f to -4f, 30f to -4f, -14f to 18f, 18f to 18f)) {
        line(line, Offset(x, y - 8f), Offset(x, y + 8f), 3f)
    }
    fill(
        path {
            moveTo(-16f, 30f)
            quadraticTo(-16f, 2f, 0f, 2f)
            quadraticTo(16f, 2f, 16f, 30f)
            close()
        },
        Color(0xFF5B7FA8),
    )
    line(Color(0xFF9ED8F5), Offset(-56f, 30f), Offset(56f, 30f), 4f)
}

private fun DrawScope.bear() {
    val brown = Color(0xFFA86B45)
    val light = Color(0xFFE3BC95)
    circle(brown, Offset(-28f, -24f), 13f)
    circle(brown, Offset(28f, -24f), 13f)
    circle(light, Offset(-28f, -24f), 7f)
    circle(light, Offset(28f, -24f), 7f)
    circle(brown, Offset(0f, 0f), 36f)
    ellipse(light, Offset(0f, 13f), 17f, 13f)
    ellipse(Color(0xFF3A2A24), Offset(0f, 6f), 7f, 5f)
    stroke(
        path {
            moveTo(-6f, 17f)
            quadraticTo(0f, 22f, 6f, 17f)
        },
        Color(0xFF3A2A24),
        2.6f,
    )
    circle(Color(0xFF2A2238), Offset(-13f, -7f), 4f)
    circle(Color(0xFF2A2238), Offset(13f, -7f), 4f)
}

private fun DrawScope.grapes() {
    val grape = Color(0xFF8E6CC9)
    val light = Color(0xFFB59BE3)
    line(Color(0xFF6B4A2E), Offset(0f, -40f), Offset(4f, -26f), 4f)
    fill(
        path {
            moveTo(4f, -34f)
            quadraticTo(22f, -44f, 30f, -30f)
            quadraticTo(16f, -26f, 4f, -34f)
            close()
        },
        Color(0xFF2E9E5B),
    )
    val rows = listOf(listOf(-24f, -8f, 8f, 24f), listOf(-16f, 0f, 16f), listOf(-8f, 8f), listOf(0f))
    rows.forEachIndexed { row, xs ->
        for (x in xs) {
            val c = Offset(x, -14f + row * 15f)
            circle(grape, c, 9.5f)
            circle(light, c + Offset(-3f, -3f), 2.6f)
        }
    }
}

private fun DrawScope.apple() {
    circle(Color(0xFFE4483C), Offset(-10f, 6f), 26f)
    circle(Color(0xFFE4483C), Offset(10f, 6f), 26f)
    circle(Color(0xFFFF8A80), Offset(-14f, -4f), 6f, alpha = 0.7f)
    line(Color(0xFF6B4A2E), Offset(0f, -18f), Offset(3f, -34f), 4f)
    fill(
        path {
            moveTo(3f, -28f)
            quadraticTo(20f, -40f, 28f, -26f)
            quadraticTo(14f, -20f, 3f, -28f)
            close()
        },
        Color(0xFF2E9E5B),
    )
}

private fun DrawScope.ball() {
    circle(Color(0xFF2C74D8), Offset.Zero, 34f)
    stroke(
        path {
            moveTo(-34f, 0f)
            quadraticTo(0f, -22f, 34f, 0f)
        },
        Color.White,
        6f,
    )
    stroke(
        path {
            moveTo(-30f, 16f)
            quadraticTo(0f, -4f, 30f, 16f)
        },
        Color(0xFFF4C423),
        6f,
    )
    circle(Color.White, Offset(-12f, -16f), 5f, alpha = 0.6f)
}

private fun DrawScope.moon() {
    fill(
        path {
            moveTo(6f, -36f)
            cubicTo(-36f, -30f, -36f, 30f, 6f, 36f)
            cubicTo(-14f, 22f, -14f, -22f, 6f, -36f)
            close()
        },
        Color(0xFFF4C423),
    )
    fill(star(Offset(22f, -12f), 7f), Color(0xFFF4C423))
}

private fun DrawScope.sun() {
    for (i in 0 until 8) {
        val angle = i * PI / 4
        val from = Offset((24 * cos(angle)).toFloat(), (24 * sin(angle)).toFloat())
        val to = Offset((38 * cos(angle)).toFloat(), (38 * sin(angle)).toFloat())
        line(Color(0xFFF28B2E), from, to, 6f)
    }
    circle(Color(0xFFF4C423), Offset.Zero, 22f)
    circle(Color(0xFF1D2B53), Offset(-7f, -4f), 2.5f)
    circle(Color(0xFF1D2B53), Offset(7f, -4f), 2.5f)
    stroke(
        path {
            moveTo(-8f, 6f)
            quadraticTo(0f, 13f, 8f, 6f)
        },
        Color(0xFF1D2B53),
        2.5f,
    )
}

/** A present box, [size] units: [open] from 0 to 1 lifts its lid away. */
internal fun DrawScope.drawPresent(center: Offset, size: Float, open: Float) {
    val box = Color(0xFF9B6BD3)
    val ribbon = Color(0xFFF4B623)
    val half = size / 2
    drawRoundRect(Shadow, Offset(center.x - half, center.y - half * 0.6f + 8f), Size(size, size * 0.8f), CornerRadius(12f))
    drawRoundRect(box, Offset(center.x - half, center.y - half * 0.6f), Size(size, size * 0.8f), CornerRadius(12f))
    drawRect(ribbon, Offset(center.x - size * 0.08f, center.y - half * 0.6f), Size(size * 0.16f, size * 0.8f))
    val lift = open * size * 1.1f
    rotate(-25f * open, Offset(center.x - half, center.y - half * 0.6f)) {
        val lidTop = center.y - half * 0.6f - size * 0.2f - lift
        drawRoundRect(Color(0xFF8257C2), Offset(center.x - half * 1.08f, lidTop), Size(size * 1.08f, size * 0.22f), CornerRadius(10f))
        drawRect(ribbon, Offset(center.x - size * 0.08f, lidTop), Size(size * 0.16f, size * 0.22f))
        val bow = Offset(center.x, lidTop)
        fill(
            path {
                moveTo(bow.x, bow.y)
                quadraticTo(bow.x - size * 0.32f, bow.y - size * 0.3f, bow.x - size * 0.08f, bow.y - size * 0.02f)
                close()
            },
            ribbon,
        )
        fill(
            path {
                moveTo(bow.x, bow.y)
                quadraticTo(bow.x + size * 0.32f, bow.y - size * 0.3f, bow.x + size * 0.08f, bow.y - size * 0.02f)
                close()
            },
            ribbon,
        )
    }
}

/** An accessory on its own, the one that comes out of the present, [size] units across. */
internal fun DrawScope.drawAccessoryIcon(accessory: Accessory, center: Offset, size: Float) {
    // Drawn where it goes on Bruno, moved so that it is centred on [center].
    val anchor = when (accessory.slot) {
        Slot.HEAD -> Offset(0f, 55f)
        Slot.FACE -> Offset(0f, 14f)
        Slot.NECK -> Offset(0f, -32f)
    }
    val k = size / 70f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
        translate(anchor.x, anchor.y)
    }) {
        drawAccessory(accessory)
    }
}

/** A hand pointing up, white with a dark edge (the «touch here» of the mockups), [size] units high. */
internal fun DrawScope.drawPointer(center: Offset, size: Float) {
    val k = size / 60f
    withTransform({
        translate(center.x - 24f * k, center.y - 30f * k)
        scale(k, k, Offset.Zero)
    }) {
        val hand = path {
            moveTo(19f, 33f)
            lineTo(19f, 9f)
            cubicTo(19f, 2.3f, 29f, 2.3f, 29f, 9f)
            lineTo(29f, 27f)
            lineTo(30f, 26f)
            cubicTo(32.5f, 23.5f, 38f, 25f, 38f, 29f)
            lineTo(39f, 28.5f)
            cubicTo(41.8f, 26f, 45.6f, 28.5f, 45.6f, 32f)
            lineTo(45.6f, 44f)
            cubicTo(45.6f, 51.7f, 39.3f, 58f, 31.6f, 58f)
            lineTo(25f, 58f)
            cubicTo(20.4f, 58f, 16f, 55.7f, 13.5f, 52f)
            lineTo(5.5f, 40.5f)
            cubicTo(3.3f, 37f, 8.6f, 32.4f, 12.5f, 34.5f)
            close()
        }
        fill(hand, Color.White)
        stroke(hand, Color(0xFF1D2B53), 2.6f)
    }
}

/** A balloon of [color], [size] units high, on its string. */
internal fun DrawScope.drawBalloon(center: Offset, size: Float, color: Color) {
    val w = size * 0.42f
    val h = size / 2
    fill(balloonShape(center, size), color)
    ellipse(Color.White, center + Offset(-w * 0.45f, -h * 0.4f), w * 0.18f, h * 0.25f, alpha = 0.35f)
    fill(
        path {
            moveTo(center.x - 6f, center.y + h + 8f)
            lineTo(center.x + 6f, center.y + h + 8f)
            lineTo(center.x, center.y + h - 2f)
            close()
        },
        color,
    )
}

/** The outline of a balloon [size] units high centred on [center], without its knot. */
internal fun balloonShape(center: Offset, size: Float): Path {
    val w = size * 0.42f
    val h = size / 2
    return path {
        moveTo(center.x, center.y - h)
        cubicTo(center.x + w * 1.3f, center.y - h, center.x + w * 1.1f, center.y + h * 0.55f, center.x, center.y + h)
        cubicTo(center.x - w * 1.1f, center.y + h * 0.55f, center.x - w * 1.3f, center.y - h, center.x, center.y - h)
        close()
    }
}

/** A balloon whose string goes from its knot to [tiedTo], bending a little as if the wind moved it. */
internal fun DrawScope.drawTiedBalloon(
    center: Offset,
    size: Float,
    color: Color,
    tiedTo: Offset,
) {
    val knot = center + Offset(0f, size / 2 + 8f)
    stroke(
        path {
            moveTo(knot.x, knot.y)
            quadraticTo((knot.x + tiedTo.x) / 2 + 10f, (knot.y + tiedTo.y) / 2, tiedTo.x, tiedTo.y)
        },
        Color(0xFF8C8FA6),
        2f,
    )
    drawBalloon(center, size, color)
}

/** The outline of [shape] filling [box]: the square and the rectangle with soft corners, the triangle pointing up. */
internal fun shapePath(shape: Shape, box: Rect): Path = when (shape) {
    Shape.SQUARE, Shape.RECTANGLE -> Path().apply { addRoundRect(RoundRect(box, CornerRadius(6f))) }
    Shape.TRIANGLE -> path {
        moveTo(box.center.x, box.top)
        lineTo(box.right, box.bottom)
        lineTo(box.left, box.bottom)
        close()
    }
    Shape.CIRCLE -> Path().apply { addOval(box) }
}

/** The box a loose piece of [shape] takes centred on [center], about [size] units: a rectangle stands up, like a door. */
internal fun pieceBox(shape: Shape, center: Offset, size: Float): Rect = when (shape) {
    Shape.SQUARE -> Rect(center, size * 0.43f)
    Shape.TRIANGLE -> Rect(center.x - size * 0.5f, center.y - size * 0.43f, center.x + size * 0.5f, center.y + size * 0.43f)
    Shape.CIRCLE -> Rect(center, size * 0.45f)
    Shape.RECTANGLE -> Rect(center.x - size * 0.28f, center.y - size * 0.5f, center.x + size * 0.28f, center.y + size * 0.5f)
}

/** A piece of [shape] filling [box] in [color], with its shadow under it. */
internal fun DrawScope.drawShapePiece(shape: Shape, box: Rect, color: Color) {
    fill(shapePath(shape, box.translate(0f, 5f)), Shadow)
    fill(shapePath(shape, box), color)
}

/** The colour of [hue] on the screen: bright, and different enough from the others to tell them apart at a glance. */
internal fun Hue.paint(): Color = when (this) {
    Hue.RED -> Color(0xFFE4483C)
    Hue.YELLOW -> Color(0xFFF4C423)
    Hue.BLUE -> Color(0xFF2C74D8)
    Hue.GREEN -> Color(0xFF2E9E5B)
    Hue.ORANGE -> Color(0xFFF28B2E)
    Hue.PURPLE -> Color(0xFF9B6BD3)
}

/** A hand with [fingers] fingers up, [size] units high: how many, the way they count in the classroom. */
internal fun DrawScope.drawHand(center: Offset, size: Float, fingers: Int) {
    val skin = Color(0xFFF5C7A1)
    val edge = Color(0xFFD9A07A)
    val k = size / 100f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        val palm = path {
            moveTo(-26f, 0f)
            lineTo(26f, 0f)
            quadraticTo(30f, 40f, 0f, 46f)
            quadraticTo(-30f, 40f, -26f, 0f)
            close()
        }
        val tips = listOf(Offset(-22f, -32f), Offset(-8f, -42f), Offset(6f, -44f), Offset(20f, -36f))
        tips.forEachIndexed { i, tip ->
            val up = i < fingers.coerceAtMost(4)
            val end = if (up) tip else Offset(tip.x, 2f)
            line(edge, Offset(tip.x, 8f), end, 14f)
            line(skin, Offset(tip.x, 8f), end, 11f)
        }
        val thumbUp = fingers >= 5
        val thumbEnd = if (thumbUp) Offset(-46f, -6f) else Offset(-20f, 12f)
        line(edge, Offset(-20f, 22f), thumbEnd, 14f)
        line(skin, Offset(-20f, 22f), thumbEnd, 11f)
        fill(palm, skin)
        stroke(palm, edge, 2f)
    }
}

/** A round button with its [drawIcon], [radius] units; [glow] from 0 to 1 pulses a ring round it to call her. */
internal fun DrawScope.drawRoundButton(
    center: Offset,
    radius: Float,
    color: Color,
    glow: Float = 0f,
    drawIcon: DrawScope.() -> Unit,
) {
    if (glow > 0f) ring(Color.White, center, radius * (1.05f + 0.25f * glow), 5f, alpha = 1f - glow)
    circle(Shadow, center + Offset(0f, 5f), radius)
    circle(color, center, radius)
    drawIcon()
}

/** The ▶ of the big button that goes to the islands. */
internal fun DrawScope.playIcon(center: Offset, size: Float) = fill(
    path {
        moveTo(center.x - size * 0.32f, center.y - size * 0.45f)
        lineTo(center.x + size * 0.48f, center.y)
        lineTo(center.x - size * 0.32f, center.y + size * 0.45f)
        close()
    },
    Color.White,
)

/** The little house of the button that goes back home. */
internal fun DrawScope.homeIcon(center: Offset, size: Float) = fill(
    path {
        val s = size / 26f
        moveTo(center.x - 13f * s, center.y - 0.5f * s)
        lineTo(center.x, center.y - 12f * s)
        lineTo(center.x + 13f * s, center.y - 0.5f * s)
        lineTo(center.x + 13f * s, center.y + 11f * s)
        lineTo(center.x + 4.5f * s, center.y + 11f * s)
        lineTo(center.x + 4.5f * s, center.y + 3f * s)
        lineTo(center.x - 4.5f * s, center.y + 3f * s)
        lineTo(center.x - 4.5f * s, center.y + 11f * s)
        lineTo(center.x - 13f * s, center.y + 11f * s)
        close()
    },
    Color(0xFF1D2B53),
)

/** The loudspeaker of the button that says it again. */
internal fun DrawScope.speakerIcon(center: Offset, size: Float) {
    val s = size / 26f
    fill(
        path {
            moveTo(center.x - 13f * s, center.y - 5f * s)
            lineTo(center.x - 7f * s, center.y - 5f * s)
            lineTo(center.x + 1f * s, center.y - 12f * s)
            lineTo(center.x + 1f * s, center.y + 12f * s)
            lineTo(center.x - 7f * s, center.y + 5f * s)
            lineTo(center.x - 13f * s, center.y + 5f * s)
            close()
        },
        Color(0xFF1D2B53),
    )
    stroke(
        path {
            moveTo(center.x + 6f * s, center.y - 6f * s)
            quadraticTo(
                center.x + 10f * s,
                center.y,
                center.x + 6f * s,
                center.y + 6f * s,
            )
        },
        Color(0xFF1D2B53),
        3f * s,
    )
    stroke(
        path {
            moveTo(center.x + 10f * s, center.y - 10f * s)
            quadraticTo(
                center.x + 17f * s,
                center.y,
                center.x + 10f * s,
                center.y + 10f * s,
            )
        },
        Color(0xFF1D2B53),
        3f * s,
    )
}

/** The padlock of the grown-ups' corner; [held] from 0 to 1 fills the ring while a finger holds it. */
internal fun DrawScope.drawLock(center: Offset, radius: Float, held: Float) {
    circle(Color(0x66FFFFFF), center, radius)
    val s = radius / 22f
    stroke(
        path {
            moveTo(center.x - 6f * s, center.y - 2f * s)
            lineTo(center.x - 6f * s, center.y - 7f * s)
            quadraticTo(
                center.x,
                center.y - 15f * s,
                center.x + 6f * s,
                center.y - 7f * s,
            )
            lineTo(center.x + 6f * s, center.y - 2f * s)
        },
        Color(0xFF46526F),
        3f * s,
    )
    drawRoundRect(Color(0xFF46526F), Offset(center.x - 9f * s, center.y - 3f * s), Size(18f * s, 14f * s), CornerRadius(3f * s))
    if (held > 0f) {
        drawArc(
            Color(0xFF2E9E5B),
            -90f,
            360f * held,
            false,
            Offset(center.x - radius, center.y - radius),
            Size(2 * radius, 2 * radius),
            style = Stroke(5f),
        )
    }
}

/** Little sparkles round [center] while [time] goes, for what has just gone well. */
internal fun DrawScope.drawSparkles(
    center: Offset,
    radius: Float,
    time: Float,
    color: Color = Color(0xFFFFD36E),
) {
    for (i in 0 until 6) {
        val angle = i * PI / 3 + time * 0.8
        val pulse = 0.6f + 0.4f * sin(time * 6f + i)
        val p = center + Offset((radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat())
        fill(sparkle(p, 9f * pulse), color)
    }
}

/** A five-point star, the sticker of the map and the progress. */
internal fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) = fill(star(center, radius), color)
