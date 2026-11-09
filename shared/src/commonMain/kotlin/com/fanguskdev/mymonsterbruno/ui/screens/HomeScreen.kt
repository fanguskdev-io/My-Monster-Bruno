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
import com.fanguskdev.mymonsterbruno.game.chosenPet
import com.fanguskdev.mymonsterbruno.game.nameLetters
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.STAGE_WIDTH
import com.fanguskdev.mymonsterbruno.ui.Scene
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.andika
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.STICKER_RADIUS
import com.fanguskdev.mymonsterbruno.ui.art.Shadow
import com.fanguskdev.mymonsterbruno.ui.art.drawBalloon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawHanger
import com.fanguskdev.mymonsterbruno.ui.art.drawPet
import com.fanguskdev.mymonsterbruno.ui.art.drawPetShelf
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawRoundButton
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.art.drawStar
import com.fanguskdev.mymonsterbruno.ui.art.drawSticker
import com.fanguskdev.mymonsterbruno.ui.art.micIcon
import com.fanguskdev.mymonsterbruno.ui.art.playIcon
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.distance
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.pendingStickers
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import com.fanguskdev.mymonsterbruno.ui.rememberPhoto
import com.fanguskdev.mymonsterbruno.ui.stickerLine
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.stuck
import com.fanguskdev.mymonsterbruno.ui.stuckStickers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

private val BRUNO_AT_HOME = Offset(390f, 236f)
private const val BRUNO_HEIGHT = 230f
private val PLAY_BUTTON = Offset(680f, 290f)
private const val PLAY_RADIUS = 68f
private val CALM_BUTTON = Offset(82f, 318f)
private const val CALM_RADIUS = 46f
private val TALK_BUTTON = Offset(82f, 200f)

/** The button of Bruno's wardrobe (ADR 0032): a hanger on the floor, at his left. */
private val WARDROBE_BUTTON = Offset(198f, 344f)

/** Her animal on its shelf on the wall, at the right of Bruno and clear of the big button and its glow (ADR 0033). */
private val PET_SHELF = Offset(554f, 238f)
private const val PET_SHELF_WIDTH = 104f
private val PET = Offset(554f, 204f)
private const val PET_SIZE = 66f
private const val PET_HAPPY_SECONDS = 1.2f
private const val WARDROBE_RADIUS = 34f

/** Her photo hangs on the wall on the right, over the big button: beside the window it would cover Bruno. */
private val PHOTO_FRAME = Offset(712f, 152f)
private const val FRAME_SIZE = 88f
private const val LOOK_SECONDS = 2.2f

/** Where the stickers she has not stuck yet wait, on the rug beside Bruno (ADR 0024), and where the wall ends. */
private val STICKER_SHEET = Offset(536f, 354f)
private const val WALL_BOTTOM = 262f

/** Bruno in the room, his hat included: no sticker goes behind him. */
private const val BRUNO_HALF_WIDTH = 110f
private const val BRUNO_TOP = 96f

/** How far from the sides of the stage a sticker keeps: there an iPhone can have its Dynamic Island. */
private const val SIDE_MARGIN = 16f

/**
 * Bruno's room, where every game starts: Bruno says hello and waits, laughs if she tickles him, and the big green button
 * goes to the islands. The microphone is «Habla con Bruno», the balloon the calm corner, and the padlock, held a while,
 * the grown-ups' corner. Her name, if the grown-ups wrote it, hangs on the bunting, and her photo, if they chose one, in a
 * frame on the wall (ADR 0016): the first time, Bruno looks at it, «¡Mira! ¡Eres tú!», and again when she touches it.
 * The bunting with her name leads to writing it (ADR 0017): until she has written it, Bruno asks her once each time the
 * game opens, «¿Me enseñas a escribir tu nombre? Toca el banderín», and the bunting waves. And the sticker of each island
 * she finishes waits on the rug (ADR 0024): she takes it to the wall, sticks it wherever she wants and moves it when she
 * likes.
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
    val photo = rememberPhoto(game)

    // When Bruno last looked at her photo, and when he asked her to teach him her name.
    var lookedAt by remember { mutableStateOf(-10f) }

    // When her animal was touched, or arrived: it jumps a moment.
    var petAt by remember { mutableStateOf(-10f) }
    var invitedAt by remember { mutableStateOf(-10f) }

    // The sticker her finger has, where, and from where; the one going back where it was; and the last one stuck.
    var dragging by remember { mutableStateOf<Island?>(null) }
    var dragAt by remember { mutableStateOf(Offset.Zero) }
    var grab by remember { mutableStateOf(Offset.Zero) }
    var downAt by remember { mutableStateOf(Offset.Zero) }
    var dragStage by remember { mutableStateOf<Stage?>(null) }
    var back by remember { mutableStateOf<Pair<Island, Flight>?>(null) }
    var stuckAt by remember { mutableStateOf<Pair<Offset, Float>?>(null) }

    /** The sticker under [point]: the one waiting on the rug, or the topmost one on the wall. */
    fun stickerUnder(point: Offset): Pair<Island, Offset>? {
        val waiting = game.data.pendingStickers().firstOrNull()
        if (waiting != null && hits(point, STICKER_SHEET, STICKER_RADIUS)) return waiting to STICKER_SHEET
        return game.data.stuckStickers().lastOrNull { hits(point, it.second, STICKER_RADIUS) }
    }

    fun letGoSticker(point: Offset, stage: Stage) {
        val island = dragging ?: return
        dragging = null
        val from = game.data.stuckStickers().firstOrNull { it.first == island }?.second
        when {
            // Just a touch: the one waiting says whose it is; one on the wall makes a pop.
            distance(point, downAt) < TAP_MOVE -> if (from == null) audio.speak(island.stickerLine, Voice.STICK_IT) else audio.sfx(Sfx.POP)
            stickerFits(dragAt, stage, game.data.childName, pet = game.data.chosenPet != null) -> {
                game.update { it.stuck(island, dragAt) }
                stuckAt = dragAt to clock.value
                audio.sfx(Sfx.POP)
                if (from == null) {
                    cheerAt = clock.value
                    scope.launch {
                        audio.say(Voice.SO_PRETTY)
                        game.data.pendingStickers().firstOrNull()?.let { audio.say(it.stickerLine) }
                    }
                }
            }
            else -> {
                // Not on the wall, or on something it would hide: back where it was, and the voice says where it goes.
                back = island to Flight(dragAt, from ?: STICKER_SHEET, clock.value, BACK_SECONDS)
                audio.sfx(Sfx.WHOOSH)
                if (from == null) audio.speak(Voice.STICK_IT)
            }
        }
    }

    LaunchedEffect(Unit) {
        val greeting = !game.greeted
        game.greeted = true
        // After «¿Cómo te sientes hoy?», which says hello itself, the room only invites her to the islands.
        val toIsland = !game.invitedToIsland
        game.invitedToIsland = true
        // A sticker waiting for her comes first; the name, the next time.
        val sticker = game.data.pendingStickers().firstOrNull()
        val inviting = sticker == null &&
            !game.askedForName &&
            game.data.namesWritten == 0 &&
            nameLetters(game.data.childName).isNotEmpty()
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
        val pet = game.data.chosenPet
        if (pet != null && !game.data.petSeen) {
            petAt = clock.value
            audio.say(Voice.NEW_PET, Voice.petHello(pet))
            game.update { it.copy(petSeen = true) }
        }
        if (sticker != null) {
            audio.say(sticker.stickerLine, Voice.STICK_IT)
        } else if (inviting) {
            invitedAt = clock.value
            audio.say(Voice.TEACH_ME_YOUR_NAME)
        } else if (toIsland) {
            audio.say(Voice.GO_TO_ISLAND)
        }
    }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                val now = clock.value
                val sticker = stickerUnder(point)
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
                    hits(point, WARDROBE_BUTTON, WARDROBE_RADIUS) -> {
                        audio.sfx(Sfx.TAP)
                        audio.hush()
                        game.go(Scene.Wardrobe)
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
                    game.data.chosenPet != null && hits(point, PET, PET_SIZE / 2) -> {
                        petAt = now
                        audio.sfx(Sfx.POP)
                        game.data.chosenPet?.let { audio.speak(Voice.petHello(it)) }
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
                    sticker != null -> {
                        dragging = sticker.first
                        dragAt = sticker.second
                        grab = sticker.second - point
                        downAt = point
                        dragStage = stage
                        back = null
                        audio.sfx(Sfx.TAP)
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

            override fun move(point: Offset) {
                if (dragging != null) dragAt = point + grab
            }

            override fun up(point: Offset) {
                lockSince = null
                dragStage?.let { letGoSticker(point, it) }
            }

            override fun cancel() {
                lockSince = null
                dragging = null
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawRoom(stage, t, game.weather)
        val inviting = t - invitedAt < INVITE_SECONDS
        drawBunting(game.data.childName, measurer, font, t, waving = inviting)
        if (inviting) drawSparkles(Offset(BUNTING_CENTER_X, 70f), 110f, t)
        photo?.let { drawPhotoFrame(it, t) }
        game.data.chosenPet?.let { pet ->
            drawPetShelf(PET_SHELF, PET_SHELF_WIDTH)
            val happy = (1f - (t - petAt) / PET_HAPPY_SECONDS).coerceIn(0f, 1f)
            drawPet(pet, PET, PET_SIZE, t, happy = happy * kotlin.math.abs(sin((t - petAt) * 6f)))
        }
        val going = back?.takeIf { !it.second.over(t) }
        // The stickers on the wall, behind Bruno.
        for ((island, at) in game.data.stuckStickers()) {
            if (island == dragging) continue
            val flight = going?.takeIf { it.first == island }?.second
            drawSticker(island, flight?.at(t) ?: at, STICKER_RADIUS, t, measurer, font)
        }
        val since = t - cheerAt
        val pose = when {
            since < 1.3f -> Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin((since / 1.3f) * 3.14f))
            t - lookedAt < LOOK_SECONDS -> Pose(Mouth.GRIN, Eyes.OPEN, Arms.UP, lookX = 1f, lookY = -0.7f)
            inviting -> Pose(Mouth.GRIN, Eyes.OPEN, Arms.UP, lookX = 0.6f, lookY = -1f)
            else -> Pose(lookX = 0.5f * sin(t * 0.4f), hop = 1.5f * sin(t * 1.6f))
        }
        drawBruno(game.data.look, BRUNO_AT_HOME, BRUNO_HEIGHT, pose, t)
        if (since < 1.3f) drawSparkles(BRUNO_AT_HOME, 140f, t)
        // The stickers waiting for her on the rug: the first on top, calling her, and the next ones peeking out under it.
        val waiting = game.data.pendingStickers()
        for (i in (waiting.size - 1).coerceAtMost(2) downTo 1) {
            drawSticker(waiting[i], STICKER_SHEET + Offset(7f * i, 5f * i), STICKER_RADIUS, t, measurer, font)
        }
        waiting.firstOrNull()?.takeIf { it != dragging }?.let { island ->
            val flight = going?.takeIf { it.first == island }?.second
            if (flight == null) drawCallRing(STICKER_SHEET, STICKER_RADIUS * 1.3f, t)
            drawSticker(island, flight?.at(t) ?: (STICKER_SHEET + Offset(0f, bob(t, 2f, 2f))), STICKER_RADIUS, t, measurer, font)
        }
        stuckAt?.let { (at, at0) -> if (t - at0 < STUCK_SECONDS) drawSparkles(at, STICKER_RADIUS * 1.6f, t) }
        drawRoundButton(PLAY_BUTTON, PLAY_RADIUS, Color(0xFF2E9E5B), glow = (t % 1.8f) / 1.8f) { playIcon(PLAY_BUTTON, 70f) }
        drawRoundButton(CALM_BUTTON, CALM_RADIUS, Color(0xFFDDF1E3)) {
            drawBalloon(CALM_BUTTON + Offset(0f, -8f), 52f, Color(0xFF2E9E5B))
            line(Color(0xFF46526F), CALM_BUTTON + Offset(0f, 22f), CALM_BUTTON + Offset(0f, 34f), 2f)
        }
        drawRoundButton(TALK_BUTTON, CALM_RADIUS, Color(0xFFE85A7A)) { micIcon(TALK_BUTTON, 40f) }
        drawRoundButton(WARDROBE_BUTTON, WARDROBE_RADIUS, Color(0xFFB79BE8)) { drawHanger(WARDROBE_BUTTON, 40f) }
        drawGrownUpsLock(stage, t, lockSince)
        // The sticker in her finger, a little bigger, over everything.
        dragging?.let { drawSticker(it, dragAt, STICKER_RADIUS * 1.12f, t, measurer, font) }
    }
}

/**
 * Whether a sticker can stay at [at] (ADR 0024): whole on the wall and inside the stage, clear of where an iPhone has its
 * Dynamic Island, and off the buttons, the padlock, the bunting, the photo frame and Bruno, so that it never hides them.
 */
internal fun stickerFits(
    at: Offset,
    stage: Stage,
    name: String,
    pet: Boolean = false,
): Boolean {
    val r = STICKER_RADIUS
    val frame = Rect(
        PHOTO_FRAME.x - FRAME_SIZE / 2,
        PHOTO_FRAME.y - FRAME_SIZE / 2 - NAIL,
        PHOTO_FRAME.x + FRAME_SIZE / 2,
        PHOTO_FRAME.y + FRAME_SIZE / 2,
    )
    val onWall = at.x - r >= SIDE_MARGIN && at.x + r <= STAGE_WIDTH - SIDE_MARGIN && at.y - r >= stage.top + 4f && at.y + r <= WALL_BOTTOM
    return onWall &&
        distance(at, PLAY_BUTTON) >= PLAY_RADIUS + r &&
        distance(at, TALK_BUTTON) >= CALM_RADIUS + r &&
        distance(at, lockButton(stage)) >= LOCK_RADIUS + TOUCH_SLACK + r &&
        !buntingArea(name).inflate(r).contains(at) &&
        !frame.inflate(r).contains(at) &&
        !(pet && petArea().inflate(r).contains(at)) &&
        !(abs(at.x - BRUNO_AT_HOME.x) < BRUNO_HALF_WIDTH + r && at.y + r > BRUNO_TOP)
}

/** Her [photo] in a wooden frame hanging from a nail, swaying a little, cut to the frame. */
private fun DrawScope.drawPhotoFrame(photo: ImageBitmap, time: Float) {
    val half = FRAME_SIZE / 2
    val nail = PHOTO_FRAME + Offset(0f, -half - NAIL)
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
private const val NAIL = 18f
private const val TAP_MOVE = 14f
private const val BACK_SECONDS = 0.35f
private const val STUCK_SECONDS = 1.2f

/** Where her animal and its shelf are on the wall, which a sticker must not hide. */
private fun petArea() = Rect(
    PET_SHELF.x - PET_SHELF_WIDTH / 2,
    PET.y - PET_SIZE / 2 - 12f,
    PET_SHELF.x + PET_SHELF_WIDTH / 2,
    PET_SHELF.y + 26f,
)
