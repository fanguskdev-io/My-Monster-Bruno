package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Clothes
import com.fanguskdev.mymonsterbruno.game.DressRound
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.dressRounds
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawClothes
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawWorn
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** Bruno in his room, right of the middle, looking at the window, with the weather, and at the things. */
private val BRUNO_CENTER = Offset(540f, 236f)
private const val BRUNO_HEIGHT = 230f

/**
 * Where the three things wait, on a low bench at the left, under the window: below where an iPhone can have its Dynamic
 * Island, and far from where the present of the island comes after.
 */
private val SPOTS = listOf(Offset(112f, 316f), Offset(208f, 316f), Offset(304f, 316f))
private const val THING_SIZE = 84f
private const val THING_TOUCH = 46f

/** How near Bruno a thing has to be let go to give it to him. */
private const val REACH = 130f

private enum class DressPhase { ASKING, WRONG, RIGHT, THANKS }

/**
 * Dressing Bruno for the weather (ADR 0044), on the island of the house by turns with the bins: the window of his room
 * shows sun, rain or snow, and Bruno asks «¡Llueve! ¿Qué me pongo?». Three things wait on the bench, one for each
 * weather, and she takes the right one to him: he puts it on, «¡Sí! El paraguas, para la lluvia». If not, «Eso es para
 * cuando hace sol», it goes back and the right one calls. Three rounds, one for each weather.
 */
@Composable
internal fun DressScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { dressRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(DressPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    val positions = remember { mutableStateListOf<Offset>().apply { addAll(SPOTS) } }
    val flights = remember { mutableStateMapOf<Int, Flight>() }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var worn by remember { mutableStateOf<Clothes?>(null) }
    var hinted by remember { mutableStateOf(false) }

    fun current(): DressRound = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        phase = DressPhase.ASKING
        phaseAt = clock.value
        worn = null
        hinted = false
        flights.clear()
        for (i in SPOTS.indices) positions[i] = SPOTS[i]
        if (round == 0) audio.say(Voice.DRESS_INTRO)
        audio.say(Voice.whatToWear(current().weather))
    }

    // Bruno asks again when she waits a while.
    LaunchedEffect(round, phase) {
        if (phase != DressPhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(Voice.whatToWear(current().weather))
        }
    }

    fun give(i: Int) {
        val dress = current()
        val clothes = dress.choices[i]
        val now = clock.value
        dragging = null
        if (clothes == dress.right) {
            phase = DressPhase.RIGHT
            phaseAt = now
            flights[i] = Flight(positions[i], BRUNO_CENTER, now, FLY_SECONDS)
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(dressed = it.dressed.plusOne(dress.weather.word)) }
            scope.launch {
                delay(FLY_MILLIS)
                worn = clothes
                audio.say(Voice.wearIt(clothes))
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = DressPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_DRESS)
                    onDone()
                }
            }
        } else {
            phase = DressPhase.WRONG
            phaseAt = now
            hinted = true
            flights[i] = Flight(positions[i], SPOTS[i], now, FLY_SECONDS)
            positions[i] = SPOTS[i]
            audio.sfx(Sfx.BOING)
            game.missed(Miss.dress(dress.weather))
            scope.launch {
                audio.say(Voice.thatIsFor(clothes.weather), Voice.whatToWear(dress.weather))
                phase = DressPhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == DressPhase.ASKING) audio.speak(Voice.whatToWear(current().weather))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != DressPhase.ASKING) return false
                val i = positions.indices.firstOrNull { hits(point, positions[it], THING_TOUCH) } ?: return false
                dragging = i
                grab = positions[i] - point
                flights.remove(i)
                audio.sfx(Sfx.TAP)
                return true
            }

            override fun move(point: Offset) {
                val i = dragging ?: return
                positions[i] = point + grab
            }

            override fun up(point: Offset) {
                val i = dragging ?: return
                if (distance(positions[i], BRUNO_CENTER) < REACH) {
                    give(i)
                } else {
                    flights[i] = Flight(positions[i], SPOTS[i], clock.value, FLY_SECONDS * 0.6f)
                    positions[i] = SPOTS[i]
                    dragging = null
                }
            }

            override fun cancel() {
                val i = dragging ?: return
                positions[i] = SPOTS[i]
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val dress = current()
        val since = t - phaseAt
        drawRoom(stage, t, dress.weather)
        val pose = when (phase) {
            DressPhase.RIGHT, DressPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            DressPhase.WRONG -> Pose(Mouth.O, Eyes.OPEN, Arms.DOWN, lookX = -0.8f, lookY = 0.6f)
            DressPhase.ASKING -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = -1f, lookY = -0.2f, hop = 1.5f * sin(t * 3f))
        }
        // Without the presents he wears: what she puts on him is what shows.
        val bare = game.data.look.copy(dressed = emptyList())
        drawBruno(bare, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        worn?.let { drawWorn(it, bare, BRUNO_CENTER, BRUNO_HEIGHT) }
        if (phase == DressPhase.RIGHT || phase == DressPhase.THANKS) drawSparkles(BRUNO_CENTER, 150f, t)

        // The bench, and on it the three things, but the one he has on.
        drawRoundRect(Shadow, Offset(58f, 352f), Size(300f, 16f), CornerRadius(8f))
        drawRoundRect(Color(0xFFB5743F), Offset(56f, 344f), Size(300f, 14f), CornerRadius(7f))
        for ((i, clothes) in dress.choices.withIndex()) {
            if (worn == clothes) continue
            val flight = flights[i]
            val at = if (flight != null && !flight.over(t)) flight.at(t) else positions[i]
            if (phase == DressPhase.RIGHT && clothes == dress.right && flight != null && flight.over(t)) continue
            circle(Color.White, at, THING_SIZE * 0.52f, alpha = 0.85f)
            drawClothes(clothes, at, THING_SIZE)
            if (hinted && phase == DressPhase.ASKING && clothes == dress.right && dragging == null) {
                drawCallRing(at, THING_SIZE * 0.6f, t, Color(0xFFF4B623))
            }
        }

        for (i in rounds.indices) {
            val done = i < round || (i == round && (phase == DressPhase.RIGHT || phase == DressPhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(370f + 30f * i, 384f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val FLY_SECONDS = 0.4f
private const val FLY_MILLIS = 400L
private const val PAUSE_MILLIS = 900L
private const val IDLE_MILLIS = 9000L
