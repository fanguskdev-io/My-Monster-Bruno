package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.fanguskdev.mymonsterbruno.game.Story
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The picture [step] (0 first, 1 next, 2 at the end) of [story], for «Ordena la historia» (ADR 0025), centred on [center]
 * and [size] units across. The three of a story share what they are about (the pot, the girl and her toothbrush, the cake,
 * the snow), so that they go together, and each one shows its moment on its own. [time] only moves what moves in it (the
 * seed, the drops, the brush, the bubbles, the whisk, the light of the oven, the flame, the snowflakes), never what it shows.
 */
internal fun DrawScope.drawStoryPicture(
    story: Story,
    step: Int,
    center: Offset,
    size: Float,
    time: Float,
) = inBox(center, size) {
    when (story) {
        Story.FLOWER -> when (step) {
            0 -> sowing(time)
            1 -> watering(time)
            else -> blooming(time)
        }
        Story.TEETH -> when (step) {
            0 -> squeezing()
            1 -> brushing(time)
            else -> shining(time)
        }
        Story.CAKE -> when (step) {
            0 -> mixing(time)
            1 -> baking(time)
            else -> cakeOnPlate(time)
        }
        Story.SNOWMAN -> when (step) {
            0 -> firstSnowball(time)
            1 -> twoSnowballs(time)
            else -> snowman(time)
        }
    }
}

private val Gold = Color(0xFFF4B623)

/** Little gold sparkles at [places], each one pulsing a little with [time]. */
private fun DrawScope.sparkles(places: List<Offset>, size: Float, time: Float) {
    places.forEachIndexed { i, at -> fill(sparkle(at, size * (0.85f + 0.15f * sin(time * 3f + i * 2f))), Gold) }
}

// The flower.

private val Clay = Color(0xFFD9774B)
private val ClayRim = Color(0xFFE98E62)
private val Earth = Color(0xFF7A5134)
private val EarthDark = Color(0xFF5A3A24)
private val Leaf = Color(0xFF6CC062)
private val Stalk = Color(0xFF3E9A4A)
private val Petal = Color(0xFFF2617E)
private val Water = Color(0xFF4A9BE8)

/** The terracotta pot of the three pictures of the flower, with its rim and the soil heaped in it up to (0, 8). */
private fun DrawScope.flowerPot() {
    ellipse(Shadow, Offset(0f, 44f), 26f, 5f)
    fill(
        path {
            moveTo(-24f, 18f)
            quadraticTo(0f, -2f, 24f, 18f)
            close()
        },
        Earth,
    )
    for ((x, y) in listOf(-12f to 14f, 9f to 12f, 16f to 15f)) circle(EarthDark, Offset(x, y), 1.5f)
    fill(
        path {
            moveTo(-22f, 22f)
            lineTo(22f, 22f)
            lineTo(17f, 40f)
            quadraticTo(16f, 44f, 12f, 44f)
            lineTo(-12f, 44f)
            quadraticTo(-16f, 44f, -17f, 40f)
            close()
        },
        Clay,
    )
    drawRoundRect(ClayRim, Offset(-26f, 13f), Size(52f, 11f), CornerRadius(4f))
    line(Color.White, Offset(-13f, 28f), Offset(-11f, 39f), 3f, alpha = 0.35f)
}

/** A pointed leaf from [base] to [tip], [width] units across at its widest. */
private fun DrawScope.leaf(
    base: Offset,
    tip: Offset,
    width: Float,
    color: Color,
) {
    val along = tip - base
    val normal = Offset(-along.y, along.x) * (width / along.getDistance())
    val middle = base + along * 0.5f
    fill(
        path {
            moveTo(base.x, base.y)
            quadraticTo(middle.x + normal.x, middle.y + normal.y, tip.x, tip.y)
            quadraticTo(middle.x - normal.x, middle.y - normal.y, base.x, base.y)
            close()
        },
        color,
    )
}

/** First, the seed: it falls, with the lines of its fall, into the hole in the soil of the pot, where nothing grows yet. */
private fun DrawScope.sowing(time: Float) {
    flowerPot()
    ellipse(EarthDark, Offset(0f, 9.5f), 6.5f, 2.4f)
    val seed = Offset(0f, -14f + sin(time * 2.4f))
    for (x in listOf(-6f, 0f, 6f)) line(Color(0xFFC3CCDD), Offset(x, -38f), Offset(x, -29f), 2.5f)
    // It sways a little as it falls, like a seed in the air.
    rotate(20f + 12f * sin(time * 2.4f), seed) {
        ellipse(Color(0xFF8A5A3C), seed, 6f, 8.5f)
        ellipse(Color(0xFFC08B5C), seed + Offset(-2f, -1f), 1.6f, 4.2f)
    }
}

/** Next, the water: the watering can waters the pot, and a little sprout has come out of the soil. */
private fun DrawScope.watering(time: Float) {
    flowerPot()
    stroke(
        path {
            moveTo(0f, 9f)
            quadraticTo(1f, 2f, 0f, -4f)
        },
        Stalk,
        3.5f,
    )
    leaf(Offset(0f, -3f), Offset(-11f, -9f), 6f, Leaf)
    leaf(Offset(0f, -4f), Offset(11f, -11f), 6f, Leaf)
    for (i in 0 until 3) {
        val fall = (time * 0.8f + i / 3f) % 1f
        waterDrop(Offset(-11f + 3.5f * i, -14f + 20f * fall), 2.6f)
    }
    withTransform({
        translate(28f, -23f)
        rotate(-28f, Offset.Zero)
    }) { wateringCan() }
}

/** A drop of water pointing up, round at the bottom: [radius] units the round part. */
private fun DrawScope.waterDrop(center: Offset, radius: Float) {
    circle(Water, center, radius)
    fill(
        path {
            moveTo(center.x - radius * 0.88f, center.y - radius * 0.48f)
            lineTo(center.x, center.y - radius * 2.2f)
            lineTo(center.x + radius * 0.88f, center.y - radius * 0.48f)
            close()
        },
        Water,
    )
}

/** A watering can standing up, round its origin: its handle over the top, and its spout to the left with the rose at (-32, -11). */
private fun DrawScope.wateringCan() {
    val body = Color(0xFF2BB3A3)
    val dark = Color(0xFF1E8C80)
    stroke(
        path {
            moveTo(-7f, -11f)
            cubicTo(-7f, -27f, 13f, -27f, 13f, -9f)
        },
        dark,
        5f,
    )
    fill(
        path {
            moveTo(-12f, 1f)
            lineTo(-12f, 10f)
            lineTo(-33f, -8f)
            lineTo(-30f, -13f)
            close()
        },
        body,
    )
    tiltedEllipse(dark, Offset(-32f, -11f), 3.5f, 6.5f, 40f)
    drawRoundRect(body, Offset(-15f, -12f), Size(32f, 26f), CornerRadius(7f))
    drawRect(dark, Offset(-15f, 4f), Size(32f, 4f))
    line(Color.White, Offset(-9f, -6f), Offset(-9f, 0f), 3f, alpha = 0.5f)
}

/** At the end, the flower: big and open in the same pot, swaying a little. */
private fun DrawScope.blooming(time: Float) {
    flowerPot()
    rotate(2.5f * sin(time * 1.5f), Offset(0f, 9f)) {
        stroke(
            path {
                moveTo(0f, 9f)
                quadraticTo(-3f, -6f, 0f, -24f)
            },
            Stalk,
            4.5f,
        )
        leaf(Offset(-1f, 0f), Offset(-17f, -8f), 9f, Leaf)
        leaf(Offset(-1f, -5f), Offset(15f, -14f), 9f, Leaf)
        val bloom = Offset(0f, -28f)
        for (i in 0 until 6) rotate(i * 60f, bloom) { ellipse(Petal, bloom + Offset(0f, -10f), 7.5f, 9.5f) }
        circle(Color(0xFFF4C423), bloom, 8f)
        circle(Color(0xFFFFE27A), bloom + Offset(-2.5f, -2.5f), 2.5f)
    }
    sparkles(listOf(Offset(-27f, -36f), Offset(26f, -22f), Offset(-24f, -12f)), 6f, time)
}

// The teeth.

private val Skin = Color(0xFFF5C7A1)
private val SkinEdge = Color(0xFFD9A07A)
private val Hair = Color(0xFF6B3E26)
private val BrushPink = Color(0xFFFF8FB1)
private val Bristle = Color(0xFF8CCBF0)
private val PasteEdge = Color(0xFF6FA8E6)
private val PasteStripe = Color(0xFF4FD1B0)
private val MouthInside = Color(0xFF7A1F32)
private val Tongue = Color(0xFFFF7FA0)
private val Bubble = Color(0xFF8CCBF0)

/** A toothbrush lying across, its head to the right: the bristles stand up from x 0 to 23, their tops at -10. */
private fun DrawScope.toothbrush() {
    drawRoundRect(BrushPink, Offset(-42f, -2f), Size(44f, 10f), CornerRadius(5f))
    drawRoundRect(BrushPink, Offset(-4f, -1f), Size(30f, 7f), CornerRadius(3.5f))
    drawRoundRect(Color.White, Offset(-35f, 1f), Size(18f, 3.5f), CornerRadius(1.75f), alpha = 0.5f)
    for (i in 0 until 6) drawRoundRect(Bristle, Offset(4f * i, -10f), Size(3f, 10f), CornerRadius(1.5f))
}

/** A tube of toothpaste upside down, its open tip at the origin and its crimped end up. */
private fun DrawScope.toothpasteTube() {
    val blue = Color(0xFF2C74D8)
    val body = path {
        moveTo(-4f, -6f)
        quadraticTo(-12f, -8f, -12f, -16f)
        lineTo(-13f, -38f)
        lineTo(13f, -38f)
        lineTo(12f, -16f)
        quadraticTo(12f, -8f, 4f, -6f)
        close()
    }
    drawRoundRect(Color.White, Offset(-4f, -7f), Size(8f, 7f), CornerRadius(1.5f))
    drawRoundRect(PictureInk, Offset(-4f, -7f), Size(8f, 7f), CornerRadius(1.5f), style = Stroke(2f))
    fill(body, Color(0xFFF4F6FB))
    clipPath(body) { drawRect(blue, Offset(-14f, -31f), Size(28f, 12f)) }
    stroke(body, PictureInk, 2.5f)
    drawRoundRect(blue, Offset(-14.5f, -44f), Size(29f, 7f), CornerRadius(2f))
    for (x in listOf(-9f, -4.5f, 0f, 4.5f, 9f)) line(Color.White, Offset(x, -43f), Offset(x, -38.5f), 1.2f, alpha = 0.6f)
    circle(Color.White, Offset(0f, -25f), 3.5f)
}

/** First, the toothpaste: the tube squeezes a ribbon of paste on the bristles of the brush. */
private fun DrawScope.squeezing() {
    ellipse(Shadow, Offset(-10f, 42f), 38f, 4.5f)
    translate(-5f, 34f) { toothbrush() }
    val ribbon = path {
        moveTo(16f, 8f)
        lineTo(16f, 16f)
        cubicTo(16f, 22f, 11f, 22f, 9f, 19f)
        cubicTo(7f, 16f, 3f, 16f, 1f, 19f)
        cubicTo(-1f, 22f, -5f, 22f, -5f, 19f)
    }
    stroke(ribbon, PasteEdge, 9f)
    stroke(ribbon, Color.White, 6.5f)
    stroke(ribbon, PasteStripe, 2f)
    withTransform({
        translate(16f, 7f)
        rotate(22f, Offset.Zero)
    }) { toothpasteTube() }
}

/** The girl of the teeth, round [head]: her hair with two bunches, her face, her fringe, her cheeks and her shadow. */
private fun DrawScope.girl(head: Offset) {
    for (side in listOf(-1f, 1f)) circle(Shadow, head + Offset(side * 31f, 11f), 10f)
    circle(Shadow, head + Offset(0f, 1f), 32f)
    circle(Shadow, head + Offset(0f, 9f), 27f)
    for (side in listOf(-1f, 1f)) {
        circle(Hair, head + Offset(side * 31f, 7f), 10f)
        circle(BrushPink, head + Offset(side * 24f, 2f), 3.5f)
    }
    circle(Hair, head + Offset(0f, -3f), 32f)
    circle(Skin, head + Offset(0f, 5f), 27f)
    fill(
        path {
            moveTo(head.x - 27f, head.y + 2f)
            quadraticTo(head.x - 22f, head.y - 26f, head.x + 4f, head.y - 24f)
            quadraticTo(head.x + 26f, head.y - 22f, head.x + 27f, head.y + 2f)
            quadraticTo(head.x + 8f, head.y - 12f, head.x - 27f, head.y + 2f)
            close()
        },
        Hair,
    )
    for (side in listOf(-1f, 1f)) circle(Color(0xFFF29AA5), head + Offset(side * 16f, 12f), 4.5f, alpha = 0.7f)
}

/** Her eyes open, round [head]. */
private fun DrawScope.openEyes(head: Offset) {
    for (side in listOf(-1f, 1f)) {
        val eye = head + Offset(side * 10f, 2f)
        circle(PictureInk, eye, 3.4f)
        circle(Color.White, eye + Offset(1.2f, -1.2f), 1.2f)
    }
}

/** Where the girl's head is, in both pictures of her. */
private val HEAD = Offset(-4f, 0f)

/** Next, the brushing: the girl brushes her teeth, with foam round her mouth and bubbles in the air. */
private fun DrawScope.brushing(time: Float) {
    girl(HEAD)
    openEyes(HEAD)
    val mouth = HEAD + Offset(0f, 17f)
    val open = path {
        moveTo(mouth.x - 9f, mouth.y)
        cubicTo(mouth.x - 9f, mouth.y - 9f, mouth.x + 9f, mouth.y - 9f, mouth.x + 9f, mouth.y)
        cubicTo(mouth.x + 9f, mouth.y + 8f, mouth.x - 9f, mouth.y + 8f, mouth.x - 9f, mouth.y)
        close()
    }
    fill(open, MouthInside)
    clipPath(open) { drawRect(Color.White, Offset(mouth.x - 10f, mouth.y - 8f), Size(20f, 5f)) }
    val scrub = 1.6f * sin(time * 9f)
    translate(scrub, 0f) {
        rotate(-8f, mouth) {
            drawRoundRect(BrushPink, Offset(mouth.x + 2f, mouth.y - 1f), Size(46f, 8f), CornerRadius(4f))
            val fist = mouth + Offset(27f, -4f)
            drawRoundRect(Skin, fist, Size(13f, 14f), CornerRadius(5f))
            drawRoundRect(SkinEdge, fist, Size(13f, 14f), CornerRadius(5f), style = Stroke(1.5f))
            for (k in 1..2) line(SkinEdge, fist + Offset(4.3f * k, 1.5f), fist + Offset(4.3f * k, 5f), 1.4f)
        }
    }
    for ((at, radius) in FOAM) circle(Color.White, mouth + at, radius)
    BUBBLES.forEachIndexed { i, (at, radius) ->
        val rise = at + Offset(0f, -2f * sin(time * 2f + i))
        ring(Bubble, rise, radius, 1.8f)
        circle(Color.White, rise + Offset(-radius * 0.35f, -radius * 0.35f), radius * 0.25f)
    }
}

/** The foam round her mouth while she brushes: where each blob is from the middle of her mouth, and how big. */
private val FOAM = listOf(
    Offset(-9f, -3f) to 3.5f,
    Offset(-10f, 3f) to 3f,
    Offset(-4f, 7f) to 3.2f,
    Offset(3f, 8f) to 2.6f,
    Offset(-6f, -7f) to 2.4f,
    Offset(10f, 5f) to 2.6f,
)

/** The bubbles in the air round her head while she brushes, and how big. */
private val BUBBLES = listOf(
    Offset(32f, -26f) to 5f,
    Offset(42f, -12f) to 3.5f,
    Offset(-40f, -24f) to 4f,
    Offset(38f, -40f) to 3f,
    Offset(-33f, -38f) to 2.6f,
)

/** At the end, clean: the girl smiles wide with her white teeth, and they shine. */
private fun DrawScope.shining(time: Float) {
    girl(HEAD)
    for (side in listOf(-1f, 1f)) {
        val eye = HEAD + Offset(side * 10f, 3f)
        stroke(
            path {
                moveTo(eye.x - 5f, eye.y + 1f)
                quadraticTo(eye.x, eye.y - 5f, eye.x + 5f, eye.y + 1f)
            },
            PictureInk,
            3f,
        )
    }
    val mouth = HEAD + Offset(0f, 15f)
    val smile = path {
        moveTo(mouth.x - 15f, mouth.y - 3f)
        lineTo(mouth.x + 15f, mouth.y - 3f)
        quadraticTo(mouth.x + 14f, mouth.y + 14f, mouth.x, mouth.y + 14f)
        quadraticTo(mouth.x - 14f, mouth.y + 14f, mouth.x - 15f, mouth.y - 3f)
        close()
    }
    fill(smile, MouthInside)
    clipPath(smile) {
        ellipse(Tongue, mouth + Offset(0f, 14f), 9f, 5f)
        drawRect(Color.White, Offset(mouth.x - 16f, mouth.y - 4f), Size(32f, 8f))
        for (x in listOf(-7.5f, 0f, 7.5f)) line(Color(0xFFD5DCEA), mouth + Offset(x, -3f), mouth + Offset(x, 3.5f), 1.2f)
    }
    sparkles(listOf(mouth + Offset(11f, 0f), Offset(32f, -30f), Offset(-38f, -26f), Offset(34f, 30f)), 6.5f, time)
}

// The cake.

private val Sponge = Color(0xFFF6C46A)
private val Crust = Color(0xFFDE9442)
private val Steel = Color(0xFF9AA5B8)
private val Cream = Color(0xFFFFF3E0)
private val Icing = Color(0xFFFFB3C8)

/** A half of an eggshell, brown, broken along the top, centred on [center] and tilted [tilt] degrees. */
private fun DrawScope.eggShell(center: Offset, tilt: Float) = rotate(tilt, center) {
    val shell = path {
        moveTo(center.x - 9f, center.y)
        lineTo(center.x - 6f, center.y - 4f)
        lineTo(center.x - 3f, center.y - 0.5f)
        lineTo(center.x, center.y - 4.5f)
        lineTo(center.x + 3f, center.y - 0.5f)
        lineTo(center.x + 6f, center.y - 4f)
        lineTo(center.x + 9f, center.y)
        cubicTo(center.x + 9f, center.y + 8f, center.x + 5f, center.y + 11f, center.x, center.y + 11f)
        cubicTo(center.x - 5f, center.y + 11f, center.x - 9f, center.y + 8f, center.x - 9f, center.y)
        close()
    }
    fill(shell, Color(0xFFF1D2AE))
    stroke(shell, Color(0xFFD9A877), 1.6f)
}

/** A whisk standing up, its loops ending at the origin and its red handle up. */
private fun DrawScope.whisk() {
    for (w in listOf(12f, 7f, 2.5f)) {
        stroke(
            path {
                moveTo(0f, -26f)
                cubicTo(-w * 1.5f, -18f, -w * 1.2f, 0f, 0f, 0f)
                cubicTo(w * 1.2f, 0f, w * 1.5f, -18f, 0f, -26f)
            },
            Steel,
            2.2f,
        )
    }
    drawRoundRect(Steel, Offset(-3f, -31f), Size(6f, 6f), CornerRadius(1.5f))
    line(Color(0xFFE4483C), Offset(0f, -32f), Offset(0f, -50f), 7f)
}

/** First, the dough: flour and an egg in a bowl, the whisk mixing them, and the broken shell beside it. */
private fun DrawScope.mixing(time: Float) {
    ellipse(Shadow, Offset(4f, 42f), 40f, 5f)
    fill(
        path {
            moveTo(-32f, 8f)
            cubicTo(-32f, 32f, -14f, 42f, 4f, 42f)
            cubicTo(22f, 42f, 40f, 32f, 40f, 8f)
            close()
        },
        Color(0xFF4A9BE8),
    )
    ellipse(Color(0xFF8CCBF0), Offset(4f, 8f), 36f, 9f)
    ellipse(Color(0xFF2C74D8), Offset(4f, 9f), 32f, 7f)
    ellipse(Color(0xFFFFE3A3), Offset(4f, 10.5f), 29f, 5f)
    val flour = path {
        moveTo(-28f, 12f)
        cubicTo(-27f, 1f, -22f, -4f, -17.5f, -4f)
        cubicTo(-13f, -4f, -8f, 1f, -7f, 12f)
        close()
    }
    fill(flour, Color.White)
    stroke(flour, Color(0xFFE6DCCB), 1.5f)
    circle(Color(0xFFF6A821), Offset(-1f, 11f), 4.5f)
    circle(Color(0xFFFFD36E), Offset(-2.2f, 9.8f), 1.4f)
    withTransform({
        translate(15f, 11f)
        rotate(32f + 5f * sin(time * 6f), Offset.Zero)
    }) { whisk() }
    eggShell(Offset(-38f, 34f), -15f)
    eggShell(Offset(-27f, 39f), 12f)
}

/** Next, the oven: the cake tin inside, the light on, the dough rising and the heat coming out on top. */
private fun DrawScope.baking(time: Float) {
    val body = Color(0xFFEB7465)
    val panel = Color(0xFFD45A4B)
    ellipse(Shadow, Offset(0f, 45f), 42f, 5f)
    for (x in listOf(-32f, 26f)) drawRoundRect(PictureInk, Offset(x, 36f), Size(6f, 8f), CornerRadius(2f))
    drawRoundRect(body, Offset(-40f, -36f), Size(80f, 76f), CornerRadius(10f))
    drawRoundRect(panel, Offset(-40f, -36f), Size(80f, 18f), CornerRadius(10f))
    drawRect(panel, Offset(-40f, -26f), Size(80f, 8f))
    for (x in listOf(-28f, -16f, -4f)) {
        circle(Color.White, Offset(x, -27f), 4f)
        line(PictureInk, Offset(x, -27f), Offset(x, -30f), 1.6f)
    }
    val glow = 0.8f + 0.2f * sin(time * 3f)
    circle(Color(0xFFFFD23F), Offset(28f, -27f), 5.5f, alpha = 0.4f * glow)
    circle(Color(0xFFFFD23F), Offset(28f, -27f), 3.2f)
    drawRoundRect(Color(0xFFF1F2F8), Offset(-26f, -13f), Size(52f, 5f), CornerRadius(2.5f))
    drawRoundRect(Color(0xFF5B3A3A), Offset(-31f, -3f), Size(62f, 36f), CornerRadius(7f))
    drawRoundRect(Color(0xFFFFC55C), Offset(-27f, 1f), Size(54f, 28f), CornerRadius(5f))
    ellipse(Color(0xFFFFEDB0), Offset(0f, 9f), 23f, 9f, alpha = glow)
    line(Color(0xFFE0A040), Offset(-25f, 25f), Offset(25f, 25f), 2f)
    fill(
        path {
            moveTo(-15f, 13f)
            quadraticTo(0f, 0f, 15f, 13f)
            close()
        },
        Crust,
    )
    drawRoundRect(Steel, Offset(-17f, 12f), Size(34f, 12f), CornerRadius(2f))
    line(Color(0xFFC3CCDD), Offset(-14f, 15f), Offset(14f, 15f), 1.6f)
    for ((i, x) in listOf(-14f, 0f, 14f).withIndex()) {
        val wave = 1.5f * sin(time * 3f + i)
        stroke(
            path {
                moveTo(x + wave, -39f)
                cubicTo(x - 4f + wave, -42f, x + 4f + wave, -45f, x + wave, -48f)
            },
            Color(0xFFF4A057),
            2.2f,
            alpha = 0.7f,
        )
    }
}

/** At the end, the cake: out of the oven, on a plate, with a candle lit on top. */
private fun DrawScope.cakeOnPlate(time: Float) {
    ellipse(Shadow, Offset(0f, 40f), 46f, 6f)
    ellipse(Color(0xFF9CC7EE), Offset(0f, 35f), 46f, 10f)
    ellipse(Color(0xFFDDF1FC), Offset(0f, 34f), 38f, 7f)
    drawRect(Sponge, Offset(-32f, 4f), Size(64f, 26f))
    ellipse(Sponge, Offset(0f, 30f), 32f, 7f)
    stroke(
        path {
            moveTo(-32f, 18f)
            cubicTo(-32f, 28f, 32f, 28f, 32f, 18f)
        },
        Cream,
        4f,
    )
    fill(
        path {
            moveTo(-32f, 4f)
            cubicTo(-30f, -8f, 30f, -8f, 32f, 4f)
            cubicTo(32f, 14f, -32f, 14f, -32f, 4f)
            close()
        },
        Icing,
    )
    for ((x, length) in ICING_DRIPS) {
        // The drips hang from the front edge of the icing, half an ellipse from one side of the cake to the other.
        val edge = 4f + 7.5f * sqrt(1f - (x / 32f) * (x / 32f))
        drawRoundRect(Icing, Offset(x - 3.5f, edge - 4f), Size(7f, length + 4f), CornerRadius(3.5f))
    }
    for (x in listOf(-17f, 17f)) drawStrawberry(Offset(x, -3f), 6f)
    drawRoundRect(Color(0xFF6FA8E6), Offset(-3.5f, -20f), Size(7f, 24f), CornerRadius(2f))
    for (y in listOf(-16f, -9f, -2f)) line(Color.White, Offset(-3f, y + 2f), Offset(3f, y - 1f), 1.8f)
    line(PictureInk, Offset(0f, -20f), Offset(0f, -24f), 1.5f)
    val flicker = 1f + 0.12f * sin(time * 12f)
    val flame = Offset(0f, -30f)
    fill(
        path {
            moveTo(flame.x, flame.y - 10f * flicker)
            quadraticTo(flame.x + 7f, flame.y + 1f, flame.x, flame.y + 6f)
            quadraticTo(flame.x - 7f, flame.y + 1f, flame.x, flame.y - 10f * flicker)
            close()
        },
        Color(0xFFF28B2E),
    )
    ellipse(Color(0xFFFFD23F), flame + Offset(0f, 1.5f), 2.6f, 4f * flicker)
    sparkles(listOf(Offset(-22f, -22f), Offset(24f, -16f)), 5.5f, time)
}

/** Where the icing drips down the side of the cake, and how far. */
private val ICING_DRIPS = listOf(-26f to 4f, -15f to 8f, -4f to 5f, 8f to 9f, 19f to 5f, 28f to 3f)

// The snowman.

private val Snow = Color(0xFFF8FBFF)
private val SnowShade = Color(0xFFD9E9F8)
private val SnowEdge = Color(0xFFA9C8E8)
private val Flake = Color(0xFF9CC7EE)

/** The snowy ground of the three pictures of the snowman, and the snowflakes falling over it. */
private fun DrawScope.snowyGround(time: Float) {
    ellipse(Color(0xFFDDEEFB), Offset(0f, 38f), 50f, 9f)
    FLAKES.forEachIndexed { i, (at, radius) ->
        val flake = at + Offset(0f, 1.8f * sin(time * 1.3f + i))
        for (k in 0 until 3) {
            val angle = PI / 2 + k * PI / 3
            val arm = Offset((radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat())
            line(Flake, flake - arm, flake + arm, 1.8f)
        }
    }
}

/** The snowflakes, and how big: up in the air, clear of the snowman's hat and his arms. */
private val FLAKES = listOf(
    Offset(-40f, -36f) to 4.5f,
    Offset(-22f, -46f) to 3f,
    Offset(-31f, -20f) to 3.5f,
    Offset(38f, -38f) to 4f,
    Offset(31f, -21f) to 3f,
)

/** A ball of snow, white with its shade and its edge, so that it shows on the white card. */
private fun DrawScope.snowBall(center: Offset, radius: Float) {
    circle(SnowShade, center, radius)
    circle(Snow, center + Offset(-radius * 0.1f, -radius * 0.12f), radius * 0.86f)
    ring(SnowEdge, center, radius, 2.5f)
}

/** First, a small ball of snow, the first one, on the snowy ground, and the track it left rolling. */
private fun DrawScope.firstSnowball(time: Float) {
    snowyGround(time)
    drawRoundRect(Color(0xFFC9DEF3), Offset(-42f, 33f), Size(50f, 6f), CornerRadius(3f))
    val ball = Offset(8f, 24f)
    for ((radius, from) in listOf(19f to 150f, 24f to 160f)) {
        drawArc(Flake, from, 60f, false, ball - Offset(radius, radius), Size(2 * radius, 2 * radius), style = Stroke(2.2f))
    }
    ellipse(Shadow, Offset(10f, 38.5f), 15f, 3f)
    snowBall(ball, 14f)
}

/** The body and the head of the snowman: two big balls, one on the other. */
private fun DrawScope.twoBalls() {
    ellipse(Shadow, Offset(0f, 41f), 24f, 4f)
    snowBall(Offset(0f, 20f), 22f)
    snowBall(Offset(0f, -11f), 15f)
}

/** Next, two big balls of snow, one on the other: the snowman without his face yet. */
private fun DrawScope.twoSnowballs(time: Float) {
    snowyGround(time)
    twoBalls()
}

/** At the end, the snowman: his eyes, his carrot nose, his smile, his scarf, his hat and his arms of twigs. */
private fun DrawScope.snowman(time: Float) {
    snowyGround(time)
    val twig = Color(0xFF8A5A3C)
    for (side in listOf(-1f, 1f)) {
        line(twig, Offset(side * 18f, 12f), Offset(side * 40f, -4f), 3f)
        line(twig, Offset(side * 34f, 0f), Offset(side * 38f, -10f), 2.5f)
    }
    twoBalls()
    for (y in listOf(16f, 26f)) circle(PictureInk, Offset(0f, y), 2.4f)
    for (side in listOf(-1f, 1f)) {
        circle(PictureInk, Offset(side * 5.5f, -15f), 2.6f)
        circle(Color.White, Offset(side * 5.5f + 0.9f, -15.9f), 0.9f)
    }
    fill(
        path {
            moveTo(0f, -12f)
            lineTo(15f, -8f)
            lineTo(0f, -7f)
            close()
        },
        Color(0xFFF28B2E),
    )
    for (k in 0 until 5) {
        val angle = PI * (0.2 + 0.15 * k)
        circle(PictureInk, Offset((7f * cos(angle)).toFloat(), -6f + (3f * sin(angle)).toFloat()), 1.2f)
    }
    val scarf = Color(0xFFE4483C)
    drawRoundRect(scarf, Offset(-17f, -1f), Size(34f, 8f), CornerRadius(4f))
    drawRoundRect(scarf, Offset(7f, 3f), Size(8f, 20f), CornerRadius(3f))
    for (y in listOf(9f, 15f)) drawRect(Color.White, Offset(7f, y), Size(8f, 2.5f))
    for (x in listOf(-9f, 1f)) drawRect(Color.White, Offset(x, -1f), Size(2.5f, 8f))
    drawRoundRect(PictureInk, Offset(-20f, -27f), Size(40f, 5f), CornerRadius(2.5f))
    drawRoundRect(PictureInk, Offset(-12f, -46f), Size(24f, 21f), CornerRadius(3f))
    drawRect(scarf, Offset(-12f, -31f), Size(24f, 4f))
}
