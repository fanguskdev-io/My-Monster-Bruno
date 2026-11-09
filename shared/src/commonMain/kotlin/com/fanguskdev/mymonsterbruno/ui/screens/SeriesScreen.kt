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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.SERIES_CHOICES
import com.fanguskdev.mymonsterbruno.game.SeriesRound
import com.fanguskdev.mymonsterbruno.game.seriesRounds
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.balloonShape
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRainbowPark
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawTiedBalloon
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(112f, 290f)
private const val BRUNO_HEIGHT = 176f
private const val ROW_Y = 118f
private const val ROW_SPACE = 92f
private const val ROW_SIZE = 96f
private const val ROPE_Y = 218f
private val CHOICES = listOf(Offset(470f, 312f), Offset(592f, 312f), Offset(714f, 312f))
private const val CHOICE_SIZE = 100f
private const val CHOICE_TOUCH = 48f

/** The centre of the [i]th place of a row of [places]: the row stays in the middle of the free space. */
private fun place(i: Int, places: Int) = Offset(470f - ROW_SPACE * (places - 1) / 2f + ROW_SPACE * i, ROW_Y)

private enum class SeriesPhase { ASKING, RIGHT, WRONG, THANKS }

/**
 * The series, on the island of colours (ADR 0007): a row of balloons, red, blue, red, blue… and a gap. The voice reads
 * the row while each balloon jumps at its name, and asks which one goes now; she gives one of three, dragging it to the
 * gap or touching it. If it is not that one, Bruno says which it is, and the voice reads the row again. Four series, two
 * of two colours and two of three.
 */
@Composable
internal fun SeriesScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { seriesRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(SeriesPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    val positions = remember { mutableStateListOf(*CHOICES.toTypedArray()) }
    val flights = remember { mutableStateListOf<Flight?>(null, null, null) }
    var filled by remember { mutableStateOf(false) }
    var reading by remember { mutableStateOf<Int?>(null) }
    var readingAt by remember { mutableStateOf(0f) }
    var reader by remember { mutableStateOf<Job?>(null) }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }

    fun current(): SeriesRound = rounds[round.coerceAtMost(rounds.lastIndex)]

    fun gap() = place(current().shown, current().shown + 1)

    /** Reads the row out, each balloon jumping at its name, and asks which one goes now; [first] what to say before. */
    fun read(first: String?) {
        reader?.cancel()
        reader = scope.launch {
            try {
                if (first != null) audio.say(first)
                for (i in 0 until current().shown) {
                    reading = i
                    readingAt = clock.value
                    audio.say(Voice.color(current().at(i)))
                }
                reading = current().shown
                readingAt = clock.value
                audio.say(Voice.WHICH_NEXT)
            } finally {
                if (reader === coroutineContext[Job]) reading = null
            }
        }
    }

    LaunchedEffect(round) {
        for (i in 0 until SERIES_CHOICES) {
            positions[i] = CHOICES[i]
            flights[i] = null
        }
        filled = false
        phase = SeriesPhase.ASKING
        phaseAt = clock.value
        read(if (round == 0) Voice.LOOK_AT_THE_ROW else null)
    }

    fun give(i: Int) {
        val now = clock.value
        val hue = current().choices[i]
        dragging = null
        reader?.cancel()
        audio.sfx(Sfx.WHOOSH)
        if (hue == current().answer) {
            phase = SeriesPhase.RIGHT
            phaseAt = now
            flights[i] = Flight(positions[i], gap(), now, FLY_SECONDS)
            scope.launch {
                delay(FLY_MILLIS)
                filled = true
                flights[i] = null
                audio.sfx(Sfx.RIGHT)
                game.update { it.copy(series = it.series + 1) }
                audio.say(Voice.yes(hue))
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = SeriesPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_SERIES)
                    onDone()
                }
            }
        } else {
            phase = SeriesPhase.WRONG
            phaseAt = now
            game.missed(Miss.SERIES)
            val near = lerp(positions[i], gap(), 0.7f)
            flights[i] = Flight(positions[i], near, now, FLY_SECONDS * 0.6f)
            scope.launch {
                delay(FLY_MILLIS * 6 / 10)
                audio.sfx(Sfx.BOING)
                flights[i] = Flight(near, CHOICES[i], clock.value, FLY_SECONDS)
                positions[i] = CHOICES[i]
                audio.say(Voice.thatColor(hue))
                flights[i] = null
                phase = SeriesPhase.ASKING
                phaseAt = clock.value
                read(Voice.LOOK_AGAIN)
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == SeriesPhase.ASKING) read(null)
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    reader?.cancel()
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != SeriesPhase.ASKING) return false
                for (i in 0 until SERIES_CHOICES) {
                    if (hits(point, positions[i], CHOICE_TOUCH)) {
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
                    distance(positions[i], gap()) < REACH -> give(i)
                    distance(point, downAt) < TAP_MOVE -> give(i)
                    else -> {
                        flights[i] = Flight(positions[i], CHOICES[i], clock.value, FLY_SECONDS * 0.6f)
                        positions[i] = CHOICES[i]
                        dragging = null
                    }
                }
            }

            override fun cancel() {
                val i = dragging ?: return
                positions[i] = CHOICES[i]
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawRainbowPark(stage, t)
        val since = t - phaseAt
        val series = current()
        val places = series.shown + 1
        drawRope(place(0, places).x - 46f, place(places - 1, places).x + 46f)
        for (i in 0 until places) {
            val jump = if (reading == i) 14f * sin(((t - readingAt) * 9f).coerceIn(0f, 3.14f)) else 0f
            val at = place(i, places) + Offset(0f, bob(t, 1.4f + 0.25f * i, 3f) - jump)
            val tie = Offset(place(i, places).x, ROPE_Y)
            when {
                i < series.shown -> drawTiedBalloon(at, ROW_SIZE, series.at(i).paint(), tie)
                filled -> drawTiedBalloon(at, ROW_SIZE, series.answer.paint(), tie)
                else -> drawGap(at, measurer, font, t)
            }
        }
        val pose = when (phase) {
            SeriesPhase.ASKING -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 0.9f, lookY = -0.8f, hop = 1.5f * sin(t * 3f))
            SeriesPhase.RIGHT, SeriesPhase.THANKS ->
                Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(since * 5f).coerceAtLeast(0f))
            SeriesPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.8f, lookY = -0.4f)
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (filled) drawSparkles(gap(), 70f, t)
        if (!filled) {
            for (i in 0 until SERIES_CHOICES) {
                if (i == dragging) continue
                val flight = flights[i]
                val flying = flight != null && !flight.over(t)
                val at = if (flight != null && flying) flight.at(t) else positions[i] + Offset(0f, bob(t, 1.7f + 0.3f * i, 4f))
                drawTiedBalloon(at, CHOICE_SIZE, series.choices[i].paint(), at + Offset(0f, 96f))
            }
        }
        dragging?.let { drawTiedBalloon(positions[it], CHOICE_SIZE * 1.06f, series.choices[it].paint(), positions[it] + Offset(0f, 96f)) }
        for (i in rounds.indices) {
            val done = i < round || phase == SeriesPhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(355f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** The rope the balloons of the row are tied to, hanging a little between its ends. */
private fun DrawScope.drawRope(from: Float, to: Float) {
    stroke(
        path {
            moveTo(from, ROPE_Y)
            quadraticTo((from + to) / 2, ROPE_Y + 14f, to, ROPE_Y)
        },
        Color(0xFFB98A5E),
        5f,
    )
    circle(Color(0xFF8A5A3C), Offset(from, ROPE_Y), 7f)
    circle(Color(0xFF8A5A3C), Offset(to, ROPE_Y), 7f)
}

/** The gap of the row: a balloon drawn in dashes, with a question mark that breathes. */
private fun DrawScope.drawGap(
    center: Offset,
    measurer: TextMeasurer,
    font: FontFamily,
    time: Float,
) {
    val shape = balloonShape(center, ROW_SIZE)
    fill(shape, Color.White, alpha = 0.7f)
    drawPath(shape, Color(0xFF8E89E8), style = Stroke(4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), time * 20f)))
    label(measurer, "?", center + Offset(0f, 2f), 46f + 3f * sin(time * 3f), Color(0xFF5B5F97), font)
}

private const val FLY_SECONDS = 0.45f
private const val FLY_MILLIS = 450L
private const val PAUSE_MILLIS = 500L
private const val REACH = 90f
private const val TAP_MOVE = 14f
