package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Glyph
import com.fanguskdev.mymonsterbruno.game.Glyphs
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.TraceProgress
import com.fanguskdev.mymonsterbruno.game.baseLetter
import com.fanguskdev.mymonsterbruno.game.nameLetters
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
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawTracingWall
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeBack
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.sparkle
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * The sheet is lower than in the tracing, under her name, and the letter smaller and lower on it, so that an accent or a
 * tilde (up to 40 units over the letter), with its wide road, fits on it too.
 */
private val SHEET = Offset(262f, 100f)
private val SHEET_SIZE = Size(336f, 290f)
private const val LETTER_HEIGHT = 180f
private const val LETTER_TOP = 178f
private const val LETTER_CENTER_X = 430f
private val LETTER_CENTER = Offset(LETTER_CENTER_X, LETTER_TOP + LETTER_HEIGHT / 2)
private val BRUNO_CENTER = Offset(120f, 262f)
private const val BRUNO_HEIGHT = 170f

/** Her name hangs as a bunting over the sheet, a pennant for each letter. */
private const val BUNTING_CENTER_X = 430f
private const val ROPE_Y = 22f
private const val PENNANT_WIDTH = 44f
private const val PENNANT_HEIGHT = 50f
private const val PENNANT_LETTER = 22f
private val PENNANT_COLORS = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
private val INK = Color(0xFF1D2B53)

/**
 * How far from the path a finger can go and still trace it, and how near the start it has to touch, in glyph units: the
 * same on the screen as in the tracing, where the letter is bigger.
 */
private const val TOLERANCE = 42f
private const val START_TOLERANCE = 50f

/**
 * Writing her name (ADR 0017): the letters of the bunting of the room, one after the other, big on the sheet and with the
 * strokes of the school, while the voice says each one («La ele»). Her name hangs over the sheet as a bunting, and each
 * letter she finishes flies up into its pennant and gives it its colour. With all of them, her name is on the sheet and
 * Bruno says «¡Ya sé escribir tu nombre!». Her name itself is never said: the voices are recorded.
 */
@Composable
internal fun NameScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val letters = remember { nameLetters(game.data.childName) }
    if (letters.isEmpty()) {
        LaunchedEffect(Unit) { onDone() }
        return
    }
    var index by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(TraceProgress(Glyphs.of(letters[0]))) }
    // Where the paint has got to, stroke and point: the progress itself is not observable, this is.
    var mark by remember { mutableStateOf(0 to 0) }
    var tracing by remember { mutableStateOf(false) }
    var finger by remember { mutableStateOf<Offset?>(null) }
    var doneAt by remember { mutableStateOf<Float?>(null) }
    // How many letters already hang in their colour, and when the whole name was done.
    var written by remember { mutableStateOf(0) }
    var finishedAt by remember { mutableStateOf<Float?>(null) }

    fun scale() = LETTER_HEIGHT / Glyphs.GLYPH_HEIGHT

    fun origin(glyph: Glyph) = Offset(LETTER_CENTER_X - glyph.width * scale() / 2, LETTER_TOP)

    fun toGlyph(point: Offset): Offset = (point - origin(progress.glyph)) / scale()

    LaunchedEffect(index) {
        if (index == 0) audio.say(Voice.NAME_INTRO)
        audio.say(Voice.letterName(letters[index]))
    }

    fun nameDone() {
        finishedAt = clock.value
        game.update { data ->
            val traced = letters.map(::baseLetter)
                .filter { it in LETTERS }
                .fold(data.traced) { all, letter -> all.plusOne(letter.toString()) }
            data.copy(namesWritten = data.namesWritten + 1, traced = traced)
        }
        scope.launch {
            audio.say(Voice.KNOWS_YOUR_NAME)
            delay(GOODBYE_MILLIS)
            onDone()
        }
    }

    fun letterDone() {
        doneAt = clock.value
        tracing = false
        finger = null
        audio.sfx(Sfx.LETTER)
        scope.launch {
            delay(FLY_MILLIS + PAUSE_MILLIS)
            written = index + 1
            if (index + 1 < letters.size) {
                // All at once, so that no frame draws the new letter flying or the old one undone.
                progress = TraceProgress(Glyphs.of(letters[index + 1]))
                mark = 0 to 0
                doneAt = null
                index++
            } else {
                nameDone()
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (finishedAt == null) audio.speak(Voice.letterName(letters[index]))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (doneAt != null || progress.done) return false
                if (!progress.canStart(toGlyph(point), START_TOLERANCE)) return true
                tracing = true
                finger = point
                return true
            }

            override fun move(point: Offset) {
                if (!tracing) return
                finger = point
                if (progress.moveTo(toGlyph(point), TOLERANCE)) {
                    audio.sfx(Sfx.STROKE)
                    tracing = false
                }
                mark = progress.stroke to progress.reached
                if (progress.done) letterDone()
            }

            override fun up(point: Offset) {
                tracing = false
                finger = null
            }

            override fun cancel() {
                tracing = false
                finger = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawTracingWall(stage, SHEET, SHEET_SIZE)
        val finished = finishedAt
        val done = doneAt
        if (finished != null) {
            drawWholeName(letters, t - finished, measurer, font)
        } else {
            val flight = done?.let { clamp01((t - it) / FLY_SECONDS) }
            if (flight == null || flight < LIFT_OFF) {
                val glyph = progress.glyph
                drawGlyph(glyph, origin(glyph), scale(), mark, done != null, t)
            } else {
                // The letter she has just written flies up into its pennant, shrinking on the way.
                val k = easeInOut((flight - LIFT_OFF) / (1f - LIFT_OFF))
                val to = pennantCenter(index, letters.size)
                label(measurer, letters[index].uppercase(), lerp(LETTER_CENTER, to, k), lerp(LETTER_HEIGHT, PENNANT_LETTER, k), GREEN, font)
            }
        }
        val since = finished ?: done
        val pose = when {
            since != null && (finished != null || t - since < CHEER_SECONDS) ->
                Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin((t - since) * 6f).coerceAtLeast(0f))
            else -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.3f)
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (finished != null) drawSparkles(LETTER_CENTER, 160f, t)
        finger?.let { fill(sparkle(it, 14f), Color(0xFFFFD36E)) }
        drawNameBunting(letters, written, if (finished == null) index else -1, finished?.let { t - it }, t, measurer, font)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** Where the pennant of letter [i] of [count] has its letter. */
private fun pennantCenter(i: Int, count: Int): Offset {
    val x = BUNTING_CENTER_X - PENNANT_WIDTH * count / 2 + PENNANT_WIDTH * (i + 0.5f)
    return Offset(x, ROPE_Y + sag(x, count) + PENNANT_HEIGHT * 0.36f)
}

/** How far the rope hangs at [x]: most in the middle. */
private fun sag(x: Float, count: Int): Float {
    val half = PENNANT_WIDTH * count / 2 + ROPE_END
    val k = (x - BUNTING_CENTER_X) / half
    return SAG * (1f - k * k)
}

/**
 * Her name as a bunting: the [written] letters in their colours, the one she is on ([current]) white and swaying, the
 * rest pale. When it is all done ([cheering] seconds ago), the pennants jump one after the other.
 */
private fun DrawScope.drawNameBunting(
    letters: List<Char>,
    written: Int,
    current: Int,
    cheering: Float?,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val count = letters.size
    val left = BUNTING_CENTER_X - PENNANT_WIDTH * count / 2
    val right = left + PENNANT_WIDTH * count
    stroke(
        path {
            moveTo(left - ROPE_END, ROPE_Y)
            quadraticTo(BUNTING_CENTER_X, ROPE_Y + 2 * SAG, right + ROPE_END, ROPE_Y)
        },
        Color(0xFF8A5A3C),
        3f,
    )
    letters.forEachIndexed { i, letter ->
        val center = pennantCenter(i, count)
        val jump = cheering?.let { 8f * sin(it * 8f - i * 0.7f).coerceAtLeast(0f) } ?: 0f
        val sway = if (i == current) 3f * sin(time * 3f) else 0f
        val top = center.y - PENNANT_HEIGHT * 0.36f - jump
        val x = center.x
        val (cloth, ink) = when {
            i < written -> PENNANT_COLORS[i % PENNANT_COLORS.size] to Color.White
            i == current -> Color.White to INK
            else -> Color.White.copy(alpha = 0.55f) to INK.copy(alpha = 0.45f)
        }
        fill(
            path {
                moveTo(x - PENNANT_WIDTH * 0.45f, top)
                lineTo(x + PENNANT_WIDTH * 0.45f, top)
                lineTo(x + sway * 2f, top + PENNANT_HEIGHT)
                close()
            },
            cloth,
        )
        label(measurer, letter.uppercase(), Offset(x + sway * 0.6f, top + PENNANT_HEIGHT * 0.36f), PENNANT_LETTER, ink, font)
    }
}

/** Her whole name on the sheet, in green, each letter popping in after the one before: what she has written. */
private fun DrawScope.drawWholeName(
    letters: List<Char>,
    since: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val height = minOf(WHOLE_NAME_HEIGHT, SHEET_SIZE.width * 0.9f / (letters.size * LETTER_WIDTH))
    val step = height * LETTER_WIDTH
    val first = LETTER_CENTER_X - step * (letters.size - 1) / 2
    letters.forEachIndexed { i, letter ->
        val pop = easeBack(clamp01((since - 0.12f * i) / 0.4f))
        if (pop > 0.05f) label(measurer, letter.uppercase(), Offset(first + step * i, LETTER_CENTER.y), height * pop, GREEN, font)
    }
}

private val GREEN = Color(0xFF2E9E5B)
private const val WHOLE_NAME_HEIGHT = 96f

/** About how wide a capital is, for its height. */
private const val LETTER_WIDTH = 0.72f
private const val ROPE_END = 26f
private const val SAG = 12f

/** Until this much of its flight the letter stays green on the sheet, so that she sees it done. */
private const val LIFT_OFF = 0.3f
private const val FLY_SECONDS = 1f
private const val FLY_MILLIS = 1000L
private const val PAUSE_MILLIS = 300L
private const val CHEER_SECONDS = 1.6f
private const val GOODBYE_MILLIS = 600L
