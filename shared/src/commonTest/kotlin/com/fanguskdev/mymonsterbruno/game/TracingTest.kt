package com.fanguskdev.mymonsterbruno.game

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TracingTest {
    private val tolerance = 30f

    /** A finger that follows each stroke of [glyph] point by point, like a careful child. */
    private fun traceAll(progress: TraceProgress, wobble: Float = 0f) {
        while (!progress.done) {
            val stroke = progress.glyph.strokes[progress.stroke]
            assertTrue(progress.canStart(stroke[progress.reached], tolerance))
            for ((i, point) in stroke.withIndex()) {
                val side = if (i % 2 == 0) wobble else -wobble
                if (progress.moveTo(point + Offset(side, side), tolerance)) break
            }
        }
    }

    @Test
    fun everyLetterCanBeTracedToTheEnd() {
        for (vowel in LETTERS) {
            val progress = TraceProgress(Glyphs.of(vowel))
            traceAll(progress)
            assertTrue(progress.done, "la $vowel no se acaba")
        }
    }

    @Test
    fun aShakyFingerStillTraces() {
        for (vowel in LETTERS) {
            val progress = TraceProgress(Glyphs.of(vowel))
            traceAll(progress, wobble = 18f)
            assertTrue(progress.done, "la $vowel no se acaba con un dedo que tiembla")
        }
    }

    @Test
    fun theStrokesGoInTheSchoolOrder() {
        assertEquals(3, Glyphs.of('a').strokes.size)
        assertEquals(4, Glyphs.of('e').strokes.size)
        assertEquals(1, Glyphs.of('i').strokes.size)
        assertEquals(1, Glyphs.of('o').strokes.size)
        assertEquals(1, Glyphs.of('u').strokes.size)
        // The A starts at its top, the E with its stick, the O at the top and to the left.
        assertEquals(Offset(55f, 0f), Glyphs.of('a').strokes[0].first())
        assertEquals(Offset(10f, 0f), Glyphs.of('e').strokes[0].first())
        val o = Glyphs.of('o').strokes[0]
        assertEquals(55f, o.first().x, 0.01f)
        assertEquals(0f, o.first().y, 0.01f)
        assertTrue(o[5].x < o.first().x, "la O tiene que ir hacia la izquierda")
        // The M and the P, their stick first and then the rest from the top; the L and the S, in one go.
        for (letter in listOf('m', 'p')) {
            val strokes = Glyphs.of(letter).strokes
            assertEquals(2, strokes.size)
            assertEquals(Offset(10f, 0f), strokes[0].first())
            assertEquals(Offset(10f, 0f), strokes[1].first())
        }
        assertEquals(1, Glyphs.of('l').strokes.size)
        val s = Glyphs.of('s').strokes.single()
        assertTrue(s.first().x > 60f && s.first().y < 30f, "la S empieza arriba a la derecha")
        assertTrue(s.last().x < 30f && s.last().y > 110f, "la S acaba abajo a la izquierda")
    }

    @Test
    fun aFingerFarAwayDoesNotStartNorAdvance() {
        val progress = TraceProgress(Glyphs.of('a'))
        assertFalse(progress.canStart(Offset(100f, 140f), tolerance))
        assertFalse(progress.moveTo(Offset(100f, 140f), tolerance))
        assertEquals(0, progress.reached)
    }

    @Test
    fun theFingerCannotJumpToTheEndOfTheO() {
        val progress = TraceProgress(Glyphs.of('o'))
        val stroke = progress.glyph.strokes[0]
        // The end of the O is where it starts: touching there at the start cannot finish it at once.
        assertFalse(progress.moveTo(stroke.last(), tolerance))
        assertFalse(progress.done)
        assertTrue(progress.reached < stroke.size / 2)
    }

    @Test
    fun liftingTheFingerKeepsWhatWasDone() {
        val progress = TraceProgress(Glyphs.of('i'))
        val stroke = progress.glyph.strokes[0]
        for (point in stroke.take(stroke.size / 2)) progress.moveTo(point, tolerance)
        val reached = progress.reached
        assertTrue(reached > 0)
        assertTrue(progress.canStart(stroke[reached], tolerance))
        assertFalse(progress.canStart(stroke.first(), 5f))
    }
}
