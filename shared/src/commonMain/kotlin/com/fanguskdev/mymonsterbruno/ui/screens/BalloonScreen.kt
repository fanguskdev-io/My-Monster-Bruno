package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.BALLOONS
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.balloonRounds
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.BRUNO_UNITS
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBalloon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRainbowPark
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.art.drawTiedBalloon
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(215f, 238f)
private const val BRUNO_HEIGHT = 250f
private val SLOTS = listOf(Offset(520f, 170f), Offset(630f, 144f), Offset(740f, 170f))
private val STAKES = listOf(Offset(520f, 306f), Offset(630f, 306f), Offset(740f, 306f))
private const val BALLOON_SIZE = 116f
private const val BALLOON_TOUCH = 52f
private const val HELD_SIZE = 72f
private val BUBBLE = Offset(92f, 76f)

/** Where the balloons he has float, from his hand: a bunch that grows above his shoulder. */
private val BUNCH = listOf(Offset(40f, -190f), Offset(96f, -166f), Offset(2f, -150f), Offset(70f, -118f), Offset(128f, -132f))

/** Where Bruno holds the strings: his hand on the side of the balloons, wherever his size puts it. */
private fun hand(look: BrunoLook) = BRUNO_CENTER + Offset(50f, 16f) * (BRUNO_HEIGHT * look.size / BRUNO_UNITS)

private enum class BalloonPhase { ASKING, GIVING, WRONG, THANKS }

/**
 * The balloons, on the island of colours (ADR 0007): Bruno wants one, «¡Quiero el globo rojo!», and there are three. She
 * drags his to him, or just touches it, and he keeps it: a bunch that grows round after round. If it is another colour,
 * Bruno makes a funny face, says which one it is and asks again, and from then on he thinks of his balloon in a bubble,
 * a clue instead of the answer. Five rounds, five colours.
 */
@Composable
internal fun BalloonScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { balloonRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(BalloonPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    val positions = remember { mutableStateListOf(*SLOTS.toTypedArray()) }
    val given = remember { mutableStateListOf(false, false, false) }
    val flights = remember { mutableStateListOf<Flight?>(null, null, null) }
    val held = remember { mutableStateListOf<Hue>() }
    var hint by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }

    // Read when it is needed: the touches are made once and live through all the rounds.
    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        for (i in 0 until BALLOONS) {
            positions[i] = SLOTS[i]
            given[i] = false
            flights[i] = null
        }
        hint = false
        phase = BalloonPhase.ASKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.LOVE_BALLOONS)
        audio.say(Voice.wantBalloon(current().wanted))
    }

    fun give(i: Int) {
        val now = clock.value
        val hue = current().balloons[i]
        val to = hand(game.data.look)
        dragging = null
        audio.sfx(Sfx.WHOOSH)
        if (hue == current().wanted) {
            phase = BalloonPhase.GIVING
            phaseAt = now
            flights[i] = Flight(positions[i], to + BUNCH[held.size.coerceAtMost(BUNCH.lastIndex)], now, FLY_SECONDS)
            scope.launch {
                delay(FLY_MILLIS)
                given[i] = true
                flights[i] = null
                held += hue
                audio.sfx(Sfx.RIGHT)
                game.update { it.copy(balloons = it.balloons.plusOne(hue.word)) }
                audio.say(Voice.yes(hue))
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = BalloonPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_BALLOONS)
                    onDone()
                }
            }
        } else {
            phase = BalloonPhase.WRONG
            phaseAt = now
            hint = true
            val near = lerp(positions[i], to, 0.7f)
            flights[i] = Flight(positions[i], near, now, FLY_SECONDS * 0.6f)
            scope.launch {
                delay(FLY_MILLIS * 6 / 10)
                audio.sfx(Sfx.BOING)
                flights[i] = Flight(near, SLOTS[i], clock.value, FLY_SECONDS)
                positions[i] = SLOTS[i]
                audio.say(Voice.thatColor(hue), Voice.iWantBalloon(current().wanted))
                flights[i] = null
                phase = BalloonPhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == BalloonPhase.ASKING) audio.speak(Voice.wantBalloon(current().wanted))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != BalloonPhase.ASKING) return false
                for (i in 0 until BALLOONS) {
                    if (!given[i] && hits(point, positions[i], BALLOON_TOUCH)) {
                        dragging = i
                        grab = positions[i] - point
                        downAt = point
                        audio.sfx(Sfx.TAP)
                        return true
                    }
                }
                return false
            }

            override fun move(point: Offset) {
                val i = dragging ?: return
                positions[i] = point + grab
            }

            override fun up(point: Offset) {
                val i = dragging ?: return
                val near = distance(positions[i], hand(game.data.look)) < REACH || distance(positions[i], BRUNO_CENTER) < REACH
                when {
                    near -> give(i)
                    distance(point, downAt) < TAP_MOVE -> give(i)
                    else -> {
                        flights[i] = Flight(positions[i], SLOTS[i], clock.value, FLY_SECONDS * 0.6f)
                        positions[i] = SLOTS[i]
                        dragging = null
                    }
                }
            }

            override fun cancel() {
                val i = dragging ?: return
                positions[i] = SLOTS[i]
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawRainbowPark(stage, t)
        val since = t - phaseAt
        val pose = when (phase) {
            BalloonPhase.ASKING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.2f, hop = 1.5f * sin(t * 3f))
            BalloonPhase.GIVING ->
                if (since < FLY_SECONDS) {
                    Pose(Mouth.OPEN, Eyes.WIDE, Arms.DOWN, lookX = 0.8f, lookY = -0.4f)
                } else {
                    Pose(Mouth.GRIN, Eyes.HAPPY, Arms.DOWN, hop = 8f * sin((since - FLY_SECONDS) * 6f).coerceAtLeast(0f))
                }
            BalloonPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.8f)
            BalloonPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.DOWN, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
        }
        val look = game.data.look
        val hand = hand(look)
        // The balloons he has, drawn before him so their strings come out of his hand.
        held.forEachIndexed { n, hue ->
            drawTiedBalloon(hand + BUNCH[n] + Offset(0f, bob(t, 1.3f + 0.2f * n, 4f)), HELD_SIZE, hue.paint(), hand)
        }
        drawBruno(look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (hint && phase != BalloonPhase.THANKS) {
            drawThoughtBubble(BUBBLE, 66f, 56f, BRUNO_CENTER + Offset(-40f, -118f))
            drawBalloon(BUBBLE + Offset(0f, -4f), 74f, current().wanted.paint())
        }
        if (phase == BalloonPhase.GIVING && since > FLY_SECONDS || phase == BalloonPhase.THANKS) {
            drawSparkles(hand + Offset(60f, -150f), 100f, t)
        }
        for (stake in STAKES) line(Color(0xFF8A5A3C), stake, stake + Offset(0f, 16f), 5f)
        for (i in 0 until BALLOONS) {
            if (given[i] || i == dragging) continue
            val flight = flights[i]
            val flying = flight != null && !flight.over(t)
            val at = if (flight != null && flying) flight.at(t) else positions[i] + Offset(0f, bob(t, 1.6f + 0.3f * i, 5f))
            drawTiedBalloon(at, BALLOON_SIZE, current().balloons[i].paint(), if (flying) at + Offset(0f, 130f) else STAKES[i])
        }
        dragging?.let {
            drawTiedBalloon(
                positions[it],
                BALLOON_SIZE * 1.06f,
                current().balloons[it].paint(),
                positions[it] + Offset(0f, 130f),
            )
        }
        for (i in rounds.indices) {
            val done = i < round || phase == BalloonPhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(340f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val FLY_SECONDS = 0.45f
private const val FLY_MILLIS = 450L
private const val REACH = 140f
private const val TAP_MOVE = 14f
