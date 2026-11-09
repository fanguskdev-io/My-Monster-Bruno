package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Bin
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.Rubbish
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.rubbishRounds
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBin
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawRubbish
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/** Bruno, on the left, cheering. */
private val BRUNO_CENTER = Offset(108f, 262f)
private const val BRUNO_HEIGHT = 170f

/** The three bins in a row, their mouths up: yellow, blue and green. */
private val BINS: List<Pair<Bin, Rect>> = Bin.entries.mapIndexed { i, bin ->
    val x = 300f + i * 170f
    bin to Rect(x - 58f, 236f, x + 58f, 364f)
}

/** Where each thing appears, waiting to be taken. */
private val THING_HOME = Offset(470f, 112f)
private const val THING_SIZE = 96f
private const val THING_TOUCH = 56f

private enum class RecyclePhase { ASKING, WRONG, RIGHT, THANKS }

/**
 * The bins (ADR 0044), on the island of the house by turns with dressing Bruno: the yellow one for packaging, the blue
 * one for paper and the green one for glass, as in the street. One thing at a time appears, «¿Dónde va la lata?», and
 * she takes it to a bin: if it is its bin, the lid lifts and in it goes, «¡Sí! La lata, al amarillo»; if not, it goes
 * back, «La lata va al amarillo», and its bin calls. Six things, two for each bin.
 */
@Composable
internal fun RecycleScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val things = remember { rubbishRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(RecyclePhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    var position by remember { mutableStateOf(THING_HOME) }
    var flight by remember { mutableStateOf<Flight?>(null) }
    var holding by remember { mutableStateOf(false) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var hinted by remember { mutableStateOf(false) }

    fun current(): Rubbish = things[round.coerceAtMost(things.lastIndex)]

    LaunchedEffect(round) {
        phase = RecyclePhase.ASKING
        phaseAt = clock.value
        position = THING_HOME
        flight = null
        hinted = false
        if (round == 0) audio.say(Voice.RECYCLE_INTRO)
        audio.say(Voice.whereGoes(current()))
    }

    LaunchedEffect(round, phase) {
        if (phase != RecyclePhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(Voice.whereGoes(current()))
        }
    }

    fun drop(bin: Bin, box: Rect) {
        val thing = current()
        val now = clock.value
        holding = false
        if (bin == thing.bin) {
            phase = RecyclePhase.RIGHT
            phaseAt = now
            flight = Flight(position, Offset(box.center.x, box.top + 10f), now, FLY_SECONDS)
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(recycled = it.recycled.plusOne(bin.word)) }
            scope.launch {
                audio.say(Voice.goesRight(thing))
                delay(PAUSE_MILLIS)
                if (round + 1 < things.size) {
                    round++
                } else {
                    phase = RecyclePhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_RECYCLE)
                    onDone()
                }
            }
        } else {
            phase = RecyclePhase.WRONG
            phaseAt = now
            hinted = true
            flight = Flight(position, THING_HOME, now, FLY_SECONDS)
            position = THING_HOME
            audio.sfx(Sfx.BOING)
            game.missed(Miss.bin(thing.bin))
            scope.launch {
                audio.say(Voice.goesTo(thing))
                phase = RecyclePhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == RecyclePhase.ASKING) audio.speak(Voice.whereGoes(current()))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != RecyclePhase.ASKING || !hits(point, position, THING_TOUCH)) return false
                holding = true
                flight = null
                grab = position - point
                audio.sfx(Sfx.TAP)
                return true
            }

            override fun move(point: Offset) {
                if (holding) position = point + grab
            }

            override fun up(point: Offset) {
                if (!holding) return
                val into = BINS.firstOrNull { (_, box) -> box.inflate(TOUCH_SLACK).contains(position) }
                if (into != null) {
                    drop(into.first, into.second)
                } else {
                    flight = Flight(position, THING_HOME, clock.value, FLY_SECONDS * 0.6f)
                    position = THING_HOME
                    holding = false
                }
            }

            override fun cancel() {
                position = THING_HOME
                holding = false
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val thing = current()
        drawGarden(stage, t)
        val pose = when (phase) {
            RecyclePhase.RIGHT, RecyclePhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            RecyclePhase.WRONG -> Pose(Mouth.O, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = 0.4f)
            RecyclePhase.ASKING -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.3f, hop = 1.5f * sin(t * 3f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == RecyclePhase.THANKS) drawSparkles(BRUNO_CENTER, 120f, t)

        val going = flight
        val falling = phase == RecyclePhase.RIGHT && going != null
        for ((bin, box) in BINS) {
            val open = if (falling && bin == thing.bin) clamp01(since / FLY_SECONDS) * clamp01((1.4f - since) / 0.4f) else 0f
            drawBin(bin, box, open)
            if (hinted && phase == RecyclePhase.ASKING && bin == thing.bin) drawCallRing(box.center, 74f, t, bin.paint())
        }
        // The thing, waiting, flying back or into its bin, where it goes in and is not seen any more.
        val at = if (going != null && !going.over(t)) going.at(t) else position
        val gone = phase == RecyclePhase.RIGHT && going != null && going.over(t) || phase == RecyclePhase.THANKS
        if (!gone) {
            val bob = if (phase == RecyclePhase.ASKING && !holding) 4f * sin(t * 2.5f) else 0f
            if (!holding) circle(Color.White, at + Offset(0f, bob), THING_SIZE * 0.55f, alpha = 0.7f)
            drawRubbish(thing, at + Offset(0f, bob), THING_SIZE)
        }

        for (i in things.indices) {
            val done = i < round || (i == round && (phase == RecyclePhase.RIGHT || phase == RecyclePhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(340f + 24f * i, 388f), 8f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val FLY_SECONDS = 0.45f
private const val PAUSE_MILLIS = 800L
private const val IDLE_MILLIS = 9000L
