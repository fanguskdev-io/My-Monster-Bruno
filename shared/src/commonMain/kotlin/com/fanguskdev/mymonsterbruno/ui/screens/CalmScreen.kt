package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBalloon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawCalm
import com.fanguskdev.mymonsterbruno.ui.art.drawPointer
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay

private val BRUNO_CENTER = Offset(210f, 236f)
private const val BRUNO_HEIGHT = 220f
private val BALLOON = Offset(560f, 150f)
private val HOLD_BUTTON = Offset(560f, 326f)
private const val HOLD_RADIUS = 48f

/** How long the balloon takes to fill while the finger holds, and to empty when it lets go, in seconds. */
private const val BREATH_SECONDS = 3.5f
private const val BREATHS = 3

/**
 * The calm corner (docs/diseno.md), the rincón de la calma of her classroom: she keeps a finger on the button and the
 * balloon fills while she breathes in, then lets go and it empties while she breathes out. Bruno breathes with her.
 * Three breaths and back to his room; the house button leaves before.
 */
@Composable
internal fun CalmScreen(game: GameState) {
    val clock = rememberClock()
    val audio = game.audio
    var holding by remember { mutableStateOf(false) }
    var air by remember { mutableStateOf(0f) }
    var breaths by remember { mutableStateOf(0) }
    var full by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { audio.say(Voice.CALM_INTRO) }

    // The balloon moves a frame at a time: up while she holds, down when she lets go.
    LaunchedEffect(Unit) {
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                val seconds = (now - last) / 1_000_000_000f
                last = now
                val before = air
                air = (air + (if (holding) 1f else -1f) * seconds / BREATH_SECONDS).coerceIn(0f, 1f)
                if (air >= 1f && before < 1f && !full) {
                    full = true
                    audio.speak(Voice.CALM_LET_GO)
                }
                if (full && air <= 0f) {
                    full = false
                    breaths++
                    if (breaths >= BREATHS) {
                        audio.speak(Voice.CALM_END)
                    } else {
                        audio.speak(Voice.CALM_AGAIN)
                    }
                }
            }
            if (breaths >= BREATHS) {
                delay(END_MILLIS)
                game.back()
                return@LaunchedEffect
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (!hits(point, HOLD_BUTTON, HOLD_RADIUS) && !hits(point, BALLOON, 80f)) return false
                holding = true
                return true
            }

            override fun up(point: Offset) {
                holding = false
            }

            override fun cancel() {
                holding = false
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawCalm(stage)
        for (k in 1..3) ring(Color(0xFF79B97A), BALLOON, 70f + 40f * k + 30f * air, 3f, alpha = 0.25f - 0.06f * k)
        val breath = 1f + 0.06f * air
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT * breath, Pose(if (holding) Mouth.O else Mouth.SMILE, Eyes.CLOSED), t)
        stroke(
            path {
                moveTo(BALLOON.x, BALLOON.y + 62f + 50f * air)
                quadraticTo(BALLOON.x - 14f, (BALLOON.y + HOLD_BUTTON.y) / 2 + 30f, HOLD_BUTTON.x, HOLD_BUTTON.y - HOLD_RADIUS)
            },
            Color(0xFF3A3557),
            2f,
        )
        drawBalloon(BALLOON + Offset(0f, 30f * (1f - air)), 100f + 120f * air, Color(0xFF79B97A))
        circle(Shadow, HOLD_BUTTON + Offset(0f, 5f), HOLD_RADIUS)
        circle(Color.White, HOLD_BUTTON, HOLD_RADIUS)
        ring(Color(0xFFDDEBD9), HOLD_BUTTON, HOLD_RADIUS, 7f)
        drawArc(
            Color(0xFF2E9E5B),
            -90f,
            360f * air,
            false,
            Offset(HOLD_BUTTON.x - HOLD_RADIUS, HOLD_BUTTON.y - HOLD_RADIUS),
            Size(HOLD_RADIUS * 2, HOLD_RADIUS * 2),
            style = Stroke(7f),
        )
        drawPointer(HOLD_BUTTON + Offset(0f, 4f), 54f)
        for (i in 0 until BREATHS) {
            circle(if (i < breaths) Color(0xFF2E9E5B) else Color.White, Offset(372f + 28f * i, 24f), 9f)
        }
        drawHomeButton(stage)
    }
}

private const val END_MILLIS = 1800L
