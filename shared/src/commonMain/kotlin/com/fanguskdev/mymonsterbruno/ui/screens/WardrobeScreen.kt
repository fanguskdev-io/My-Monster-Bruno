package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Accessory
import com.fanguskdev.mymonsterbruno.game.BrunoColor
import com.fanguskdev.mymonsterbruno.game.Topping
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawAccessoryIcon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawWardrobe
import com.fanguskdev.mymonsterbruno.ui.art.palette
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import kotlin.math.sin

/** Bruno, big, at the left; his wardrobe at the right, clear of the speaker button and of the Dynamic Island. */
private val BRUNO_CENTER = Offset(250f, 236f)
private const val BRUNO_HEIGHT = 220f
private val WARDROBE = Rect(472f, 78f, 768f, 372f)

/** Where his presents are kept, two rows of three; then his colours and what he can have on his head. */
private val PRESENT_SPOTS = listOf(128f, 204f).flatMap { y -> listOf(532f, 620f, 708f).map { x -> Offset(x, y) } }
private const val PRESENT_RADIUS = 32f
private val COLOR_SPOTS = BrunoColor.entries.indices.map { Offset(524f + 64f * it, 274f) }
private val TOP_SPOTS = Topping.entries.indices.map { Offset(556f + 64f * it, 336f) }
private const val CHOICE_RADIUS = 24f
private val SHELVES = listOf(166f, 242f, 306f)

private val GOLD = Color(0xFFF4B623)

/**
 * Bruno's wardrobe, from the room (ADR 0032): she chooses which of his presents he wears, one of each place (head, face
 * and neck) or none, touching them in the wardrobe; and changes his colour or what he has on his head, as when he hatched.
 * Bruno is pleased with each change. The home button goes back to the room.
 */
@Composable
internal fun WardrobeScreen(game: GameState) {
    val clock = rememberClock()
    val audio = game.audio
    var changedAt by remember { mutableStateOf(-10f) }

    LaunchedEffect(Unit) { audio.say(Voice.WARDROBE_INTRO) }

    /** The presents he has, in the order he got them. */
    fun presents(): List<Accessory> = game.data.look.accessories.distinct()

    fun changed() {
        changedAt = clock.value
        audio.sfx(Sfx.POP)
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    audio.speak(Voice.WARDROBE_INTRO)
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                val present = presents().withIndex().firstOrNull { hits(point, PRESENT_SPOTS[it.index], PRESENT_RADIUS) }?.value
                if (present != null) {
                    game.update { it.copy(look = it.look.toggled(present)) }
                    changed()
                    audio.speak(if (present in game.data.look.wearing) Voice.LOVE_IT else Voice.TAKE_OFF)
                    return true
                }
                val color = COLOR_SPOTS.indices.firstOrNull { hits(point, COLOR_SPOTS[it], CHOICE_RADIUS) }
                if (color != null) {
                    game.update { it.copy(look = it.look.copy(color = BrunoColor.entries[color])) }
                    changed()
                    audio.speak(Voice.HANDSOME)
                    return true
                }
                val top = TOP_SPOTS.indices.firstOrNull { hits(point, TOP_SPOTS[it], CHOICE_RADIUS) } ?: return false
                game.update { it.copy(look = it.look.copy(top = Topping.entries[top])) }
                changed()
                audio.speak(Voice.HANDSOME)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val look = game.data.look
        drawRoom(stage, t, game.weather)
        drawWardrobe(WARDROBE, SHELVES)
        for ((i, present) in presents().withIndex()) {
            val spot = PRESENT_SPOTS[i]
            val worn = present in look.wearing
            circle(Shadow, spot + Offset(0f, 4f), PRESENT_RADIUS)
            circle(if (worn) Color(0xFFFFF1C2) else Color.White, spot, PRESENT_RADIUS)
            if (worn) ring(GOLD, spot, PRESENT_RADIUS, 4f)
            drawAccessoryIcon(present, spot, PRESENT_RADIUS * 1.5f)
        }
        for ((i, color) in BrunoColor.entries.withIndex()) {
            val spot = COLOR_SPOTS[i]
            circle(Shadow, spot + Offset(0f, 3f), CHOICE_RADIUS)
            circle(color.palette().body, spot, CHOICE_RADIUS)
            circle(color.palette().belly, spot + Offset(0f, 7f), CHOICE_RADIUS * 0.45f)
            if (color == look.color) ring(Color.White, spot, CHOICE_RADIUS + 5f, 4f)
        }
        for ((i, top) in Topping.entries.withIndex()) {
            val spot = TOP_SPOTS[i]
            circle(Shadow, spot + Offset(0f, 3f), CHOICE_RADIUS)
            circle(Color.White, spot, CHOICE_RADIUS)
            scale(CHOICE_RADIUS / HATCH_CHOICE_RADIUS, spot) { drawToppingIcon(top, spot) }
            if (top == look.top) ring(GOLD, spot, CHOICE_RADIUS + 5f, 4f)
        }
        val since = t - changedAt
        val pose = if (since < HAPPY_SECONDS) {
            Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 7f).coerceAtLeast(0f))
        } else {
            Pose(lookX = 1f, lookY = -0.2f, hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (since < HAPPY_SECONDS) drawSparkles(BRUNO_CENTER, 110f, t)
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** How big the buttons of what he has on his head are when he hatches, which their icons are drawn for. */
private const val HATCH_CHOICE_RADIUS = 48f
private const val HAPPY_SECONDS = 1.2f
