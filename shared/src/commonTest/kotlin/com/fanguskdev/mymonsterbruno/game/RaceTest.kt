package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The race of animals of the island of numbers (ADR 0037): first, second and third. */
class RaceTest {
    @Test
    fun threeRacesAskingOnceAboutEachPlace() {
        repeat(20) { seed ->
            val races = raceRounds(Random(seed))
            assertEquals(Ordinal.entries.toSet(), races.map { it.asked }.toSet(), "una pregunta por cada puesto")
            for (race in races) {
                assertEquals(LANES, race.runners.toSet().size, "tres animales distintos")
                assertTrue(race.runners.all { it in RUNNERS }, "el pez no corre")
                assertEquals((1..LANES).toSet(), race.places.toSet(), "un puesto para cada uno")
                for (place in Ordinal.entries) assertEquals(place, race.placeOf(race.laneOf(place)))
            }
        }
    }

    @Test
    fun eachPlaceHasItsLines() {
        for (place in Ordinal.entries) {
            val lines = listOf(Voice.whoArrived(place), Voice.arrivedYes(place), Voice.thatOneArrived(place))
            assertTrue(lines.all { it in Voice.ALL }, "las frases del puesto ${place.word}")
        }
        assertEquals("n_quien_segundo", Voice.whoArrived(Ordinal.SECOND))
    }
}
