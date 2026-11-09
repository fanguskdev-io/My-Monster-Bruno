package com.fanguskdev.mymonsterbruno.storage

import com.fanguskdev.mymonsterbruno.game.SaveData
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** The copy of her game (ADR 0039): it comes back as it went, with her photo, and nothing else passes for one. */
class BackupTest {
    private val game = SaveData(hatched = true, childName = "Lucía", islands = mapOf("VOWELS" to 3), pet = "gato")

    @Test
    fun aCopyComesBackAsItWentWithHerPhoto() {
        val photo = byteArrayOf(1, 2, 3, -1, 0, 127)
        val back = Backup.read(Backup.write(game, photo))
        assertEquals(game, back?.data)
        assertContentEquals(photo, back?.photo)
        val withoutPhoto = Backup.read(Backup.write(game, null))
        assertEquals(game, withoutPhoto?.data)
        assertNull(withoutPhoto?.photo)
    }

    @Test
    fun anythingElseIsNotACopy() {
        assertNull(Backup.read(""))
        assertNull(Backup.read("hola"))
        assertNull(Backup.read("""{"juego":"Otro juego","version":1,"partida":{}}"""), "de otro juego")
        assertNull(Backup.read("""{"juego":"Mi amigo Bruno","version":1,"partida":{},"foto":"%%%"}"""), "con la foto rota")
        assertEquals(SaveData(), Backup.read("""{"juego":"Mi amigo Bruno","version":1,"partida":{}}""")?.data, "vacía, la de empezar")
    }
}
