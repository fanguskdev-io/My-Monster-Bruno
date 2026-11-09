package com.fanguskdev.mymonsterbruno.game

import androidx.compose.ui.geometry.Offset
import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** «Trazos antes de las letras» (ADR 0031): the paths of a visit, their points, and the days they take turns with the letters. */
class PathsTest {
    @Test
    fun theStraightOnesFirstAndThenTheLeastDone() {
        assertEquals(listOf(PathShape.ACROSS, PathShape.DOWN, PathShape.SLANT), pathRounds(emptyMap()))
        val straight = mapOf("recta" to 1, "abajo" to 1, "inclinada" to 1)
        assertEquals(listOf(PathShape.WAVES, PathShape.LOOPS, PathShape.SPIRAL), pathRounds(straight))
        val all = PathShape.entries.associate { it.word to 1 } + ("bucles" to 2)
        assertEquals(listOf(PathShape.ACROSS, PathShape.DOWN, PathShape.SLANT), pathRounds(all))
    }

    @Test
    fun everyPathIsEvenlyStepped() {
        for (shape in PathShape.entries) {
            val points = shape.points()
            assertTrue(points.size > 50, "$shape es muy corto: menos de 200 unidades")
            points.zipWithNext().dropLast(1).forEach { (a, b) ->
                assertEquals(PATH_STEP, hypot(b.x - a.x, b.y - a.y), 0.05f, "$shape no va a pasos iguales")
            }
        }
    }

    @Test
    fun everyPathStaysWhereSheCanReachIt() {
        // Clear of the corner buttons, of the Dynamic Island on either side of her iPhone, and of the edges.
        for (shape in PathShape.entries) {
            for (point in shape.points()) {
                assertTrue(point.x in 120f..680f && point.y in 70f..340f, "$shape sale de su sitio en $point")
            }
        }
    }

    @Test
    fun brunoWalksUnderAFingerThatFollowsThePathAndWaitsOtherwise() {
        for (shape in PathShape.entries) {
            val points = shape.points()
            val progress = TraceProgress(Glyph(' ', 0f, listOf(points)))
            assertTrue(progress.canStart(points.first(), START), "$shape: no se coge a Bruno")
            assertFalse(progress.moveTo(points.last(), FINGER), "$shape: un dedo lejos lo lleva a casa")
            assertEquals(0, progress.reached, "$shape: un dedo lejos lo mueve")
            // Half the way, a little to one side of the middle of the path, a point every few steps.
            val half = points.size / 2
            for (i in 0..half step 3) progress.moveTo(points[i] + Offset(0f, 12f), FINGER)
            assertTrue(progress.reached >= half - 3, "$shape: se ha quedado en ${progress.reached} de $half")
            // The finger lifted and put down at the start again does not take him back; on Bruno, it goes on.
            assertFalse(progress.canStart(points.first(), START), "$shape: vuelve atrás")
            assertTrue(progress.canStart(points[progress.reached], START))
            var home = false
            for (i in progress.reached..points.lastIndex step 3) home = progress.moveTo(points[i], FINGER) || home
            if (!home) home = progress.moveTo(points.last(), FINGER)
            assertTrue(home && progress.done, "$shape: no llega a casa")
        }
    }

    @Test
    fun theLettersAndThePathsTakeTurnsByTheDaysSheGoes() {
        var data = SaveData()
        assertFalse(data.pathsToday, "sin haber ido, las letras")
        data = data.lettersOn(20261108)
        assertFalse(data.pathsToday, "el primer día, las letras")
        data = data.lettersOn(20261108)
        assertFalse(data.pathsToday, "el mismo día, lo mismo")
        data = data.lettersOn(20261111)
        assertTrue(data.pathsToday, "el siguiente día que va, los caminos")
        data = data.lettersOn(20261112)
        assertFalse(data.pathsToday)
        assertEquals(3, data.lettersDays)
    }

    @Test
    fun everyPathHasItsLine() {
        for (shape in PathShape.entries) assertTrue(Voice.pathIs(shape) in Voice.ALL)
        assertEquals("n_camino_caracol", Voice.pathIs(PathShape.SPIRAL))
    }

    private companion object {
        /** The tolerances of the screen of the paths (ui/screens/PathsScreen.kt). */
        const val START = 50f
        const val FINGER = 40f
    }
}
