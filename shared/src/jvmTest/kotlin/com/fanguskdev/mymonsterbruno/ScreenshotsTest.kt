package com.fanguskdev.mymonsterbruno

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.BrunoColor
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.game.Topping
import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every screen drawn in PNG, without opening a window, in `shared/build/capturas/`: on a phone on its side (20:9) and on
 * a tablet (4:3). It checks that each one draws; looking at them is for a person.
 */
class ScreenshotsTest {
    @Test
    fun everyScreenDraws() {
        val folder = File("build/capturas").apply { mkdirs() }
        val scenes = listOf(
            "1-nacer" to Scene.Hatch,
            "2-casa" to Scene.Home,
            "3-mapa" to Scene.Map,
            "4-comeletras" to Scene.Play(Island.VOWELS, 0),
            "5-trazos" to Scene.Play(Island.VOWELS, 1),
            "6-fresas" to Scene.Play(Island.NUMBERS, 0),
            "7-regalo" to Scene.Present(Island.VOWELS),
            "8-dormir" to Scene.Sleep,
            "9-calma" to Scene.Calm,
            "10-mayores" to Scene.GrownUps,
            "11-globos" to Scene.Play(Island.COLORS, 0),
            "12-series" to Scene.Play(Island.COLORS, 1),
            "13-casa" to Scene.Play(Island.SHAPES, 0),
        )
        for ((device, size) in SIZES) {
            for ((name, scene) in scenes) {
                val file = File(folder, "$device-$name.png")
                file.writeBytes(render(scene, size.first, size.second))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
        }
    }

    private fun render(scene: Scene, width: Int, height: Int): ByteArray {
        val store = MemoryStore()
        val save = SaveData(
            hatched = scene != Scene.Hatch,
            childName = "Lucía",
            look = BrunoLook(BrunoColor.MINT, Topping.HORNS, listOf(Accessory.BEANIE, Accessory.BOW_TIE)),
        )
        SaveStore.save(store, save)
        val audio = Audio(SilentPlayer(), CoroutineScope(Dispatchers.Unconfined))
        val game = GameState(store, audio, Random(SEED), day = { DAY })
        game.greeted = true
        game.go(scene)
        val image = ImageComposeScene(width, height, Density(height / STAGE_DP)) { AppContent(game) }
        try {
            var frame = 0L
            repeat(FRAMES) {
                image.render(frame)
                frame += FRAME_NANOS
            }
            return image.render(frame).encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
        } finally {
            image.close()
        }
    }

    private companion object {
        val SIZES = listOf("movil" to (1560 to 720), "tablet" to (1200 to 900))
        const val STAGE_DP = 400f
        const val FRAMES = 90
        const val FRAME_NANOS = 16_666_667L
        const val SEED = 7
        const val DAY = 20261102
        const val MIN_BYTES = 10_000L
    }
}
