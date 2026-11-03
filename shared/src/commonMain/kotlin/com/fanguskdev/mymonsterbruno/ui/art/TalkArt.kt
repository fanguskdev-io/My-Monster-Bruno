package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.Word
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.label
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.stroke
import com.fanguskdev.mymonsterbruno.ui.tiltedEllipse
import kotlin.math.sin

/** The picture of each word of «Habla con Bruno» (ADR 0011), [size] units across: the ones of the game, and a butterfly. */
internal fun DrawScope.drawTalkPicture(
    word: TalkWord,
    center: Offset,
    size: Float,
    time: Float,
) {
    when (word) {
        TalkWord.SOL -> drawWord(Word.SOL, center, size)
        TalkWord.LUNA -> drawWord(Word.LUNA, center, size)
        TalkWord.OSO -> drawWord(Word.OSO, center, size)
        TalkWord.PELOTA -> drawWord(Word.PELOTA, center, size)
        TalkWord.MANZANA -> drawWord(Word.MANZANA, center, size)
        TalkWord.ELEFANTE -> drawWord(Word.ELEFANTE, center, size)
        TalkWord.FRESA -> drawStrawberry(center + Offset(0f, size * 0.04f), size * 0.36f)
        TalkWord.GLOBO -> drawBalloon(center + Offset(0f, -size * 0.04f), size * 0.7f, Color(0xFFE4483C))
        TalkWord.REGALO -> drawPresent(center + Offset(0f, size * 0.08f), size * 0.56f, 0f)
        TalkWord.MARIPOSA -> withTransform({
            translate(center.x, center.y)
            scale(size / 100f, size / 100f, Offset.Zero)
        }) { butterfly(time) }
    }
}

/** A butterfly that opens and closes its wings, in a box of 100 units. */
private fun DrawScope.butterfly(time: Float) {
    val open = 0.8f + 0.2f * sin(time * 5f)
    withTransform({ scale(open, 1f, Offset.Zero) }) {
        for (side in listOf(-1f, 1f)) {
            tiltedEllipse(Color(0xFF9B6BD3), Offset(side * 20f, -12f), 22f, 17f, side * -30f)
            tiltedEllipse(Color(0xFFF28B2E), Offset(side * 16f, 15f), 14f, 11f, side * 30f)
            circle(Color(0xFFF4C423), Offset(side * 24f, -15f), 6f)
            circle(Color.White, Offset(side * 15f, -4f), 3.5f, alpha = 0.8f)
            circle(Color(0xFFF4C423), Offset(side * 17f, 17f), 4f)
        }
    }
    ellipse(Color(0xFF1D2B53), Offset(0f, 4f), 4.5f, 22f)
    circle(Color(0xFF1D2B53), Offset(0f, -21f), 6f)
    for (side in listOf(-1f, 1f)) {
        stroke(
            path {
                moveTo(side * 2f, -25f)
                quadraticTo(side * 4f, -38f, side * 13f, -40f)
            },
            Color(0xFF1D2B53),
            2.2f,
        )
        circle(Color(0xFF1D2B53), Offset(side * 13f, -40f), 2.8f)
    }
}

/** The microphone of the big button, white: its head with a grille, its holder and its foot. */
internal fun DrawScope.micIcon(center: Offset, size: Float) {
    val s = size / 26f
    val ink = Color.White
    drawRoundRect(ink, Offset(center.x - 5.5f * s, center.y - 13f * s), Size(11f * s, 17f * s), CornerRadius(5.5f * s))
    for (i in 0 until 3) {
        val y = center.y - 8f * s + 4f * s * i
        line(Color(0xFFE85A7A), Offset(center.x - 2.5f * s, y), Offset(center.x + 2.5f * s, y), 1.4f * s)
    }
    stroke(
        path {
            moveTo(center.x - 9f * s, center.y - 1f * s)
            quadraticTo(center.x - 9f * s, center.y + 9f * s, center.x, center.y + 9f * s)
            quadraticTo(center.x + 9f * s, center.y + 9f * s, center.x + 9f * s, center.y - 1f * s)
        },
        ink,
        2.4f * s,
    )
    line(ink, Offset(center.x, center.y + 9f * s), Offset(center.x, center.y + 14f * s), 2.4f * s)
    line(ink, Offset(center.x - 6f * s, center.y + 14f * s), Offset(center.x + 6f * s, center.y + 14f * s), 2.4f * s)
}

/** A syllable on its card, [height] units high, as Bruno claps it; [pop] from 0 to 1 is how much it has just jumped. */
internal fun DrawScope.drawSyllable(
    syllable: String,
    center: Offset,
    height: Float,
    color: Color,
    pop: Float,
    measurer: TextMeasurer,
    font: FontFamily,
) {
    val k = 1f + 0.18f * pop
    val width = syllableWidth(syllable, height) * k
    val tall = height * k
    drawRoundRect(Shadow, Offset(center.x - width / 2, center.y - tall / 2 + 5f), Size(width, tall), CornerRadius(14f))
    drawRoundRect(color, Offset(center.x - width / 2, center.y - tall / 2), Size(width, tall), CornerRadius(14f))
    label(measurer, syllable.uppercase(), center, height * 0.56f * k, Color.White, font)
}

/** How wide the card of [syllable] is, [height] units high: wider the more letters it has. */
internal fun syllableWidth(syllable: String, height: Float) = height * (0.7f + 0.32f * syllable.length)
