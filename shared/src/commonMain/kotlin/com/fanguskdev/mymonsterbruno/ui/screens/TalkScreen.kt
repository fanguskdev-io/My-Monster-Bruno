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
import androidx.compose.ui.text.rememberTextMeasurer
import com.fanguskdev.mymonsterbruno.audio.Microphone
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.audio.WordCheck
import com.fanguskdev.mymonsterbruno.audio.higher
import com.fanguskdev.mymonsterbruno.audio.normalized
import com.fanguskdev.mymonsterbruno.audio.saysWord
import com.fanguskdev.mymonsterbruno.audio.trimmed
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.game.talkRounds
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.clapIcon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawSyllable
import com.fanguskdev.mymonsterbruno.ui.art.drawTalkPicture
import com.fanguskdev.mymonsterbruno.ui.art.drawThoughtBubble
import com.fanguskdev.mymonsterbruno.ui.art.drawTwoOfUs
import com.fanguskdev.mymonsterbruno.ui.art.micIcon
import com.fanguskdev.mymonsterbruno.ui.art.syllableWidth
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.clamp01
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.ring
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(300f, 236f)
private const val BRUNO_HEIGHT = 230f
private val BUBBLE = Offset(604f, 104f)
private const val SYLLABLES_Y = 216f
private const val SYLLABLE_HEIGHT = 56f
private const val SYLLABLE_GAP = 12f
private const val CLAP_CARD_HEIGHT = 46f
private val MIC = Offset(604f, 322f)
private const val MIC_RADIUS = 56f
private val SYLLABLE_COLORS = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF28B2E))

private enum class TalkPhase { ASKING, LISTENING, HEARING, ECHO, CLAPPING, COUNTED, HER_CLAPS, THANKS }

/**
 * «Habla con Bruno» (ADR 0011): Bruno shows a picture and asks for its word, «¡Dime mariposa!». She keeps her finger on the
 * big microphone while she says it, and Bruno says it back with her own voice, higher, a monster's. Then he claps it, a
 * clap for each syllable, MA-RI-PO-SA, and the voice counts the claps. Four words, the fewest claps first. Her voice is
 * played once and forgotten: nothing of it is saved nor leaves the device. Without a microphone, or if he does not hear
 * her twice, Bruno says the word himself and claps it all the same. Where the device can make out words by itself
 * ([check], ADR 0015), Bruno knows when she said another one: «Huum... Eso no es manzana. ¿Me dices manzana?», and the
 * second time he says it himself, so that she is never stuck. Last, a turn for two (ADR 0036): a grown-up says a word
 * aloud, any word, and she claps it on the big hands; the voice counts her claps, and nobody says if they are right but
 * the grown-up.
 */
@Composable
internal fun TalkScreen(
    game: GameState,
    microphone: Microphone,
    check: WordCheck,
    onDone: () -> Unit,
) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    val rounds = remember { talkRounds(game.random, game.data.talked) }
    var round by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(TalkPhase.ASKING) }

    // Whether it may listen: null until the platform has asked, which it does the first time.
    var allowed by remember { mutableStateOf<Boolean?>(null) }

    // Whether the device can tell which word she said, and how many times this round it was another one.
    var checking by remember { mutableStateOf(false) }
    var wrong by remember { mutableStateOf(0) }
    var missed by remember { mutableStateOf(false) }
    var listeningSince by remember { mutableStateOf(0f) }
    var clapped by remember { mutableStateOf(0) }
    var clapAt by remember { mutableStateOf(-10f) }
    var countedAt by remember { mutableStateOf(-10f) }

    // The turn for two, after the words (ADR 0036): how many claps she has given.
    val together = round == rounds.size
    var herClaps by remember { mutableStateOf(0) }

    fun current() = rounds[round.coerceAtMost(rounds.lastIndex)]

    LaunchedEffect(round) {
        missed = false
        wrong = 0
        clapped = 0
        phase = TalkPhase.ASKING
        if (allowed == null) {
            val listens = microphone.allowed()
            checking = listens && check.allowed()
            allowed = listens
        }
        when {
            round == 0 -> audio.say(Voice.TALK, Voice.tellMe(current()), Voice.HOLD_MIC)
            together -> {
                phase = TalkPhase.HER_CLAPS
                audio.say(Voice.WITH_A_GROWN_UP, Voice.CLAP_THE_HANDS)
            }
            else -> audio.say(Voice.tellMe(current()))
        }
    }

    // When she stops clapping for a moment, or has given the most claps, the voice counts them and the turn ends. Each
    // clap starts the wait again; the phase is not a key, so that changing it here does not stop the count.
    LaunchedEffect(herClaps) {
        if (phase != TalkPhase.HER_CLAPS || herClaps == 0) return@LaunchedEffect
        if (herClaps < Voice.MOST_CLAPS) delay(CLAPS_DONE_MILLIS)
        phase = TalkPhase.COUNTED
        countedAt = clock.value
        audio.sfx(Sfx.SPARKLE)
        audio.say(Voice.claps(herClaps), Voice.PLAYING_TOGETHER)
        delay(PAUSE_MILLIS)
        phase = TalkPhase.THANKS
        onDone()
    }

    suspend fun clap(word: TalkWord) {
        phase = TalkPhase.CLAPPING
        for ((i, syllable) in word.syllables.withIndex()) {
            clapped = i + 1
            clapAt = clock.value
            audio.sfx(Sfx.CLAP)
            audio.say(Voice.syllable(syllable))
        }
        phase = TalkPhase.COUNTED
        countedAt = clock.value
        audio.sfx(Sfx.SPARKLE)
        game.update { it.copy(talked = it.talked.plusOne(word.word)) }
        audio.say(Voice.claps(word.syllables.size))
        delay(PAUSE_MILLIS)
        // After the last word, Bruno says thanks, and then comes the turn for two.
        if (round + 1 == rounds.size) audio.say(Voice.THANKS_WORDS)
        round++
    }

    // She let go of the button: Bruno says back what he heard, or the word himself, and claps it.
    fun heard() {
        phase = TalkPhase.HEARING
        val word = current()
        scope.launch {
            val voice = if (allowed == true) microphone.stop()?.trimmed()?.takeIf { it.millis >= MIN_VOICE_MILLIS } else null
            when {
                voice != null -> {
                    phase = TalkPhase.ECHO
                    // Bruno says back what he heard while the device makes out which word it was.
                    val guesses = async { if (checking) check.heard(voice, word.word) else null }
                    audio.echo(voice.normalized().higher(MONSTER_PITCH))
                    val said = guesses.await()?.let { saysWord(it, word.word) } ?: true
                    if (!said && wrong == 0) {
                        wrong++
                        phase = TalkPhase.ASKING
                        audio.say(Voice.notThat(word))
                        return@launch
                    }
                    if (!said) audio.say(Voice.word(word))
                }
                allowed == true && !missed -> {
                    missed = true
                    phase = TalkPhase.ASKING
                    audio.say(Voice.CANT_HEAR, Voice.HOLD_MIC)
                    return@launch
                }
                else -> {
                    phase = TalkPhase.ECHO
                    audio.say(Voice.word(word))
                }
            }
            clap(word)
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (hits(point, speakerButton(stage), CORNER_RADIUS)) {
                    when {
                        phase == TalkPhase.HER_CLAPS && herClaps == 0 -> audio.speak(Voice.WITH_A_GROWN_UP, Voice.CLAP_THE_HANDS)
                        phase == TalkPhase.ASKING -> audio.speak(Voice.tellMe(current()), Voice.HOLD_MIC)
                    }
                    return true
                }
                if (hits(point, homeButton(stage), CORNER_RADIUS)) {
                    audio.hush()
                    game.back()
                    return true
                }
                if (phase == TalkPhase.HER_CLAPS && hits(point, MIC, MIC_RADIUS)) {
                    if (herClaps < Voice.MOST_CLAPS) {
                        if (herClaps == 0) audio.hush()
                        herClaps++
                        clapAt = clock.value
                        audio.sfx(Sfx.CLAP)
                    }
                    return true
                }
                if (phase != TalkPhase.ASKING || allowed == null || !hits(point, MIC, MIC_RADIUS)) return false
                // Silence while she speaks, so that the microphone hears only her.
                audio.hush()
                phase = TalkPhase.LISTENING
                listeningSince = clock.value
                if (allowed == true) microphone.start()
                return true
            }

            override fun up(point: Offset) = letGo()

            override fun cancel() = letGo()

            private fun letGo() {
                if (phase == TalkPhase.LISTENING) heard()
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        val word = current()
        val sinceClap = t - clapAt
        drawRoom(stage, t, game.weather)
        val pose = when (phase) {
            TalkPhase.LISTENING -> Pose(Mouth.SMILE, Eyes.WIDE, Arms.DOWN, lookX = 0.8f, lookY = 0.5f)
            TalkPhase.ECHO -> {
                val mouth = if (sin(t * 16f) > 0f) Mouth.O else Mouth.OPEN
                Pose(mouth, Eyes.HAPPY, Arms.UP, hop = 4f * sin(t * 8f).coerceAtLeast(0f))
            }
            TalkPhase.CLAPPING -> Pose(Mouth.GRIN, Eyes.HAPPY, if (sinceClap < CLAP_SECONDS) Arms.CLAP else Arms.UP)
            TalkPhase.HER_CLAPS ->
                if (sinceClap < CLAP_SECONDS) Pose(Mouth.GRIN, Eyes.HAPPY, Arms.CLAP) else Pose(lookX = 0.7f, hop = 1.5f * sin(t * 1.6f))
            TalkPhase.COUNTED, TalkPhase.THANKS ->
                Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 8f * sin((t - countedAt) * 6f).coerceAtLeast(0f))
            else -> Pose(lookX = 0.6f, lookY = -0.2f, hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        if (phase == TalkPhase.COUNTED || phase == TalkPhase.THANKS) drawSparkles(BRUNO_CENTER, 140f, t)
        if (phase != TalkPhase.THANKS) {
            drawThoughtBubble(BUBBLE, 100f, 74f, BRUNO_CENTER + Offset(70f, -120f))
            if (together) drawTwoOfUs(BUBBLE, 130f) else drawTalkPicture(word, BUBBLE, 110f, t)
        }

        // The syllables, one more with each clap; in the turn for two, a card with its number for each of her claps.
        val cards = if (together) (1..herClaps).map { it.toString() } else word.syllables
        val shown = if (together) herClaps else clapped
        // Her six claps fit smaller cards.
        val tall = if (together) CLAP_CARD_HEIGHT else SYLLABLE_HEIGHT
        val widths = cards.map { syllableWidth(it, tall) }
        var left = BUBBLE.x - (widths.sum() + SYLLABLE_GAP * (widths.size - 1)) / 2
        for ((i, syllable) in cards.withIndex()) {
            val center = Offset(left + widths[i] / 2, SYLLABLES_Y)
            left += widths[i] + SYLLABLE_GAP
            if (i >= shown) continue
            val pop = if (i == shown - 1) 1f - clamp01(sinceClap / CLAP_SECONDS) else 0f
            val lift = when {
                phase != TalkPhase.COUNTED -> 0f
                i % 2 == 0 -> bob(t, 6f, 4f)
                else -> -bob(t, 6f, 4f)
            }
            val color = SYLLABLE_COLORS[i % SYLLABLE_COLORS.size]
            drawSyllable(syllable, center + Offset(0f, lift), tall, color, pop, measurer, font)
        }

        val listening = phase == TalkPhase.LISTENING
        if (listening) {
            // Waves round the button while it listens.
            for (k in 0 until 3) {
                val wave = ((t - listeningSince) * 1.4f + k / 3f) % 1f
                ring(Color.White, MIC, MIC_RADIUS * (1.05f + 0.5f * wave), 5f, alpha = 0.8f * (1f - wave))
            }
        } else if (phase == TalkPhase.ASKING && allowed != null) {
            drawCallRing(MIC, MIC_RADIUS + 4f, t)
        }
        if (together) {
            // Her hands, where the microphone was: they jump a little with each clap.
            val clapping = phase == TalkPhase.HER_CLAPS
            if (clapping && herClaps == 0) drawCallRing(MIC, MIC_RADIUS + 4f, t)
            val pop = if (sinceClap < CLAP_SECONDS) 1.1f else 1f
            drawRoundButton(MIC, MIC_RADIUS * pop, if (clapping) Color(0xFFF4B623) else Color(0xFFF2D9A0)) { clapIcon(MIC, 50f * pop) }
        } else {
            val ready = phase == TalkPhase.ASKING || listening
            drawRoundButton(MIC, MIC_RADIUS * if (listening) 1.08f else 1f, if (ready) Color(0xFFE85A7A) else Color(0xFFE6B3C0)) {
                micIcon(MIC, if (listening) 52f else 48f)
            }
        }

        // A dot for each word and one more, a different colour, for the turn for two.
        for (i in 0..rounds.size) {
            val done = i < round || (i == round && phase == TalkPhase.COUNTED) || phase == TalkPhase.THANKS
            val colour = when {
                !done -> Color(0x66FFFFFF)
                i == rounds.size -> Color(0xFFF4B623)
                else -> Color(0xFFE9B36A)
            }
            circle(colour, Offset(325f + 30f * i, 382f), 10f)
        }
        drawHomeButton(stage)
        drawSpeakerButton(stage)
    }
}

/** How much higher and quicker her voice is when Bruno says it back: a monster's voice, as his own lines. */
private const val MONSTER_PITCH = 1.35f

/** Shorter than this, what the microphone heard was a knock or a breath, not a word. */
private const val MIN_VOICE_MILLIS = 300L
private const val CLAP_SECONDS = 0.25f
private const val PAUSE_MILLIS = 700L

/** How long she has to stop clapping, in the turn for two, for the voice to count her claps. */
private const val CLAPS_DONE_MILLIS = 2_000L
