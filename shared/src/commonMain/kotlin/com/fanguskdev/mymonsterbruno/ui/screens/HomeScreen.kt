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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBalloon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStar
import com.fanguskdev.mymonsterbruno.ui.art.micIcon
import com.fanguskdev.mymonsterbruno.ui.art.playIcon
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_AT_HOME = Offset(390f, 236f)
private const val BRUNO_HEIGHT = 230f
private val PLAY_BUTTON = Offset(680f, 290f)
private const val PLAY_RADIUS = 68f
private val CALM_BUTTON = Offset(82f, 318f)
private const val CALM_RADIUS = 46f
private val TALK_BUTTON = Offset(82f, 200f)

/**
 * Bruno's room, where every game starts: Bruno says hello and waits, laughs if she tickles him, and the big green button
 * goes to the islands. The microphone is «Habla con Bruno», the balloon the calm corner, and the padlock, held a while,
 * the grown-ups' corner. Her name, if the grown-ups wrote it, hangs on the bunting.
 */
@Composable
internal fun HomeScreen(game: GameState) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var cheerAt by remember { mutableStateOf(-10f) }
    var lockSince by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        if (!game.greeted) {
            game.greeted = true
            cheerAt = clock.value
            audio.say(Voice.HELLO, Voice.GO_TO_ISLAND)
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                val now = clock.value
                when {
                    hits(point, PLAY_BUTTON, PLAY_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.play()
                    }
                    hits(point, CALM_BUTTON, CALM_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Calm)
                    }
                    hits(point, TALK_BUTTON, CALM_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Talk)
                    }
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
                    hits(point, BRUNO_AT_HOME, BRUNO_HEIGHT / 2) -> {
                        cheerAt = now
                        audio.sfx(Sfx.POP)
                        audio.speak(if (now.toInt() % 2 == 0) Voice.TICKLES else Voice.GIGGLE)
                    }
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
        drawRoom(stage, t)
        drawBunting(game.data.childName, measurer, font)
        val since = t - cheerAt
        val pose = if (since < 1.3f) {
            Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin((since / 1.3f) * 3.14f))
        } else {
            Pose(lookX = 0.5f * sin(t * 0.4f), hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_AT_HOME, BRUNO_HEIGHT, pose, t)
        if (since < 1.3f) drawSparkles(BRUNO_AT_HOME, 140f, t)
        drawRoundButton(PLAY_BUTTON, PLAY_RADIUS, Color(0xFF2E9E5B), glow = (t % 1.8f) / 1.8f) { playIcon(PLAY_BUTTON, 70f) }
        drawRoundButton(CALM_BUTTON, CALM_RADIUS, Color(0xFFDDF1E3)) {
            drawBalloon(CALM_BUTTON + Offset(0f, -8f), 52f, Color(0xFF2E9E5B))
            line(Color(0xFF46526F), CALM_BUTTON + Offset(0f, 22f), CALM_BUTTON + Offset(0f, 34f), 2f)
        }
        drawRoundButton(TALK_BUTTON, CALM_RADIUS, Color(0xFFE85A7A)) { micIcon(TALK_BUTTON, 40f) }
        drawGrownUpsLock(stage, t, lockSince)
    }
}

/** The bunting over the room: a pennant for each letter of her name, or stars if there is no name. */
private fun DrawScope.drawBunting(name: String, measurer: TextMeasurer, font: FontFamily) {
    val letters = name.uppercase().filter { it.isLetter() }.take(MAX_LETTERS)
    val count = if (letters.isEmpty()) 5 else letters.length
    val width = PENNANT_WIDTH * count
    val left = 520f - width / 2
    val rope = path {
        moveTo(left - 30f, 36f)
        quadraticTo(520f, 60f, left + width + 30f, 36f)
    }
    stroke(rope, Color(0xFF8A5A3C), 2.5f)
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
    for (i in 0 until count) {
        val x = left + PENNANT_WIDTH * i + PENNANT_WIDTH / 2
        val sag = 18f * (1f - ((x - 520f) / (width / 2 + 30f)).let { it * it })
        val top = 38f + sag
        fill(
            path {
                moveTo(x - PENNANT_WIDTH * 0.45f, top)
                lineTo(x + PENNANT_WIDTH * 0.45f, top)
                lineTo(x, top + 50f)
                close()
            },
            colors[i % colors.size],
        )
        if (letters.isEmpty()) {
            drawStar(Offset(x, top + 17f), 9f, Color.White)
        } else {
            label(measurer, letters[i].toString(), Offset(x, top + 17f), 24f, Color.White, font)
        }
    }
}

private const val MAX_LETTERS = 10
private const val PENNANT_WIDTH = 40f
