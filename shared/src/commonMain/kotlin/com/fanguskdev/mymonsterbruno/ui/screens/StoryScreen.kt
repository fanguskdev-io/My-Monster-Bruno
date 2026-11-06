package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.STORY_STEPS
import com.fanguskdev.mymonsterbruno.game.dealtSteps
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.storyRounds
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStoryPicture
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(112f, 268f)
private const val BRUNO_HEIGHT = 170f

/** The three places of the story, numbered one, two and three, and under them the pictures, out of order: clear of the window. */
private val COLUMNS = listOf(347f, 507f, 667f)
private val SLOTS = COLUMNS.map { Offset(it, 136f) }
private val CARDS = COLUMNS.map { Offset(it, 310f) }
private val CARD = Size(150f, 120f)
private const val PICTURE = 104f
private val GOLD = Color(0xFFF4B623)
private val INK = Color(0xFF1D2B53)

private enum class StoryPhase { ASKING, PLACING, WRONG, TELLING, THANKS }

/**
 * «Ordena la historia», the second thing of the island of feelings (ADR 0025): three pictures of something of every day
 * (planting a seed, brushing the teeth, making a cake, building a snowman) dealt out of order, and over them three empty
 * places numbered one, two and three. The voice asks «¿Qué pasa primero?», and she touches the picture: if it is, it
 * flies to its place, and «¿Y después?»; if it happens later, it wiggles, «Huum... Eso pasa más tarde», and the voice asks
 * again. With the three in order, the voice tells the story while each picture lights up. Two stories, those she has
 * ordered least.
 */
@Composable
internal fun StoryScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { storyRounds(game.random, game.data.stories) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(StoryPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }

    // The pictures by where they are dealt, each its step of the story; the step that goes now; the one being told.
    var dealt by remember { mutableStateOf(dealtSteps(game.random)) }
    var next by remember { mutableStateOf(0) }
    var telling by remember { mutableStateOf<Int?>(null) }

    // The picture that happens later, and when she touched it: it wiggles; and those flying to their place.
    var wrong by remember { mutableStateOf<Pair<Int, Float>?>(null) }
    val flights = remember { mutableStateMapOf<Int, Flight>() }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        dealt = dealtSteps(game.random)
        next = 0
        telling = null
        wrong = null
        flights.clear()
        phase = StoryPhase.ASKING
        phaseAt = clock.value
        if (round == 0) audio.say(Voice.STORY_INTRO)
        audio.say(Voice.askStep(0))
    }

    suspend fun tellIt() {
        val story = current()
        phase = StoryPhase.TELLING
        phaseAt = clock.value
        game.update { it.copy(stories = it.stories.plusOne(story.key)) }
        for (step in 0 until STORY_STEPS) {
            telling = step
            audio.say(Voice.storyStep(story, step))
        }
        telling = null
        audio.sfx(Sfx.SPARKLE)
        delay(PAUSE_MILLIS)
        if (round + 1 < rounds.size) {
            round++
        } else {
            phase = StoryPhase.THANKS
            phaseAt = clock.value
            audio.say(Voice.THANKS_STORIES)
            onDone()
        }
    }

    fun choose(card: Int) {
        if (dealt[card] == next) {
            phase = StoryPhase.PLACING
            flights[card] = Flight(CARDS[card], SLOTS[next], clock.value, FLY_SECONDS)
            audio.sfx(Sfx.RIGHT)
            next++
            scope.launch {
                delay(FLY_MILLIS)
                if (next < STORY_STEPS) {
                    phase = StoryPhase.ASKING
                    audio.say(Voice.askStep(next))
                } else {
                    tellIt()
                }
            }
        } else {
            phase = StoryPhase.WRONG
            wrong = card to clock.value
            audio.sfx(Sfx.BOING)
            scope.launch {
                audio.say(Voice.LATER, Voice.askStep(next))
                phase = StoryPhase.ASKING
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == StoryPhase.ASKING) audio.speak(Voice.askStep(next))
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != StoryPhase.ASKING) return false
                val card = CARDS.indices.firstOrNull { dealt[it] >= next && inCard(point, CARDS[it], CARD) } ?: return false
                audio.sfx(Sfx.TAP)
                choose(card)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        drawRoom(stage, t)
        for (i in 0 until STORY_STEPS) drawPlace(SLOTS[i], waiting = phase == StoryPhase.ASKING && i == next, t)
        val story = current()
        for ((card, step) in dealt.withIndex()) {
            val flight = flights[card]?.takeIf { !it.over(t) }
            val shake = wrong?.takeIf { it.first == card }?.let { (_, at) ->
                7f * sin((t - at) * 30f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
            } ?: 0f
            val lit = telling == step
            val at = when {
                flight != null -> flight.at(t)
                step < next -> SLOTS[step] + Offset(0f, if (lit) -8f else 0f)
                else -> CARDS[card] + Offset(shake, if (phase == StoryPhase.ASKING) bob(t, 1.5f + 0.3f * card, 3f) else 0f)
            }
            drawPictureCard(at, CARD, aside = false, right = lit, edge = GOLD) {
                drawStoryPicture(story, step, at, PICTURE, t)
            }
        }
        // The numbers go over the pictures, so that the one that rises while it is told does not cover its own.
        for (i in 0 until STORY_STEPS) {
            drawNumber(SLOTS[i], i + 1, waiting = phase == StoryPhase.ASKING && i == next, measurer, font)
        }
        val pose = when (phase) {
            StoryPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 1f, lookY = 0.4f)
            StoryPhase.TELLING -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.5f)
            StoryPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            else -> Pose(lookX = 1f, lookY = 0.3f, hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == StoryPhase.THANKS) drawSparkles(BRUNO_CENTER, 110f, t)
        for (i in rounds.indices) {
            val done = i < round || phase == StoryPhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(82f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** A place of the story: a dashed outline of a picture. The one the voice is asking about ([waiting]) glows. */
private fun DrawScope.drawPlace(center: Offset, waiting: Boolean, time: Float) {
    val topLeft = center - Offset(CARD.width / 2, CARD.height / 2)
    drawRoundRect(Color.White, topLeft, CARD, CornerRadius(18f), alpha = if (waiting) 0.55f + 0.2f * sin(time * 4f) else 0.35f)
    drawRoundRect(
        Color(0xFF8A7F99),
        topLeft,
        CARD,
        CornerRadius(18f),
        style = Stroke(3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), if (waiting) time * 20f else 0f)),
    )
}

/** The [number] of a place, one, two or three, over it: gold for the one the voice is asking about ([waiting]). */
private fun DrawScope.drawNumber(
    center: Offset,
    number: Int,
    waiting: Boolean,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val badge = center + Offset(0f, -CARD.height / 2 - 18f)
    circle(if (waiting) GOLD else Color.White, badge, 16f)
    label(measurer, number.toString(), badge + Offset(0f, 1f), 22f, if (waiting) Color.White else INK, font)
}

private const val FLY_SECONDS = 0.45f
private const val FLY_MILLIS = 450L
private const val WIGGLE_SECONDS = 0.5f
private const val PAUSE_MILLIS = 700L
