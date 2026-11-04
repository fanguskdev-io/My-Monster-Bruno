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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Glyph
import com.fanguskdev.mymonsterbruno.game.Glyphs
import com.fanguskdev.mymonsterbruno.game.TraceProgress
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.tracePicks
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
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.sparkle
import kotlinx.coroutines.launch
import kotlin.math.sin

private val SHEET = Offset(262f, 26f)
private val SHEET_SIZE = Size(336f, 300f)
private const val LETTER_HEIGHT = 250f
private const val LETTER_TOP = 50f
private const val LETTER_CENTER_X = 430f
private val BRUNO_CENTER = Offset(120f, 262f)
private const val BRUNO_HEIGHT = 170f

/** How far from the path a finger can go and still trace it, and how near the start it has to touch, in glyph units. */
private const val TOLERANCE = 30f
private const val START_TOLERANCE = 36f

/**
 * The tracing (docs/diseno.md): a capital vowel on a sheet of paper, with its strokes in the school's order. A green dot
 * says where to start; the finger paints the path red as it follows it, and leaving the path only stops the paint. When
 * the letter is done, Bruno cheers and learns it. Two vowels each time, the ones she has traced least.
 */
@Composable
internal fun TraceScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val letters = remember { tracePicks(game.data.traced, game.random) }
    var index by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(TraceProgress(Glyphs.of(letters[0]))) }
    // Where the paint has got to, stroke and point: the progress itself is not observable, this is.
    var mark by remember { mutableStateOf(0 to 0) }
    var tracing by remember { mutableStateOf(false) }
    var finger by remember { mutableStateOf<Offset?>(null) }
    var doneAt by remember { mutableStateOf<Float?>(null) }

    fun scale() = LETTER_HEIGHT / Glyphs.GLYPH_HEIGHT

    fun origin(glyph: Glyph) = Offset(LETTER_CENTER_X - glyph.width * scale() / 2, LETTER_TOP)

    fun toGlyph(point: Offset): Offset = (point - origin(progress.glyph)) / scale()

    LaunchedEffect(index) {
        progress = TraceProgress(Glyphs.of(letters[index]))
        mark = 0 to 0
        doneAt = null
        audio.say(Voice.trace(letters[index]))
    }

    fun letterDone() {
        val letter = letters[index]
        doneAt = clock.value
        tracing = false
        finger = null
        audio.sfx(Sfx.LETTER)
        game.update { it.copy(traced = it.traced.plusOne(letter.toString())) }
        scope.launch {
            audio.say(Voice.WELL_DONE, Voice.traced(letter))
            if (index + 1 < letters.size) index++ else onDone()
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    audio.speak(Voice.trace(letters[index]))
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
        val glyph = progress.glyph
        drawGlyph(glyph, origin(glyph), scale(), mark, doneAt != null, t)
        val celebrating = doneAt?.let { t - it < 2.5f } ?: false
        val pose = if (celebrating) {
            Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin((t - (doneAt ?: 0f)) * 6f).coerceAtLeast(0f))
        } else {
            Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.3f)
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (celebrating) drawSparkles(Offset(LETTER_CENTER_X, LETTER_TOP + LETTER_HEIGHT / 2), 150f, t)
        finger?.let { fill(sparkle(it, 14f), Color(0xFFFFD36E)) }
        drawLetterDots(letters, index, doneAt != null, measurer, font)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** The letter: its road, its dots, the red paint up to [mark] (stroke and point) and where to go on. */
internal fun DrawScope.drawGlyph(
    glyph: Glyph,
    origin: Offset,
    k: Float,
    mark: Pair<Int, Int>,
    done: Boolean,
    t: Float,
) {
    fun at(p: Offset) = origin + p * k
    val road = Stroke(46f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val dots = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.1f, 13f)))
    val paint = Stroke(22f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    for (stroke in glyph.strokes) {
        val line = path { stroke.forEachIndexed { i, p -> if (i == 0) moveTo(at(p).x, at(p).y) else lineTo(at(p).x, at(p).y) } }
        drawPath(line, Color(0xFFE8EDF6), style = road)
        drawPath(line, Color(0xFFA9B5CE), style = dots)
    }
    val red = if (done) Color(0xFF2E9E5B) else Color(0xFFE4483C)
    glyph.strokes.forEachIndexed { s, stroke ->
        val until = when {
            done || s < mark.first -> stroke.lastIndex
            s == mark.first -> mark.second
            else -> -1
        }
        if (until >= 1) {
            val line = path {
                for (i in 0..until) {
                    if (i ==
                        0
                    ) {
                        moveTo(at(stroke[0]).x, at(stroke[0]).y)
                    } else {
                        lineTo(at(stroke[i]).x, at(stroke[i]).y)
                    }
                }
            }
            drawPath(line, red, style = paint)
        }
    }
    if (!done && mark.first < glyph.strokes.size) {
        val stroke = glyph.strokes[mark.first]
        val p = at(stroke[mark.second])
        val pulse = (t % 1.2f) / 1.2f
        drawCircle(Color(0xFF2E9E5B), 22f + 14f * pulse, p, alpha = 0.5f * (1f - pulse))
        circle(Color(0xFF2E9E5B), p, 16f)
        // The way the stroke goes: a little arrow towards its next points.
        val ahead = at(stroke[(mark.second + 6).coerceAtMost(stroke.lastIndex)])
        val dir = (ahead - p).let { d ->
            val len = d.getDistance()
            if (len < 1f) Offset(0f, 1f) else d / len
        }
        val tip = p + dir * 34f
        drawLine(Color(0xFF2E9E5B), p + dir * 18f, tip, 5f, StrokeCap.Round)
        val side = Offset(-dir.y, dir.x)
        drawPath(
            path {
                moveTo(tip.x + dir.x * 8f, tip.y + dir.y * 8f)
                lineTo(tip.x + side.x * 8f, tip.y + side.y * 8f)
                lineTo(
                    tip.x - side.x * 8f,
                    tip.y - side.y * 8f,
                )
                close()
            },
            Color(0xFF2E9E5B),
        )
    }
}

private fun DrawScope.drawLetterDots(
    letters: List<Char>,
    index: Int,
    done: Boolean,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    letters.forEachIndexed { i, letter ->
        val center = Offset(400f - 22f * (letters.size - 1) + 44f * i, 356f)
        val finished = i < index || (i == index && done)
        circle(if (finished) Color(0xFF2E9E5B) else Color.White, center, 18f)
        label(
            measurer,
            letter.uppercaseChar().toString(),
            center + Offset(0f, 1f),
            24f,
            if (finished) Color.White else Color(0xFF1D2B53),
            font,
        )
    }
}
