package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** «Dentro y fuera» (ADR 0028): the rounds of a visit, when a place is the one asked for, and that each has its lines. */
class PlacesTest {
    @Test
    fun everyPlaceOnceWithBrunoAndTheBallByTurns() {
        val firsts = mutableSetOf<Thing>()
        repeat(200) { seed ->
            val rounds = placings(Random(seed))
            assertEquals(Place.entries.toSet(), rounds.map { it.place }.toSet(), "falta algún sitio: $rounds")
            assertEquals(Place.entries.size, rounds.size)
            rounds.zipWithNext().forEach { (a, b) -> assertEquals(a.thing.other, b.thing, "dos seguidas con lo mismo: $rounds") }
            firsts += rounds.first().thing
        }
        assertEquals(Thing.entries.toSet(), firsts, "siempre empieza con lo mismo")
    }

    @Test
    fun outOfTheBoxIsAnywhereButABox() {
        val out = Placing(Thing.BALL, Place.OUT_OF_BOX)
        assertTrue(out.rightAt(null), "en el suelo")
        assertTrue(out.rightAt(Place.ON_TABLE))
        assertTrue(out.rightAt(Place.UNDER_TABLE))
        assertFalse(out.rightAt(Place.BIG_BOX))
        assertFalse(out.rightAt(Place.SMALL_BOX))
    }

    @Test
    fun everyOtherPlaceIsJustThatOne() {
        for (place in Place.entries - Place.OUT_OF_BOX) {
            val placing = Placing(Thing.BRUNO, place)
            assertTrue(placing.rightAt(place))
            assertFalse(placing.rightAt(null), "$place no es el suelo")
            for (other in Place.entries - place - Place.OUT_OF_BOX) assertFalse(placing.rightAt(other), "$place no es $other")
        }
    }

    @Test
    fun everyRoundHasItsLines() {
        for (thing in Thing.entries) {
            for (place in Place.entries) {
                assertTrue(Voice.putIt(Placing(thing, place)) in Voice.ALL)
                assertTrue(Voice.itIs(Placing(thing, place)) in Voice.ALL)
            }
        }
        for (place in Place.entries - Place.OUT_OF_BOX) assertTrue(Voice.leftAt(place) in Voice.ALL)
        assertTrue(Voice.leftAt(null) in Voice.ALL)
        assertEquals("n_pon_bruno_caja_grande", Voice.putIt(Placing(Thing.BRUNO, Place.BIG_BOX)))
    }
}
