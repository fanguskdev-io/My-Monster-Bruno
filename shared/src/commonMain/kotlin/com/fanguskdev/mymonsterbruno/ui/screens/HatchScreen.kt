package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.BrunoColor
import com.fanguskdev.mymonsterbruno.game.Topping
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Ink
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawEgg
import com.fanguskdev.mymonsterbruno.ui.art.drawEggBottom
import com.fanguskdev.mymonsterbruno.ui.art.drawNightSky
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.palette
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.easeBack
import com.fanguskdev.mymonsterbruno.ui.easeOut
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

private enum class HatchPhase { EGG, HATCHING, CHOOSE }

private val EGG_CENTER = Offset(400f, 236f)
private const val EGG_WIDTH = 190f
private const val EGG_HEIGHT = 230f
private const val TAPS_TO_HATCH = 5
private val BRUNO_IN_EGG = Offset(400f, 190f)

/** Where the bottom half of the egg goes once he is out: lower, so his belly and his colour are seen. */
private val SHELL_CENTER = Offset(400f, 272f)
private const val BRUNO_HEIGHT = 200f
private val COLOR_BUTTONS = listOf(Offset(88f, 132f), Offset(196f, 132f), Offset(88f, 264f), Offset(196f, 264f))
private val TOP_BUTTONS = listOf(Offset(712f, 96f), Offset(712f, 206f), Offset(712f, 316f))
private const val CHOICE_RADIUS = 48f
private val OK_BUTTON = Offset(566f, 330f)
private const val OK_RADIUS = 44f

/**
 * The first time: an egg that cracks a bit more with each touch until Bruno comes out, says hello and lets her choose his
 * colour and what he has on his head (docs/diseno.md, «Nace tu monstruo»). The green button keeps him and goes to his room.
 */
@Composable
internal fun HatchScreen(game: GameState) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var phase by remember { mutableStateOf(HatchPhase.EGG) }
    var cracks by remember { mutableStateOf(0) }
    var tappedAt by remember { mutableStateOf(-10f) }
    var hatchedAt by remember { mutableStateOf(0f) }
    var cheerAt by remember { mutableStateOf(-10f) }
    var look by remember { mutableStateOf(game.data.look) }
    var colorChosen by remember { mutableStateOf(false) }
    var topChosen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { audio.say(Voice.EGG) }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                val now = clock.value
                when (phase) {
                    HatchPhase.EGG -> {
                        if (!hits(point, EGG_CENTER, EGG_WIDTH / 2)) return false
                        cracks++
                        tappedAt = now
                        audio.sfx(Sfx.CRACK)
                        if (cracks == 2) audio.speak(Voice.EGG_AGAIN)
                        if (cracks >= TAPS_TO_HATCH) {
                            phase = HatchPhase.HATCHING
                            hatchedAt = now
                            audio.hush()
                            audio.sfx(Sfx.HATCH)
                            scope.launch {
                                delay(HATCH_MILLIS)
                                phase = HatchPhase.CHOOSE
                                cheerAt = clock.value
                                audio.say(Voice.HELLO_I_AM_BRUNO, Voice.CHOOSE_COLOR)
                            }
                        }
                    }
                    HatchPhase.HATCHING -> Unit
                    HatchPhase.CHOOSE -> choose(point, now)
                }
                return true
            }

            private fun choose(point: Offset, now: Float) {
                COLOR_BUTTONS.forEachIndexed { i, button ->
                    if (hits(point, button, CHOICE_RADIUS)) {
                        look = look.copy(color = BrunoColor.entries[i])
                        cheerAt = now
                        audio.sfx(Sfx.POP)
                        if (!colorChosen) {
                            colorChosen = true
                            audio.speak(Voice.LOVE_IT, Voice.CHOOSE_TOP)
                        } else {
                            audio.speak(Voice.GIGGLE)
                        }
                        return
                    }
                }
                if (colorChosen) {
                    TOP_BUTTONS.forEachIndexed { i, button ->
                        if (hits(point, button, CHOICE_RADIUS)) {
                            look = look.copy(top = Topping.entries[i])
                            cheerAt = now
                            audio.sfx(Sfx.POP)
                            if (!topChosen) {
                                topChosen = true
                                audio.speak(Voice.HANDSOME, Voice.GREEN_BUTTON)
                            } else {
                                audio.speak(Voice.GIGGLE)
                            }
                            return
                        }
                    }
                }
                if (colorChosen && topChosen && hits(point, OK_BUTTON, OK_RADIUS)) {
                    audio.sfx(Sfx.RIGHT)
                    audio.hush()
                    // The day he hatches is the egg's: «¿Cómo te sientes hoy?» starts the next day (ADR 0019).
                    game.update { it.copy(hatched = true, look = look, moodDay = game.currentDay()) }
                    game.go(Scene.Home)
                    return
                }
                if (hits(point, BRUNO_IN_EGG, BRUNO_HEIGHT / 2)) {
                    cheerAt = now
                    audio.speak(Voice.GIGGLE)
                }
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawNightSky(stage, t)
        ellipse(Color(0xFFE79BBA), Offset(400f, 352f), 170f, 34f)
        ellipse(Color(0xFFF6B9D0), Offset(400f, 344f), 150f, 24f)
        val cheering = t - cheerAt < CHEER_SECONDS
        val pose = if (cheering) {
            Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 6f * sin(((t - cheerAt) / CHEER_SECONDS) * 3.14f))
        } else {
            Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN)
        }
        when (phase) {
            HatchPhase.EGG -> {
                val since = t - tappedAt
                val tilt = if (since < 1f) 9f * sin(since * 30f) * exp(-5f * since) else 2.5f * sin(t * 2.5f)
                if (cracks == 0) drawCallRing(EGG_CENTER + Offset(0f, 30f), 120f, t)
                drawEgg(EGG_CENTER, EGG_WIDTH, EGG_HEIGHT, cracks, tilt)
            }
            HatchPhase.HATCHING -> {
                val since = t - hatchedAt
                val pop = easeBack((since / POP_SECONDS).coerceIn(0f, 1f))
                drawBruno(
                    look,
                    BRUNO_IN_EGG + Offset(0f, (1f - pop) * 70f),
                    BRUNO_HEIGHT * (0.5f + 0.5f * pop),
                    Pose(Mouth.O, Eyes.WIDE),
                    t,
                )
                drawEggBottom(SHELL_CENTER, EGG_WIDTH, EGG_HEIGHT)
                drawFlyingShell(since)
                drawSparkles(BRUNO_IN_EGG, 130f, t)
            }
            HatchPhase.CHOOSE -> {
                drawBruno(look, BRUNO_IN_EGG, BRUNO_HEIGHT, pose, t)
                drawEggBottom(SHELL_CENTER, EGG_WIDTH, EGG_HEIGHT)
                if (cheering) drawSparkles(BRUNO_IN_EGG, 125f, t)
                drawChoices(look.color, look.top, colorChosen, topChosen, t)
                drawNameRibbon(measurer, font)
            }
        }
    }
}

private fun DrawScope.drawFlyingShell(since: Float) {
    val fly = easeOut(since / FLY_SECONDS)
    if (fly >= 1f) return
    val center = EGG_CENTER + Offset(60f * fly, -60f - 240f * fly)
    rotate(110f * fly, center) {
        val half = EGG_WIDTH / 2
        val shell = path {
            moveTo(center.x - half * 0.95f, center.y + 20f)
            quadraticTo(center.x - half * 0.95f, center.y - 90f, center.x, center.y - 92f)
            quadraticTo(center.x + half * 0.95f, center.y - 90f, center.x + half * 0.95f, center.y + 20f)
            for (i in 1..8) lineTo(center.x + half * 0.95f - half * 1.9f * i / 8, if (i % 2 == 1) center.y + 2f else center.y + 20f)
            close()
        }
        fill(shell, Color(0xFFFFF6E5), alpha = 1f - fly * 0.6f)
    }
}

private fun DrawScope.drawChoices(
    color: BrunoColor,
    top: Topping,
    colorChosen: Boolean,
    topChosen: Boolean,
    t: Float,
) {
    BrunoColor.entries.forEachIndexed { i, option ->
        val center = COLOR_BUTTONS[i]
        circle(Color(0x33FFFFFF), center, CHOICE_RADIUS + 8f)
        circle(option.palette().body, center, CHOICE_RADIUS)
        circle(option.palette().belly, center + Offset(0f, 14f), CHOICE_RADIUS * 0.45f)
        if (colorChosen && option == color) ring(Color.White, center, CHOICE_RADIUS + 9f, 6f)
    }
    if (!colorChosen) {
        drawCallRing(COLOR_BUTTONS[0], CHOICE_RADIUS + 6f, t)
        return
    }
    Topping.entries.forEachIndexed { i, option ->
        val center = TOP_BUTTONS[i]
        circle(Color.White, center, CHOICE_RADIUS)
        drawToppingIcon(option, center)
        if (topChosen && option == top) ring(Color(0xFFFFD36E), center, CHOICE_RADIUS + 9f, 6f)
    }
    if (!topChosen) {
        drawCallRing(TOP_BUTTONS[0], CHOICE_RADIUS + 6f, t)
        return
    }
    drawRoundButton(OK_BUTTON, OK_RADIUS, Color(0xFF2E9E5B), glow = (t % 1.6f) / 1.6f) {
        stroke(
            path {
                moveTo(OK_BUTTON.x - 18f, OK_BUTTON.y + 1f)
                lineTo(OK_BUTTON.x - 5f, OK_BUTTON.y + 14f)
                lineTo(OK_BUTTON.x + 20f, OK_BUTTON.y - 12f)
            },
            Color.White,
            9f,
        )
    }
}

private fun DrawScope.drawToppingIcon(top: Topping, center: Offset) {
    when (top) {
        Topping.HORNS -> {
            for (side in listOf(-1f, 1f)) {
                fill(
                    path {
                        moveTo(center.x + side * 22f, center.y + 20f)
                        quadraticTo(center.x + side * 26f, center.y - 14f, center.x + side * 10f, center.y - 22f)
                        quadraticTo(center.x + side * 4f, center.y, center.x + side * 6f, center.y + 18f)
                        close()
                    },
                    Ink.horn,
                )
            }
        }
        Topping.EARS -> {
            circle(Color(0xFF9B7BEA), center + Offset(-17f, 0f), 15f)
            circle(Color(0xFF9B7BEA), center + Offset(17f, 0f), 15f)
            circle(Ink.cheek, center + Offset(-17f, 0f), 7f)
            circle(Ink.cheek, center + Offset(17f, 0f), 7f)
        }
        Topping.ANTENNAE -> {
            line(Color(0xFF46526F), center + Offset(-8f, 24f), center + Offset(-18f, -12f), 4f)
            line(Color(0xFF46526F), center + Offset(8f, 24f), center + Offset(18f, -12f), 4f)
            circle(Color(0xFFFFD36E), center + Offset(-18f, -16f), 8f)
            circle(Color(0xFFFF8FB1), center + Offset(18f, -16f), 8f)
        }
    }
}

private fun DrawScope.drawNameRibbon(measurer: androidx.compose.ui.text.TextMeasurer, font: androidx.compose.ui.text.font.FontFamily) {
    val c = Offset(400f, 44f)
    fill(
        path {
            moveTo(c.x - 96f, c.y - 22f)
            lineTo(c.x + 96f, c.y - 22f)
            lineTo(c.x + 82f, c.y)
            lineTo(c.x + 96f, c.y + 22f)
            lineTo(c.x - 96f, c.y + 22f)
            lineTo(c.x - 82f, c.y)
            close()
        },
        Color(0xFFFF8FB1),
    )
    label(measurer, "BRUNO", c + Offset(0f, 2f), 30f, Color.White, font)
}

private const val HATCH_MILLIS = 1300L
private const val POP_SECONDS = 0.8f
private const val FLY_SECONDS = 1.1f
private const val CHEER_SECONDS = 1.2f
