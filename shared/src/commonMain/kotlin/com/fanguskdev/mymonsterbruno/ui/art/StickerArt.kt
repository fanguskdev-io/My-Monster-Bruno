package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.ui.Island
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ring

/** How big a sticker is on the wall, its radius in stage units. */
internal const val STICKER_RADIUS = 30f

/**
 * The sticker of [island] (ADR 0024), [radius] units: a round white sticker with an edge of a colour and the picture of
 * its island, a little tilted, each its own way: a cookie with its A, a strawberry, a balloon, Bruno's house, a happy face
 * and the open book of the stories.
 */
internal fun DrawScope.drawSticker(
    island: Island,
    center: Offset,
    radius: Float,
    time: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    rotate(TILTS[island.ordinal], center) {
        circle(Shadow, center + Offset(0f, radius * 0.12f), radius)
        circle(Color.White, center, radius)
        ring(EDGES[island.ordinal], center, radius * 0.86f, radius * 0.09f)
        val k = radius / STICKER_RADIUS
        when (island) {
            Island.VOWELS -> drawCookie(center, 19f * k, 'a', measurer, font)
            Island.NUMBERS -> drawStrawberry(center + Offset(0f, -1f * k), 15f * k)
            Island.COLORS -> drawBalloon(center + Offset(0f, -3f * k), 38f * k, Color(0xFFE4483C))
            Island.SHAPES -> drawStartPicture(StartWord.CASA, center, 42f * k, time)
            Island.FEELINGS -> drawFeelingFace(Feeling.JOY, center, 18f * k)
            Island.WORDS -> drawOpenBook(center, 42f * k)
        }
    }
}

private val TILTS = listOf(-8f, 6f, -4f, 9f, -6f, 4f)
private val EDGES = listOf(
    Color(0xFFE4483C),
    Color(0xFF2C74D8),
    Color(0xFF2E9E5B),
    Color(0xFFF4B623),
    Color(0xFF9B6BD3),
    Color(0xFFF28B2E),
)
