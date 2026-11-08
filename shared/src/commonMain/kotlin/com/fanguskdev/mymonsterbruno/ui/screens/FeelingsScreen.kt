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
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.CHOICES
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.feelingRounds
import com.fanguskdev.mymonsterbruno.game.narrowed
import com.fanguskdev.mymonsterbruno.game.played
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawFeelingFace
import com.fanguskdev.mymonsterbruno.ui.art.drawHappening
import com.fanguskdev.mymonsterbruno.ui.art.drawHearts
import com.fanguskdev.mymonsterbruno.ui.art.drawLamp
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.art.feelingPalette
import com.fanguskdev.mymonsterbruno.ui.art.paint
import com.fanguskdev.mymonsterbruno.ui.art.palette
import com.fanguskdev.mymonsterbruno.ui.art.pose
import com.fanguskdev.mymonsterbruno.ui.art.towards
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(300f, 236f)
private const val BRUNO_HEIGHT = 230f
private const val BRUNO_TOUCH = 110f
private val BUBBLE = Offset(604f, 104f)

/** Where the little faces are, as many as she chooses from (ADR 0020): four go closer, and a little smaller. */
private fun spots(count: Int): List<Offset> =
    if (count > CHOICES) row(Offset(612f, 326f), count, 95f) else row(Offset(622f, 326f), count, 110f)

private fun faceRadius(count: Int) = if (count > CHOICES) 40f else 42f
private val LAMP = Offset(120f, 318f)
private const val LAMP_TOUCH = 50f

private enum class FeelingPhase { HAPPENING, ASKING, NAMING, HELPING, HELPED, WRONG, THANKS }

/**
 * The island of feelings (ADR 0010): something happens to Bruno, «¡Oh, no! A Bruno se le ha caído el helado», and he turns
 * the colour of how he feels, as in the corner of feelings of the classroom. «¿Cómo se siente Bruno?»: she chooses one of
 * three little faces, and the voice names the feeling. Then she helps him: a hug if he is sad, breathing together if he is
 * angry, the light on if he is scared, clapping if he is happy, a caress if he is calm. Five things, the lullaby last.
 * There are four faces when it is easy for her, and two when it is hard (ADR 0020).
 */
@Composable
internal fun FeelingsScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { feelingRounds(game.random) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(FeelingPhase.HAPPENING) }
    var feelAt by remember { mutableStateOf(0f) }
    var helpedAt by remember { mutableStateOf<Float?>(null) }
    var lampOn by remember { mutableStateOf(false) }
    var holdingSince by remember { mutableStateOf<Float?>(null) }
    var holding by remember { mutableStateOf<Job?>(null) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    fun feeling() = current().happening.feeling

    // The faces of this round, as many as she chooses from now, and whether she has chosen a wrong one.
    fun shown(n: Int) = rounds[n].faces.narrowed(rounds[n].happening.feeling, game.data.choices(Choosing.FACES))
    var faces by remember { mutableStateOf(shown(0)) }
    var missed by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        faces = shown(round)
        missed = false
        helpedAt = null
        lampOn = false
        holdingSince = null
        phase = FeelingPhase.HAPPENING
        feelAt = clock.value
        audio.say(Voice.happened(current().happening))
        phase = FeelingPhase.ASKING
        audio.say(Voice.HOW_FEELS)
    }

    fun helped() {
        if (phase != FeelingPhase.HELPING) return
        phase = FeelingPhase.HELPED
        helpedAt = clock.value
        holding?.cancel()
        holdingSince = null
        audio.sfx(Sfx.SPARKLE)
        val feeling = feeling()
        game.update { it.copy(feelings = it.feelings.plusOne(feeling.word)) }
        scope.launch {
            audio.say(Voice.helped(feeling))
            delay(PAUSE_MILLIS)
            if (round + 1 < rounds.size) {
                round++
            } else {
                phase = FeelingPhase.THANKS
                audio.say(Voice.THANKS_FEELINGS)
                onDone()
            }
        }
    }

    fun choose(face: Feeling) {
        if (face == feeling()) {
            phase = FeelingPhase.NAMING
            audio.sfx(Sfx.RIGHT)
            game.update { it.played(Choosing.FACES, firstTry = !missed) }
            scope.launch {
                audio.say(Voice.feels(face))
                phase = FeelingPhase.HELPING
                audio.say(Voice.help(face))
            }
        } else {
            phase = FeelingPhase.WRONG
            missed = true
            audio.sfx(Sfx.BOING)
            scope.launch {
                audio.say(Voice.notThatFace(face))
                phase = FeelingPhase.ASKING
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    when (phase) {
                        FeelingPhase.ASKING -> audio.speak(Voice.happened(current().happening), Voice.HOW_FEELS)
                        FeelingPhase.HELPING -> audio.speak(Voice.help(feeling()))
                        else -> Unit
                    }
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                when (phase) {
                    FeelingPhase.ASKING -> {
                        val places = spots(faces.size)
                        val face = faces.indices.firstOrNull { hits(point, places[it], faceRadius(faces.size)) } ?: return false
                        audio.sfx(Sfx.TAP)
                        choose(faces[face])
                        return true
                    }
                    FeelingPhase.HELPING -> when (feeling()) {
                        Feeling.FEAR -> {
                            if (!hits(point, LAMP + Offset(0f, -100f), LAMP_TOUCH)) return false
                            lampOn = true
                            audio.sfx(Sfx.TAP)
                            helped()
                            return true
                        }
                        Feeling.ANGER -> {
                            if (!hits(point, BRUNO_CENTER, BRUNO_TOUCH)) return false
                            holdingSince = clock.value
                            holding?.cancel()
                            holding = scope.launch {
                                delay(BREATH_MILLIS)
                                helped()
                            }
                            return true
                        }
                        else -> {
                            if (!hits(point, BRUNO_CENTER, BRUNO_TOUCH)) return false
                            audio.sfx(Sfx.TAP)
                            helped()
                            return true
                        }
                    }
                    else -> return false
                }
            }

            override fun up(point: Offset) = letGo()

            override fun cancel() = letGo()

            private fun letGo() {
                if (phase == FeelingPhase.HELPING) {
                    holding?.cancel()
                    holdingSince = null
                }
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val happening = current().happening
        val feeling = happening.feeling
        drawRoom(stage, t, game.weather)
        val scared = feeling == Feeling.FEAR && !lampOn && phase != FeelingPhase.THANKS
        if (scared) {
            drawRect(
                Color(0xFF1D2B53),
                Offset(stage.left, stage.top),
                Size(stage.right - stage.left, stage.bottom - stage.top),
                alpha = 0.45f,
            )
        }
        if (feeling == Feeling.FEAR) drawLamp(LAMP, lampOn)
        val own = game.data.look.color.palette()
        val felt = feelingPalette(feeling.paint())
        val back = helpedAt
        val palette = when {
            phase == FeelingPhase.THANKS -> own
            back != null -> felt.towards(own, clamp01((t - back) / COLOUR_SECONDS))
            else -> own.towards(felt, clamp01((t - feelAt) / COLOUR_SECONDS))
        }
        val pose = when (phase) {
            FeelingPhase.HELPED, FeelingPhase.THANKS -> {
                val since = t - (back ?: t)
                if (feeling == Feeling.JOY && since < 1.4f) {
                    Pose(Mouth.GRIN, Eyes.HAPPY, Arms.CLAP)
                } else {
                    Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
                }
            }
            FeelingPhase.HAPPENING -> if (t - feelAt < COLOUR_SECONDS) Pose(Mouth.O, Eyes.WIDE, Arms.DOWN) else feeling.pose(t)
            else -> feeling.pose(t)
        }
        // Breathing with Bruno: a ring that grows while she keeps her finger on him.
        holdingSince?.let { since ->
            val k = clamp01((t - since) / (BREATH_MILLIS / 1000f))
            ring(Color.White, BRUNO_CENTER, 90f + 60f * k, 8f, alpha = 0.7f)
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t, palette)
        if (back != null) {
            when (feeling) {
                Feeling.SADNESS, Feeling.CALM -> drawHearts(BRUNO_CENTER + Offset(0f, -60f), t - back)
                else -> drawSparkles(BRUNO_CENTER + Offset(0f, -20f), 130f, t)
            }
        }
        if (phase != FeelingPhase.THANKS) {
            drawThoughtBubble(BUBBLE, 100f, 74f, BRUNO_CENTER + Offset(70f, -120f))
            drawHappening(happening, BUBBLE, 116f, t)
        }
        if (phase == FeelingPhase.ASKING || phase == FeelingPhase.WRONG || phase == FeelingPhase.NAMING) {
            val places = spots(faces.size)
            val radius = faceRadius(faces.size)
            for ((i, face) in faces.withIndex()) {
                val chosen = phase == FeelingPhase.NAMING && face == feeling
                drawFeelingFace(face, places[i] + Offset(0f, bob(t, 1.5f + 0.3f * i, 3f)), radius * if (chosen) 1.15f else 1f)
            }
        }
        if (phase == FeelingPhase.HELPING) {
            val hint = if (feeling == Feeling.FEAR) LAMP + Offset(0f, -100f) else BRUNO_CENTER
            drawCallRing(hint, if (feeling == Feeling.FEAR) 48f else 100f, t)
        }
        for (i in rounds.indices) {
            val done = i < round || phase == FeelingPhase.THANKS
            circle(if (done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(340f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** How long he takes to turn the colour of how he feels, and to come back. */
private const val COLOUR_SECONDS = 0.8f
private const val BREATH_MILLIS = 2500L
private const val PAUSE_MILLIS = 500L
