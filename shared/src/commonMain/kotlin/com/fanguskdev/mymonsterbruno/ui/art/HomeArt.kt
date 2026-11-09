package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.fanguskdev.mymonsterbruno.game.Bin
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Clothes
import com.fanguskdev.mymonsterbruno.game.Rubbish
import com.fanguskdev.mymonsterbruno.game.TEETH
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.sin

/** The mouth of Bruno, close, wide open (ADR 0044): where it is on the stage. */
internal val MOUTH = Rect(250f, 118f, 550f, 336f)

/** Each of his teeth, four above and four below, where they are on the stage: what the brush has to go over. */
internal val TOOTH_BOXES: List<Rect> = (0 until TEETH).map { i ->
    val column = i % 4
    val left = 270f + column * 66f
    if (i < 4) Rect(left, 124f, left + 58f, 184f) else Rect(left, 272f, left + 58f, 330f)
}

/**
 * Bruno's face, close, with his mouth wide open (ADR 0044): his colour all over, his eyes looking at her, his cheeks,
 * and the mouth with its tongue and its teeth, each with as much of a stain as [dirt] says (1, all; 0, clean).
 */
internal fun DrawScope.drawOpenMouth(
    look: BrunoLook,
    dirt: FloatArray,
    time: Float,
    stage: Stage,
) {
    // The bathroom behind him: light blue tiles.
    drawRect(Color(0xFFD9EFF7), Offset(stage.left, stage.top), Size(stage.right - stage.left, stage.bottom - stage.top))
    var x = stage.left - stage.left % TILE
    while (x < stage.right) {
        drawLine(Color(0xFFBFE1EE), Offset(x, stage.top), Offset(x, stage.bottom), 3f)
        x += TILE
    }
    var y = 0f
    while (y < stage.bottom) {
        drawLine(Color(0xFFBFE1EE), Offset(stage.left, y), Offset(stage.right, y), 3f)
        y += TILE
    }
    val palette = look.color.palette()
    ellipse(palette.body, Offset(400f, 230f), 360f, 300f)
    ellipse(palette.belly, Offset(400f, 300f), 230f, 150f)
    for (side in listOf(-1f, 1f)) {
        val eye = Offset(400f + side * 112f, 62f)
        circle(Color.White, eye, 36f)
        circle(Ink.dark, eye + Offset(0f, 6f), 16f)
        circle(Color.White, eye + Offset(-5f, 1f), 5f)
        circle(Ink.cheek, Offset(400f + side * 205f, 170f), 28f, alpha = 0.8f)
    }
    drawRoundRect(Color(0xFF5A2338), MOUTH.topLeft, MOUTH.size, CornerRadius(90f))
    ellipse(Color(0xFFE88A9A), Offset(400f, 236f), 96f, 34f)
    line(Color(0xFFD06F80), Offset(400f, 214f), Offset(400f, 250f), 4f)
    for ((i, box) in TOOTH_BOXES.withIndex()) {
        val upper = i < 4
        val radius = CornerRadius(14f)
        drawRoundRect(Color(0x33000000), box.topLeft + Offset(0f, 3f), box.size, radius)
        drawRoundRect(Color.White, box.topLeft, box.size, radius)
        val stain = dirt.getOrElse(i) { 0f }
        if (stain > 0f) {
            val at = box.center + Offset(if (i % 2 == 0) -6f else 7f, if (upper) 8f else -8f)
            circle(Color(0xFFB08A3E), at, 13f, alpha = stain)
            circle(Color(0xFF8C6A2A), at + Offset(9f, -6f), 6f, alpha = stain)
        } else {
            val shine = 0.5f + 0.5f * sin(time * 4f + i)
            fill(sparkle(box.center + Offset(10f, -12f), 8f * shine), Color(0xFFFFE27A))
        }
    }
}

private const val TILE = 50f

/** A sparkle of four points, [size] units, for what shines. */
private fun sparkle(center: Offset, size: Float) = path {
    moveTo(center.x, center.y - size)
    quadraticTo(center.x, center.y, center.x + size, center.y)
    quadraticTo(center.x, center.y, center.x, center.y + size)
    quadraticTo(center.x, center.y, center.x - size, center.y)
    quadraticTo(center.x, center.y, center.x, center.y - size)
    close()
}

/** A toothbrush lying down, its head with its bristles and a little foam at [head], and its handle to the right. */
internal fun DrawScope.drawToothbrush(head: Offset, size: Float = 1f, foam: Boolean = true) {
    val s = size
    drawRoundRect(Shadow, head + Offset(-10f * s, 22f * s), Size(170f * s, 20f * s), CornerRadius(10f * s))
    drawRoundRect(Color(0xFF2C74D8), head + Offset(-12f * s, 6f * s), Size(170f * s, 18f * s), CornerRadius(9f * s))
    drawRoundRect(Color(0xFF5FA3F0), head + Offset(40f * s, 9f * s), Size(100f * s, 5f * s), CornerRadius(3f * s))
    for (k in 0 until 5) {
        drawRoundRect(Color(0xFFE9F6FF), head + Offset((-12f + k * 9f) * s, -18f * s), Size(7f * s, 26f * s), CornerRadius(3f * s))
    }
    if (foam) {
        for ((dx, r) in listOf(-8f to 10f, 6f to 12f, 20f to 9f)) circle(Color.White, head + Offset(dx * s, -22f * s), r * s, alpha = 0.95f)
    }
}

/** A bubble of foam, [age] seconds old: it rises a little and fades. */
internal fun DrawScope.drawFoam(at: Offset, age: Float) {
    val fade = (1f - age / FOAM_SECONDS).coerceIn(0f, 1f)
    if (fade <= 0f) return
    circle(Color.White, at + Offset(0f, -18f * age), 7f + 5f * age, alpha = 0.9f * fade)
    ring(Color(0xFFBFE6F5), at + Offset(0f, -18f * age), 7f + 5f * age, 1.5f, alpha = 0.8f * fade)
}

/** How long a bubble of foam lasts. */
internal const val FOAM_SECONDS = 0.9f

/** [clothes] as it waits to be chosen: drawn in a box [size] units a side round [center]. */
internal fun DrawScope.drawClothes(clothes: Clothes, center: Offset, size: Float) {
    val k = size / 100f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        when (clothes) {
            Clothes.SUNGLASSES -> sunglasses(Offset.Zero, 1.6f)
            Clothes.CAP -> cap(Offset(0f, 12f), 1.7f)
            Clothes.UMBRELLA -> umbrella(Offset(0f, -8f), 1f)
            Clothes.BOOTS -> boots(Offset(0f, 10f), 1.6f)
            Clothes.WOOLLY_HAT -> woollyHat(Offset(0f, 14f), 1.6f)
            Clothes.SCARF -> scarf(Offset(0f, 0f), 1.5f)
        }
    }
}

/** [clothes] on Bruno, drawn at [center] with [height], as [drawBruno] draws him: on his eyes, head, neck, feet or above. */
internal fun DrawScope.drawWorn(
    clothes: Clothes,
    look: BrunoLook,
    center: Offset,
    height: Float,
) {
    val k = height * look.size / BRUNO_UNITS
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        when (clothes) {
            Clothes.SUNGLASSES -> sunglasses(Offset(0f, -14f), 1f)
            Clothes.CAP -> cap(Offset(0f, -40f), 1f)
            // Held up by his right hand, the stick beside him and not over his face.
            Clothes.UMBRELLA -> umbrella(Offset(46f, -78f), 1f)
            Clothes.BOOTS -> boots(Offset(0f, 46f), 1f)
            Clothes.WOOLLY_HAT -> woollyHat(Offset(0f, -38f), 1f)
            Clothes.SCARF -> scarf(Offset(0f, 30f), 1f)
        }
    }
}

private fun DrawScope.sunglasses(at: Offset, s: Float) {
    val lens = Color(0xFF26203A)
    for (side in listOf(-1f, 1f)) {
        drawRoundRect(lens, at + Offset((side * 15f - 13f) * s, -10f * s), Size(26f * s, 20f * s), CornerRadius(9f * s))
        circle(Color(0x66FFFFFF), at + Offset((side * 15f - 5f) * s, -4f * s), 3f * s)
    }
    line(lens, at + Offset(-3f * s, -4f * s), at + Offset(3f * s, -4f * s), 3f * s)
}

private fun DrawScope.cap(at: Offset, s: Float) {
    val red = Color(0xFFE4483C)
    fill(
        path {
            moveTo(at.x - 30f * s, at.y)
            quadraticTo(at.x - 28f * s, at.y - 30f * s, at.x, at.y - 30f * s)
            quadraticTo(at.x + 28f * s, at.y - 30f * s, at.x + 30f * s, at.y)
            close()
        },
        red,
    )
    drawRoundRect(Color(0xFFB8322A), at + Offset(4f * s, -5f * s), Size(42f * s, 9f * s), CornerRadius(5f * s))
    circle(Color.White, at + Offset(0f, -30f * s), 4f * s)
}

private fun DrawScope.umbrella(at: Offset, s: Float) {
    val colors = listOf(Color(0xFF2C74D8), Color(0xFFF4B623), Color(0xFF2E9E5B), Color(0xFFE4483C))
    for ((i, color) in colors.withIndex()) {
        val from = -60f + i * 30f
        fill(
            path {
                moveTo(at.x, at.y - 40f * s)
                quadraticTo(at.x + (from + 2f) * s, at.y - 34f * s, at.x + from * s, at.y)
                lineTo(at.x + (from + 30f) * s, at.y)
                quadraticTo(at.x + (from + 28f) * s, at.y - 34f * s, at.x, at.y - 40f * s)
                close()
            },
            color,
        )
    }
    line(Color(0xFF5B4636), at + Offset(0f, -46f * s), at + Offset(0f, 70f * s), 4f * s)
    stroke(
        path {
            moveTo(at.x, at.y + 70f * s)
            quadraticTo(at.x, at.y + 84f * s, at.x + 10f * s, at.y + 84f * s)
            quadraticTo(at.x + 18f * s, at.y + 84f * s, at.x + 18f * s, at.y + 74f * s)
        },
        Color(0xFF5B4636),
        4f * s,
    )
}

private fun DrawScope.boots(at: Offset, s: Float) {
    val yellow = Color(0xFFF4B623)
    for (side in listOf(-1f, 1f)) {
        val x = at.x + side * 16f * s
        drawRoundRect(yellow, Offset(x - 9f * s, at.y - 16f * s), Size(18f * s, 20f * s), CornerRadius(5f * s))
        drawRoundRect(yellow, Offset(x - 11f * s + side * 3f * s, at.y), Size(22f * s, 10f * s), CornerRadius(5f * s))
        line(Color(0xFFD9961C), Offset(x - 9f * s, at.y - 12f * s), Offset(x + 9f * s, at.y - 12f * s), 2f * s)
    }
}

private fun DrawScope.woollyHat(at: Offset, s: Float) {
    val blue = Color(0xFF2C74D8)
    fill(
        path {
            moveTo(at.x - 29f * s, at.y)
            quadraticTo(at.x - 27f * s, at.y - 34f * s, at.x, at.y - 34f * s)
            quadraticTo(at.x + 27f * s, at.y - 34f * s, at.x + 29f * s, at.y)
            close()
        },
        blue,
    )
    for (k in -2..2) line(Color(0xFF5FA3F0), at + Offset(k * 10f * s, -4f * s), at + Offset(k * 8f * s, -28f * s), 2f * s)
    drawRoundRect(Color.White, at + Offset(-31f * s, -6f * s), Size(62f * s, 11f * s), CornerRadius(5f * s))
    circle(Color.White, at + Offset(0f, -37f * s), 7f * s)
}

private fun DrawScope.scarf(at: Offset, s: Float) {
    val red = Color(0xFFE4483C)
    fill(
        path {
            moveTo(at.x - 40f * s, at.y - 6f * s)
            quadraticTo(at.x, at.y + 8f * s, at.x + 40f * s, at.y - 6f * s)
            lineTo(at.x + 38f * s, at.y + 4f * s)
            quadraticTo(at.x, at.y + 18f * s, at.x - 38f * s, at.y + 4f * s)
            close()
        },
        red,
    )
    drawRoundRect(red, at + Offset(-28f * s, 0f), Size(11f * s, 28f * s), CornerRadius(4f * s))
    for (k in 0 until 3) line(Color.White, at + Offset(-28f * s, (8f + k * 7f) * s), at + Offset(-17f * s, (8f + k * 7f) * s), 2f * s)
}

/** The colour of each bin. */
internal fun Bin.paint(): Color = when (this) {
    Bin.YELLOW -> Color(0xFFF4C430)
    Bin.BLUE -> Color(0xFF2C74D8)
    Bin.GREEN -> Color(0xFF2E9E5B)
}

/** A bin of the street, its mouth at the top of [box], with a white sign of what goes in it; [open], lifting its lid. */
internal fun DrawScope.drawBin(bin: Bin, box: Rect, open: Float) {
    val color = bin.paint()
    drawRoundRect(Shadow, box.topLeft + Offset(4f, 8f), box.size, CornerRadius(16f))
    drawRoundRect(color, box.topLeft, box.size, CornerRadius(16f))
    drawRoundRect(Color(0x22000000), Offset(box.left, box.top + 18f), Size(box.width, 8f))
    rotate(-30f * open, Offset(box.left + 6f, box.top + 4f)) {
        drawRoundRect(color, Offset(box.left - 6f, box.top - 14f), Size(box.width + 12f, 20f), CornerRadius(8f))
        drawRoundRect(Color(0x33000000), Offset(box.center.x - 18f, box.top - 20f), Size(36f, 8f), CornerRadius(4f))
    }
    val sign = Offset(box.center.x, box.top + box.height * 0.58f)
    when (bin) {
        Bin.YELLOW -> {
            drawRubbish(Rubbish.BOTTLE, sign + Offset(-14f, 0f), 46f, Color.White)
            drawRubbish(Rubbish.CAN, sign + Offset(16f, 6f), 38f, Color.White)
        }
        Bin.BLUE -> drawRubbish(Rubbish.NEWSPAPER, sign, 52f, Color.White)
        Bin.GREEN -> drawRubbish(Rubbish.GLASS_BOTTLE, sign, 54f, Color.White)
    }
    if (open > 0f) circle(Color.White, Offset(box.center.x, box.top - 30f - 20f * open), 6f * open, alpha = open)
}

/** [rubbish] in a box [size] units a side round [center]; in one colour, [ink], for the signs of the bins. */
internal fun DrawScope.drawRubbish(
    rubbish: Rubbish,
    center: Offset,
    size: Float,
    ink: Color? = null,
) {
    val k = size / 100f
    withTransform({
        translate(center.x, center.y)
        scale(k, k, Offset.Zero)
    }) {
        fun c(color: Color) = ink ?: color
        when (rubbish) {
            Rubbish.BOTTLE -> {
                drawRoundRect(c(Color(0xFF9ED8F2)), Offset(-18f, -24f), Size(36f, 70f), CornerRadius(14f))
                drawRoundRect(c(Color(0xFF9ED8F2)), Offset(-9f, -40f), Size(18f, 20f), CornerRadius(5f))
                drawRoundRect(c(Color(0xFF2C74D8)), Offset(-11f, -48f), Size(22f, 10f), CornerRadius(3f))
                if (ink == null) drawRoundRect(Color(0xFF2C74D8), Offset(-18f, 0f), Size(36f, 18f))
            }
            Rubbish.CAN -> {
                drawRoundRect(c(Color(0xFFE4483C)), Offset(-20f, -34f), Size(40f, 68f), CornerRadius(8f))
                if (ink == null) {
                    drawRoundRect(Color(0xFFC9CED6), Offset(-20f, -34f), Size(40f, 8f), CornerRadius(4f))
                    drawRoundRect(Color(0xFFC9CED6), Offset(-20f, 26f), Size(40f, 8f), CornerRadius(4f))
                    drawRoundRect(Color.White, Offset(-14f, -8f), Size(28f, 14f), CornerRadius(4f))
                }
            }
            Rubbish.NEWSPAPER -> {
                drawRoundRect(c(Color(0xFFF2F0EA)), Offset(-38f, -28f), Size(76f, 56f), CornerRadius(4f))
                if (ink == null) {
                    drawRoundRect(Color(0xFF9AA3AF), Offset(-30f, -20f), Size(30f, 18f))
                    for (y in listOf(-18f, -10f, -2f, 8f, 16f)) line(Color(0xFF9AA3AF), Offset(4f, y), Offset(30f, y), 3f)
                    for (y in listOf(8f, 16f)) line(Color(0xFF9AA3AF), Offset(-30f, y), Offset(-4f, y), 3f)
                } else {
                    for (y in listOf(-14f, -2f, 10f)) line(Color(0xFF2C74D8), Offset(-28f, y), Offset(28f, y), 4f)
                }
            }
            Rubbish.BOX -> {
                drawRoundRect(Color(0xFFC9935A), Offset(-36f, -26f), Size(72f, 56f), CornerRadius(4f))
                drawRoundRect(Color(0xFFB07A44), Offset(-36f, -26f), Size(72f, 12f), CornerRadius(4f))
                drawRoundRect(Color(0xFFE8D2A8), Offset(-8f, -26f), Size(16f, 56f))
            }
            Rubbish.JAR -> {
                drawRoundRect(c(Color(0xFFCFEFE8)), Offset(-26f, -24f), Size(52f, 58f), CornerRadius(12f))
                drawRoundRect(c(Color(0xFF8A5A3C)), Offset(-24f, -36f), Size(48f, 14f), CornerRadius(4f))
                if (ink == null) drawRoundRect(Color(0x66FFFFFF), Offset(-18f, -16f), Size(8f, 36f), CornerRadius(4f))
            }
            Rubbish.GLASS_BOTTLE -> {
                drawRoundRect(c(Color(0xFF3E9E5E)), Offset(-17f, -14f), Size(34f, 62f), CornerRadius(10f))
                drawRoundRect(c(Color(0xFF3E9E5E)), Offset(-7f, -44f), Size(14f, 34f), CornerRadius(5f))
                if (ink == null) {
                    drawRoundRect(Color(0xFF2B7546), Offset(-8f, -50f), Size(16f, 8f), CornerRadius(3f))
                    drawRoundRect(Color(0x55FFFFFF), Offset(-11f, -6f), Size(6f, 40f), CornerRadius(3f))
                }
            }
        }
    }
}

/** The sign of the island of the house on the map: a little house and a toothbrush by its door. */
internal fun DrawScope.drawHouseSign(center: Offset, size: Float) {
    val k = size / 100f
    val walls = Rect(center.x - 32f * k, center.y - 16f * k, center.x + 32f * k, center.y + 30f * k)
    drawRoundRect(Color(0xFFFFF4D6), walls.topLeft, walls.size, CornerRadius(4f * k))
    fill(
        path {
            moveTo(center.x - 42f * k, center.y - 14f * k)
            lineTo(center.x, center.y - 46f * k)
            lineTo(center.x + 42f * k, center.y - 14f * k)
            close()
        },
        Color(0xFFE4483C),
    )
    drawRoundRect(Color(0xFF8A5A3C), Offset(center.x - 9f * k, center.y + 4f * k), Size(18f * k, 26f * k), CornerRadius(3f * k))
    drawRoundRect(Color(0xFFA6DCF6), Offset(center.x + 14f * k, center.y - 8f * k), Size(12f * k, 12f * k), CornerRadius(2f * k))
    drawToothbrush(Offset(center.x + 46f * k, center.y + 14f * k), 0.32f * k, foam = false)
}
