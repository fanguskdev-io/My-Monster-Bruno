package com.fanguskdev.mymonsterbruno.game

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

/**
 * The capital letters to trace, «letra de palo» as in the classroom of four-year-olds, in a box [GLYPH_HEIGHT] units
 * high and [width] wide, with their strokes in the order the school teaches them (docs/diseno.md): the A, two slopes
 * from the top and the bar; the E, the stick and the three bars from the left; the I, a stick; the O, from the top and
 * round to the left; the U, down, round and up. And the first consonants (ADR 0009): the M, its left stick and then the
 * peak and the right stick without lifting the finger; the P, the stick and then its belly from the top; the L, down and
 * to the right; the S, from its top right, round to the left and back round to the right. And the next ones (ADR 0014):
 * the T, its bar from the left and then its stick from the middle; the N, its left stick and then the slope and the right
 * stick up without lifting the finger; the D, the stick and then its belly from the top. And the rest, for her name (ADR
 * 0017): the B, the stick and then both bellies from the top; the C, from its top right round to the left and to its
 * bottom right; the F, the stick and its two bars from the left; the G, the C going up to the middle and then a short bar
 * inwards; the H, the two sticks and the bar from the left; the J, down from the top right and round to the left; the K,
 * the stick and then the «<» from the top right; the Q, the O and its tail out; the R, the stick and then its belly from
 * the top and its leg; the V, down and up; the W, down, up, down and up; the X, from the top left and then from the top
 * right; the Y, its two arms down to the middle and its stick; the Z, its bars and its slope without lifting the finger.
 * The marks go last, above the letter (at negative y, from −40 to −8): the tilde of the Ñ, a wave from the left; the
 * accent of Á, É, Í, Ó and Ú, a short stroke down to the left.
 */
internal class Glyph(val letter: Char, val width: Float, val strokes: List<List<Offset>>)

internal object Glyphs {
    /** Whether there is a glyph for [letter], lowercase: the 27 letters of the Spanish alphabet and the accented vowels. */
    fun has(letter: Char): Boolean = letter in 'a'..'z' || letter in "ñáéíóú"

    fun of(letter: Char): Glyph = when (letter) {
        'a' -> Glyph(
            'a',
            110f,
            listOf(
                line(Offset(55f, 0f), Offset(5f, GLYPH_HEIGHT)),
                line(Offset(55f, 0f), Offset(105f, GLYPH_HEIGHT)),
                line(Offset(23f, 90f), Offset(87f, 90f)),
            ),
        )
        'e' -> Glyph(
            'e',
            84f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(80f, 0f)),
                line(Offset(10f, 70f), Offset(68f, 70f)),
                line(Offset(10f, GLYPH_HEIGHT), Offset(80f, GLYPH_HEIGHT)),
            ),
        )
        'i' -> Glyph('i', 20f, listOf(line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT))))
        'o' -> Glyph('o', 110f, listOf(oval(Offset(55f, 70f), 52f, 70f)))
        'u' -> Glyph('u', 110f, listOf(cup()))
        'm' -> Glyph(
            'm',
            120f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(60f, 92f)) +
                    line(Offset(60f, 92f), Offset(110f, 0f)).drop(1) +
                    line(Offset(110f, 0f), Offset(110f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'p' -> Glyph(
            'p',
            90f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(44f, 0f)) +
                    arc(Offset(44f, 38f), 38f, -PI / 2, PI / 2).drop(1) +
                    line(Offset(44f, 76f), Offset(10f, 76f)).drop(1),
            ),
        )
        'l' -> Glyph(
            'l',
            80f,
            listOf(line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)) + line(Offset(10f, GLYPH_HEIGHT), Offset(74f, GLYPH_HEIGHT)).drop(1)),
        )
        's' -> Glyph(
            's',
            90f,
            listOf(
                arc(Offset(45f, 35f), 35f, -PI / 6, -3 * PI / 2) +
                    arc(Offset(45f, 105f), 35f, -PI / 2, 5 * PI / 6).drop(1),
            ),
        )
        't' -> Glyph(
            't',
            100f,
            listOf(
                line(Offset(5f, 0f), Offset(95f, 0f)),
                line(Offset(50f, 0f), Offset(50f, GLYPH_HEIGHT)),
            ),
        )
        'n' -> Glyph(
            'n',
            100f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(90f, GLYPH_HEIGHT)) + line(Offset(90f, GLYPH_HEIGHT), Offset(90f, 0f)).drop(1),
            ),
        )
        'd' -> Glyph(
            'd',
            100f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(35f, 0f)) +
                    belly(Offset(35f, GLYPH_HEIGHT / 2), 55f, GLYPH_HEIGHT / 2).drop(1) +
                    line(Offset(35f, GLYPH_HEIGHT), Offset(10f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'b' -> Glyph(
            'b',
            90f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(40f, 0f)) +
                    arc(Offset(40f, 32f), 32f, -PI / 2, PI / 2).drop(1) +
                    line(Offset(40f, 64f), Offset(10f, 64f)).drop(1) +
                    line(Offset(10f, 64f), Offset(46f, 64f)).drop(1) +
                    arc(Offset(46f, 102f), 38f, -PI / 2, PI / 2).drop(1) +
                    line(Offset(46f, GLYPH_HEIGHT), Offset(10f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'c' -> Glyph('c', 96f, listOf(ellipse(Offset(55f, 70f), 52f, 70f, -PI / 4, -7 * PI / 4)))
        'f' -> Glyph(
            'f',
            84f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(80f, 0f)),
                line(Offset(10f, 70f), Offset(68f, 70f)),
            ),
        )
        'g' -> {
            val round = ellipse(Offset(55f, 70f), 52f, 70f, -PI / 4, -11 * PI / 6)
            val spur = Offset(round.last().x, 76f)
            Glyph('g', 106f, listOf(round + line(round.last(), spur).drop(1), line(spur, Offset(66f, 76f))))
        }
        'h' -> Glyph(
            'h',
            100f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(90f, 0f), Offset(90f, GLYPH_HEIGHT)),
                line(Offset(10f, 70f), Offset(90f, 70f)),
            ),
        )
        'j' -> Glyph(
            'j',
            76f,
            listOf(line(Offset(64f, 0f), Offset(64f, 106f)) + ellipse(Offset(37f, 106f), 27f, 34f, 0.0, PI).drop(1)),
        )
        'k' -> Glyph(
            'k',
            90f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(80f, 0f), Offset(10f, 70f)) + line(Offset(10f, 70f), Offset(84f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'q' -> Glyph('q', 110f, listOf(oval(Offset(55f, 70f), 52f, 70f), line(Offset(70f, 104f), Offset(104f, 138f))))
        'r' -> Glyph(
            'r',
            90f,
            listOf(
                line(Offset(10f, 0f), Offset(10f, GLYPH_HEIGHT)),
                line(Offset(10f, 0f), Offset(44f, 0f)) +
                    arc(Offset(44f, 38f), 38f, -PI / 2, PI / 2).drop(1) +
                    line(Offset(44f, 76f), Offset(10f, 76f)).drop(1) +
                    line(Offset(10f, 76f), Offset(84f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'v' -> Glyph(
            'v',
            100f,
            listOf(line(Offset(6f, 0f), Offset(50f, GLYPH_HEIGHT)) + line(Offset(50f, GLYPH_HEIGHT), Offset(94f, 0f)).drop(1)),
        )
        'w' -> Glyph(
            'w',
            130f,
            listOf(
                line(Offset(5f, 0f), Offset(35f, GLYPH_HEIGHT)) +
                    line(Offset(35f, GLYPH_HEIGHT), Offset(65f, 48f)).drop(1) +
                    line(Offset(65f, 48f), Offset(95f, GLYPH_HEIGHT)).drop(1) +
                    line(Offset(95f, GLYPH_HEIGHT), Offset(125f, 0f)).drop(1),
            ),
        )
        'x' -> Glyph(
            'x',
            100f,
            listOf(
                line(Offset(8f, 0f), Offset(92f, GLYPH_HEIGHT)),
                line(Offset(92f, 0f), Offset(8f, GLYPH_HEIGHT)),
            ),
        )
        'y' -> Glyph(
            'y',
            100f,
            listOf(
                line(Offset(8f, 0f), Offset(50f, 70f)),
                line(Offset(92f, 0f), Offset(50f, 70f)),
                line(Offset(50f, 70f), Offset(50f, GLYPH_HEIGHT)),
            ),
        )
        'z' -> Glyph(
            'z',
            90f,
            listOf(
                line(Offset(8f, 0f), Offset(82f, 0f)) +
                    line(Offset(82f, 0f), Offset(8f, GLYPH_HEIGHT)).drop(1) +
                    line(Offset(8f, GLYPH_HEIGHT), Offset(84f, GLYPH_HEIGHT)).drop(1),
            ),
        )
        'ñ' -> marked('ñ', 'n') { tilde(it) }
        'á' -> marked('á', 'a') { accent(it) }
        'é' -> marked('é', 'e') { accent(it) }
        'í' -> marked('í', 'i') { accent(it) }
        'ó' -> marked('ó', 'o') { accent(it) }
        'ú' -> marked('ú', 'u') { accent(it) }
        else -> error("No hay trazo para «$letter»")
    }

    /** The [letter] with a mark: the strokes of [base] and then, above its middle, the [mark] (the tilde or an accent). */
    private fun marked(letter: Char, base: Char, mark: (middle: Float) -> List<Offset>): Glyph {
        val glyph = of(base)
        return Glyph(letter, glyph.width, glyph.strokes + listOf(mark(glyph.width / 2)))
    }

    /** The tilde of the Ñ, above [middle]: a little wave from the left to the right, up and then down. */
    private fun tilde(middle: Float): List<Offset> =
        ellipse(Offset(middle - 14f, -24f), 14f, 8f, PI, 2 * PI) + ellipse(Offset(middle + 14f, -24f), 14f, 8f, PI, 0.0).drop(1)

    /** The accent of a vowel, above [middle]: a short stroke from its upper right down to its lower left. */
    private fun accent(middle: Float): List<Offset> = line(Offset(middle + 9f, -38f), Offset(middle - 9f, -14f))

    /** A round stroke around [center], [radius] units, from [from] to [to] radians (y grows down, as on the screen). */
    private fun arc(
        center: Offset,
        radius: Float,
        from: Double,
        to: Double,
    ): List<Offset> = ellipse(center, radius, radius, from, to)

    /** A round stroke around [center], [rx] units wide and [ry] high from it, from [from] to [to] radians, as [arc]. */
    private fun ellipse(
        center: Offset,
        rx: Float,
        ry: Float,
        from: Double,
        to: Double,
    ): List<Offset> {
        val steps = max(1, (abs(to - from) * (rx + ry) / 2 / STEP).toInt())
        return (0..steps).map {
            val angle = from + (to - from) * it / steps
            Offset(center.x + rx * cos(angle).toFloat(), center.y + ry * sin(angle).toFloat())
        }
    }

    /** A straight stroke from [from] to [to], a point every [STEP] units. */
    private fun line(from: Offset, to: Offset): List<Offset> {
        val steps = max(1, (hypot(to.x - from.x, to.y - from.y) / STEP).toInt())
        return (0..steps).map { from + (to - from) * (it.toFloat() / steps) }
    }

    /** The O: from the top, round to the left and back up to the top. */
    private fun oval(center: Offset, rx: Float, ry: Float): List<Offset> {
        val steps = (2 * PI * (rx + ry) / 2 / STEP).toInt()
        return (0..steps).map {
            val angle = -PI / 2 - 2 * PI * it / steps
            Offset(center.x + rx * cos(angle).toFloat(), center.y + ry * sin(angle).toFloat())
        }
    }

    /** The belly of the D: from its top, round the right to its bottom, [rx] units wide and [ry] high from [center]. */
    private fun belly(center: Offset, rx: Float, ry: Float): List<Offset> {
        val steps = (PI * (rx + ry) / 2 / STEP).toInt()
        return (0..steps).map {
            val angle = -PI / 2 + PI * it / steps
            Offset(center.x + rx * cos(angle).toFloat(), center.y + ry * sin(angle).toFloat())
        }
    }

    /** The U: down the left, round the bottom and up the right, in one stroke. */
    private fun cup(): List<Offset> {
        val left = line(Offset(10f, 0f), Offset(10f, 90f))
        val center = Offset(55f, 90f)
        val steps = (PI * 45 / STEP).toInt()
        val bottom = (1..steps).map {
            val angle = PI - PI * it / steps
            Offset(center.x + 45f * cos(angle).toFloat(), center.y + 50f * sin(angle).toFloat())
        }
        val right = line(Offset(100f, 90f), Offset(100f, 0f)).drop(1)
        return left + bottom + right
    }

    const val GLYPH_HEIGHT = 140f
    private const val STEP = 4f
}

/**
 * Where her finger is in the tracing of a [glyph]: the stroke she is on and the point of it she has reached. The finger
 * moves the progress forward along the stroke while it stays within the tolerance, never back; leaving the path or
 * lifting the finger keeps what she did, and she goes on from there. Nothing is ever wrong: it just does not advance.
 */
internal class TraceProgress(val glyph: Glyph) {
    var stroke = 0
        private set
    var reached = 0
        private set

    val done: Boolean get() = stroke >= glyph.strokes.size

    /** The point she has to touch to go on: the start of the stroke, or where she left it. */
    val target: Offset? get() = if (done) null else glyph.strokes[stroke][reached]

    /** Whether a finger put down at [point] can take the stroke up: near enough to [target]. */
    fun canStart(point: Offset, tolerance: Float): Boolean {
        val target = target ?: return false
        return distance(point, target) <= tolerance
    }

    /**
     * The finger at [point]: the progress jumps to the furthest point of the stroke, a few ahead at most, that is within
     * [tolerance]. Returns true when that ends the stroke (and the next one starts).
     */
    fun moveTo(point: Offset, tolerance: Float): Boolean {
        if (done) return false
        val points = glyph.strokes[stroke]
        var furthest = reached
        val last = minOf(points.lastIndex, reached + LOOK_AHEAD)
        for (i in reached + 1..last) if (distance(point, points[i]) <= tolerance) furthest = i
        reached = furthest
        if (reached < points.lastIndex) return false
        stroke++
        reached = 0
        return true
    }

    /** How far along the current stroke she is, from 0 to 1. */
    val fraction: Float get() = if (done) 1f else reached.toFloat() / glyph.strokes[stroke].lastIndex

    private fun distance(a: Offset, b: Offset): Float = hypot(a.x - b.x, a.y - b.y)

    private companion object {
        /** The points of the stroke the finger can jump ahead at once: some 100 units, so the O does not close at once. */
        const val LOOK_AHEAD = 25
    }
}
