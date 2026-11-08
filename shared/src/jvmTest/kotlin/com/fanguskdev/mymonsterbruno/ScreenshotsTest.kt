package com.fanguskdev.mymonsterbruno

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import com.fanguskdev.mymonsterbruno.audio.Audio
import com.fanguskdev.mymonsterbruno.audio.SilentPlayer
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.BrunoColor
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.CONSONANTS
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.NEXT_CONSONANTS
import com.fanguskdev.mymonsterbruno.game.SaveData
import com.fanguskdev.mymonsterbruno.game.SaveStore
import com.fanguskdev.mymonsterbruno.game.Topping
import com.fanguskdev.mymonsterbruno.game.VOWELS
import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import com.fanguskdev.mymonsterbruno.storage.PHOTO_SIZE
import com.fanguskdev.mymonsterbruno.storage.PhotoStore
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Iphone
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.skia.EncodedImageFormat
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every screen drawn in PNG, without opening a window, in `shared/build/capturas/`: on a phone on its side (20:9), on a
 * tablet (4:3) and on the iPhone 15 Pro she plays on, held either way, with what its Dynamic Island and its rounded
 * corners hide painted black. It checks that each one draws; looking at them is for a person.
 */
class ScreenshotsTest {
    @Test
    fun everyScreenDraws() {
        val folder = File("build/capturas").apply { mkdirs() }
        val scenes = listOf(
            "0-inicio" to Scene.Splash,
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
            "17-emociones" to Scene.Play(Island.FEELINGS, 0),
            "18-habla" to Scene.Talk,
            "19-palabras" to Scene.Play(Island.WORDS, 0),
            "23-nombre" to Scene.Name,
            "24-emocion-del-dia" to Scene.Mood,
            "25-rimas" to Scene.Play(Island.WORDS, 1),
            "29-cuenta-atras" to Scene.Play(Island.NUMBERS, 1),
            "30-historia" to Scene.Play(Island.FEELINGS, 1),
            "32-dentro-fuera" to Scene.Play(Island.SHAPES, 1),
        )
        // The same games once she knows the vowels and one to five: the consonants and the numbers up to ten.
        val knows = { save: SaveData ->
            save.copy(
                fed = VOWELS.associate { it.toString() to 1 },
                traced = VOWELS.associate { it.toString() to 1 },
                counted = (1..5).associate { it.toString() to 1 },
            )
        }
        val later = listOf(
            "14-comeletras-consonantes" to Scene.Play(Island.VOWELS, 0),
            "15-trazos-consonantes" to Scene.Play(Island.VOWELS, 1),
            "16-fresas-hasta-diez" to Scene.Play(Island.NUMBERS, 0),
        )
        // And once she knows the M, the P, the L and the S: the T, the N and the D.
        val knowsMore = { save: SaveData ->
            knows(save).copy(
                fed = (VOWELS + CONSONANTS).associate { it.toString() to 1 },
                traced = (VOWELS + CONSONANTS).associate { it.toString() to 1 },
            )
        }
        val more = listOf(
            "21-comeletras-t-n-d" to Scene.Play(Island.VOWELS, 0),
            "22-trazos-t-n-d" to Scene.Play(Island.VOWELS, 1),
        )
        // Every island finished, and four of their stickers on the wall.
        val withStickers = { save: SaveData ->
            save.copy(
                islands = Island.entries.associate { it.name to 1 },
                stickers = mapOf(
                    Island.VOWELS.name to listOf(150f, 110f),
                    Island.NUMBERS.name to listOf(590f, 180f),
                    Island.COLORS.name to listOf(60f, 60f),
                    Island.SHAPES.name to listOf(230f, 210f),
                ),
            )
        }
        // Five rounds in a row right at the first try in each game that adapts: four to choose from.
        val easy = { save: SaveData -> save.copy(firstTries = Choosing.entries.associate { it.word to List(5) { true } }) }
        val struggling = { save: SaveData -> save.copy(firstTries = mapOf(Choosing.COOKIES.word to List(5) { false })) }
        val adapting = listOf(
            "4-comeletras-cuatro" to Scene.Play(Island.VOWELS, 0),
            "11-globos-cuatro" to Scene.Play(Island.COLORS, 0),
            "17-emociones-cuatro" to Scene.Play(Island.FEELINGS, 0),
            "19-palabras-cuatro" to Scene.Play(Island.WORDS, 0),
            "25-rimas-cuatro" to Scene.Play(Island.WORDS, 1),
        )
        // And once she knows the T, the N and the D: the F, the B and the R, and their row of rocks on the map.
        val knowsAll = { save: SaveData ->
            knowsMore(save).copy(
                fed = (VOWELS + CONSONANTS + NEXT_CONSONANTS).associate { it.toString() to 1 },
                traced = (VOWELS + CONSONANTS + NEXT_CONSONANTS).associate { it.toString() to 1 },
            )
        }
        val last = listOf(
            "26-comeletras-f-b-r" to Scene.Play(Island.VOWELS, 0),
            "27-trazos-f-b-r" to Scene.Play(Island.VOWELS, 1),
            "28-mapa" to Scene.Map,
        )
        // And the map once she has finished every island, with all its flags.
        val finished = { save: SaveData -> save.copy(islands = Island.entries.associate { it.name to 1 }) }
        for (screen in SCREENS) {
            for ((name, scene) in scenes) {
                // The grown-ups' corner keeps clear of the island with the system's safe area, and here there is none: on
                // the iPhone it would come out hidden where it is not. The simulator shows it whole (docs/pruebas.md).
                if (screen.hidden != null && scene == Scene.GrownUps) continue
                val file = File(folder, "${screen.name}-$name.png")
                file.writeBytes(render(scene, screen))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
            for ((name, scene) in later) {
                val file = File(folder, "${screen.name}-$name.png")
                file.writeBytes(render(scene, screen, knows))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
            for ((name, scene) in more) {
                val file = File(folder, "${screen.name}-$name.png")
                file.writeBytes(render(scene, screen, knowsMore))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
            for ((name, scene) in last) {
                val file = File(folder, "${screen.name}-$name.png")
                file.writeBytes(render(scene, screen, knowsAll))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
            val egg = File(folder, "${screen.name}-0-inicio-huevo.png")
            egg.writeBytes(render(Scene.Splash, screen, { it.copy(hatched = false) }))
            assertTrue(egg.length() > MIN_BYTES, "$egg está vacía")
            val withPhoto = File(folder, "${screen.name}-2-casa-con-foto.png")
            withPhoto.writeBytes(render(Scene.Home, screen, photo = samplePhoto()))
            assertTrue(withPhoto.length() > MIN_BYTES, "$withPhoto está vacía")
            // The room with four stickers on the wall and two waiting on the rug (ADR 0024).
            val stickers = File(folder, "${screen.name}-2-casa-pegatinas.png")
            stickers.writeBytes(render(Scene.Home, screen, withStickers))
            assertTrue(stickers.length() > MIN_BYTES, "$stickers está vacía")
            // The games that adapt (ADR 0020), with four to choose from when it is easy for her, and Comeletras with two.
            for ((name, scene) in adapting) {
                val file = File(folder, "${screen.name}-$name.png")
                file.writeBytes(render(scene, screen, easy))
                assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            }
            val hard = File(folder, "${screen.name}-4-comeletras-dos.png")
            hard.writeBytes(render(Scene.Play(Island.VOWELS, 0), screen, struggling))
            assertTrue(hard.length() > MIN_BYTES, "$hard está vacía")
            val moodWithPhoto = File(folder, "${screen.name}-24-emocion-del-dia-con-foto.png")
            moodWithPhoto.writeBytes(render(Scene.Mood, screen, photo = samplePhoto()))
            assertTrue(moodWithPhoto.length() > MIN_BYTES, "$moodWithPhoto está vacía")
            // The room when Bruno asks her to teach him her name, and the bunting waves.
            val asking = File(folder, "${screen.name}-2-casa-banderin.png")
            asking.writeBytes(render(Scene.Home, screen, asksForName = true))
            assertTrue(asking.length() > MIN_BYTES, "$asking está vacía")
            val file = File(folder, "${screen.name}-20-mapa-terminado.png")
            file.writeBytes(render(Scene.Map, screen, finished))
            assertTrue(file.length() > MIN_BYTES, "$file está vacía")
            // The number line, on the second day she goes to the island of the numbers (ADR 0026).
            val line = File(folder, "${screen.name}-31-recta.png")
            line.writeBytes(render(Scene.Play(Island.NUMBERS, 1), screen, { it.copy(numbersDay = DAY, numbersDays = 2) }))
            assertTrue(line.length() > MIN_BYTES, "$line está vacía")
        }
    }

    /** A made-up photo, a sky and a round face, as the gallery would give it: a JPEG [PHOTO_SIZE] pixels a side. */
    private fun samplePhoto(): ByteArray {
        val image = BufferedImage(PHOTO_SIZE, PHOTO_SIZE, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        g.color = java.awt.Color(0xA6DCF6)
        g.fillRect(0, 0, PHOTO_SIZE, PHOTO_SIZE)
        g.color = java.awt.Color(0xF5C7A1)
        g.fillOval(90, 70, 180, 200)
        g.color = java.awt.Color(0x6B3E26)
        g.fillOval(140, 140, 22, 22)
        g.fillOval(200, 140, 22, 22)
        g.fillArc(140, 170, 80, 60, 200, 140)
        g.dispose()
        return ByteArrayOutputStream().also { ImageIO.write(image, "jpg", it) }.toByteArray()
    }

    private fun render(
        scene: Scene,
        screen: Screen,
        practised: (SaveData) -> SaveData = { it },
        photo: ByteArray? = null,
        asksForName: Boolean = false,
    ): ByteArray {
        val store = MemoryStore()
        val save = SaveData(
            hatched = scene != Scene.Hatch,
            childName = "Lucía",
            look = BrunoLook(BrunoColor.MINT, Topping.HORNS, listOf(Accessory.BEANIE, Accessory.BOW_TIE)),
        )
        SaveStore.save(store, practised(save))
        photo?.let { PhotoStore.save(store, it) }
        val audio = Audio(SilentPlayer(), CoroutineScope(Dispatchers.Unconfined))
        val game = GameState(store, audio, Random(SEED), day = { DAY })
        game.greeted = true
        game.askedForName = !asksForName
        game.go(scene)
        // With a way out, as on Android and the desktop, so the grown-ups' corner shows its «Salir del juego».
        val image = ImageComposeScene(screen.width, screen.height, Density(screen.height / STAGE_DP)) {
            Box(Modifier.fillMaxSize()) {
                AppContent(game, onExit = {})
                screen.hidden?.let { Canvas(Modifier.fillMaxSize(), onDraw = it) }
            }
        }
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

    /** A screen [width] by [height] pixels, and what of it cannot be seen, drawn over the game, if there is anything. */
    private class Screen(
        val name: String,
        val width: Int,
        val height: Int,
        val hidden: (DrawScope.() -> Unit)? = null,
    )

    private companion object {
        val IPHONE = Iphone.PRO_15
        val SCREENS = listOf(Screen("movil", 1560, 720), Screen("tablet", 1200, 900)) +
            listOf(true, false).map { islandOnLeft ->
                // Two pixels a point, with the island on one side and then on the other.
                val side = if (islandOnLeft) "izquierda" else "derecha"
                Screen("${IPHONE.id}-$side", (IPHONE.width * 2).toInt(), (IPHONE.height * 2).toInt()) {
                    drawHidden(IPHONE, islandOnLeft)
                }
            }
        const val STAGE_DP = 400f
        const val FRAMES = 90
        const val FRAME_NANOS = 16_666_667L
        const val SEED = 7
        const val DAY = 20261102
        const val MIN_BYTES = 10_000L
    }
}

/** What [iphone] hides, with its island on the left or on the right, painted black: the island and the four corners. */
private fun DrawScope.drawHidden(iphone: Iphone, islandOnLeft: Boolean) {
    val k = size.width / iphone.width
    val island = iphone.island(islandOnLeft)
    drawRoundRect(Color.Black, island.topLeft * k, island.size * k, CornerRadius(island.width * k / 2))
    for (right in listOf(false, true)) {
        for (bottom in listOf(false, true)) {
            val corner = (listOf(Offset.Zero) + iphone.cornerEdge).map {
                Offset(if (right) iphone.width - it.x else it.x, if (bottom) iphone.height - it.y else it.y) * k
            }
            fill(
                path {
                    moveTo(corner.first().x, corner.first().y)
                    corner.drop(1).forEach { lineTo(it.x, it.y) }
                    close()
                },
                Color.Black,
            )
        }
    }
}
