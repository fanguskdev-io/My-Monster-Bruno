package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.Word
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.star
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The picture of each word of the island of words (ADR 0013), centred on [center] and [size] units across: the things
 * the game already draws where it has them, and the rest drawn here. [time] moves the butterfly and bobs the duck.
 */
internal fun DrawScope.drawStartPicture(
    word: StartWord,
    center: Offset,
    size: Float,
    time: Float,
) {
    when (word) {
        // Its sides make the glasses wider than on Bruno: a little smaller, so they fit.
        StartWord.GAFAS -> glasses(center, size * 0.92f)
        StartWord.CORONA -> crown(center, size * 1.2f)
        // The bow tie is small on Bruno's neck: bigger, so it fills the card like the others.
        StartWord.PAJARITA -> drawAccessoryIcon(Accessory.BOW_TIE, center, size * 1.7f)
        StartWord.GORRO -> beanie(center, size)
        StartWord.LUNA -> drawWord(Word.LUNA, center, size)
        StartWord.OSO -> drawWord(Word.OSO, center, size)
        StartWord.PELOTA -> drawWord(Word.PELOTA, center, size)
        StartWord.REGALO -> drawTalkPicture(TalkWord.REGALO, center, size, time)
        StartWord.MARIPOSA -> drawTalkPicture(TalkWord.MARIPOSA, center, size, time)
        // The hand stands to the left of its centre, where the thumb is: moved so that it is centred.
        StartWord.MANO -> drawHand(center + Offset(size * 0.11f, size * 0.02f), size * 0.9f, fingers = 5)
        StartWord.GALLETA -> inBox(center, size) { cookie() }
        StartWord.CORAZON -> inBox(center, size) { heart() }
        StartWord.CASA -> inBox(center, size) { house() }
        StartWord.CAMA -> inBox(center, size) { bed() }
        StartWord.PATO -> inBox(center, size) { duck(time) }
        StartWord.LUPA -> inBox(center, size) { magnifyingGlass() }
        StartWord.PERA -> inBox(center, size) { pear() }
        StartWord.GOTA -> inBox(center, size) { drop() }
        StartWord.OJO -> inBox(center, size) { eye() }
        StartWord.RELOJ -> inBox(center, size) { alarmClock() }
    }
}

/** Draws [draw], made in a box of 100 units round the origin, centred on [center] and [size] units across. */
internal fun DrawScope.inBox(center: Offset, size: Float, draw: DrawScope.() -> Unit) = withTransform({
    translate(center.x, center.y)
    scale(size / 100f, size / 100f, Offset.Zero)
}) { draw() }

internal val PictureInk = Color(0xFF1D2B53)
private val PictureWood = Color(0xFF8A5A3C)
private val Glass = Color(0xFFDDF1FC)

/**
 * Draws [draw] in Bruno's units, with the point [anchor] of him on [center], the way [drawAccessoryIcon] places an
 * accessory [size] units across: for what the pictures add round the accessories.
 */
private fun DrawScope.onAccessory(
    center: Offset,
    size: Float,
    anchor: Offset,
    draw: DrawScope.() -> Unit,
) = withTransform({
    translate(center.x, center.y)
    scale(size / 70f, size / 70f, Offset.Zero)
    translate(anchor.x, anchor.y)
}) { draw() }

/** Bruno's glasses with their lenses and the sides that go to the ears: two rings alone could be anything. */
private fun DrawScope.glasses(center: Offset, size: Float) {
    onAccessory(center, size, Offset(0f, 14f)) {
        for (side in listOf(-1f, 1f)) {
            circle(Glass, Offset(side * 15f, -14f), 13.5f)
            line(Ink.dark, Offset(side * 29f, -19f), Offset(side * 35f, -23f), 3f)
        }
    }
    drawAccessoryIcon(Accessory.GLASSES, center, size)
}

/** Bruno's crown, with a gold edge round its white star, which would not show on a white card. */
private fun DrawScope.crown(center: Offset, size: Float) {
    onAccessory(center, size, Offset(0f, 55f)) { fill(star(Offset(0f, -72f), 7.5f), Color(0xFFF4B623)) }
    drawAccessoryIcon(Accessory.CROWN, center, size)
}

/** Bruno's beanie, with an edge round its white pompom, which would not show on a white card. */
private fun DrawScope.beanie(center: Offset, size: Float) {
    onAccessory(center, size, Offset(0f, 55f)) { circle(Color(0xFFD5DCEA), Offset(0f, -72f), 8.8f) }
    drawAccessoryIcon(Accessory.BEANIE, center, size)
}

/** A cookie like the ones of Comeletras, with chips where the letter was. */
private fun DrawScope.cookie() {
    val radius = 36f
    circle(Shadow, Offset(0f, 5f), radius)
    circle(Color(0xFFE9B36A), Offset.Zero, radius)
    ring(Color(0xFFD49A4E), Offset.Zero, radius * 0.93f, radius * 0.1f)
    for ((dx, dy) in COOKIE_CHIPS) circle(Color(0xFFA7693A), Offset(dx * radius, dy * radius), radius * 0.09f)
}

private val COOKIE_CHIPS = listOf(
    -0.55f to -0.35f,
    0.58f to 0.3f,
    -0.38f to 0.55f,
    0.35f to -0.55f,
    -0.08f to -0.12f,
    0.22f to 0.6f,
    -0.62f to 0.14f,
    0.18f to 0.12f,
)

/** A red heart with its shine. */
private fun DrawScope.heart() {
    fill(
        path {
            moveTo(0f, 38f)
            cubicTo(-16f, 26f, -44f, 8f, -44f, -12f)
            cubicTo(-44f, -30f, -32f, -38f, -22f, -38f)
            cubicTo(-12f, -38f, -4f, -32f, 0f, -24f)
            cubicTo(4f, -32f, 12f, -38f, 22f, -38f)
            cubicTo(32f, -38f, 44f, -30f, 44f, -12f)
            cubicTo(44f, 8f, 16f, 26f, 0f, 38f)
            close()
        },
        Color(0xFFE4483C),
    )
    tiltedEllipse(Color(0x80FFFFFF), Offset(-26f, -18f), 6f, 10f, 30f)
}

/** A little house: yellow walls, a red roof with its chimney, a door and a window. */
private fun DrawScope.house() {
    ellipse(Shadow, Offset(0f, 38f), 42f, 6f)
    drawRect(Color(0xFFB5643A), Offset(14f, -38f), Size(10f, 22f))
    drawRoundRect(Color(0xFFFFD36E), Offset(-32f, -10f), Size(64f, 48f), CornerRadius(4f))
    val roof = path {
        moveTo(-40f, -6f)
        lineTo(0f, -40f)
        lineTo(40f, -6f)
        close()
    }
    fill(roof, Color(0xFFE4483C))
    stroke(roof, Color(0xFFE4483C), 6f)
    fill(
        path {
            moveTo(-24f, 38f)
            lineTo(-24f, 22f)
            quadraticTo(-24f, 13f, -16f, 13f)
            quadraticTo(-8f, 13f, -8f, 22f)
            lineTo(-8f, 38f)
            close()
        },
        PictureWood,
    )
    circle(Color(0xFFFFD36E), Offset(-11f, 27f), 1.8f)
    drawRoundRect(Color.White, Offset(2f, 2f), Size(24f, 22f), CornerRadius(3f))
    drawRoundRect(Color(0xFFA6DCF6), Offset(5f, 5f), Size(18f, 16f), CornerRadius(2f))
    line(Color.White, Offset(14f, 5f), Offset(14f, 21f), 2.5f)
    line(Color.White, Offset(5f, 13f), Offset(23f, 13f), 2.5f)
}

/** A small bed seen from the side: the headboard and the foot, the mattress, the pillow and the blanket. */
private fun DrawScope.bed() {
    ellipse(Shadow, Offset(0f, 37f), 46f, 5f)
    drawRoundRect(PictureWood, Offset(-46f, -30f), Size(11f, 68f), CornerRadius(5f))
    drawRoundRect(PictureWood, Offset(35f, -6f), Size(11f, 44f), CornerRadius(5f))
    drawRoundRect(PictureWood, Offset(-38f, 12f), Size(76f, 10f), CornerRadius(3f))
    drawRoundRect(Color(0xFFF1F2F8), Offset(-37f, -2f), Size(74f, 15f), CornerRadius(6f))
    drawRoundRect(Color(0xFFBFDDF7), Offset(-35f, -14f), Size(26f, 14f), CornerRadius(7f))
    drawRoundRect(Color(0xFF9B7BEA), Offset(-14f, -6f), Size(51f, 22f), CornerRadius(5f))
    drawRoundRect(Color(0xFFCDBDF6), Offset(-14f, -6f), Size(8f, 22f), CornerRadius(4f))
    for ((x, y) in listOf(4f to 2f, 20f to 9f, 30f to -1f, 8f to 12f)) circle(Color(0xFFCDBDF6), Offset(x, y), 2.6f)
}

/** A yellow duck in profile, looking right, that bobs a little on the water with [time]. */
private fun DrawScope.duck(time: Float) {
    ellipse(Color(0xFFBFE6F5), Offset(0f, 34f), 46f, 7f)
    withTransform({
        translate(0f, 1.6f * sin(time * 2.2f))
        rotate(2.5f * sin(time * 2.2f + 0.8f), Offset(0f, 30f))
    }) {
        val yellow = Color(0xFFFFD23F)
        fill(
            path {
                moveTo(-46f, -10f)
                quadraticTo(-36f, 4f, -14f, 4f)
                lineTo(8f, 4f)
                cubicTo(30f, 4f, 40f, 12f, 38f, 22f)
                cubicTo(36f, 32f, 22f, 36f, 2f, 36f)
                lineTo(-18f, 36f)
                cubicTo(-32f, 36f, -40f, 30f, -40f, 20f)
                quadraticTo(-38f, 4f, -46f, -10f)
                close()
            },
            yellow,
        )
        fill(
            path {
                moveTo(-26f, 12f)
                cubicTo(-18f, 2f, 2f, 4f, 8f, 14f)
                cubicTo(0f, 24f, -18f, 24f, -26f, 12f)
                close()
            },
            Color(0xFFF5B82E),
        )
        circle(yellow, Offset(14f, -14f), 18f)
        fill(
            path {
                moveTo(28f, -15f)
                quadraticTo(46f, -17f, 48f, -9f)
                quadraticTo(46f, -3f, 28f, -5f)
                close()
            },
            Color(0xFFF28B2E),
        )
        circle(PictureInk, Offset(19f, -19f), 3.6f)
        circle(Color.White, Offset(20.3f, -20.3f), 1.3f)
    }
}

/** A magnifying glass: the round lens with its shine and its rim, and the handle. */
private fun DrawScope.magnifyingGlass() {
    val lens = Offset(-8f, -8f)
    line(Color(0xFFE4483C), Offset(12f, 12f), Offset(36f, 36f), 13f)
    circle(Glass, lens, 27f)
    stroke(
        path {
            moveTo(lens.x - 17f, lens.y + 2f)
            quadraticTo(lens.x - 16f, lens.y - 14f, lens.x - 2f, lens.y - 17f)
        },
        Color.White,
        5f,
    )
    ring(Color(0xFF2C74D8), lens, 27f, 8f)
}

/** A pear, yellow green, with its stalk and a leaf. */
private fun DrawScope.pear() {
    ellipse(Shadow, Offset(0f, 41f), 26f, 5f)
    stroke(
        path {
            moveTo(0f, -28f)
            quadraticTo(0f, -38f, 5f, -44f)
        },
        Color(0xFF6B4A2E),
        4f,
    )
    fill(
        path {
            moveTo(0f, -32f)
            cubicTo(10f, -32f, 13f, -22f, 14f, -12f)
            cubicTo(15f, -2f, 32f, 6f, 32f, 22f)
            cubicTo(32f, 36f, 18f, 42f, 0f, 42f)
            cubicTo(-18f, 42f, -32f, 36f, -32f, 22f)
            cubicTo(-32f, 6f, -15f, -2f, -14f, -12f)
            cubicTo(-13f, -22f, -10f, -32f, 0f, -32f)
            close()
        },
        Color(0xFFC3D84A),
    )
    fill(
        path {
            moveTo(3f, -38f)
            quadraticTo(16f, -48f, 26f, -38f)
            quadraticTo(14f, -32f, 3f, -38f)
            close()
        },
        Color(0xFF2E9E5B),
    )
    tiltedEllipse(Color(0x66FFFFFF), Offset(-15f, 16f), 5f, 10f, 15f)
}

/** A drop of water, blue, with its shine. */
private fun DrawScope.drop() {
    fill(
        path {
            moveTo(0f, -42f)
            cubicTo(6f, -28f, 32f, -6f, 32f, 14f)
            cubicTo(32f, 32f, 18f, 42f, 0f, 42f)
            cubicTo(-18f, 42f, -32f, 32f, -32f, 14f)
            cubicTo(-32f, -6f, -6f, -28f, 0f, -42f)
            close()
        },
        Color(0xFF4A9BE8),
    )
    stroke(
        path {
            moveTo(-20f, 10f)
            quadraticTo(-20f, 26f, -8f, 32f)
        },
        Color.White,
        5f,
        alpha = 0.7f,
    )
}

/** An eye: the white almond with its lashes, the iris, the pupil and a shine. */
private fun DrawScope.eye() {
    val almond = path {
        moveTo(-44f, 4f)
        quadraticTo(0f, -40f, 44f, 4f)
        quadraticTo(0f, 44f, -44f, 4f)
        close()
    }
    // The lashes stand out of the upper lid, which goes through 4 - 88 t (1 - t) as t goes from one corner to the other.
    for (t in listOf(0.2f, 0.35f, 0.5f, 0.65f, 0.8f)) {
        val lid = Offset(44f * (2f * t - 1f), 4f - 88f * t * (1f - t))
        val out = lid - Offset(0f, 30f)
        val length = out.getDistance()
        line(PictureInk, lid, lid + out * (9f / length), 3.5f)
    }
    fill(almond, Color.White)
    circle(Color(0xFF4A8FE0), Offset(0f, 3f), 16f)
    circle(PictureInk, Offset(0f, 3f), 7.5f)
    circle(Color.White, Offset(4.5f, -1.5f), 3f)
    stroke(almond, PictureInk, 3.5f)
}

/** An alarm clock: its round face with the marks and the hands, two bells on top and its little feet. */
private fun DrawScope.alarmClock() {
    val red = Color(0xFFE4483C)
    val gold = Color(0xFFF4B623)
    val face = Offset(0f, 4f)
    ellipse(Shadow, Offset(0f, 42f), 30f, 5f)
    line(PictureInk, Offset(-16f, 28f), Offset(-24f, 40f), 6f)
    line(PictureInk, Offset(16f, 28f), Offset(24f, 40f), 6f)
    line(PictureInk, Offset(0f, face.y - 30f), Offset(0f, face.y - 38f), 3f)
    circle(PictureInk, Offset(0f, face.y - 40f), 4f)
    for (side in listOf(-1f, 1f)) {
        rotate(side * 40f, face) {
            val base = face.y - 28f
            fill(
                path {
                    moveTo(-11f, base)
                    quadraticTo(-11f, base - 14f, 0f, base - 14f)
                    quadraticTo(11f, base - 14f, 11f, base)
                    close()
                },
                gold,
            )
            circle(gold, Offset(0f, base - 15f), 2.5f)
        }
    }
    circle(red, face, 32f)
    circle(Color.White, face, 25f)
    for (i in 0 until 12) {
        val angle = i * PI / 6
        val at = face + Offset((20f * sin(angle)).toFloat(), (-20f * cos(angle)).toFloat())
        circle(PictureInk, at, if (i % 3 == 0) 2.6f else 1.5f)
    }
    line(PictureInk, face, face + Offset(-10f, -6f), 4f)
    line(PictureInk, face, face + Offset(0f, -16f), 3f)
    circle(red, face, 3f)
}

/** A small open book, [size] units across: the cover under the two pages, with their lines, as if she were reading it. */
internal fun DrawScope.drawOpenBook(center: Offset, size: Float) = inBox(center, size) {
    val cover = path {
        moveTo(0f, -18f)
        quadraticTo(-24f, -30f, -48f, -22f)
        lineTo(-48f, 26f)
        quadraticTo(-24f, 20f, 0f, 32f)
        quadraticTo(24f, 20f, 48f, 26f)
        lineTo(48f, -22f)
        quadraticTo(24f, -30f, 0f, -18f)
        close()
    }
    translate(0f, 5f) { fill(cover, Shadow) }
    fill(cover, Color(0xFF2C74D8))
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(0f, -24f)
                quadraticTo(side * 20f, -36f, side * 44f, -28f)
                lineTo(side * 44f, 20f)
                quadraticTo(side * 20f, 12f, 0f, 26f)
                close()
            },
            Color(0xFFFFF6E5),
        )
        for (k in 0 until 4) {
            val y = 9.5f * k
            stroke(
                path {
                    moveTo(side * 8f, y - 14f)
                    quadraticTo(side * 21f, y - 19f, side * 36f, y - 17f)
                },
                Color(0xFFC9D2E6),
                2.6f,
            )
        }
    }
    line(Color(0xFFE2D3BC), Offset(0f, -24f), Offset(0f, 26f), 2f)
}

/**
 * The reading corner of the classroom, on the island of words: a warm wall with a low bookshelf on the left, behind
 * Bruno, a round rug on the wooden floor and a little garland up on the right. The rest of the wall is plain, for the
 * bubble, the syllables and the cards; [time] only sways the garland.
 */
internal fun DrawScope.drawLibrary(stage: Stage, time: Float) {
    fullWidth(stage, Color(0xFFFFF0CF), stage.top, 296f)
    fullWidth(stage, Color(0xFFFBE0B4), 262f, 296f)
    fullWidth(stage, Color(0xFFE6B47C), 296f, stage.bottom)
    var plank = 330f
    while (plank < stage.bottom) {
        drawLine(Color(0xFFD49B5E), Offset(stage.left, plank), Offset(stage.right, plank), 2f)
        plank += 36f
    }
    garland(time)
    bookshelf()
    rug()
}

/** A band of [color] across the whole screen, from [from] to [to]. */
private fun DrawScope.fullWidth(
    stage: Stage,
    color: Color,
    from: Float,
    to: Float,
) = drawRect(color, Offset(stage.left, from), Size(stage.right - stage.left, to - from))

/** The low bookshelf of the left, standing on the floor: two shelves of books, a plant and a pile of books on top. */
private fun DrawScope.bookshelf() {
    drawRoundRect(Shadow, Offset(14f, 166f), Size(248f, 140f), CornerRadius(6f))
    drawRoundRect(Color(0xFFC98A4B), Offset(14f, 160f), Size(248f, 140f), CornerRadius(6f))
    for (top in listOf(172f, 236f)) drawRect(Color(0xFFA86E37), Offset(26f, top), Size(224f, 52f))
    books(26f, 250f, 224f, 0)
    books(26f, 250f, 288f, 3)
    drawRoundRect(Color(0xFFD99C5C), Offset(8f, 152f), Size(260f, 12f), CornerRadius(5f))
    // A little plant at one end, and a pile of books at the other.
    fill(
        path {
            moveTo(30f, 132f)
            lineTo(56f, 132f)
            lineTo(52f, 152f)
            lineTo(34f, 152f)
            close()
        },
        Color(0xFFD9774B),
    )
    for ((x, tilt) in listOf(36f to -30f, 43f to 0f, 50f to 30f)) {
        tiltedEllipse(Color(0xFF6CC062), Offset(x, 120f), 6f, 13f, tilt)
    }
    // Low, under the little circles of Bruno's thought bubble.
    drawRoundRect(Color(0xFF6FA8E6), Offset(214f, 145f), Size(42f, 8f), CornerRadius(2.5f))
    drawRoundRect(Color(0xFFEB7465), Offset(219f, 138f), Size(34f, 7f), CornerRadius(2.5f))
}

/** A row of books standing on a shelf at [bottom], from [left] to [right], the [first] of the list first. */
private fun DrawScope.books(
    left: Float,
    right: Float,
    bottom: Float,
    first: Int,
) {
    var x = left + 2f
    var i = first
    while (x + BOOK_WIDTHS[i % BOOK_WIDTHS.size] <= right - 2f) {
        val width = BOOK_WIDTHS[i % BOOK_WIDTHS.size]
        val height = BOOK_HEIGHTS[i % BOOK_HEIGHTS.size]
        val color = BOOK_COLORS[i % BOOK_COLORS.size]
        drawRoundRect(color, Offset(x, bottom - height), Size(width, height), CornerRadius(2f))
        drawRect(Color(0x55FFFFFF), Offset(x, bottom - height + 5f), Size(width, 3f))
        drawRect(Color(0x55FFFFFF), Offset(x, bottom - 9f), Size(width, 3f))
        x += width + 1.5f
        i++
    }
}

private val BOOK_WIDTHS = listOf(14f, 11f, 16f, 12f, 15f, 10f, 13f)
private val BOOK_HEIGHTS = listOf(42f, 38f, 46f, 40f, 44f, 36f, 45f, 39f)
private val BOOK_COLORS = listOf(
    Color(0xFFEB7465),
    Color(0xFF6FA8E6),
    Color(0xFFF6CB55),
    Color(0xFF62B98A),
    Color(0xFFB08FE0),
    Color(0xFFF4A057),
    Color(0xFFF59AB8),
    Color(0xFF5EC2B4),
)

/** The round rug of the reading corner, where Bruno stands. */
private fun DrawScope.rug() {
    val center = Offset(150f, 342f)
    ellipse(Color(0xFFE7907C), center, 140f, 28f)
    ellipse(Color(0xFFF9D9A0), center, 120f, 22f)
    ellipse(Color(0xFFF2B37C), center, 92f, 15f)
}

/** A little garland of paper flags, pinned to the wall up on the right, swaying a little with [time]. */
private fun DrawScope.garland(time: Float) {
    val from = Offset(560f, 14f)
    val sag = Offset(644f, 44f)
    val to = Offset(728f, 14f)
    stroke(
        path {
            moveTo(from.x, from.y)
            quadraticTo(sag.x, sag.y, to.x, to.y)
        },
        Color(0xFFC9A87C),
        2f,
    )
    GARLAND_COLORS.forEachIndexed { i, color ->
        val t = (i + 0.5f) / GARLAND_COLORS.size
        val at = from * ((1 - t) * (1 - t)) + sag * (2 * t * (1 - t)) + to * (t * t)
        rotate(2.5f * sin(time * 0.8f + i), at) {
            fill(
                path {
                    moveTo(at.x - 9f, at.y)
                    lineTo(at.x + 9f, at.y)
                    lineTo(at.x, at.y + 20f)
                    close()
                },
                color,
            )
        }
    }
    for (pin in listOf(from, to)) circle(Color(0xFFC9A87C), pin, 3f)
}

private val GARLAND_COLORS = listOf(
    Color(0xFFF2A39A),
    Color(0xFFF7D37A),
    Color(0xFF9FD3C7),
    Color(0xFFB9A6E8),
    Color(0xFF8FC1EE),
)
