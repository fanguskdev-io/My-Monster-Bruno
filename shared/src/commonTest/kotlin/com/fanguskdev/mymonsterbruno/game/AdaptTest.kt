package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** That the games adapt to what she knows (ADR 0020): how many she chooses from, and that her answer is always there. */
class AdaptTest {
    private fun after(vararg rounds: Boolean) =
        rounds.fold(SaveData()) { save, right -> save.played(Choosing.COOKIES, firstTry = right) }.choices(Choosing.COOKIES)

    @Test
    fun threeToStartWithFourWhenEasyTwoWhenHard() {
        assertEquals(CHOICES, SaveData().choices(Choosing.COOKIES), "al principio, tres")
        assertEquals(CHOICES, after(true, true, true, true), "con menos de cinco rondas, tres")
        assertEquals(MAX_CHOICES, after(true, true, true, true, true), "cinco seguidas a la primera, cuatro")
        assertEquals(CHOICES, after(true, true, true, true, true, false), "un fallo, otra vez tres")
        assertEquals(CHOICES, after(false, false, true, true, true), "dos fallos de cinco, tres")
        assertEquals(MIN_CHOICES, after(false, true, false, true, false), "más de la mitad mal, dos")
        assertEquals(CHOICES, after(false, false, false, true, true, true), "solo cuentan las cinco últimas")
    }

    @Test
    fun eachGameKeepsItsOwnLastRounds() {
        var save = SaveData()
        repeat(7) { save = save.played(Choosing.BALLOONS, firstTry = true) }
        repeat(5) { save = save.played(Choosing.CARDS, firstTry = false) }
        assertEquals(LAST_ROUNDS, save.firstTries[Choosing.BALLOONS.word]?.size)
        assertEquals(MAX_CHOICES, save.choices(Choosing.BALLOONS))
        assertEquals(MIN_CHOICES, save.choices(Choosing.CARDS))
        assertEquals(CHOICES, save.choices(Choosing.FACES), "las caritas, sin jugar, tres")
    }

    @Test
    fun theAnswerIsAlwaysAmongTheOptionsWhateverTheirNumber() {
        repeat(100) { seed ->
            val random = Random(seed)
            for (count in MIN_CHOICES..MAX_CHOICES) {
                for (round in letterRounds(random)) {
                    val plate = round.cookies.narrowed(round.wanted, count)
                    assertEquals(count, plate.size)
                    assertTrue(round.wanted in plate)
                    assertEquals(plate, round.cookies.filter { it in plate }, "en el mismo orden")
                }
                for (round in balloonRounds(random)) assertTrue(round.wanted in round.balloons.narrowed(round.wanted, count))
                for (round in feelingRounds(random)) {
                    val faces = round.faces.narrowed(round.happening.feeling, count)
                    assertEquals(count, faces.toSet().size)
                    assertTrue(round.happening.feeling in faces)
                }
                for (round in startRounds(random)) {
                    val cards = round.choices.narrowed(round.answer, count)
                    assertEquals(1, cards.count { it.start == round.said.start }, "una sola empieza igual: $cards")
                }
            }
        }
    }
}
