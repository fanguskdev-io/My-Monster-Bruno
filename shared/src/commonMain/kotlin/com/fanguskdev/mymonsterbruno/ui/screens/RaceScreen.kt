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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.LANES
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.Race
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.raceRounds
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
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawPet
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeBack
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/** Bruno, who gives the start, at the left of the track. */
private val BRUNO_CENTER = Offset(94f, 252f)
private const val BRUNO_HEIGHT = 172f

/** The track: its three lanes, top to bottom, the start, the finish line and where the animals stop past it. */
private const val TRACK_LEFT = 178f
private const val TRACK_RIGHT = 776f
private val LANE_Y = listOf(188f, 258f, 328f)
private const val LANE_HEIGHT = 70f
private const val START_X = 232f
private const val FINISH_X = 640f
private const val STOP_X = 692f
private const val RUNNER_SIZE = 60f

/** Where the number of each place goes when its animal arrives: at its right, in its lane. */
private const val BADGE_X = 746f
private const val BADGE_RADIUS = 21f

/** The card at the top with the place Bruno asks about: its number and a question mark. */
private val ASK_CARD = Offset(400f, 70f)
private val ASK_CARD_SIZE = Size(170f, 80f)

/** When each place arrives, in seconds from the start: the first, the second and the third. */
private val ARRIVALS = listOf(2.6f, 3.2f, 3.8f)

/**
 * How each place runs: the first starts slower and the third faster, so that who leads changes on the way and the end
 * is a surprise; all of them, always forward.
 */
private val STRIDES = listOf(-0.1f, 0.02f, 0.12f)

/** Gold, silver and bronze, for the first, the second and the third. */
private val PLACE_COLORS = listOf(Color(0xFFF4B623), Color(0xFFAAB6C4), Color(0xFFCD8A4E))
private val INK = Color(0xFF1D2B53)

private enum class RacePhase { READY, RUNNING, ASKING, WRONG, RIGHT, THANKS }

/**
 * «Primero, segundo y tercero», on the island of numbers by turns with the countdown, the number line and the sums
 * (ADR 0037): three animals race, each in its lane. Bruno gives the start, «Preparados, listos... ¡ya!», they run to the
 * finish, and each gets its number as it arrives: the 1 in gold, the 2 in silver and the 3 in bronze. The voice asks
 * «¿Quién ha llegado primero?», and the card at the top shows that number; she touches the animal. If it is, «¡Sí! ¡Ha
 * llegado primero!», and it jumps; if not, «Ese animal ha llegado segundo. Mira su número», the right one calls a while
 * and the voice asks again. Three races, asking once about each place.
 */
@Composable
internal fun RaceScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { raceRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(RacePhase.READY) }
    var phaseAt by remember { mutableStateOf(0f) }

    // When the race started; the lane she touched that was not, and when; whether the right one calls.
    var startAt by remember { mutableStateOf<Float?>(null) }
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }
    var hinted by remember { mutableStateOf(false) }

    fun current(): Race = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        phase = RacePhase.READY
        phaseAt = clock.value
        startAt = null
        wrong = null
        hinted = false
        if (round == 0) audio.say(Voice.RACE_INTRO)
        audio.say(Voice.READY_GO)
        phase = RacePhase.RUNNING
        startAt = clock.value
        audio.sfx(Sfx.WHOOSH)
        var before = 0f
        for (arrival in ARRIVALS) {
            delay(((arrival - before) * 1000).toLong())
            before = arrival
            audio.sfx(Sfx.POP)
        }
        delay(SETTLE_MILLIS)
        phase = RacePhase.ASKING
        phaseAt = clock.value
        audio.say(Voice.whoArrived(current().asked))
    }

    // The voice asks again when she waits a while.
    LaunchedEffect(round, phase) {
        if (phase != RacePhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(Voice.whoArrived(current().asked))
        }
    }

    fun choose(lane: Int) {
        val race = current()
        val place = race.placeOf(lane)
        if (place == race.asked) {
            phase = RacePhase.RIGHT
            phaseAt = clock.value
            hinted = false
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(races = it.races.plusOne(place.word)) }
            scope.launch {
                audio.say(Voice.arrivedYes(place))
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = RacePhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_RACE)
                    onDone()
                }
            }
        } else {
            phase = RacePhase.WRONG
            phaseAt = clock.value
            wrong = lane to clock.value
            audio.sfx(Sfx.BOING)
            game.missed(Miss.ordinal(race.asked))
            scope.launch {
                audio.say(Voice.thatOneArrived(place))
                hinted = true
                phase = RacePhase.ASKING
                phaseAt = clock.value
                audio.say(Voice.whoArrived(race.asked))
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == RacePhase.ASKING) audio.speak(Voice.whoArrived(current().asked))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != RacePhase.ASKING) return false
                // Anywhere in its lane past the middle of the track: the animal, its number or around them.
                val lane = LANE_Y.indices.firstOrNull { abs(point.y - LANE_Y[it]) < LANE_HEIGHT / 2 && point.x > FINISH_X - 120f }
                    ?: return false
                audio.sfx(Sfx.TAP)
                choose(lane)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val race = current()
        drawGarden(stage, t)
        drawTrack()

        val pose = when (phase) {
            RacePhase.READY -> Pose(Mouth.OPEN, Eyes.WIDE, if (since > 1.2f) Arms.UP else Arms.DOWN, lookX = 1f)
            RacePhase.RUNNING -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 6f * sin(t * 8f).coerceAtLeast(0f))
            RacePhase.RIGHT, RacePhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
            RacePhase.WRONG -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = 0.3f)
            RacePhase.ASKING -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, hop = 1.5f * sin(t * 3f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)

        val running = startAt?.let { t - it }
        for (lane in 0 until LANES) {
            val place = race.places[lane]
            val arrival = ARRIVALS[place - 1]
            val p = running?.let { clamp01(it / arrival) } ?: 0f
            val x = START_X + (STOP_X - START_X) * (p + STRIDES[place - 1] * sin(PI.toFloat() * p))
            val moving = running != null && p < 1f
            val bounce = if (moving) -7f * abs(sin(t * 11f + lane * 1.7f)) else 0f
            val chosen = phase == RacePhase.RIGHT && race.placeOf(lane) == race.asked
            val shake = wrong?.takeIf { it.first == lane }?.let { (_, at) ->
                6f * sin((t - at) * 30f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
            } ?: 0f
            val happy = if (chosen) abs(sin(since * 6f)) else 0f
            val center = Offset(x + shake, LANE_Y[lane] - 4f + bounce)
            drawPet(race.runners[lane], center, RUNNER_SIZE, t, happy = happy)
            if (chosen) drawSparkles(center, 70f, t)

            // Its number, as it arrives.
            if (running != null && running >= arrival) {
                val grow = easeBack(clamp01((running - arrival) / POP_SECONDS))
                val badge = Offset(BADGE_X, LANE_Y[lane])
                drawPlace(place, badge, BADGE_RADIUS * grow, measurer, font)
                val calls = hinted && phase == RacePhase.ASKING && race.placeOf(lane) == race.asked
                if (calls) drawCallRing(badge, BADGE_RADIUS + 8f, t, PLACE_COLORS[place - 1])
            }
        }

        val asking = phase == RacePhase.ASKING || phase == RacePhase.WRONG || phase == RacePhase.RIGHT
        if (asking) drawAskCard(race, phase == RacePhase.RIGHT, t, measurer, font)

        for (i in rounds.indices) {
            val done = i < round || (i == round && (phase == RacePhase.RIGHT || phase == RacePhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(370f + 30f * i, 384f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** The track in the garden: three lanes of clay with white lines, the start line and the chequered finish, with its flag. */
private fun DrawScope.drawTrack() {
    val top = LANE_Y.first() - LANE_HEIGHT / 2
    val bottom = LANE_Y.last() + LANE_HEIGHT / 2
    val size = Size(TRACK_RIGHT - TRACK_LEFT, bottom - top)
    drawRoundRect(Shadow, Offset(TRACK_LEFT, top + 6f), size, CornerRadius(18f))
    drawRoundRect(Color(0xFFE59A5C), Offset(TRACK_LEFT, top), size, CornerRadius(18f))
    for (k in 1 until LANES) {
        val y = top + LANE_HEIGHT * k
        var x = TRACK_LEFT + 14f
        while (x < TRACK_RIGHT - 14f) {
            line(Color.White, Offset(x, y), Offset((x + 18f).coerceAtMost(TRACK_RIGHT - 14f), y), 3f)
            x += 30f
        }
    }
    line(Color.White, Offset(START_X - 36f, top + 6f), Offset(START_X - 36f, bottom - 6f), 5f)
    val square = 10f
    var y = top
    var row = 0
    while (y < bottom - 0.5f) {
        val tall = minOf(square, bottom - y)
        for (column in 0..1) {
            val colour = if ((row + column) % 2 == 0) Color.White else INK
            drawRect(colour, Offset(FINISH_X - square + square * column, y), Size(square, tall))
        }
        y += square
        row++
    }
    // The flag of the finish, on its pole above the track.
    line(Color(0xFF8A5A3C), Offset(FINISH_X, top), Offset(FINISH_X, top - 52f), 4f)
    for (r in 0 until 2) {
        for (c in 0 until 3) {
            val colour = if ((r + c) % 2 == 0) INK else Color.White
            drawRect(colour, Offset(FINISH_X + 2f + 10f * c, top - 52f + 10f * r), Size(10f, 10f))
        }
    }
}

/** The number of a [place], round, in its gold, silver or bronze, with a white border. */
private fun DrawScope.drawPlace(
    place: Int,
    center: Offset,
    radius: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    if (radius <= 0.5f) return
    circle(Shadow, center + Offset(0f, 3f), radius)
    circle(Color.White, center, radius)
    circle(PLACE_COLORS[place - 1], center, radius * 0.84f)
    label(measurer, place.toString(), center + Offset(0f, 1f), radius * 1.1f, Color.White, font)
}

/** The card of the question: the number of the place Bruno asks about and a question mark; once found, the animal. */
private fun DrawScope.drawAskCard(
    race: Race,
    found: Boolean,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val topLeft = ASK_CARD - Offset(ASK_CARD_SIZE.width / 2, ASK_CARD_SIZE.height / 2)
    drawRoundRect(Shadow, topLeft + Offset(0f, 5f), ASK_CARD_SIZE, CornerRadius(20f))
    drawRoundRect(Color.White, topLeft, ASK_CARD_SIZE, CornerRadius(20f))
    drawPlace(race.asked.number, ASK_CARD + Offset(-38f, 0f), 27f, measurer, font)
    val answer = ASK_CARD + Offset(40f, 0f)
    if (found) {
        drawPet(race.runners[race.laneOf(race.asked)], answer, 58f, time)
    } else {
        drawRoundRect(Color(0xFFFFF4D6), answer - Offset(24f, 26f), Size(48f, 52f), CornerRadius(10f))
        label(measurer, "?", answer + Offset(0f, 2f * sin(time * 4f)), 36f, INK, font)
    }
}

private const val SETTLE_MILLIS = 500L
private const val POP_SECONDS = 0.35f
private const val WIGGLE_SECONDS = 0.5f
private const val PAUSE_MILLIS = 900L
private const val IDLE_MILLIS = 9000L
