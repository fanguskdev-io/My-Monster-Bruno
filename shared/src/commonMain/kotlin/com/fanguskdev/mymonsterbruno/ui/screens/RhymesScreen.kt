package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.narrowed
import com.fanguskdev.mymonsterbruno.game.played
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.rhymeRounds
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawLibrary
import com.fanguskdev.mymonsterbruno.ui.art.drawRhymePicture
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(150f, 240f)
private const val BRUNO_HEIGHT = 220f
private val BUBBLE = Offset(430f, 110f)
private const val BUBBLE_PICTURE = 110f
private const val PAIR_PICTURE = 84f
private val GOLD = Color(0xFFF4B623)

private enum class RhymesPhase { SAYING, ASKING, RIGHT, WRONG, THANKS }

/**
 * The rhymes, the second thing of the island of words (ADR 0021): Bruno thinks of a picture and asks «¡Gato! ¿Qué rima
 * con gato?», and of the cards she touches the one that ends the same. If it is, the voice says «¡Sí! Gato y pato
 * riman», the two go together in his bubble and Bruno makes a silly rhyme, «¡Un gato con zapato!», and laughs. If not,
 * «¿Rana? Huum... No termina igual», that card stays aside and he asks again. After each question the voice names the
 * cards, one by one. Five rounds, of the pairs she has found least; the cards are two, three or four, as the island of
 * words (ADR 0020).
 */
@Composable
internal fun RhymesScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val rounds = remember { rhymeRounds(game.random, game.data.rhymes) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(RhymesPhase.SAYING) }
    var phaseAt by remember { mutableStateOf(0f) }
    var chosen by remember { mutableStateOf<Int?>(null) }

    // The card the voice is naming: it jumps while it is said.
    var naming by remember { mutableStateOf<Int?>(null) }

    // The cards she chose that do not rhyme: they stay aside until the next round.
    var aside by remember { mutableStateOf(emptySet<Int>()) }

    // The cards of this round, as many as she chooses from now, and whether she has chosen one that does not rhyme.
    fun dealt(n: Int) = rounds[n].choices.narrowed(rounds[n].answer, game.data.choices(Choosing.RHYMES))
    var cards by remember { mutableStateOf(dealt(0)) }
    var missed by remember { mutableStateOf(false) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    suspend fun ask() {
        phase = RhymesPhase.SAYING
        phaseAt = clock.value
        audio.say(Voice.whatRhymes(current().said))
        // The voice names each card: she plays with the words she hears, whatever she calls the pictures (a rocket, a nave).
        for (i in cards.indices) {
            if (i in aside) continue
            naming = i
            audio.say(Voice.card(cards[i]))
        }
        naming = null
        phase = RhymesPhase.ASKING
        phaseAt = clock.value
    }

    LaunchedEffect(round) {
        cards = dealt(round)
        missed = false
        chosen = null
        aside = emptySet()
        if (round == 0) audio.say(Voice.RHYMES_INTRO)
        ask()
    }

    fun choose(card: Int) {
        val now = current()
        val word = cards[card]
        chosen = card
        phaseAt = clock.value
        if (word == now.answer) {
            phase = RhymesPhase.RIGHT
            audio.sfx(Sfx.RIGHT)
            game.update { it.copy(rhymes = it.rhymes.plusOne(now.said.pair)).played(Choosing.RHYMES, firstTry = !missed) }
            scope.launch {
                audio.say(Voice.rhyme(now.said), Voice.sillyRhyme(now.said), Voice.GIGGLE)
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = RhymesPhase.THANKS
                    phaseAt = clock.value
                    audio.sfx(Sfx.SPARKLE)
                    audio.say(Voice.THANKS_RHYMES)
                    onDone()
                }
            }
        } else {
            phase = RhymesPhase.WRONG
            missed = true
            audio.sfx(Sfx.BOING)
            game.missed(Miss.rhyme(current().said.pair))
            scope.launch {
                audio.say(Voice.noRhyme(word))
                aside = aside + card
                chosen = null
                ask()
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == RhymesPhase.ASKING) scope.launch { ask() }
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != RhymesPhase.ASKING) return false
                val places = cardSpots(cards.size)
                val size = cardSize(cards.size)
                val card = cards.indices.firstOrNull { it !in aside && inCard(point, places[it], size) } ?: return false
                audio.sfx(Sfx.TAP)
                choose(card)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val since = t - phaseAt
        val now = current()
        drawLibrary(stage, t)
        val pose = when (phase) {
            RhymesPhase.SAYING -> Pose(Mouth.OPEN, Eyes.OPEN, Arms.UP, lookX = 0.5f, lookY = -0.4f, hop = 2f * sin(t * 4f))
            RhymesPhase.ASKING -> Pose(lookX = 0.7f, lookY = 0.4f, hop = 1.5f * sin(t * 1.6f))
            RhymesPhase.RIGHT, RhymesPhase.THANKS -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin(since * 6f).coerceAtLeast(0f))
            RhymesPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.7f, lookY = 0.4f)
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == RhymesPhase.RIGHT || phase == RhymesPhase.THANKS) drawSparkles(BRUNO_CENTER, 130f, t)
        if (phase != RhymesPhase.THANKS) {
            drawThoughtBubble(BUBBLE, 96f, 70f, BRUNO_CENTER + Offset(60f, -110f))
            if (phase == RhymesPhase.RIGHT) {
                // The two words that rhyme, together in his bubble, jumping in turns.
                drawRhymePicture(now.said, BUBBLE + Offset(-44f, bob(t, 6f, 4f)), PAIR_PICTURE, t)
                drawRhymePicture(now.answer, BUBBLE + Offset(44f, -bob(t, 6f, 4f)), PAIR_PICTURE, t)
            } else {
                drawRhymePicture(now.said, BUBBLE, BUBBLE_PICTURE, t)
            }
            val places = cardSpots(cards.size)
            val size = cardSize(cards.size)
            for ((i, word) in cards.withIndex()) {
                val asking = phase == RhymesPhase.ASKING && i !in aside
                val lift = if (naming == i) {
                    -14f
                } else if (asking) {
                    bob(t, 1.5f + 0.3f * i, 3f)
                } else {
                    0f
                }
                val center = places[i] + Offset(0f, lift)
                drawPictureCard(center, size, aside = i in aside, right = phase == RhymesPhase.RIGHT && chosen == i, edge = GOLD) {
                    drawRhymePicture(word, center, cardPicture(cards.size), t)
                }
            }
        }
        val done = round + if (phase == RhymesPhase.RIGHT || phase == RhymesPhase.THANKS) 1 else 0
        for (i in rounds.indices) {
            circle(if (i < done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(90f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

private const val PAUSE_MILLIS = 700L
