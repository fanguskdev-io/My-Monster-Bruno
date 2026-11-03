package com.fanguskdev.mymonsterbruno.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.fanguskdev.mymonsterbruno.audio.Sfx
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.plusOne
import com.fanguskdev.mymonsterbruno.ui.GameState
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.Stage
import com.fanguskdev.mymonsterbruno.ui.StageCanvas
import com.fanguskdev.mymonsterbruno.ui.Touch
import com.fanguskdev.mymonsterbruno.ui.art.Arms
import com.fanguskdev.mymonsterbruno.ui.art.Eyes
import com.fanguskdev.mymonsterbruno.ui.art.Mouth
import com.fanguskdev.mymonsterbruno.ui.art.Pose
import com.fanguskdev.mymonsterbruno.ui.art.drawAccessoryIcon
import com.fanguskdev.mymonsterbruno.ui.art.drawBruno
import com.fanguskdev.mymonsterbruno.ui.art.drawPresent
import com.fanguskdev.mymonsterbruno.ui.art.drawRoom
import com.fanguskdev.mymonsterbruno.ui.art.drawSparkles
import com.fanguskdev.mymonsterbruno.ui.easeInOut
import com.fanguskdev.mymonsterbruno.ui.easeOut
import com.fanguskdev.mymonsterbruno.ui.lerp
import com.fanguskdev.mymonsterbruno.ui.rememberClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

private val BRUNO_CENTER = Offset(250f, 236f)
private const val BRUNO_HEIGHT = 230f
private val PRESENT = Offset(560f, 270f)
private const val PRESENT_SIZE = 150f

/**
 * The present at the end of an island (docs/diseno.md, «Bruno crece»): a box she opens with a touch and an accessory that
 * flies to Bruno, who wears it from then on; every two islands he also grows a little. When he has them all, the box
 * holds sparkles and a thank you.
 */
@Composable
internal fun PresentScreen(game: GameState, island: Island, onDone: () -> Unit) {
    val clock = rememberClock()
    val scope = rememberCoroutineScope()
    val audio = game.audio
    val gift = remember { game.data.look.nextPresent }
    var openedAt by remember { mutableStateOf<Float?>(null) }
    var worn by remember { mutableStateOf(false) }
    var lookBefore by remember { mutableStateOf(game.data.look) }

    LaunchedEffect(Unit) { audio.say(Voice.PRESENT) }

    val touch = remember {
        object : Touch {
            override fun down(point: Offset, stage: Stage): Boolean {
                if (openedAt != null || !hits(point, PRESENT, PRESENT_SIZE / 2)) return false
                openedAt = clock.value
                audio.hush()
                audio.sfx(Sfx.PRESENT)
                scope.launch {
                    delay(OPEN_MILLIS + FLY_MILLIS)
                    lookBefore = game.data.look
                    val done = game.data.islandsDone + 1
                    game.update {
                        val look = it.look
                        val grown = if (done % 2 == 0) (look.growth + 1).coerceAtMost(BrunoLook.MAX_GROWTH) else look.growth
                        it.copy(
                            look = look.copy(accessories = if (gift == null) look.accessories else look.accessories + gift, growth = grown),
                            islands = it.islands.plusOne(island.name),
                        )
                    }
                    worn = true
                    audio.sfx(Sfx.SPARKLE)
                    val grew = game.data.look.growth > lookBefore.growth
                    if (grew) audio.say(Voice.THANKS_PRESENT, Voice.GROWING) else audio.say(Voice.THANKS_PRESENT)
                    delay(STAY_MILLIS)
                    onDone()
                }
                return true
            }
        }
    }

    StageCanvas(touch) { stage ->
        val t = clock.value
        drawRoom(stage, t)
        val opened = openedAt
        val since = if (opened == null) 0f else t - opened
        val pose = if (worn) {
            Pose(Mouth.GRIN, Eyes.HAPPY, Arms.UP, hop = 10f * sin(t * 5f).coerceAtLeast(0f))
        } else {
            Pose(Mouth.O, Eyes.WIDE, Arms.DOWN, lookX = 1f, lookY = 0.3f)
        }
        drawBruno(if (worn) game.data.look else lookBefore, BRUNO_CENTER, BRUNO_HEIGHT, pose, t)
        val open = if (opened == null) 0f else easeOut(since / (OPEN_MILLIS / 1000f))
        val shake = if (opened == null) 4f * sin(t * 9f) * (if ((t % 2f) < 0.6f) 1f else 0f) else 0f
        drawPresent(PRESENT + Offset(shake, 0f), PRESENT_SIZE, open)
        if (opened == null) drawCallRing(PRESENT, PRESENT_SIZE * 0.62f, t)
        if (opened != null && !worn) {
            val fly = easeInOut((since - OPEN_MILLIS / 1000f) / (FLY_MILLIS / 1000f))
            val from = PRESENT + Offset(0f, -40f)
            val to = BRUNO_CENTER + Offset(0f, -60f)
            val at = lerp(from, to, fly) + Offset(0f, -90f * sin(fly * 3.14f))
            if (gift != null) drawAccessoryIcon(gift, at, 90f)
            drawSparkles(at, 60f, t)
        }
        if (worn) drawSparkles(BRUNO_CENTER, 150f, t)
    }
}

private const val OPEN_MILLIS = 700L
private const val FLY_MILLIS = 1100L
private const val STAY_MILLIS = 1200L
