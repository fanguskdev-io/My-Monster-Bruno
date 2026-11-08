package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.feltOn
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBench
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawBrunoToken
import com.fanguskdev.mymonsterbruno.ui.art.drawHearts
import com.fanguskdev.mymonsterbruno.ui.art.drawJar
import com.fanguskdev.mymonsterbruno.ui.art.drawPortrait
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.easeOut
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.math.sin

private val BRUNO_CENTER = Offset(150f, 262f)
private const val BRUNO_HEIGHT = 180f
private val CARD_HOME = Offset(530f, 106f)
private const val CARD_SIZE = 96f
private const val CARD_TOUCH = 60f
private const val TOKEN = 36f

/**
 * The five jars stand on a bench, joy first and calm last, as in the corner of feelings of the classroom; the last one
 * clear of the Dynamic Island of her iPhone, held with the island on the right.
 */
private const val BENCH_TOP = 316f
private val JARS = Feeling.entries.indices.map { Offset(300f + 104f * it, BENCH_TOP) }
private const val JAR_WIDTH = 84f
private const val JAR_HEIGHT = 104f

/** How far from a jar her photo still drops into it. */
private const val REACH = 26f

private enum class MoodPhase { ASKING, PUTTING, ANSWERING, BRUNO, DONE }

/**
 * The feeling of the day (ADR 0019), as in the assembly of her classroom: the first time she comes to the room each day,
 * Bruno says hello and asks «¿Cómo te sientes hoy?». She takes her photo (or a drawn face, if there is none) to one of
 * five jars: joy, sadness, anger, fear and calm; touching a jar says its name. Bruno answers kindly («Vaya... Te doy un
 * abrazo muy grande») and puts his own in the jar of joy, because she has come to play. The house skips it.
 */
@Composable
internal fun MoodScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val photo = remember(game.photo) { game.photo?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() } }
    val take = if (photo != null) Voice.TAKE_YOUR_PHOTO else Voice.TAKE_THE_FACE
    var phase by remember { mutableStateOf(MoodPhase.ASKING) }
    var phaseAt by remember { mutableStateOf(0f) }
    var chosen by remember { mutableStateOf<Feeling?>(null) }

    // Her photo: where it is, whether her finger has it, and where it was let go to fly back from.
    var card by remember { mutableStateOf(CARD_HOME) }
    var dragging by remember { mutableStateOf(false) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var returning by remember { mutableStateOf<Pair<Offset, Float>?>(null) }

    // The jar she last touched, and when: it wiggles while the voice says its name.
    var touched by remember { mutableStateOf<Pair<Int, Float>?>(null) }

    LaunchedEffect(Unit) {
        game.greeted = true
        phaseAt = clock.value
        audio.say(Voice.HOW_DO_YOU_FEEL, take)
    }

    fun jarBox(i: Int) = Rect(JARS[i].x - JAR_WIDTH / 2, JARS[i].y - JAR_HEIGHT - 16f, JARS[i].x + JAR_WIDTH / 2, JARS[i].y)

    fun jarAt(point: Offset): Int? = JARS.indices.firstOrNull { jarBox(it).inflate(REACH).contains(point) }

    fun put(i: Int) {
        val feeling = Feeling.entries[i]
        chosen = feeling
        dragging = false
        phase = MoodPhase.PUTTING
        phaseAt = clock.value
        audio.hush()
        audio.sfx(Sfx.POP)
        game.update { it.feltOn(game.currentDay(), feeling) }
        scope.launch {
            delay(PUT_MILLIS)
            phase = MoodPhase.ANSWERING
            phaseAt = clock.value
            audio.sfx(Sfx.SPARKLE)
            audio.say(Voice.today(feeling))
            phase = MoodPhase.BRUNO
            phaseAt = clock.value
            audio.sfx(Sfx.WHOOSH)
            delay(FLY_MILLIS)
            audio.say(Voice.ME_TOO)
            phase = MoodPhase.DONE
            phaseAt = clock.value
            delay(GOODBYE_MILLIS)
            onDone()
        }
    }

    fun letGo() {
        dragging = false
        returning = card to clock.value
        card = CARD_HOME
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == MoodPhase.ASKING) audio.speak(Voice.HOW_DO_YOU_FEEL, take)
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != MoodPhase.ASKING) return false
                if (distance(point, card) <= CARD_TOUCH) {
                    dragging = true
                    returning = null
                    grab = card - point
                    audio.sfx(Sfx.TAP)
                    return true
                }
                val jar = JARS.indices.firstOrNull { jarBox(it).contains(point) } ?: return false
                touched = jar to clock.value
                audio.sfx(Sfx.TAP)
                audio.speak(Voice.jar(Feeling.entries[jar]))
                return true
            }

            override fun move(point: Offset) {
                if (dragging) card = point + grab
            }

            override fun up(point: Offset) {
                if (!dragging) return
                val jar = jarAt(card)
                if (jar != null) put(jar) else letGo()
            }

            override fun cancel() {
                if (dragging) letGo()
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        drawRoom(stage, t, game.weather)
        drawBench(JARS.first().x - JAR_WIDTH, JARS.last().x + JAR_WIDTH * 0.75f, BENCH_TOP)
        val over = if (dragging) jarAt(card) else null
        for (i in JARS.indices) {
            val wiggle = touched?.takeIf { it.first == i }?.let { (_, at) ->
                5f * sin((t - at) * 18f) * (1f - clamp01((t - at) / WIGGLE_SECONDS))
            } ?: 0f
            rotate(wiggle, JARS[i]) { drawJar(Feeling.entries[i], JARS[i], JAR_WIDTH, JAR_HEIGHT, glow = if (over == i) 1f else 0f) }
        }

        // In the jar she chose, her photo; and in the jar of joy, Bruno's.
        val feeling = chosen
        val joy = JARS[Feeling.JOY.ordinal] + Offset(if (feeling == Feeling.JOY) 18f else 0f, -26f)
        if (feeling != null) {
            val inside = JARS[feeling.ordinal] + Offset(if (feeling == Feeling.JOY) -18f else 0f, -26f)
            if (phase == MoodPhase.PUTTING) {
                val k = easeInOut(since / PUT_SECONDS)
                drawPortrait(photo, lerp(card, inside, k), lerp(CARD_SIZE, TOKEN, k))
            } else {
                drawPortrait(photo, inside, TOKEN)
            }
        }
        val waiting = Pose(Mouth.SMILE, Eyes.OPEN, Arms.DOWN, lookX = 1f, lookY = -0.2f, hop = 1.5f * sin(t * 2f))
        val pose = when (phase) {
            MoodPhase.ASKING, MoodPhase.PUTTING -> waiting
            MoodPhase.ANSWERING -> answer(feeling, since)
            MoodPhase.BRUNO, MoodPhase.DONE -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == MoodPhase.ANSWERING) {
            when (feeling) {
                Feeling.JOY -> drawSparkles(BRUNO_CENTER + Offset(0f, -20f), 110f, t)
                else -> drawHearts(BRUNO_CENTER + Offset(0f, -50f), since)
            }
        }
        // Bruno's own, from his hand to the jar of joy, over him.
        when (phase) {
            MoodPhase.BRUNO -> {
                val k = easeInOut(since / FLY_SECONDS)
                drawBrunoToken(game.data.look, lerp(BRUNO_CENTER + Offset(60f, -30f), joy, k), lerp(56f, TOKEN, k), t)
            }
            MoodPhase.DONE -> drawBrunoToken(game.data.look, joy, TOKEN, t)
            else -> Unit
        }

        // Her photo, waiting to be taken to a jar.
        if (phase == MoodPhase.ASKING) {
            val back = returning?.let { (from, at) -> lerp(from, CARD_HOME, easeOut((t - at) / RETURN_SECONDS)) }
            val at = back ?: if (dragging) card else card + Offset(0f, bob(t, 2f, 3f))
            if (!dragging && back == null) drawCallRing(CARD_HOME, CARD_SIZE * 0.78f, t, Color(0xFF2E9E5B))
            drawPortrait(photo, at, if (dragging) CARD_SIZE * 1.08f else CARD_SIZE)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** How Bruno answers what she feels: glad with her joy, a hug for sadness or fear, calm for anger and calm. */
private fun answer(feeling: Feeling?, since: Float): Pose = when (feeling) {
    Feeling.JOY -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
    Feeling.SADNESS, Feeling.FEAR -> Pose(Mouth.SMILE, Eyes.OPEN, Arms.UP, lookX = 1f)
    else -> Pose(Mouth.SMILE, Eyes.CLOSED, Arms.DOWN)
}

private const val PUT_SECONDS = 0.5f
private const val PUT_MILLIS = 500L
private const val FLY_SECONDS = 0.8f
private const val FLY_MILLIS = 800L
private const val RETURN_SECONDS = 0.35f
private const val WIGGLE_SECONDS = 0.6f
private const val GOODBYE_MILLIS = 900L
