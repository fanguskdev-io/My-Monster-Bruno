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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.Choosing
import com.fanguskdev.mymonsterbruno.game.Miss
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.choices
import com.fanguskdev.mymonsterbruno.game.narrowed
import com.fanguskdev.mymonsterbruno.game.plain
import com.fanguskdev.mymonsterbruno.game.played
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.startRounds
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
import com.fanguskdev.mymonsterbruno.ui.art.drawLibrary
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStartPicture
import com.fanguskdev.mymonsterbruno.ui.art.drawSyllable
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.art.syllableWidth
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(150f, 240f)
private const val BRUNO_HEIGHT = 220f
private val BUBBLE = Offset(430f, 96f)
private const val SAID_Y = 196f
private const val SAID_HEIGHT = 44f
private const val CHOSEN_Y = 250f
private const val CHOSEN_HEIGHT = 38f
private const val SYLLABLE_GAP = 10f

/** The first syllable is gold when it is the one Bruno's word starts with, and purple when it is another. */
private val SAME = Color(0xFFF4B623)
private val OTHER = Color(0xFF9B6BD3)
private val SYLLABLE_COLORS = listOf(Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFE4483C))

private enum class WordsPhase { SAYING, ASKING, CHECKING, RIGHT, WRONG, THANKS }

/**
 * The island of words (ADR 0013): Bruno thinks of a picture and says its word clapping it, «¡PA! ¡TO!», and the voice
 * asks «¿Qué empieza como pato?». Of three pictures, she touches the one that starts the same. Bruno claps it too: if it
 * is, the two first syllables shine gold and he says «¡Sí! Pato y pajarita empiezan por pa»; if not, «Huum... Luna
 * empieza por lu», the voice asks for another one that starts with pa, and that card stays aside. Five rounds. There are
 * four cards when it is easy for her, and two when it is hard (ADR 0020).
 */
@Composable
internal fun WordsScreen(game: GameState, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { startRounds(game.random, game.data.starts) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(WordsPhase.SAYING) }

    // How many syllables of Bruno's word, and of the one she chose, have been clapped.
    var said by remember { mutableStateOf(0) }
    var chosen by remember { mutableStateOf<Int?>(null) }
    var shown by remember { mutableStateOf(0) }
    var clapAt by remember { mutableStateOf(-10f) }
    var rightAt by remember { mutableStateOf(-10f) }

    // The cards she chose that do not start the same: they stay aside until the next round.
    var aside by remember { mutableStateOf(emptySet<Int>()) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    // The cards of this round, as many as she chooses from now, and whether she has chosen one that does not start the same.
    fun dealt(n: Int) = rounds[n].choices.narrowed(rounds[n].answer, game.data.choices(Choosing.CARDS))
    var cards by remember { mutableStateOf(dealt(0)) }
    var missed by remember { mutableStateOf(false) }

    suspend fun clap(word: StartWord, clapped: (Int) -> Unit) {
        for ((i, syllable) in word.syllables.withIndex()) {
            clapped(i + 1)
            clapAt = clock.value
            audio.sfx(Sfx.CLAP)
            audio.say(Voice.syllable(syllable))
        }
    }

    suspend fun sayIt() {
        phase = WordsPhase.SAYING
        said = 0
        clap(current().said) { said = it }
        phase = WordsPhase.ASKING
        audio.say(Voice.startsLike(current().said))
    }

    LaunchedEffect(round) {
        cards = dealt(round)
        missed = false
        chosen = null
        shown = 0
        aside = emptySet()
        if (round == 0) audio.say(Voice.WORDS_INTRO)
        sayIt()
    }

    fun choose(card: Int) {
        val now = current()
        val word = cards[card]
        phase = WordsPhase.CHECKING
        chosen = card
        shown = 0
        scope.launch {
            clap(word) { shown = it }
            if (word.start == now.said.start) {
                phase = WordsPhase.RIGHT
                rightAt = clock.value
                audio.sfx(Sfx.RIGHT)
                game.update { it.copy(starts = it.starts.plusOne(plain(now.said.start))).played(Choosing.CARDS, firstTry = !missed) }
                audio.say(Voice.bothStart(now.said, word))
                delay(PAUSE_MILLIS)
                if (round + 1 < rounds.size) {
                    round++
                } else {
                    phase = WordsPhase.THANKS
                    audio.say(Voice.THANKS_STARTS)
                    onDone()
                }
            } else {
                phase = WordsPhase.WRONG
                missed = true
                audio.sfx(Sfx.BOING)
                game.missed(Miss.start(current().said.start))
                audio.say(Voice.startsWith(word), Voice.findAnother(now.said.start))
                aside = aside + card
                chosen = null
                shown = 0
                phase = WordsPhase.ASKING
            }
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    if (phase == WordsPhase.ASKING) scope.launch { sayIt() }
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase != WordsPhase.ASKING) return false
                val places = cardSpots(cards.size)
                val size = cardSize(cards.size)
                val card = cards.indices.firstOrNull { it !in aside && inCard(point, places[it], size) } ?: return false
                choose(card)
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val now = current()
        val sinceClap = t - clapAt
        drawLibrary(stage, t)
        // Clapping since his first clap: while the voice explains the game, he waits.
        val clapping = (phase == WordsPhase.SAYING && said > 0) || phase == WordsPhase.CHECKING
        val pose = when {
            clapping -> Pose(Mouth.GRIN, Eyes.HAPPY, if (sinceClap < CLAP_SECONDS) Arms.CLAP else Arms.UP)
            phase == WordsPhase.RIGHT || phase == WordsPhase.THANKS ->
                Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin((t - rightAt) * 6f).coerceAtLeast(0f))
            phase == WordsPhase.WRONG -> Pose(Mouth.WAVY, Eyes.WIDE, Arms.DOWN, lookX = 0.7f, lookY = 0.4f)
            else -> Pose(lookX = 0.7f, lookY = 0.4f, hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == WordsPhase.RIGHT || phase == WordsPhase.THANKS) drawSparkles(BRUNO_CENTER, 130f, t)
        if (phase == WordsPhase.THANKS) {
            drawCorners(stage, rounds.size, rounds.size)
            return@StageCanvas
        }

        drawThoughtBubble(BUBBLE, 96f, 70f, BRUNO_CENTER + Offset(60f, -110f))
        drawStartPicture(now.said, BUBBLE, 104f, t)
        val saying = sinceClap.takeIf { clapping && chosen == null }
        drawSyllables(stage, now.said, said, Offset(BUBBLE.x, SAID_Y), SAID_HEIGHT, SAME, saying, measurer, font)

        val places = cardSpots(cards.size)
        val size = cardSize(cards.size)
        for ((i, word) in cards.withIndex()) {
            val asking = phase == WordsPhase.ASKING && i !in aside
            val center = places[i] + Offset(0f, if (asking) bob(t, 1.5f + 0.3f * i, 3f) else 0f)
            drawPictureCard(center, size, aside = i in aside, right = phase == WordsPhase.RIGHT && chosen == i, edge = SAME) {
                drawStartPicture(word, center, cardPicture(cards.size), t)
            }
        }
        chosen?.let { card ->
            val word = cards[card]
            val first = if (word.start == now.said.start) SAME else OTHER
            val at = Offset(places[card].x, CHOSEN_Y)
            drawSyllables(stage, word, shown, at, CHOSEN_HEIGHT, first, sinceClap.takeIf { clapping }, measurer, font)
        }
        val done = round + if (phase == WordsPhase.RIGHT) 1 else 0
        drawCorners(stage, rounds.size, done)
    }
}

/** The progress dots under Bruno, and the two small buttons of the corners. */
private fun DrawScope.drawCorners(stage: Stage, rounds: Int, done: Int) {
    for (i in 0 until rounds) {
        circle(if (i < done) Color(0xFFE9B36A) else Color(0x66FFFFFF), Offset(90f + 30f * i, 382f), 10f)
    }
    drawHomeButton(stage)
    drawSpeakerButton(stage)
}

/**
 * The first [count] syllables of [word] on their cards, in a row centred on [at] but inside the screen, the first one in
 * [first]; [sinceClap], while they are being clapped, makes the last one jump.
 */
private fun DrawScope.drawSyllables(
    stage: Stage,
    word: StartWord,
    count: Int,
    at: Offset,
    height: Float,
    first: Color,
    sinceClap: Float?,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    if (count <= 0) return
    val widths = word.syllables.map { syllableWidth(it, height) }
    val width = widths.sum() + SYLLABLE_GAP * (widths.size - 1)
    var left = at.x.coerceIn(stage.left + width / 2 + SYLLABLE_GAP, stage.right - width / 2 - SYLLABLE_GAP) - width / 2
    for ((i, syllable) in word.syllables.withIndex()) {
        val center = Offset(left + widths[i] / 2, at.y)
        left += widths[i] + SYLLABLE_GAP
        if (i >= count) break
        val pop = if (sinceClap != null && i == count - 1) 1f - clamp01(sinceClap / CLAP_SECONDS) else 0f
        val color = if (i == 0) first else SYLLABLE_COLORS[(i - 1) % SYLLABLE_COLORS.size]
        drawSyllable(syllable, center, height, color, pop, measurer, font)
    }
}

private const val CLAP_SECONDS = 0.25f
private const val PAUSE_MILLIS = 700L
