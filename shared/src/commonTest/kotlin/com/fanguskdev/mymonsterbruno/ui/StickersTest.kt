package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.ui.geometry.Offset
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.ui.screens.stickerFits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The stickers of the room (ADR 0024): one for each finished island, and only where they hide nothing. */
class StickersTest {
    @Test
    fun aStickerForEachFinishedIslandUntilSheSticksIt() {
        val save = SaveData(islands = mapOf(Island.NUMBERS.name to 2, Island.VOWELS.name to 1))
        assertEquals(listOf(Island.VOWELS, Island.NUMBERS), save.pendingStickers(), "una por isla, en el orden del mapa")
        val stuck = save.stuck(Island.NUMBERS, Offset(150f, 110f))
        assertEquals(listOf(Island.VOWELS), stuck.pendingStickers())
        assertEquals(listOf(Island.NUMBERS to Offset(150f, 110f)), stuck.stuckStickers())
        val moved = stuck.stuck(Island.NUMBERS, Offset(590f, 180f))
        assertEquals(listOf(Island.NUMBERS to Offset(590f, 180f)), moved.stuckStickers(), "moverla no la duplica")
        assertTrue(SaveData().pendingStickers().isEmpty())
    }

    @Test
    fun onlyOnTheWallAndNeverOverWhatSheTouches() {
        val stage = Stage(STAGE_WIDTH, STAGE_HEIGHT)
        val name = "Lucía"
        assertTrue(stickerFits(Offset(150f, 110f), stage, name), "en la pared de la izquierda")
        assertTrue(stickerFits(Offset(590f, 180f), stage, name), "en la pared de la derecha")
        assertFalse(stickerFits(Offset(150f, 300f), stage, name), "en el suelo")
        assertFalse(stickerFits(Offset(680f, 230f), stage, name), "sobre el botón de jugar")
        assertFalse(stickerFits(Offset(90f, 200f), stage, name), "sobre el micrófono")
        assertFalse(stickerFits(Offset(390f, 160f), stage, name), "detrás de Bruno")
        assertFalse(stickerFits(Offset(520f, 60f), stage, name), "sobre el banderín")
        assertFalse(stickerFits(Offset(712f, 150f), stage, name), "sobre el marco de su foto")
        assertFalse(stickerFits(Offset(775f, 200f), stage, name), "donde un iPhone tiene la isla")
    }
}
