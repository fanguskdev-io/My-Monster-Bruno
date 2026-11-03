package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.countRounds
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawBush
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawHand
import com.fanguskdev.mymonsterbruno.ui.art.drawPlate
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStrawberry
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(172f, 226f)
private const val BRUNO_HEIGHT = 220f
private val PLATE = Offset(330f, 340f)
private val BUSH = Offset(610f, 262f)
private val BERRIES = listOf(
    Offset(530f, 214f),
    Offset(612f, 188f),
    Offset(694f, 214f),
    Offset(548f, 296f),
    Offset(632f, 284f),
    Offset(712f, 302f),
)
private val ON_PLATE = listOf(Offset(276f, 328f), Offset(306f, 318f), Offset(336f, 328f), Offset(366f, 318f), Offset(396f, 328f))
private const val BERRY_RADIUS = 30f
private const val BERRY_TOUCH = 40f
private val CARD = Offset(400f, 62f)

/**
 * The strawberries (docs/diseno.md): «Bruno quiere tres fresas. ¿Se las das?». The card says how many with the number and
 * with the fingers of a hand, as they count in the classroom; each strawberry she touches flies to his plate and the voice
 * counts it, «una, dos, tres». With the right number Bruno eats them. Five rounds, from one to five.
 */
@Composable
internal fun CountScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { countRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    val picked = remember { mutableStateListOf<Int>() }
    val pickedAt = remember { mutableStateListOf<Float>() }
    var eatingAt by remember { mutableStateOf<Float?>(null) }
    var thanks by remember { mutableStateOf(false) }

    fun wanted() = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        picked.clear()
        pickedAt.clear()
        eatingAt = null
        audio.say(Voice.askCount(wanted()))
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (eatingAt == null) audio.speak(Voice.askCount(wanted()))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (eatingAt != null || thanks) return false
                val berry = BERRIES.indices.firstOrNull { it !in picked && hits(point, BERRIES[it], BERRY_TOUCH) } ?: return false
                picked.add(berry)
                pickedAt.add(clock.value)
                audio.sfx(Sfx.PICK)
                val count = picked.size
                val target = wanted()
                if (count < target) {
                    audio.speak(Voice.number(count))
                } else {
                    eatingAt = clock.value + FLY_SECONDS
                    scope.launch {
                        audio.say(Voice.number(count))
                        audio.sfx(Sfx.MUNCH)
                        game.update { it.copy(counted = it.counted.plusOne(target.toString())) }
                        audio.say(Voice.strawberries(target))
                        delay(PAUSE_MILLIS)
                        if (round + 1 < rounds.size) {
                            round++
                        } else {
                            thanks = true
                            audio.sfx(Sfx.SPARKLE)
                            audio.say(Voice.THANKS_STRAWBERRIES)
                            onDone()
                        }
                    }
                }
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawGarden(stage, t)
        val eating = eatingAt?.let { t >= it } ?: false
        val pose = when {
            thanks -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(t * 5f).coerceAtLeast(0f))
            eating -> Pose(Mouth.MUNCH, Eyes.HAPPY, Arms.UP, hop = 6f * sin(t * 6f).coerceAtLeast(0f))
            else -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = 0.3f, hop = 1.5f * sin(t * 3f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        drawPlate(PLATE, 190f)
        drawBush(BUSH, 300f)
        BERRIES.forEachIndexed { i, home ->
            if (i in picked) return@forEachIndexed
            drawStrawberry(home + Offset(0f, 2f * sin(t * 2f + i)), BERRY_RADIUS)
        }
        picked.forEachIndexed { n, berry ->
            val fly = easeInOut((t - pickedAt[n]) / FLY_SECONDS)
            val at = lerp(BERRIES[berry], ON_PLATE[n], fly)
            val gone = eatingAt?.let { (t - it) * 3f > n + 1 } ?: false
            if (!gone) drawStrawberry(at, BERRY_RADIUS * 0.8f)
        }
        if (eating || thanks) drawSparkles(BRUNO_CENTER + Offset(0f, 20f), 120f, t)
        drawCountCard(wanted(), picked.size, measurer, font)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** The card of how many: the number, a hand with as many fingers, and a dot for each one counted. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCountCard(
    wanted: Int,
    counted: Int,
    measurer: androidx.compose.ui.text.TextMeasurer,
    font: androidx.compose.ui.text.font.FontFamily,
) {
    drawRoundRect(Shadow, Offset(CARD.x - 110f, CARD.y - 46f + 5f), Size(220f, 92f), CornerRadius(18f))
    drawRoundRect(Color.White, Offset(CARD.x - 110f, CARD.y - 46f), Size(220f, 92f), CornerRadius(18f))
    label(measurer, wanted.toString(), Offset(CARD.x - 58f, CARD.y - 2f), 78f, Color(0xFFE4483C), font)
    drawHand(Offset(CARD.x + 40f, CARD.y + 6f), 82f, wanted)
    for (i in 0 until wanted) {
        val center = Offset(CARD.x - 12f * (wanted - 1) + 24f * i, CARD.y + 62f)
        circle(if (i < counted) Color(0xFFE4483C) else Color.White, center, 8f)
        circle(Color(0xFFE4483C), center, 8f, alpha = if (i < counted) 1f else 0.25f)
    }
}

private const val FLY_SECONDS = 0.4f
private const val PAUSE_MILLIS = 400L
