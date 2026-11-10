package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.CONSONANTS
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LAST_CONSONANTS
import com.fanguskdev.mymonsterbruno.game.NEXT_CONSONANTS
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.VOWELS
import com.fanguskdev.mymonsterbruno.game.hidden
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.STAGE_WIDTH
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawFeelingFace
import com.fanguskdev.mymonsterbruno.ui.art.drawHouseSign
import com.fanguskdev.mymonsterbruno.ui.art.drawOpenBook
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundPhoto
import com.fanguskdev.mymonsterbruno.ui.art.drawSea
import com.fanguskdev.mymonsterbruno.ui.art.drawShapePiece
import com.fanguskdev.mymonsterbruno.ui.art.drawTiedBalloon
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.art.pieceBox
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.hasIslandsIn
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.rememberPhoto
import com.fanguskdev.mymonsterbruno.ui.sea
import com.fanguskdev.mymonsterbruno.ui.seaToOpen
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.suggestedIsland
import com.fanguskdev.mymonsterbruno.ui.suggestionLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val VOWELS_ISLAND = Offset(262f, 258f)
private val NUMBERS_ISLAND = Offset(588f, 150f)
private const val ISLAND_RX = 110f
private const val ISLAND_RY = 52f
private val COLORS_ISLAND = Offset(674f, 318f)
private val SHAPES_ISLAND = Offset(340f, 78f)
private val FEELINGS_ISLAND = Offset(112f, 150f)
private val WORDS_ISLAND = Offset(712f, 56f)
private const val WORDS_RX = 80f
private const val WORDS_RY = 38f
private val BOAT = Offset(410f, 336f)

/** The other part of the sea (ADR 0043): the island of the house, and the button by the boat to sail there and back. */
private val HOME_ISLAND = Offset(430f, 168f)
private val SAIL_BUTTON = Offset(522f, 356f)
private const val SAIL_RADIUS = 34f
private const val SAIL_SECONDS = 1.3f

/**
 * The map of the islands: the one of the letters, the one of the numbers, the one of the colours, the one of the shapes,
 * the one of the feelings and, the smallest, far up on the right, the one of the words; and, in the other part of the
 * sea, sailing with the button by the boat, the one of the house (ADR 0043). A touch on an island says where they go and
 * goes there; the flag says she has finished it at least once.
 */
@Composable
internal fun MapScreen(game: GameState) {
    val clock = rememberClock()
    val photo = rememberPhoto(game)
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var going by remember { mutableStateOf(false) }

    // The part of the sea on screen, the one of the last island; and, while the boat sails to the other, since when. The
    // button to sail is there only when both parts have islands: the grown-ups can take every island of one off, and then
    // the map shows the other (ADR 0041).
    fun otherSea() = game.data.hasIslandsIn(0) && game.data.hasIslandsIn(1)
    var sea by remember { mutableStateOf(game.data.seaToOpen(game.mapSea)) }
    var sailingAt by remember { mutableStateOf<Float?>(null) }

    // The first time she gets to the map since the game opened, Bruno suggests the island she has done least, which
    // shines while they are on the map; any other is as good (ADR 0035).
    val suggestion = remember { if (game.suggestedIsland) null else game.data.suggestedIsland(game.currentDay()) }

    LaunchedEffect(Unit) {
        if (suggestion != null) {
            game.suggestedIsland = true
            audio.say(Voice.WHICH_ISLAND, suggestion.suggestionLine)
        } else {
            audio.say(Voice.WHICH_ISLAND)
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (going || sailingAt != null) return true
                when {
                    hits(point, homeButton(stage), CORNER_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        game.back()
                    }
                    otherSea() && hits(point, SAIL_BUTTON, SAIL_RADIUS) -> sail()
                    sea == 1 -> {
                        if (shown(Island.HOME) && inIsland(point, HOME_ISLAND)) go(Island.HOME, Voice.TO_HOME) else return false
                    }
                    // Only the islands on the map: the grown-ups can take some off (ADR 0041).
                    shown(Island.VOWELS) && inIsland(point, VOWELS_ISLAND) -> go(Island.VOWELS, Voice.TO_LETTERS)
                    shown(Island.NUMBERS) && inIsland(point, NUMBERS_ISLAND) -> go(Island.NUMBERS, Voice.TO_NUMBERS)
                    shown(Island.COLORS) && inIsland(point, COLORS_ISLAND) -> go(Island.COLORS, Voice.TO_COLORS)
                    shown(Island.SHAPES) && inIsland(point, SHAPES_ISLAND) -> go(Island.SHAPES, Voice.TO_SHAPES)
                    shown(Island.FEELINGS) && inIsland(point, FEELINGS_ISLAND) -> go(Island.FEELINGS, Voice.TO_FEELINGS)
                    shown(Island.WORDS) && inIsland(point, WORDS_ISLAND, WORDS_RX, WORDS_RY) -> go(Island.WORDS, Voice.TO_WORDS)
                    else -> return false
                }
                return true
            }

            private fun shown(island: Island) = island.name !in game.data.hiddenIslands

            /** The boat sails to the other part of the sea, and the map slides with it. */
            private fun sail() {
                val to = 1 - sea
                sailingAt = clock.value
                audio.sfx(Sfx.WHOOSH)
                audio.speak(if (to == 1) Voice.OTHER_SEA else Voice.BACK_SEA)
                scope.launch {
                    delay((SAIL_SECONDS * 1000).toLong())
                    sea = to
                    game.mapSea = to
                    sailingAt = null
                }
            }

            private fun go(island: Island, line: String) {
                going = true
                audio.sfx(Sfx.WHOOSH)
                scope.launch {
                    audio.say(line)
                    game.goTo(island)
                }
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawSea(stage, t)
        // The islands the grown-ups took off the map are not there, nor their way (ADR 0041).
        val hidden = game.data.hiddenIslands
        fun shown(island: Island) = island.name !in hidden
        fun done(island: Island) = shown(island) && (game.data.islands[island.name] ?: 0) > 0
        // While the boat sails, the part of the sea it leaves slides away and the other one comes in (ADR 0043).
        val sailing = sailingAt?.let { easeInOut(clamp01((t - it) / SAIL_SECONDS)) }
        val toward = if (sea == 0) -1f else 1f
        val parts = if (sailing ==
            null
        ) {
            listOf(sea to 0f)
        } else {
            listOf(sea to toward * STAGE_WIDTH * sailing, (1 - sea) to -toward * STAGE_WIDTH * (1f - sailing))
        }
        for ((part, dx) in parts) {
            translate(left = dx) {
                if (part == 0) {
                    drawPath(t, ::shown)
                    if (suggestion != null &&
                        sailing == null &&
                        !going &&
                        shown(suggestion) &&
                        suggestion.sea == 0
                    ) {
                        drawSuggestion(suggestion, t)
                    }
                    drawFirstSea(t, ::shown, ::done, game.data.hidden, measurer, font)
                } else {
                    if (suggestion != null && sailing == null && !going && suggestion.sea == 1) drawSuggestion(suggestion, t)
                    if (shown(Island.HOME)) {
                        drawIsland(HOME_ISLAND, t)
                        drawHouseSign(HOME_ISLAND + Offset(0f, -18f), 74f)
                    }
                    if (done(Island.HOME)) drawFlag(HOME_ISLAND + Offset(92f, -34f))
                }
            }
        }
        // The island Bruno suggests, when it is in the other part: the button to sail there shines, and Bruno looks at it.
        val elsewhere = suggestion != null && suggestion.sea != sea && sailing == null && !going
        if (otherSea()) {
            if (elsewhere) drawCallRing(SAIL_BUTTON, SAIL_RADIUS + 4f, t)
            drawSailButton(SAIL_BUTTON, SAIL_RADIUS, back = sea == 1)
        }
        val lookAt = when {
            sailing != null -> null
            elsewhere -> SAIL_BUTTON
            else -> suggestion?.let { islandCenter(it) }
        }
        val rock = if (sailing != null) 7f * sin(t * 9f) else 4f * sin(t * 1.5f)
        drawBoat(game.data.look, BOAT + Offset(0f, rock), t, photo, lookAt)
        drawHomeButton(stage)
    }
}

/** The first part of the sea: its six islands, each with its sign, and the flags of the ones she has finished. */
private fun DrawScope.drawFirstSea(
    t: Float,
    shown: (Island) -> Boolean,
    done: (Island) -> Boolean,
    hiddenLetters: Set<Char>,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    if (shown(Island.VOWELS)) {
        drawIsland(VOWELS_ISLAND, t)
        drawVowelRocks(VOWELS_ISLAND, hiddenLetters, measurer, font)
    }
    if (shown(Island.NUMBERS)) {
        drawIsland(NUMBERS_ISLAND, t)
        drawNumberRocks(NUMBERS_ISLAND, measurer, font)
    }
    if (shown(Island.COLORS)) {
        drawIsland(COLORS_ISLAND, t)
        drawIslandBalloons(COLORS_ISLAND, t)
    }
    if (shown(Island.SHAPES)) {
        drawIsland(SHAPES_ISLAND, t)
        drawIslandShapes(SHAPES_ISLAND)
    }
    if (shown(Island.FEELINGS)) {
        drawIsland(FEELINGS_ISLAND, t)
        drawIslandFaces(FEELINGS_ISLAND)
    }
    if (shown(Island.WORDS)) {
        drawIsland(WORDS_ISLAND, t, WORDS_RX, WORDS_RY)
        drawOpenBook(WORDS_ISLAND + Offset(0f, -14f), 58f)
    }
    if (done(Island.VOWELS)) drawFlag(VOWELS_ISLAND + Offset(92f, -34f))
    if (done(Island.NUMBERS)) drawFlag(NUMBERS_ISLAND + Offset(92f, -34f))
    // Lower than the others: higher, on an iPhone held with its island on the right, the Dynamic Island hides it.
    if (done(Island.COLORS)) drawFlag(COLORS_ISLAND + Offset(84f, 24f))
    if (done(Island.SHAPES)) drawFlag(SHAPES_ISLAND + Offset(102f, -4f))
    if (done(Island.FEELINGS)) drawFlag(FEELINGS_ISLAND + Offset(84f, -30f))
    // Low, beside the book: the island is so high that a flag at its back would go out of the screen.
    if (done(Island.WORDS)) drawFlag(WORDS_ISLAND + Offset(50f, 14f))
}

/**
 * The button to sail to the other part of the sea (ADR 0043): white and round, with a wave, a little house and an arrow
 * towards there, or back, the arrow the other way, from the other part.
 */
private fun DrawScope.drawSailButton(center: Offset, radius: Float, back: Boolean) {
    circle(Shadow, center + Offset(0f, 4f), radius)
    circle(Color.White, center, radius)
    val ink = Color(0xFF2C74D8)
    val way = if (back) -1f else 1f
    val arrowY = if (back) -radius * 0.08f else radius * 0.14f
    val tip = center + Offset(way * radius * 0.62f, arrowY)
    line(ink, center + Offset(-way * radius * 0.55f, arrowY), tip, radius * 0.14f)
    line(ink, tip, tip + Offset(-way * radius * 0.26f, -radius * 0.26f), radius * 0.14f)
    line(ink, tip, tip + Offset(-way * radius * 0.26f, radius * 0.26f), radius * 0.14f)
    stroke(
        path {
            moveTo(center.x - radius * 0.6f, center.y + radius * 0.45f)
            quadraticTo(center.x - radius * 0.3f, center.y + radius * 0.25f, center.x, center.y + radius * 0.45f)
            quadraticTo(center.x + radius * 0.3f, center.y + radius * 0.65f, center.x + radius * 0.6f, center.y + radius * 0.45f)
        },
        Color(0xFF5FA3F0),
        radius * 0.1f,
    )
    if (!back) drawHouseSign(center + Offset(-radius * 0.12f, -radius * 0.34f), radius * 0.62f)
}

private fun inIsland(
    point: Offset,
    island: Offset,
    rx: Float = ISLAND_RX,
    ry: Float = ISLAND_RY,
): Boolean {
    val dx = (point.x - island.x) / (rx + TOUCH_SLACK)
    val dy = (point.y - island.y) / (ry + 40f)
    return dx * dx + dy * dy <= 1f
}

/** Where [island] is on the map. */
private fun islandCenter(island: Island): Offset = when (island) {
    Island.VOWELS -> VOWELS_ISLAND
    Island.NUMBERS -> NUMBERS_ISLAND
    Island.COLORS -> COLORS_ISLAND
    Island.SHAPES -> SHAPES_ISLAND
    Island.FEELINGS -> FEELINGS_ISLAND
    Island.WORDS -> WORDS_ISLAND
    Island.HOME -> HOME_ISLAND
}

/**
 * Two soft ripples round the island Bruno suggests (ADR 0035), that grow and fade slowly, one after the other: they show
 * the way, but nothing jumps or calls, so that any other island is as good.
 */
private fun DrawScope.drawSuggestion(island: Island, t: Float) {
    val center = islandCenter(island) + Offset(0f, 6f)
    val words = island == Island.WORDS
    for (k in 0 until 2) {
        val phase = (t / SUGGESTION_SECONDS + k / 2f) % 1f
        val grow = 1f + 0.14f * phase
        val rx = ((if (words) WORDS_RX else ISLAND_RX) + 20f) * grow
        val ry = ((if (words) WORDS_RY else ISLAND_RY) + 16f) * grow
        drawOval(
            Color.White,
            topLeft = Offset(center.x - rx, center.y - ry),
            size = Size(rx * 2, ry * 2),
            alpha = 0.9f * (1f - phase),
            style = Stroke(6f),
        )
    }
}

private const val SUGGESTION_SECONDS = 2.4f

private fun DrawScope.drawIsland(
    center: Offset,
    t: Float,
    rx: Float = ISLAND_RX,
    ry: Float = ISLAND_RY,
) {
    ellipse(Color(0x33FFFFFF), center + Offset(0f, 8f), rx + 14f + 3f * sin(t * 1.4f), ry + 10f)
    ellipse(Color(0xFFF6DFA6), center, rx, ry)
    ellipse(Color(0xFF93D46E), center + Offset(0f, -8f), rx - 20f, ry - 16f)
}

/**
 * The rocks of the island of letters: the vowels in a row, and the consonants in front, as they come in; without the
 * ones the grown-ups took out, [hidden] (ADR 0041).
 */
private fun DrawScope.drawVowelRocks(
    center: Offset,
    hidden: Set<Char>,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val steps = listOf(VOWELS, CONSONANTS, NEXT_CONSONANTS, LAST_CONSONANTS)
    drawRocks(center, steps.map { row -> row.filter { it !in hidden }.map { it.uppercase() } }.filter { it.isNotEmpty() }, measurer, font)
}

/** The rocks of the island of numbers: one to five in a row and six to ten in front. */
private fun DrawScope.drawNumberRocks(center: Offset, measurer: TextMeasurer, font: FontFamily) =
    drawRocks(center, listOf((1..5).map { it.toString() }, (6..10).map { it.toString() }), measurer, font)

/** Rows of white rocks on an island, each with its sign in a colour, the first row at the back; four rows go closer. */
private fun DrawScope.drawRocks(
    center: Offset,
    rows: List<List<String>>,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
    val packed = rows.size > 3
    val step = if (packed) 22f else 26f
    val across = if (packed) 28f else 31f
    val radius = if (packed) 12f else 14f
    val top = if (packed) -37f else -22f - 8f * (rows.size - 2)
    for ((row, signs) in rows.withIndex()) {
        signs.forEachIndexed { i, sign ->
            val p = center + Offset(-across / 2 * (signs.size - 1) + across * i, top + step * row)
            circle(Color.White, p, radius)
            val size = (if (sign.length > 1) 16f else 20f) * radius / 14f
            label(measurer, sign, p + Offset(0f, 1f), size, colors[(i + row * 2) % colors.size], font)
        }
    }
}

/** The faces of the island of feelings, one of each, in a row on the grass. */
private fun DrawScope.drawIslandFaces(center: Offset) {
    Feeling.entries.forEachIndexed { i, feeling ->
        drawFeelingFace(feeling, center + Offset(-64f + 32f * i, -12f + if (i % 2 == 0) 0f else 8f), 14f)
    }
}

/** The pieces of the island of shapes, one of each, standing on the grass. */
private fun DrawScope.drawIslandShapes(center: Offset) {
    Shape.entries.zip(listOf(Hue.RED, Hue.BLUE, Hue.YELLOW, Hue.GREEN)).forEachIndexed { i, (shape, hue) ->
        drawShapePiece(shape, pieceBox(shape, center + Offset(-60f + 40f * i, -18f + if (i % 2 == 0) 0f else 10f), 32f), hue.paint())
    }
}

/** The balloons of the island of colours, one of each, tied to the grass and swaying. */
private fun DrawScope.drawIslandBalloons(center: Offset, t: Float) {
    Hue.entries.forEachIndexed { i, hue ->
        val tie = center + Offset(-62f + 25f * i, -6f + if (i % 2 == 0) -8f else 4f)
        val at = tie + Offset(6f * sin(t * 1.3f + i), -48f - 10f * (i % 2))
        drawTiedBalloon(at, 34f, hue.paint(), tie)
    }
}

private fun DrawScope.drawPath(t: Float, shown: (Island) -> Boolean) {
    val dots = path {
        if (shown(Island.VOWELS) && shown(Island.NUMBERS)) {
            moveTo(VOWELS_ISLAND.x + 60f, VOWELS_ISLAND.y - 40f)
            quadraticTo(400f, 150f, NUMBERS_ISLAND.x - 90f, NUMBERS_ISLAND.y + 10f)
        }
        if (shown(Island.NUMBERS) && shown(Island.COLORS)) {
            moveTo(NUMBERS_ISLAND.x + 70f, NUMBERS_ISLAND.y + 48f)
            quadraticTo(720f, 230f, COLORS_ISLAND.x + 20f, COLORS_ISLAND.y - 62f)
        }
        if (shown(Island.VOWELS) && shown(Island.SHAPES)) {
            moveTo(VOWELS_ISLAND.x + 10f, VOWELS_ISLAND.y - 60f)
            quadraticTo(290f, 170f, SHAPES_ISLAND.x - 30f, SHAPES_ISLAND.y + 56f)
        }
        if (shown(Island.VOWELS) && shown(Island.FEELINGS)) {
            moveTo(VOWELS_ISLAND.x - 90f, VOWELS_ISLAND.y - 36f)
            quadraticTo(150f, 228f, FEELINGS_ISLAND.x + 30f, FEELINGS_ISLAND.y + 56f)
        }
        if (shown(Island.SHAPES) && shown(Island.WORDS)) {
            moveTo(SHAPES_ISLAND.x + 116f, SHAPES_ISLAND.y - 10f)
            quadraticTo(520f, 26f, WORDS_ISLAND.x - WORDS_RX - 8f, WORDS_ISLAND.y)
        }
    }
    drawPath(
        dots,
        Color.White,
        alpha = 0.85f,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            6f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(0.1f, 16f), -t * 12f),
        ),
    )
}

private fun DrawScope.drawFlag(at: Offset) {
    line(Color(0xFF8A5A3C), at, at + Offset(0f, -50f), 4f)
    fill(
        path {
            moveTo(at.x, at.y - 50f)
            lineTo(at.x + 28f, at.y - 42f)
            lineTo(at.x, at.y - 34f)
            close()
        },
        Color(0xFF2E9E5B),
    )
}

/**
 * Bruno's boat on the map, and her in it at his side, where he looks, if there is a [photo] of her (ADR 0034). Bruno looks
 * at [lookingAt], the island he suggests (ADR 0035), or ahead.
 */
private fun DrawScope.drawBoat(
    look: BrunoLook,
    at: Offset,
    t: Float,
    photo: ImageBitmap?,
    lookingAt: Offset?,
) {
    val bruno = at + Offset(if (photo != null) -14f else 0f, -36f)
    val toward = lookingAt?.let { (it - bruno) / (it - bruno).getDistance() }
    drawBruno(look, bruno, 80f, Pose(lookX = toward?.x ?: 0.6f, lookY = toward?.y ?: 0f), t)
    photo?.let { drawRoundPhoto(it, at + Offset(32f, -20f), 18f) }
    fill(
        path {
            moveTo(at.x - 62f, at.y - 10f)
            lineTo(at.x + 62f, at.y - 10f)
            lineTo(at.x + 44f, at.y + 22f)
            lineTo(
                at.x - 44f,
                at.y + 22f,
            )
            close()
        },
        Color(0xFFB5643A),
    )
    line(Color(0xFF8A4B2E), Offset(at.x - 56f, at.y - 2f), Offset(at.x + 56f, at.y - 2f), 3f)
    stroke(
        path {
            moveTo(at.x - 80f, at.y + 24f)
            quadraticTo(at.x - 70f, at.y + 16f, at.x - 60f, at.y + 24f)
        },
        Color.White,
        3f,
        alpha = 0.5f,
    )
}
