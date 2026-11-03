package com.fanguskdev.mymonsterbruno.audio

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Every line the game says and every sound it plays has its file, and the script of the voices (voces/guion.tsv) has
 * exactly the lines the game asks for: a line written but not used, or used but not written, shows here.
 */
class AudioFilesTest {
    private val resources = File("src/commonMain/composeResources/files")

    @Test
    fun everyLineHasItsFileAndPlays() {
        for (line in Voice.ALL) {
            val file = File(resources, "voces/$line.wav")
            assertTrue(file.exists(), "falta $file")
            val pcm = readWav(file.readBytes())
            // The shortest are the syllables Bruno claps, «¡Po!», about a quarter of a second.
            assertTrue(pcm != null && pcm.millis in 200..9000, "$file no es una voz que se pueda decir")
        }
    }

    @Test
    fun theScriptHasTheLinesOfTheGame() {
        val script = File("../voces/guion.tsv").readLines().drop(1).filter { it.isNotBlank() }.map { it.substringBefore('\t') }
        assertEquals(Voice.ALL.sorted(), script.sorted())
        assertEquals(Voice.ALL.size, Voice.ALL.toSet().size)
    }

    @Test
    fun everySoundHasItsFile() {
        for (sound in Sfx.entries) {
            val file = File(resources, "sonidos/${sound.file}.wav")
            assertTrue(file.exists(), "falta $file")
            assertTrue(readWav(file.readBytes()) != null, "$file no se lee")
        }
    }
}
