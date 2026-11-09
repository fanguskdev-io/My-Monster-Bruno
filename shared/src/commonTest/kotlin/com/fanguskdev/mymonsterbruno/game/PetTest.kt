package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Her favourite animal (ADR 0033): the one a grown-up chose, if any, and that each has its line. */
class PetTest {
    @Test
    fun theAnimalAGrownUpChose() {
        assertNull(SaveData().chosenPet, "sin elegir, ninguno")
        assertEquals(Pet.FISH, SaveData(pet = "pez").chosenPet)
        assertNull(SaveData(pet = "dragón").chosenPet, "uno que no hay, ninguno")
    }

    @Test
    fun everyAnimalHasItsLine() {
        for (pet in Pet.entries) assertTrue(Voice.petHello(pet) in Voice.ALL)
        assertTrue(Voice.NEW_PET in Voice.ALL)
        assertEquals("b_mascota_pajaro", Voice.petHello(Pet.BIRD))
    }
}
