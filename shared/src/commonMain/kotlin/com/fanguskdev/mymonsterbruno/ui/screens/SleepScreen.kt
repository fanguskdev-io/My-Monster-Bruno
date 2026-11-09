package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBed
import com.fanguskdev.mymonsterbruno.ui.art.drawBlanket
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawNightRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundPhoto
import com.fanguskdev.mymonsterbruno.ui.art.drawToothbrush
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.rememberPhoto
import com.fanguskdev.mymonsterbruno.ui.ring
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BED = Offset(470f, 268f)
private val BRUNO_IN_BED = Offset(470f, 236f)
private const val BRUNO_HEIGHT = 190f
private val MISSION_CARD = Offset(36f, 214f)
private val MISSION_SIZE = Size(200f, 150f)

/** The mission for home that goes with what she did today (docs/diseno.md, «Misión para casa»). */
private enum class Mission(val line: String) {
    LETTER(Voice.MISSION_LETTER),
    COUNT(Voice.MISSION_COUNT),
    COLOR(Voice.MISSION_COLOR),
    SHAPE(Voice.MISSION_SHAPE),
    FEELING(Voice.MISSION_FEELING),
    NAME(Voice.MISSION_NAME),
    TEETH(Voice.MISSION_HOME),
}

/**
 * Bedtime (ADR 0006): the minutes of the day are over. If she got here playing, Bruno yawns, gives her a mission for home
 * and says goodnight; then he sleeps, and a touch only gets a sleepy «mañana jugamos más». The game wakes up the next
 * day, or when a grown-up gives more time in their corner (the padlock, held a while).
 */
@Composable
internal fun SleepScreen(game: GameState) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val mission = remember {
        when (game.lastIsland) {
            Island.VOWELS -> Mission.LETTER
            Island.NUMBERS -> Mission.COUNT
            Island.SHAPES -> Mission.SHAPE
            Island.FEELINGS -> Mission.FEELING
            Island.WORDS -> Mission.NAME
            Island.HOME -> Mission.TEETH
            Island.COLORS, null -> Mission.COLOR
        }
    }
    val photo = rememberPhoto(game)
    var asleep by remember { mutableStateOf(!game.wentToBed) }
    var lockSince by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        if (!asleep) {
            audio.say(Voice.YAWN, mission.line, Voice.SEE_YOU_TOMORROW)
            asleep = true
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                val now = clock.value
                when {
                    hits(point, lockButton(stage), LOCK_RADIUS) -> {
                        lockSince = now
                        scope.launch {
                            delay((HOLD_SECONDS * 1000).toLong())
                            if (lockSince == now) {
                                lockSince = null
                                audio.hush()
                                game.go(Scene.GrownUps)
                            }
                        }
                    }
                    asleep && hits(point, BRUNO_IN_BED, BRUNO_HEIGHT / 2) -> audio.speak(Voice.ASLEEP)
                    else -> return false
                }
                return true
            }

            override fun up(point: Offset) {
                lockSince = null
            }

            override fun cancel() {
                lockSince = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawNightRoom(stage, t)
        drawBed(BED)
        val pose = if (asleep) Pose(Mouth.SLEEP, Eyes.CLOSED) else Pose(Mouth.YAWN, Eyes.CLOSED)
        drawBruno(game.data.look, BRUNO_IN_BED, BRUNO_HEIGHT, pose, t * 0.5f)
        drawBlanket(BED + Offset(0f, 24f))
        if (asleep) drawSnores(BRUNO_IN_BED + Offset(60f, -100f), t, measurer, font)
        drawMissionCard(mission, game.data.childName.firstOrNull { it.isLetter() }, photo, measurer, font)
        drawGrownUpsLock(stage, t, lockSince)
    }
}

private fun DrawScope.drawSnores(
    at: Offset,
    t: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    for (i in 0 until 3) {
        val phase = ((t * 0.4f + i / 3f) % 1f)
        val p = at + Offset(30f * phase + 10f * sin(t + i), -70f * phase)
        label(measurer, "Z", p, 18f + 16f * phase, Color.White.copy(alpha = 1f - phase), font)
    }
}

/**
 * The mission, drawn without words: a house and what to look for there; for her name, its [initial] if they wrote it.
 * Her [photo], if there is one, is pinned on its corner: the mission is hers (ADR 0034).
 */
private fun DrawScope.drawMissionCard(
    mission: Mission,
    initial: Char?,
    photo: ImageBitmap?,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val c = MISSION_CARD
    drawRoundRect(Shadow, c + Offset(0f, 6f), MISSION_SIZE, CornerRadius(20f))
    drawRoundRect(Color.White, c, MISSION_SIZE, CornerRadius(20f))
    val house = path {
        moveTo(c.x + 26f, c.y + 72f)
        lineTo(c.x + 62f, c.y + 40f)
        lineTo(c.x + 98f, c.y + 72f)
        lineTo(c.x + 98f, c.y + 118f)
        lineTo(c.x + 26f, c.y + 118f)
        close()
    }
    stroke(house, Color(0xFF1D2B53), 5f)
    fill(
        path {
            moveTo(c.x + 54f, c.y + 118f)
            lineTo(c.x + 54f, c.y + 94f)
            lineTo(c.x + 70f, c.y + 94f)
            lineTo(c.x + 70f, c.y + 118f)
            close()
        },
        Color(0xFF1D2B53),
    )
    val what = c + Offset(148f, 78f)
    when (mission) {
        Mission.LETTER -> {
            circle(Color(0xFFE4483C), what, 34f)
            label(measurer, "A", what + Offset(0f, 2f), 44f, Color.White, font)
        }
        Mission.COUNT -> {
            circle(Color(0xFF2C74D8), what, 34f)
            label(measurer, "123", what + Offset(0f, 2f), 26f, Color.White, font)
        }
        Mission.COLOR -> {
            circle(Color(0xFFF4B623), what, 34f)
            val eye = path {
                moveTo(what.x - 22f, what.y)
                quadraticTo(what.x, what.y - 18f, what.x + 22f, what.y)
                quadraticTo(what.x, what.y + 18f, what.x - 22f, what.y)
                close()
            }
            fill(eye, Color.White)
            circle(Color(0xFF1D2B53), what, 7f)
        }
        Mission.SHAPE -> {
            circle(Color(0xFF2E9E5B), what, 34f)
            ring(Color.White, what, 17f, 6f)
        }
        Mission.NAME -> {
            circle(Color(0xFF9B6BD3), what, 34f)
            if (initial != null) {
                label(measurer, initial.uppercaseChar().toString(), what + Offset(0f, 2f), 44f, Color.White, font)
            } else {
                label(measurer, "ABC", what + Offset(0f, 2f), 24f, Color.White, font)
            }
        }
        Mission.TEETH -> {
            circle(Color(0xFF4FB3BF), what, 34f)
            rotate(-30f, what) { drawToothbrush(what + Offset(-20f, 2f), 0.3f) }
        }
        Mission.FEELING -> {
            circle(Color(0xFFFF6F91), what, 34f)
            circle(Color.White, what + Offset(-7f, -4f), 8f)
            circle(Color.White, what + Offset(7f, -4f), 8f)
            fill(
                path {
                    moveTo(what.x - 15f, what.y)
                    lineTo(what.x + 15f, what.y)
                    lineTo(what.x, what.y + 17f)
                    close()
                },
                Color.White,
            )
        }
    }
    photo?.let { drawRoundPhoto(it, c + Offset(MISSION_SIZE.width - 10f, 6f), 26f) }
}
