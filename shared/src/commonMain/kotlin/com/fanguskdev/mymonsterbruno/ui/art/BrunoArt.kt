package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.BrunoColor
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Slot
import com.fanguskdev.mymonsterbruno.game.Topping
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.star
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Bruno's mouth: how he feels or what he is doing. */
internal enum class Mouth { SMILE, GRIN, OPEN, MUNCH, WAVY, O, YAWN, SLEEP }

/** Bruno's eyes. [OPEN] blinks now and then by itself. */
internal enum class Eyes { OPEN, HAPPY, CLOSED, WIDE }

/** Bruno's arms. */
internal enum class Arms { DOWN, UP, CLAP }

/** What Bruno is doing at a moment: [lookX] and [lookY], from -1 to 1, move his pupils; [hop] lifts him, in his units. */
internal data class Pose(
    val mouth: Mouth = Mouth.SMILE,
    val eyes: Eyes = Eyes.OPEN,
    val arms: Arms = Arms.DOWN,
    val lookX: Float = 0f,
    val lookY: Float = 0f,
    val hop: Float = 0f,
)

/** The colours of Bruno in each of the colours she can choose: body, arms and feet, belly. */
internal class BrunoPalette(val body: Color, val dark: Color, val belly: Color)

internal fun BrunoColor.palette(): BrunoPalette = when (this) {
    BrunoColor.MINT -> BrunoPalette(Color(0xFF4FD1B0), Color(0xFF3FBF9F), Color(0xFF9BEBD5))
    BrunoColor.PINK -> BrunoPalette(Color(0xFFFF8FB1), Color(0xFFF06E98), Color(0xFFFFC8D9))
    BrunoColor.ORANGE -> BrunoPalette(Color(0xFFFFA552), Color(0xFFF08A30), Color(0xFFFFD2A4))
    BrunoColor.PURPLE -> BrunoPalette(Color(0xFF9B7BEA), Color(0xFF8261DA), Color(0xFFCDBDF6))
}

internal object Ink {
    val dark = Color(0xFF2A2238)
    val mouth = Color(0xFF3A2241)
    val tongue = Color(0xFFFF7FA0)
    val cheek = Color(0xFFFF7AA8)
    val horn = Color(0xFFFFD36E)
    val white = Color.White
}

/** Bruno's height in his own units, from the tips of his horns to his feet: everything below is drawn in them. */
internal const val BRUNO_UNITS = 132f

/** Where his mouth is, in his units from the centre of his body: where the cookies and the strawberries go. */
internal val BRUNO_MOUTH = Offset(0f, 8f)

/**
 * Draws Bruno with his [look] and [pose], his body centred on [center] and [height] units high on the stage (more if he
 * has grown). [time] blinks his eyes and moves him a little, so he is never still.
 */
internal fun DrawScope.drawBruno(
    look: BrunoLook,
    center: Offset,
    height: Float,
    pose: Pose,
    time: Float,
) {
    val palette = look.color.palette()
    val k = height * look.size / BRUNO_UNITS
    val breathe = sin(time * 2.2f) * 0.012f
    withTransform({
        translate(center.x, center.y - pose.hop * k)
        scale(k * (1f - breathe), k * (1f + breathe), Offset.Zero)
    }) {
        drawTop(look.top, palette)
        drawArms(pose.arms, palette)
        ellipse(palette.dark, Offset(-16f, 48f), 12f, 7f)
        ellipse(palette.dark, Offset(16f, 48f), 12f, 7f)
        ellipse(palette.body, Offset.Zero, 46f, 50f)
        ellipse(palette.belly, Offset(0f, 20f), 27f, 23f)
        val blinking = pose.eyes == Eyes.OPEN && (time % BLINK_EVERY) < BLINK_FOR
        drawEyes(if (blinking) Eyes.CLOSED else pose.eyes, pose.lookX, pose.lookY)
        circle(Ink.cheek, Offset(-27f, 2f), 6f, alpha = 0.85f)
        circle(Ink.cheek, Offset(27f, 2f), 6f, alpha = 0.85f)
        drawMouth(pose.mouth, time)
        for (accessory in look.wearing) drawAccessory(accessory)
        // The antennae stick out of a hat; horns and ears stay under it.
        if (look.top == Topping.ANTENNAE && look.wearing.any { it.slot == Slot.HEAD }) drawTop(look.top, palette)
        if (pose.arms == Arms.CLAP) drawClapHands(palette)
    }
}

private fun DrawScope.drawTop(top: Topping, palette: BrunoPalette) {
    when (top) {
        Topping.HORNS -> {
            fill(
                path {
                    moveTo(-24f, -40f)
                    quadraticTo(-27f, -62f, -15f, -66f)
                    quadraticTo(-10f, -55f, -10f, -44f)
                    close()
                },
                Ink.horn,
            )
            fill(
                path {
                    moveTo(24f, -40f)
                    quadraticTo(27f, -62f, 15f, -66f)
                    quadraticTo(10f, -55f, 10f, -44f)
                    close()
                },
                Ink.horn,
            )
        }
        Topping.EARS -> {
            circle(palette.body, Offset(-31f, -36f), 13f)
            circle(palette.body, Offset(31f, -36f), 13f)
            circle(Ink.cheek, Offset(-31f, -36f), 6.5f)
            circle(Ink.cheek, Offset(31f, -36f), 6.5f)
        }
        Topping.ANTENNAE -> {
            line(palette.dark, Offset(-12f, -44f), Offset(-22f, -70f), 4f)
            line(palette.dark, Offset(12f, -44f), Offset(22f, -70f), 4f)
            circle(Color(0xFFFFD36E), Offset(-22f, -72f), 7f)
            circle(Color(0xFFFF8FB1), Offset(22f, -72f), 7f)
        }
    }
}

private fun DrawScope.drawArms(arms: Arms, palette: BrunoPalette) {
    when (arms) {
        Arms.DOWN -> {
            tiltedEllipse(palette.dark, Offset(-44f, 12f), 9f, 15f, 20f)
            tiltedEllipse(palette.dark, Offset(44f, 12f), 9f, 15f, -20f)
        }
        Arms.UP -> {
            tiltedEllipse(palette.dark, Offset(-50f, -24f), 9f, 17f, -35f)
            tiltedEllipse(palette.dark, Offset(50f, -24f), 9f, 17f, 35f)
        }
        Arms.CLAP -> Unit
    }
}

private fun DrawScope.drawClapHands(palette: BrunoPalette) {
    tiltedEllipse(palette.dark, Offset(-9f, 26f), 8f, 14f, -50f)
    tiltedEllipse(palette.dark, Offset(9f, 26f), 8f, 14f, 50f)
    line(Ink.horn, Offset(0f, 8f), Offset(0f, 0f), 2.5f)
    line(Ink.horn, Offset(-9f, 10f), Offset(-14f, 4f), 2.5f)
    line(Ink.horn, Offset(9f, 10f), Offset(14f, 4f), 2.5f)
}

private fun DrawScope.drawEyes(eyes: Eyes, lookX: Float, lookY: Float) {
    val left = Offset(-15f, -14f)
    val right = Offset(15f, -14f)
    when (eyes) {
        Eyes.OPEN, Eyes.WIDE -> {
            val white = if (eyes == Eyes.WIDE) 13.5f else 12f
            val pupil = if (eyes == Eyes.WIDE) 4.2f else 5.5f
            val look = Offset(lookX * 4f, lookY * 3f + 1.5f)
            for (eye in listOf(left, right)) {
                circle(Ink.white, eye, white)
                circle(Ink.dark, eye + look, pupil)
                circle(Ink.white, eye + look + Offset(2f, -2.5f), 1.8f)
            }
        }
        Eyes.HAPPY -> for (eye in listOf(left, right)) {
            stroke(
                path {
                    moveTo(eye.x - 8f, eye.y + 3f)
                    quadraticTo(eye.x, eye.y - 8f, eye.x + 8f, eye.y + 3f)
                },
                Ink.dark,
                3.2f,
            )
        }
        Eyes.CLOSED -> for (eye in listOf(left, right)) {
            stroke(
                path {
                    moveTo(eye.x - 8f, eye.y)
                    quadraticTo(eye.x, eye.y + 6f, eye.x + 8f, eye.y)
                },
                Ink.dark,
                3.2f,
            )
        }
    }
}

private fun DrawScope.drawMouth(mouth: Mouth, time: Float) {
    when (mouth) {
        Mouth.SMILE -> stroke(
            path {
                moveTo(-10f, 5f)
                quadraticTo(0f, 15f, 10f, 5f)
            },
            Ink.dark,
            3f,
        )
        Mouth.GRIN -> {
            fill(
                path {
                    moveTo(-15f, 3f)
                    quadraticTo(0f, 24f, 15f, 3f)
                    close()
                },
                Ink.mouth,
            )
            ellipse(Ink.tongue, Offset(0f, 12f), 6f, 3.5f)
        }
        Mouth.OPEN -> openMouth(12f)
        Mouth.MUNCH -> {
            val open = (sin(time * 16f) + 1f) / 2f
            if (open < 0.35f) {
                stroke(
                    path {
                        moveTo(-11f, 8f)
                        quadraticTo(0f, 14f, 11f, 8f)
                    },
                    Ink.dark,
                    3f,
                )
            } else {
                openMouth(3f + 7f * open)
            }
        }
        Mouth.WAVY -> stroke(
            path {
                moveTo(-13f, 8f)
                for (i in 0 until 4) quadraticTo(-13f + 6.5f * i + 3.25f, if (i % 2 == 0) 3f else 13f, -13f + 6.5f * (i + 1), 8f)
            },
            Ink.dark,
            3f,
        )
        Mouth.O -> ellipse(Ink.mouth, Offset(0f, 8f), 6f, 6.5f)
        Mouth.YAWN -> ellipse(Ink.mouth, Offset(0f, 9f), 10f, 14f)
        Mouth.SLEEP -> stroke(
            path {
                moveTo(-5f, 8f)
                quadraticTo(0f, 10f, 5f, 8f)
            },
            Ink.dark,
            2.5f,
        )
    }
}

private fun DrawScope.openMouth(height: Float) {
    ellipse(Ink.mouth, Offset(0f, 8f), 15f, height)
    if (height > 6f) {
        ellipse(Ink.tongue, Offset(0f, 8f + height * 0.55f), 9f, height * 0.35f)
        drawRect(Ink.white, Offset(-8f, 8f - height + 1f), Size(5f, 4.5f))
        drawRect(Ink.white, Offset(3f, 8f - height + 1f), Size(5f, 4.5f))
    }
}

/** An [accessory] where it goes on Bruno, in his units. */
internal fun DrawScope.drawAccessory(accessory: Accessory) {
    when (accessory) {
        Accessory.BEANIE -> {
            fill(
                path {
                    moveTo(-28f, -38f)
                    quadraticTo(-26f, -70f, 0f, -70f)
                    quadraticTo(26f, -70f, 28f, -38f)
                    close()
                },
                Color(0xFFE4483C),
            )
            drawRoundRect(Color(0xFFF7A39B), Offset(-30f, -44f), Size(60f, 10f), CornerRadius(5f))
            circle(Color.White, Offset(0f, -72f), 7f)
        }
        Accessory.GLASSES -> {
            ring(Ink.dark, Offset(-15f, -14f), 15f, 3f)
            ring(Ink.dark, Offset(15f, -14f), 15f, 3f)
            line(Ink.dark, Offset(-1f, -15f), Offset(1f, -15f), 3f)
        }
        Accessory.BOW_TIE -> {
            val tie = Color(0xFF2C74D8)
            fill(
                path {
                    moveTo(0f, 30f)
                    lineTo(-15f, 22f)
                    lineTo(-15f, 38f)
                    close()
                },
                tie,
            )
            fill(
                path {
                    moveTo(0f, 30f)
                    lineTo(15f, 22f)
                    lineTo(15f, 38f)
                    close()
                },
                tie,
            )
            circle(Color(0xFF1F5BB0), Offset(0f, 30f), 4.5f)
        }
        Accessory.FLOWERS -> {
            val colors = listOf(Color(0xFFFF8FB1), Color(0xFFFFD36E), Color.White, Color(0xFFFF8FB1), Color(0xFFFFD36E))
            for ((i, x) in listOf(-28f, -14f, 0f, 14f, 28f).withIndex()) {
                val y = -44f - (14f - abs(x) * 0.35f)
                for (petal in 0 until 5) {
                    val a = petal * 1.2566f
                    circle(colors[i], Offset(x + 4.5f * cos(a), y + 4.5f * sin(a)), 3.6f)
                }
                circle(Color(0xFFF4A23C), Offset(x, y), 2.6f)
            }
        }
        Accessory.SCARF -> {
            val scarf = Color(0xFFF4B623)
            fill(
                path {
                    moveTo(-40f, 24f)
                    quadraticTo(0f, 38f, 40f, 24f)
                    lineTo(38f, 34f)
                    quadraticTo(0f, 48f, -38f, 34f)
                    close()
                },
                scarf,
            )
            drawRoundRect(scarf, Offset(18f, 30f), Size(11f, 26f), CornerRadius(4f))
            line(Color(0xFFE4483C), Offset(-30f, 31f), Offset(30f, 31f), 2.5f)
        }
        Accessory.CROWN -> {
            val gold = Color(0xFFF4B623)
            fill(
                path {
                    moveTo(-24f, -42f)
                    lineTo(-26f, -66f)
                    lineTo(-12f, -54f)
                    lineTo(0f, -72f)
                    lineTo(12f, -54f)
                    lineTo(26f, -66f)
                    lineTo(24f, -42f)
                    close()
                },
                gold,
            )
            circle(Color(0xFFE4483C), Offset(0f, -50f), 4f)
            fill(star(Offset(0f, -72f), 5f), Color.White)
        }
    }
}

/** How often Bruno blinks, in seconds, and for how long. */
private const val BLINK_EVERY = 4.3f
private const val BLINK_FOR = 0.13f
