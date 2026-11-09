package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.fanguskdev.mymonsterbruno.game.Pet
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlin.math.sin

/** The little wooden shelf on the wall where her animal lives, [width] units wide, its top at [top]. */
internal fun DrawScope.drawPetShelf(top: Offset, width: Float) {
    drawRect(Shadow, Offset(top.x - width / 2, top.y + 4f), Size(width, 10f))
    drawRoundRect(Color(0xFFC98A4B), Offset(top.x - width / 2, top.y), Size(width, 10f), CornerRadius(3f))
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(top.x + side * (width / 2 - 12f), top.y + 10f)
                lineTo(top.x + side * (width / 2 - 12f), top.y + 26f)
                lineTo(top.x + side * (width / 2 - 28f), top.y + 10f)
                close()
            },
            Color(0xFFA86E37),
        )
    }
}

/**
 * Her animal, [size] units across, sitting with its bottom at [center] + half its size: a cat, a dog, a rabbit, a fish in
 * its bowl, a bird or a turtle. [time] makes it breathe, blink and wag; [happy] (0 to 1) makes it jump and smile.
 */
internal fun DrawScope.drawPet(
    pet: Pet,
    center: Offset,
    size: Float,
    time: Float,
    happy: Float = 0f,
) = inBox(center + Offset(0f, -18f * happy * size / 100f), size) {
    val blink = (time % 4f) > 3.85f
    when (pet) {
        Pet.CAT -> cat(time, blink, happy)
        Pet.DOG -> dog(time, blink, happy)
        Pet.RABBIT -> rabbit(time, blink, happy)
        Pet.FISH -> fish(time, happy)
        Pet.BIRD -> bird(time, blink, happy)
        Pet.TURTLE -> turtle(time, blink, happy)
    }
}

/** Two eyes at ±[apart] from (0, [y]), closed when blinking or happy, and a little smile under them. */
private fun DrawScope.face(
    y: Float,
    apart: Float,
    blink: Boolean,
    happy: Float,
    radius: Float = 4.5f,
) {
    for (side in listOf(-1f, 1f)) {
        val eye = Offset(side * apart, y)
        if (blink || happy > 0.3f) {
            stroke(
                path {
                    moveTo(eye.x - radius, eye.y)
                    quadraticTo(eye.x, eye.y - radius * 1.2f, eye.x + radius, eye.y)
                },
                PictureInk,
                2.5f,
            )
        } else {
            circle(PictureInk, eye, radius)
            circle(Color.White, eye + Offset(-1.3f, -1.3f), radius * 0.35f)
        }
    }
    stroke(
        path {
            moveTo(-5f, y + 9f)
            quadraticTo(0f, y + 13f + 4f * happy, 5f, y + 9f)
        },
        PictureInk,
        2.2f,
    )
}

private fun DrawScope.cat(time: Float, blink: Boolean, happy: Float) {
    val fur = Color(0xFFF4A259)
    val dark = Color(0xFFD9823F)
    ellipse(Shadow, Offset(0f, 48f), 34f, 6f)
    // The tail, waving behind.
    rotate(12f * sin(time * 2.2f), Offset(22f, 34f)) {
        stroke(
            path {
                moveTo(22f, 34f)
                quadraticTo(48f, 30f, 42f, 2f)
            },
            dark,
            9f,
        )
    }
    ellipse(fur, Offset(0f, 26f), 28f, 24f)
    ellipse(Color(0xFFFFE2C0), Offset(0f, 30f), 15f, 14f)
    for (side in listOf(-1f, 1f)) {
        fill(
            path {
                moveTo(side * 26f, -16f)
                lineTo(side * 18f, -42f)
                lineTo(side * 4f, -24f)
                close()
            },
            fur,
        )
        fill(
            path {
                moveTo(side * 21f, -20f)
                lineTo(side * 17f, -34f)
                lineTo(side * 9f, -24f)
                close()
            },
            Color(0xFFFFB3C1),
        )
    }
    circle(fur, Offset(0f, -10f), 26f)
    face(-12f, 10f, blink, happy)
    circle(Color(0xFFFF7AA8), Offset(0f, -4f), 3f)
    for (side in listOf(-1f, 1f)) {
        for (dy in listOf(-2f, 3f)) {
            line(
                PictureInk,
                Offset(side * 12f, -2f + dy),
                Offset(
                    side * 26f,
                    -4f + dy * 1.6f,
                ),
                1.4f,
                alpha = 0.6f,
            )
        }
    }
}

private fun DrawScope.dog(time: Float, blink: Boolean, happy: Float) {
    val fur = Color(0xFFC89566)
    val ear = Color(0xFF8A5A3C)
    ellipse(Shadow, Offset(0f, 48f), 34f, 6f)
    rotate(25f * sin(time * 6f) * (0.4f + happy), Offset(24f, 30f)) {
        stroke(
            path {
                moveTo(24f, 30f)
                quadraticTo(40f, 24f, 38f, 8f)
            },
            fur,
            8f,
        )
    }
    ellipse(fur, Offset(0f, 26f), 28f, 24f)
    ellipse(Color(0xFFF2DCC0), Offset(0f, 30f), 14f, 14f)
    circle(fur, Offset(0f, -10f), 26f)
    for (side in listOf(-1f, 1f)) {
        rotate(side * (8f + 6f * sin(time * 1.5f)), Offset(side * 20f, -26f)) {
            ellipse(ear, Offset(side * 24f, -10f), 9f, 20f)
        }
    }
    ellipse(Color(0xFFF2DCC0), Offset(0f, 0f), 13f, 10f)
    face(-14f, 10f, blink, happy)
    ellipse(PictureInk, Offset(0f, -4f), 5f, 3.5f)
    if (happy > 0.3f) ellipse(Color(0xFFFF7FA0), Offset(0f, 9f), 4f, 6f)
}

private fun DrawScope.rabbit(time: Float, blink: Boolean, happy: Float) {
    val fur = Color(0xFFEDEFF5)
    val pink = Color(0xFFFFB3C1)
    ellipse(Shadow, Offset(0f, 48f), 32f, 6f)
    circle(fur, Offset(26f, 34f), 9f)
    ellipse(fur, Offset(0f, 26f), 27f, 24f)
    ring(Color(0xFFCDD2DE), Offset(0f, 26f), 25f, 1.5f, alpha = 0.6f)
    for (side in listOf(-1f, 1f)) {
        rotate(side * (6f + 4f * sin(time * 1.8f + side)), Offset(side * 9f, -26f)) {
            ellipse(fur, Offset(side * 10f, -52f), 8f, 24f)
            ellipse(pink, Offset(side * 10f, -52f), 4f, 17f)
        }
    }
    circle(fur, Offset(0f, -12f), 24f)
    face(-14f, 9f, blink, happy)
    circle(pink, Offset(0f, -5f), 3f)
    for (side in listOf(-1f, 1f)) circle(Color(0xFFFFC8D6), Offset(side * 15f, -4f), 4f, alpha = 0.8f)
}

private fun DrawScope.fish(time: Float, happy: Float) {
    // A round bowl of water on the shelf, and the fish swimming round in it.
    ellipse(Shadow, Offset(0f, 48f), 30f, 6f)
    circle(Color(0x8892D3F5), Offset(0f, 12f), 36f)
    fill(
        path {
            moveTo(-30f, -6f)
            lineTo(30f, -6f)
            quadraticTo(36f, 30f, 0f, 48f)
            quadraticTo(-36f, 30f, -30f, -6f)
            close()
        },
        Color(0x55A6DCF6),
    )
    ring(Color.White, Offset(0f, 12f), 36f, 2.5f, alpha = 0.8f)
    ellipse(Color.White, Offset(0f, -22f), 20f, 4f, alpha = 0.7f)
    val swim = 12f * sin(time * 1.2f)
    val turn = if (kotlin.math.cos(time * 1.2f) >= 0f) 1f else -1f
    val body = Offset(swim, 14f + 3f * sin(time * 2.4f) - 6f * happy)
    val orange = Color(0xFFF28B2E)
    fill(
        path {
            moveTo(body.x - turn * 12f, body.y)
            lineTo(body.x - turn * 24f, body.y - 8f)
            lineTo(body.x - turn * 24f, body.y + 8f)
            close()
        },
        orange,
    )
    ellipse(orange, body, 14f, 10f)
    circle(PictureInk, body + Offset(turn * 7f, -2f), 2.4f)
    for (i in 0 until 2) {
        circle(
            Color.White,
            Offset(body.x + turn * 18f + i * turn * 4f, body.y - 12f - ((time * 18f + i * 10f) % 26f)),
            2.4f - i * 0.6f,
            alpha = 0.8f,
        )
    }
}

private fun DrawScope.bird(time: Float, blink: Boolean, happy: Float) {
    val feathers = Color(0xFF4A9BE8)
    val belly = Color(0xFFBFE0FB)
    ellipse(Shadow, Offset(0f, 48f), 28f, 6f)
    for (side in listOf(-1f, 1f)) line(Color(0xFFF28B2E), Offset(side * 8f, 38f), Offset(side * 8f, 46f), 3f)
    circle(feathers, Offset(0f, 14f), 30f)
    ellipse(belly, Offset(0f, 22f), 17f, 18f)
    rotate(-20f * (happy + 0.15f * sin(time * 8f).coerceAtLeast(0f)), Offset(-24f, 8f)) {
        ellipse(Color(0xFF2C74D8), Offset(-26f, 16f), 9f, 17f)
    }
    rotate(20f * (happy + 0.15f * sin(time * 8f).coerceAtLeast(0f)), Offset(24f, 8f)) {
        ellipse(Color(0xFF2C74D8), Offset(26f, 16f), 9f, 17f)
    }
    face(2f, 10f, blink, 0f)
    fill(
        path {
            moveTo(-5f, 8f)
            lineTo(5f, 8f)
            lineTo(0f, 15f + 3f * happy)
            close()
        },
        Color(0xFFF4C423),
    )
    fill(
        path {
            moveTo(-4f, -16f)
            quadraticTo(0f, -30f, 6f, -18f)
            close()
        },
        Color(0xFF2C74D8),
    )
}

private fun DrawScope.turtle(time: Float, blink: Boolean, happy: Float) {
    val skin = Color(0xFF8BCF6A)
    val shell = Color(0xFF3E9A4A)
    ellipse(Shadow, Offset(0f, 48f), 38f, 6f)
    for (x in listOf(-24f, 24f)) ellipse(skin, Offset(x, 40f), 9f, 7f)
    val peek = 4f * sin(time * 1.2f) + 6f * happy
    circle(skin, Offset(-36f - peek, 18f), 15f)
    fill(
        path {
            moveTo(-38f, 40f)
            quadraticTo(-40f, -14f, 0f, -14f)
            quadraticTo(40f, -14f, 38f, 40f)
            close()
        },
        shell,
    )
    for (spot in listOf(
        Offset(0f, 8f),
        Offset(-18f, 22f),
        Offset(18f, 22f),
        Offset(-12f, -2f),
        Offset(12f, -2f),
    )) {
        circle(Color(0xFF6CC062), spot, 7f)
    }
    drawRect(Color(0xFF2E7D3A), Offset(-38f, 34f), Size(76f, 6f))
    // The face, on the head peeking out at the left.
    val head = Offset(-36f - peek, 18f)
    for (dx in listOf(-5f, 5f)) {
        val eye = head + Offset(dx, -4f)
        if (blink ||
            happy > 0.3f
        ) {
            stroke(
                path {
                    moveTo(eye.x - 3f, eye.y)
                    quadraticTo(eye.x, eye.y - 4f, eye.x + 3f, eye.y)
                },
                PictureInk,
                2f,
            )
        } else {
            circle(PictureInk, eye, 2.8f)
        }
    }
    stroke(
        path {
            moveTo(head.x - 4f, head.y + 5f)
            quadraticTo(head.x, head.y + 8f, head.x + 4f, head.y + 5f)
        },
        PictureInk,
        1.8f,
    )
}
