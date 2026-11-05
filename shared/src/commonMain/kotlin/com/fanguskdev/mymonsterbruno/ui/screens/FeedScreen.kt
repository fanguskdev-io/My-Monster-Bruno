package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.CHOICES
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.MAX_CHOICES
import com.fanguskdev.mymonsterbruno.game.Word
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.letterRounds
import com.fanguskdev.mymonsterbruno.game.narrowed
import com.fanguskdev.mymonsterbruno.game.played
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.BRUNO_MOUTH
import com.fanguskdev.mymonsterbruno.ui.art.BRUNO_UNITS
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawCookie
import com.fanguskdev.mymonsterbruno.ui.art.drawKitchen
import com.fanguskdev.mymonsterbruno.ui.art.drawPlate
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.art.drawWord
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(215f, 238f)
private const val BRUNO_HEIGHT = 250f
private val BUBBLE = Offset(478f, 96f)

/** Where the cookies are on the plate, as many as she chooses from (ADR 0020): four go closer, and a little smaller. */
private fun slots(count: Int): List<Offset> =
    if (count > CHOICES) row(Offset(600f, 252f), count, 92f) else row(Offset(620f, 252f), count, 115f)

private fun cookieRadius(count: Int) = if (count > CHOICES) 46f else 52f

/** Where Bruno's mouth is on the stage: where a cookie has to be dropped. */
private val MOUTH: Offset get() = BRUNO_CENTER + BRUNO_MOUTH * (BRUNO_HEIGHT / BRUNO_UNITS)

private enum class FeedPhase { ASKING, EATING, WRONG, THANKS }

/**
 * Comeletras (docs/diseno.md): Bruno wants a vowel, «¡Quiero la a! La a de avión», and the plate has three cookies with
 * capital letters. She drags his to his mouth, or just touches it; if it is another, Bruno makes a funny face, says which
 * one it is and asks again, and the cookie goes back to the plate: nothing is ever wrong. Five rounds, one per vowel.
 * The plate has four cookies when it is easy for her, and two when it is hard (ADR 0020).
 */
@Composable
internal fun FeedScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { letterRounds(game.random, game.data.fed) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(FeedPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }

    // The cookies of this round, as many as she chooses from now, and whether she has given him a wrong one.
    fun plate(n: Int) = rounds[n].cookies.narrowed(rounds[n].wanted, game.data.choices(Choosing.COOKIES))
    var cookies by remember { mutableStateOf(plate(0)) }
    var missed by remember { mutableStateOf(false) }
    val positions = remember { slots(cookies.size).toMutableStateList() }
    val eaten = remember { List(MAX_CHOICES) { false }.toMutableStateList() }
    val flights = remember { List<Flight?>(MAX_CHOICES) { null }.toMutableStateList() }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }

    // Read when it is needed: the touches are made once and live through all the rounds.
    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        cookies = plate(round)
        missed = false
        positions.clear()
        positions.addAll(slots(cookies.size))
        for (i in 0 until MAX_CHOICES) {
            eaten[i] = false
            flights[i] = null
        }
        phase = FeedPhase.ASKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.HUNGRY_FOR_LETTERS)
        audio.say(Voice.want(current().wanted))
    }

    fun feed(i: Int) {
        val now = clock.value
        val letter = cookies[i]
        val home = slots(cookies.size)[i]
        dragging = null
        audio.sfx(Sfx.WHOOSH)
        if (letter == current().wanted) {
            phase = FeedPhase.EATING
            phaseAt = now
            flights[i] = Flight(positions[i], MOUTH, now, FLY_SECONDS)
            scope.launch {
                delay((FLY_SECONDS * 1000).toLong())
                eaten[i] = true
                flights[i] = null
                audio.sfx(Sfx.MUNCH)
                game.update { it.copy(fed = it.fed.plusOne(letter.toString())).played(Choosing.COOKIES, firstTry = !missed) }
                audio.say(Voice.yum(letter))
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = FeedPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_LETTERS)
                    onDone()
                }
            }
        } else {
            phase = FeedPhase.WRONG
            phaseAt = now
            missed = true
            val near = lerp(positions[i], MOUTH, 0.75f)
            flights[i] = Flight(positions[i], near, now, FLY_SECONDS * 0.6f)
            scope.launch {
                delay((FLY_SECONDS * 600).toLong())
                audio.sfx(Sfx.BOING)
                flights[i] = Flight(near, home, clock.value, FLY_SECONDS)
                positions[i] = home
                audio.say(Voice.thatIs(letter), Voice.iWant(current().wanted))
                flights[i] = null
                phase = FeedPhase.ASKING
                phaseAt = clock.value
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == FeedPhase.ASKING) audio.speak(Voice.want(current().wanted))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != FeedPhase.ASKING) return false
                for (i in cookies.indices) {
                    if (!eaten[i] && hits(point, positions[i], cookieRadius(cookies.size))) {
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
                when {
                    distance(positions[i], MOUTH) < MOUTH_REACH -> feed(i)
                    distance(point, downAt) < TAP_MOVE -> feed(i)
                    else -> {
                        val home = slots(cookies.size)[i]
                        flights[i] = Flight(positions[i], home, clock.value, FLY_SECONDS * 0.6f)
                        positions[i] = home
                        dragging = null
                    }
                }
            }

            override fun cancel() {
                val i = dragging ?: return
                positions[i] = slots(cookies.size)[i]
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawKitchen(stage)
        val since = t - phaseAt
        val pose = when (phase) {
            FeedPhase.ASKING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = 0.4f, hop = 1.5f * sin(t * 3f))
            FeedPhase.EATING ->
                if (since < FLY_SECONDS) {
                    Pose(Mouth.OPEN, Eyes.WIDE, Arms.UP, lookX = 0.6f)
                } else {
                    Pose(Mouth.MUNCH, Eyes.HAPPY, Arms.UP, hop = 8f * sin((since - FLY_SECONDS) * 6f).coerceAtLeast(0f))
                }
            FeedPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.8f)
            FeedPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
        }
        drawThoughtBubble(BUBBLE, 108f, 66f, BRUNO_CENTER + Offset(70f, -110f))
        drawWord(Word.of(current().wanted), BUBBLE, 112f)
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == FeedPhase.EATING && since > FLY_SECONDS || phase == FeedPhase.THANKS) drawSparkles(MOUTH, 80f, t)
        val radius = cookieRadius(cookies.size)
        if (cookies.size > CHOICES) drawPlate(Offset(600f, 300f), 380f) else drawPlate(Offset(620f, 300f), 330f)
        for (i in cookies.indices) {
            if (eaten[i] || i == dragging) continue
            val flight = flights[i]
            val at = if (flight != null && !flight.over(t)) flight.at(t) else positions[i]
            drawCookie(at, radius, cookies[i], measurer, font)
        }
        dragging?.let { drawCookie(positions[it], radius * 1.08f, cookies[it], measurer, font) }
        for (i in rounds.indices) {
            val done = i < round || phase == FeedPhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(340f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val FLY_SECONDS = 0.45f
private const val MOUTH_REACH = 110f
private const val TAP_MOVE = 14f
