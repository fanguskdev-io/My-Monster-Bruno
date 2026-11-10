package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionTest {
    private val monday = 20261102
    private val tuesday = 20261103

    @Test
    fun theMinutesAddUpAndRunOut() {
        var data = SaveData(dailyMinutes = 10)
        repeat(10 * 60 - 1) { data = Session.tick(data, monday, 1) }
        assertFalse(Session.timeUp(data, monday))
        data = Session.tick(data, monday, 1)
        assertTrue(Session.timeUp(data, monday))
        assertEquals(10, Session.minutesPlayed(data, monday))
    }

    @Test
    fun aNewDayStartsFromZero() {
        var data = SaveData(dailyMinutes = 5)
        repeat(5 * 60) { data = Session.tick(data, monday, 1) }
        assertTrue(Session.timeUp(data, monday))
        assertFalse(Session.timeUp(data, tuesday))
        data = Session.tick(data, tuesday, 1)
        assertEquals(tuesday, data.playedDay)
        assertEquals(1, data.playedSeconds)
    }

    @Test
    fun moreTimeCountsFromWhatSheHasPlayed() {
        var data = SaveData(dailyMinutes = 10)
        repeat(12 * 60) { data = Session.tick(data, monday, 1) }
        assertTrue(Session.timeUp(data, monday))
        data = Session.moreTime(data, monday, 10)
        assertFalse(Session.timeUp(data, monday))
        repeat(10 * 60) { data = Session.tick(data, monday, 1) }
        assertTrue(Session.timeUp(data, monday))
        // The extra is for that day only.
        assertEquals(0, Session.onDay(data, tuesday).extraSeconds)
    }

    @Test
    fun theSaveGoesAndComesBack() {
        val store = MemoryStore()
        val data = SaveData(
            hatched = true,
            look = BrunoLook(BrunoColor.PURPLE, Topping.ANTENNAE, listOf(Accessory.BEANIE, Accessory.GLASSES), growth = 2),
            childName = "Lucía",
            fed = mapOf("a" to 3),
            islands = mapOf("VOWELS" to 2),
            dailyMinutes = 15,
            voiceOn = false,
        )
        SaveStore.save(store, data)
        assertEquals(data, SaveStore.load(store))
    }

    @Test
    fun aBrokenSaveStartsFromNothing() {
        val store = MemoryStore()
        store.putString("partida", "{ esto no es una partida")
        assertEquals(SaveData(), SaveStore.load(store))
        assertEquals(SaveData(), SaveStore.load(MemoryStore()))
    }

    @Test
    fun brunoWearsTheNewestOfEachPlaceAndGrowsUpToThree() {
        val look = BrunoLook(accessories = listOf(Accessory.BEANIE, Accessory.GLASSES, Accessory.BOW_TIE, Accessory.FLOWERS))
        assertEquals(listOf(Accessory.FLOWERS, Accessory.GLASSES, Accessory.BOW_TIE), look.wearing)
        assertEquals(Accessory.SCARF, look.nextPresent)
        assertEquals(null, BrunoLook(accessories = Accessory.entries).nextPresent)
        assertEquals(BrunoLook(growth = BrunoLook.MAX_GROWTH).size, BrunoLook(growth = 10).size)
    }

    @Test
    fun startingAgainKeepsHerNameAndTheSettings() {
        val played = SaveData(
            hatched = true,
            childName = "Lucía",
            islands = mapOf("VOWELS" to 3),
            fed = mapOf("a" to 4),
            dailyMinutes = 15,
            playedDay = monday,
            playedSeconds = 300,
            musicOn = false,
            slowVoice = true,
            pet = "gato",
            petSeen = true,
            hiddenIslands = listOf("HOME"),
            hiddenLetters = listOf("m"),
        )
        val again = played.startedAgain()
        assertFalse(again.hatched, "Bruno vuelve al huevo")
        assertTrue(again.islands.isEmpty() && again.fed.isEmpty(), "y se olvida lo practicado")
        assertEquals("Lucía", again.childName)
        assertEquals(15, again.dailyMinutes)
        assertEquals(300, again.playedSeconds, "los minutos de hoy siguen contando")
        assertFalse(again.musicOn)
        assertTrue(again.slowVoice)
        assertEquals("gato", again.pet, "su animal se queda")
        assertFalse(again.petSeen, "y el Bruno nuevo lo presenta otra vez")
        assertEquals(listOf("HOME"), again.hiddenIslands)
        assertEquals(listOf("m"), again.hiddenLetters)
    }
}
