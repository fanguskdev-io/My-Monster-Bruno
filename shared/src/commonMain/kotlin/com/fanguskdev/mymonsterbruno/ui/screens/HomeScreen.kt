package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.nameLetters
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBalloon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStar
import com.fanguskdev.mymonsterbruno.ui.art.micIcon
import com.fanguskdev.mymonsterbruno.ui.art.playIcon
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.stroke
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.math.sin

private val BRUNO_AT_HOME = Offset(390f, 236f)
private const val BRUNO_HEIGHT = 230f
private val PLAY_BUTTON = Offset(680f, 290f)
private const val PLAY_RADIUS = 68f
private val CALM_BUTTON = Offset(82f, 318f)
private const val CALM_RADIUS = 46f
private val TALK_BUTTON = Offset(82f, 200f)

/** Her photo hangs on the wall on the right, over the big button: beside the window it would cover Bruno. */
private val PHOTO_FRAME = Offset(712f, 152f)
private const val FRAME_SIZE = 88f
private const val LOOK_SECONDS = 2.2f

/**
 * Bruno's room, where every game starts: Bruno says hello and waits, laughs if she tickles him, and the big green button
 * goes to the islands. The microphone is «Habla con Bruno», the balloon the calm corner, and the padlock, held a while,
 * the grown-ups' corner. Her name, if the grown-ups wrote it, hangs on the bunting, and her photo, if they chose one, in a
 * frame on the wall (ADR 0016): the first time, Bruno looks at it, «¡Mira! ¡Eres tú!», and again when she touches it.
 * The bunting with her name leads to writing it (ADR 0017): until she has written it, Bruno asks her once each time the
 * game opens, «¿Me enseñas a escribir tu nombre? Toca el banderín», and the bunting waves.
 */
@Composable
internal fun HomeScreen(game: GameState) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val measurer = rememberTextMeasurer()
    val font = andika()
    val audio = game.audio
    var cheerAt by remember { mutableStateOf(-10f) }
    var lockSince by remember { mutableStateOf<Float?>(null) }
    val photo = remember(game.photo) { game.photo?.let { runCatching { it.decodeToImageBitmap() }.getOrNull() } }

    // When Bruno last looked at her photo, and when he asked her to teach him her name.
    var lookedAt by remember { mutableStateOf(-10f) }
    var invitedAt by remember { mutableStateOf(-10f) }

    LaunchedEffect(Unit) {
        val greeting = !game.greeted
        game.greeted = true
        val inviting = !game.askedForName && game.data.namesWritten == 0 && nameLetters(game.data.childName).isNotEmpty()
        if (inviting) game.askedForName = true
        if (greeting) {
            cheerAt = clock.value
            audio.say(Voice.HELLO)
        }
        if (photo != null && !game.data.photoSeen) {
            lookedAt = clock.value
            audio.say(Voice.ITS_YOU)
            game.update { it.copy(photoSeen = true) }
        }
        if (inviting) {
            invitedAt = clock.value
            audio.say(Voice.TEACH_ME_YOUR_NAME)
        } else if (greeting) {
            audio.say(Voice.GO_TO_ISLAND)
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                val now = clock.value
                when {
                    hits(point, PLAY_BUTTON, PLAY_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.play()
                    }
                    hits(point, CALM_BUTTON, CALM_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Calm)
                    }
                    hits(point, TALK_BUTTON, CALM_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Talk)
                    }
                    hits(point, lockButton(stage), LOCK_RADIUS) -> {
                        lockSince = now
                        scope.launch {
                            delay((HOLD_SECONDS * 1000).toLong())
                            if (lockSince == now) {
                                lockSince = null
                                audio.hush()
                                game.go(Scene.GrownUps)
                            }
                        }
                    }
                    game.photo != null && hits(point, PHOTO_FRAME, FRAME_SIZE / 2) -> {
                        lookedAt = now
                        audio.sfx(Sfx.POP)
                        audio.speak(Voice.ITS_YOU)
                    }
                    nameLetters(game.data.childName).isNotEmpty() && buntingArea(game.data.childName).contains(point) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Name)
                    }
                    hits(point, BRUNO_AT_HOME, BRUNO_HEIGHT / 2) -> {
                        cheerAt = now
                        audio.sfx(Sfx.POP)
                        audio.speak(if (now.toInt() % 2 == 0) Voice.TICKLES else Voice.GIGGLE)
                    }
                    else -> return false
                }
                return true
            }

            override fun up(point: Offset) {
                lockSince = null
            }

            override fun cancel() {
                lockSince = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawRoom(stage, t)
        val inviting = t - invitedAt < INVITE_SECONDS
        drawBunting(game.data.childName, measurer, font, t, waving = inviting)
        if (inviting) drawSparkles(Offset(BUNTING_CENTER_X, 70f), 110f, t)
        photo?.let { drawPhotoFrame(it, t) }
        val since = t - cheerAt
        val pose = when {
            since < 1.3f -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin((since / 1.3f) * 3.14f))
            t - lookedAt < LOOK_SECONDS -> Pose(Mouth.GRIN, Eyes.OPEN, Arms.UP, lookX = 1f, lookY = -0.7f)
            inviting -> Pose(Mouth.GRIN, Eyes.OPEN, Arms.UP, lookX = 0.6f, lookY = -1f)
            else -> Pose(lookX = 0.5f * sin(t * 0.4f), hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_AT_HOME, BRUNO_HEIGHT, pose, t)
        if (since < 1.3f) drawSparkles(BRUNO_AT_HOME, 140f, t)
        drawRoundButton(PLAY_BUTTON, PLAY_RADIUS, Color(0xFF2E9E5B), glow = (t % 1.8f) / 1.8f) { playIcon(PLAY_BUTTON, 70f) }
        drawRoundButton(CALM_BUTTON, CALM_RADIUS, Color(0xFFDDF1E3)) {
            drawBalloon(CALM_BUTTON + Offset(0f, -8f), 52f, Color(0xFF2E9E5B))
            line(Color(0xFF46526F), CALM_BUTTON + Offset(0f, 22f), CALM_BUTTON + Offset(0f, 34f), 2f)
        }
        drawRoundButton(TALK_BUTTON, CALM_RADIUS, Color(0xFFE85A7A)) { micIcon(TALK_BUTTON, 40f) }
        drawGrownUpsLock(stage, t, lockSince)
    }
}

/** Her [photo] in a wooden frame hanging from a nail, swaying a little, cut to the frame. */
private fun DrawScope.drawPhotoFrame(photo: ImageBitmap, time: Float) {
    val half = FRAME_SIZE / 2
    val nail = PHOTO_FRAME + Offset(0f, -half - 18f)
    rotate(1.5f * sin(time * 0.8f), nail) {
        line(Color(0xFF8A5A3C), nail, PHOTO_FRAME + Offset(-half * 0.65f, -half), 2f)
        line(Color(0xFF8A5A3C), nail, PHOTO_FRAME + Offset(half * 0.65f, -half), 2f)
        val corner = PHOTO_FRAME - Offset(half, half)
        drawRoundRect(Shadow, corner + Offset(0f, 5f), Size(FRAME_SIZE, FRAME_SIZE), CornerRadius(8f))
        drawRoundRect(Color(0xFFB5743F), corner, Size(FRAME_SIZE, FRAME_SIZE), CornerRadius(8f))
        val inner = FRAME_SIZE - 14f
        val window = Rect(PHOTO_FRAME - Offset(inner / 2, inner / 2), Size(inner, inner))
        clipPath(Path().apply { addRoundRect(RoundRect(window, CornerRadius(5f))) }) {
            drawImage(
                photo,
                srcSize = IntSize(photo.width, photo.height),
                dstOffset = IntOffset(window.left.toInt(), window.top.toInt()),
                dstSize = IntSize(inner.toInt(), inner.toInt()),
                filterQuality = FilterQuality.Medium,
            )
        }
    }
    circle(Color(0xFF6B4A2E), nail, 3f)
}

/** How many pennants the bunting has: one for each letter of her [name], or five stars if there is no name. */
private fun pennants(name: String): Int = bunting(name).length.takeIf { it > 0 } ?: 5

private fun bunting(name: String): String = name.uppercase().filter { it.isLetter() }.take(MAX_LETTERS)

/** Where the bunting can be touched: its pennants, with some room around them for a small finger. */
private fun buntingArea(name: String): Rect {
    val width = PENNANT_WIDTH * pennants(name)
    return Rect(BUNTING_CENTER_X - width / 2 - 10f, 26f, BUNTING_CENTER_X + width / 2 + 10f, 112f)
}

/**
 * The bunting over the room: a pennant for each letter of her name, or stars if there is no name. The pennants sway a
 * little, as cloth does; [waving], when Bruno asks her to touch it, they wave one after the other.
 */
private fun DrawScope.drawBunting(
    name: String,
    measurer: TextMeasurer,
    font: FontFamily,
    time: Float,
    waving: Boolean,
) {
    val letters = bunting(name)
    val count = pennants(name)
    val width = PENNANT_WIDTH * count
    val left = BUNTING_CENTER_X - width / 2
    val rope = path {
        moveTo(left - 30f, 36f)
        quadraticTo(BUNTING_CENTER_X, 60f, left + width + 30f, 36f)
    }
    stroke(rope, Color(0xFF8A5A3C), 2.5f)
    val colors = listOf(Color(0xFFE4483C), Color(0xFF2C74D8), Color(0xFF2E9E5B), Color(0xFFF4B623), Color(0xFF9B6BD3))
    for (i in 0 until count) {
        val x = left + PENNANT_WIDTH * i + PENNANT_WIDTH / 2
        val sag = 18f * (1f - ((x - BUNTING_CENTER_X) / (width / 2 + 30f)).let { it * it })
        val jump = if (waving) 7f * sin(time * 7f - i * 0.7f).coerceAtLeast(0f) else 0f
        val top = 38f + sag - jump
        val sway = (if (waving) 5f else 2f) * sin(time * 1.4f + i * 0.9f)
        fill(
            path {
                moveTo(x - PENNANT_WIDTH * 0.45f, top)
                lineTo(x + PENNANT_WIDTH * 0.45f, top)
                lineTo(x + sway, top + 50f)
                close()
            },
            colors[i % colors.size],
        )
        if (letters.isEmpty()) {
            drawStar(Offset(x + sway * 0.3f, top + 17f), 9f, Color.White)
        } else {
            label(measurer, letters[i].toString(), Offset(x + sway * 0.3f, top + 17f), 24f, Color.White, font)
        }
    }
}

private const val MAX_LETTERS = 10
private const val PENNANT_WIDTH = 40f
private const val BUNTING_CENTER_X = 520f
private const val INVITE_SECONDS = 3.5f
