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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Hop
import com.fanguskdev.mymonsterbruno.game.LINE_LAST
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.Side
import com.fanguskdev.mymonsterbruno.game.hops
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
import com.fanguskdev.mymonsterbruno.ui.art.drawGarden
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

/**
 * The squares of the line, zero to ten, on the grass from one side to the other: low enough that the present that comes
 * after them is not under them, and below the Dynamic Island of her iPhone.
 */
private val SQUARES = (0..LINE_LAST).map { Offset(60f + 68f * it, 330f) }
private val SQUARE = Size(62f, 62f)

/** Bruno stands over a square, his feet just above it, and his question goes in a bubble over his head. */
private const val BRUNO_HEIGHT = 128f
private const val BRUNO_Y = 236f
private const val BUBBLE_Y = 110f
private val BUBBLE = Size(116f, 58f)
private val BOX = Size(44f, 44f)

private val GOLD = Color(0xFFF4B623)
private val LIT = Color(0xFFFFD54A)
private val INK = Color(0xFF1D2B53)

/** The colours of the numbers, in turn, as on the number lines of a classroom. */
private val NUMBER_COLORS = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFF9B6BD3))

private enum class LinePhase { INTRO, JUMPING, ASKING, WRONG, RIGHT, THANKS }

/** A jump of Bruno from square [from] to square [to], from [start] in the screen's clock: higher the further it goes. */
private class Leap(val from: Int, val to: Int, val start: Float) {
    private val squares = abs(to - from)
    val length = LEAP_SECONDS + LEAP_PER_SQUARE * squares
    private val height = if (squares == 0) 30f else (40f + 7f * squares).coerceAtMost(100f)

    fun at(time: Float): Offset {
        val k = clamp01((time - start) / length)
        return Offset(lerp(SQUARES[from].x, SQUARES[to].x, easeInOut(k)), BRUNO_Y - height * 4f * k * (1f - k))
    }
}

/**
 * «El anterior y el siguiente», on the island of numbers by turns with the countdown (ADR 0026): the number line of her
 * class, zero to ten, and Bruno jumps on it to a square and asks «¿Qué número va antes del cinco?» or «...después del
 * cinco?», with a bubble over his head that shows it as on the line: his number, and a question mark on the side it goes.
 * She touches the square: if it is, it lights up and the voice says it all, «¡Sí! Antes del cinco va el cuatro»; if not,
 * it wiggles, the voice says which one she touched and that it is just beside Bruno, the two squares beside him glow, and
 * he asks again. Five questions, «antes» and «después» mixed.
 */
@Composable
internal fun NumberLineScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val hops = remember { hops(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(LinePhase.INTRO) }
    var phaseAt by remember { mutableStateOf(0f) }

    // Where Bruno is, or his jump; the square she got right, and the one she touched that was not, and when; and
    // whether the squares beside Bruno glow, after a mistake.
    var on by remember { mutableStateOf(LINE_LAST / 2) }
    var leap by remember { mutableStateOf<Leap?>(null) }
    var answered by remember { mutableStateOf<Int?>(null) }
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }
    var hinted by remember { mutableStateOf(false) }

    fun current() = hops[round.coerceAtMost(hops.lastIndex)]

    LaunchedEffect(round) {
        val hop = current()
        answered = null
        wrong = null
        hinted = false
        if (round == 0) audio.say(Voice.NUMBER_LINE_INTRO)
        phase = LinePhase.JUMPING
        val jump = Leap(on, hop.square, clock.value)
        leap = jump
        audio.sfx(Sfx.HOP)
        delay((jump.length * MILLIS).toLong())
        on = hop.square
        leap = null
        phase = LinePhase.ASKING
        phaseAt = clock.value
        audio.say(Voice.askSide(hop))
    }

    // Bruno asks again when she waits a while.
    LaunchedEffect(round, phase) {
        if (phase != LinePhase.ASKING) return@LaunchedEffect
        while (true) {
            delay(IDLE_MILLIS)
            audio.say(Voice.askSide(current()))
        }
    }

    fun choose(square: Int) {
        val hop = current()
        if (square == hop.answer) {
            phase = LinePhase.RIGHT
            phaseAt = clock.value
            answered = square
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(neighbours = it.neighbours.plusOne(hop.side.word)) }
            scope.launch {
                audio.say(Voice.answerSide(hop))
                delay(PAUSE_MILLIS)
                if (round + 1 < hops.size) {
                    round++
                } else {
                    phase = LinePhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_NUMBER_LINE)
                    onDone()
                }
            }
        } else {
            phase = LinePhase.WRONG
            wrong = square to clock.value
            hinted = true
            audio.sfx(Sfx.BOING)
            game.missed(Miss.side(current().side))
            scope.launch {
                audio.say(Voice.thatSquare(square), Voice.BESIDE_BRUNO, Voice.askSide(hop))
                if (phase == LinePhase.WRONG) phase = LinePhase.ASKING
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == LinePhase.ASKING) audio.speak(Voice.askSide(current()))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                // While the voice helps after a mistake she can try again at once, as on the panel of the countdown.
                if (phase != LinePhase.ASKING && phase != LinePhase.WRONG) return false
                val square = SQUARES.indices
                    .filter { inCard(point, SQUARES[it], SQUARE) }
                    .minByOrNull { abs(point.x - SQUARES[it].x) } ?: return false
                choose(square)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val hop = current()
        drawGarden(stage, t)

        // The line, with its arrow to the right, and its squares on it.
        line(INK, Offset(24f, SQUARES[0].y), Offset(772f, SQUARES[0].y), 5f, alpha = 0.55f)
        fill(
            path {
                moveTo(784f, SQUARES[0].y)
                lineTo(768f, SQUARES[0].y - 10f)
                lineTo(768f, SQUARES[0].y + 10f)
                close()
            },
            INK.copy(alpha = 0.55f),
        )
        val asking = phase == LinePhase.ASKING || phase == LinePhase.WRONG
        for ((number, center) in SQUARES.withIndex()) {
            val shake = wrong?.takeIf { it.first == number }?.let { (_, at) ->
                6f * sin((t - at) * 30f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
            } ?: 0f
            drawSquare(
                center + Offset(shake, 0f),
                number,
                lit = answered == number,
                bruno = leap == null && number == on,
                hint = hinted && asking && abs(number - on) == 1,
                time = t,
                measurer = measurer,
                font = font,
            )
        }

        val bruno = leap?.at(t) ?: Offset(SQUARES[on].x, BRUNO_Y)
        val look = if (hop.side == Side.BEFORE) -0.7f else 0.7f
        val pose = when (phase) {
            LinePhase.JUMPING -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP)
            LinePhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = look, lookY = 0.6f)
            LinePhase.RIGHT -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 6f * sin(since * 7f).coerceAtLeast(0f))
            LinePhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            LinePhase.ASKING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.DOWN, lookX = look, lookY = 0.6f, hop = 1.5f * sin(t * 1.6f))
            LinePhase.INTRO -> Pose(lookY = 0.5f, hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, bruno, BRUNO_HEIGHT, pose, t)
        if (asking || phase == LinePhase.RIGHT) drawQuestion(bruno, hop, phase == LinePhase.RIGHT, t, measurer, font)
        answered?.let { drawSparkles(SQUARES[it], 50f, t) }
        if (phase == LinePhase.THANKS) drawSparkles(bruno, 90f, t)

        for (i in hops.indices) {
            val done = i < round || (i == round && (phase == LinePhase.RIGHT || phase == LinePhase.THANKS))
            circle(if (done) Color(0xFFE9B36A) else Color(0x99FFFFFF), Offset(340f + 30f * i, 26f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/**
 * A square of the line with its [number]: gold when she got it right ([lit]), with a gold edge if Bruno is on it, and
 * glowing if it is beside him after a mistake ([hint]).
 */
private fun DrawScope.drawSquare(
    center: Offset,
    number: Int,
    lit: Boolean,
    bruno: Boolean,
    hint: Boolean,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val topLeft = center - Offset(SQUARE.width / 2, SQUARE.height / 2)
    val color = NUMBER_COLORS[number % NUMBER_COLORS.size]
    if (hint) {
        val glow = 0.45f + 0.35f * sin(time * 5f)
        drawRoundRect(GOLD, topLeft - Offset(7f, 7f), Size(SQUARE.width + 14f, SQUARE.height + 14f), CornerRadius(17f), alpha = glow)
    }
    drawRoundRect(Shadow, topLeft + Offset(0f, 5f), SQUARE, CornerRadius(12f))
    drawRoundRect(if (lit) LIT else Color.White, topLeft, SQUARE, CornerRadius(12f))
    drawRoundRect(if (bruno) GOLD else color, topLeft, SQUARE, CornerRadius(12f), style = Stroke(if (bruno) 6f else 3f))
    label(measurer, number.toString(), center + Offset(0f, 1f), if (number > 9) 30f else 36f, if (lit) Color.White else color, font)
}

/**
 * The bubble of Bruno's question over his head at [bruno], as it is on the line: his number, and a question mark before
 * or after it; once she has [answered], the number she found in its place.
 */
private fun DrawScope.drawQuestion(
    bruno: Offset,
    hop: Hop,
    answered: Boolean,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val center = Offset(bruno.x.coerceIn(96f, 704f), BUBBLE_Y)
    val topLeft = center - Offset(BUBBLE.width / 2, BUBBLE.height / 2)
    val bottom = center.y + BUBBLE.height / 2
    drawRoundRect(Shadow, topLeft + Offset(0f, 4f), BUBBLE, CornerRadius(22f))
    drawRoundRect(Color.White, topLeft, BUBBLE, CornerRadius(22f))
    val tail = bruno.x.coerceIn(center.x - 36f, center.x + 36f)
    fill(
        path {
            moveTo(tail - 11f, bottom - 1f)
            lineTo(tail + 11f, bottom - 1f)
            lineTo(tail, bottom + 15f)
            close()
        },
        Color.White,
    )
    val left = center - Offset(27f, 0f)
    val right = center + Offset(27f, 0f)
    val (known, asked) = if (hop.side == Side.BEFORE) right to left else left to right
    val color = NUMBER_COLORS[hop.square % NUMBER_COLORS.size]
    drawBox(known, color, Color.White)
    label(measurer, hop.square.toString(), known + Offset(0f, 1f), if (hop.square > 9) 22f else 26f, color, font)
    if (answered) {
        drawBox(asked, GOLD, LIT)
        label(measurer, hop.answer.toString(), asked + Offset(0f, 1f), if (hop.answer > 9) 22f else 26f, Color.White, font)
    } else {
        val boxLeft = asked - Offset(BOX.width / 2, BOX.height / 2)
        drawRoundRect(Color(0xFFFFF4D6), boxLeft, BOX, CornerRadius(9f))
        drawRoundRect(
            GOLD,
            boxLeft,
            BOX,
            CornerRadius(9f),
            style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), time * 16f)),
        )
        label(measurer, "?", asked + Offset(0f, 1f + 2f * sin(time * 4f)), 26f, INK, font)
    }
}

private fun DrawScope.drawBox(center: Offset, edge: Color, inside: Color) {
    val topLeft = center - Offset(BOX.width / 2, BOX.height / 2)
    drawRoundRect(inside, topLeft, BOX, CornerRadius(9f))
    drawRoundRect(edge, topLeft, BOX, CornerRadius(9f), style = Stroke(3f))
}

/** How long a jump lasts, and a little more for each square it goes over. */
private const val LEAP_SECONDS = 0.45f
private const val LEAP_PER_SQUARE = 0.04f
private const val WIGGLE_SECONDS = 0.5f
private const val IDLE_MILLIS = 9000L
private const val PAUSE_MILLIS = 500L
private const val MILLIS = 1000f
