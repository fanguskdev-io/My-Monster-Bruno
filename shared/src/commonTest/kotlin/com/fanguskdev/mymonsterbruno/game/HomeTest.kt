package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import com.fanguskdev.mymonsterbruno.ui.Activity
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.sea
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The island of the house (ADR 0044), in the other part of the sea (ADR 0043). */
class HomeTest {
    @Test
    fun dressingHasARoundForEachWeatherWithOneThingOfEach() {
        repeat(20) { seed ->
            val rounds = dressRounds(Random(seed))
            assertEquals(DRESS_WEATHERS.toSet(), rounds.map { it.weather }.toSet(), "un tiempo por ronda")
            for (round in rounds) {
                assertEquals(DRESS_WEATHERS.toSet(), round.choices.map { it.weather }.toSet(), "una cosa de cada tiempo")
                assertEquals(round.weather, round.right.weather)
            }
        }
    }

    @Test
    fun theSixThingsNeverTwoOfTheSameBinInARow() {
        repeat(30) { seed ->
            val order = rubbishRounds(Random(seed))
            assertEquals(Rubbish.entries.toSet(), order.toSet())
            assertTrue(order.zipWithNext().none { (a, b) -> a.bin == b.bin }, "semilla $seed")
        }
        assertEquals(2, Rubbish.entries.count { it.bin == Bin.GREEN }, "dos para cada contenedor")
    }

    @Test
    fun theTeethAndThenDressingOrTheBinsByTheDaysSheGoes() {
        val store = MemoryStore()
        SaveStore.save(store, SaveData(hatched = true))
        var today = 20261109

        fun second(): Activity {
            val game = GameState(store, Audio(SilentPlayer(), CoroutineScope(EmptyCoroutineContext)), Random(1)) { today }
            game.goTo(Island.HOME)
            assertEquals(Scene.Play(Island.HOME, 0), game.scene)
            assertEquals(1, game.mapSea, "el mapa vuelve a la otra parte del mar")
            assertEquals(Activity.TEETH, game.activities(Island.HOME).first(), "primero, los dientes")
            return game.activities(Island.HOME)[1]
        }
        assertEquals(Activity.DRESS, second(), "el primer día, vestirse")
        assertEquals(Activity.DRESS, second(), "el mismo día, lo mismo")
        today = 20261112
        assertEquals(Activity.RECYCLE, second(), "el siguiente día que va, los contenedores")
        today = 20261113
        assertEquals(Activity.DRESS, second())
        assertEquals(1, Island.HOME.sea)
        assertTrue(Island.entries.filter { it != Island.HOME }.all { it.sea == 0 })
    }

    @Test
    fun everythingHasItsLines() {
        val lines = DRESS_WEATHERS.flatMap { listOf(Voice.whatToWear(it), Voice.thatIsFor(it)) } +
            Clothes.entries.map { Voice.wearIt(it) } +
            Rubbish.entries.flatMap { listOf(Voice.whereGoes(it), Voice.goesRight(it), Voice.goesTo(it)) }
        assertTrue(lines.all { it in Voice.ALL })
        assertEquals("n_va_lata", Voice.goesTo(Rubbish.CAN))
    }
}
