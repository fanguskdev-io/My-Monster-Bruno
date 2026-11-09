package com.fanguskdev.mymonsterbruno.ui.art

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.fanguskdev.mymonsterbruno.game.BrunoLook
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.ellipse
import com.fanguskdev.mymonsterbruno.ui.fill
import com.fanguskdev.mymonsterbruno.ui.line
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.stroke

/**
 * A jar of the corner of feelings of the classroom (ADR 0019), standing on [bottom]: glass, with the lid, a little of the
 * colour of [feeling] at the bottom and its little face in front. [glow], from 0 to 1, when her photo is over it.
 */
internal fun DrawScope.drawJar(
    feeling: Feeling,
    bottom: Offset,
    width: Float,
    height: Float,
    glow: Float = 0f,
) {
    val color = feeling.paint()
    val left = bottom.x - width / 2
    val top = bottom.y - height
    ellipse(Shadow, bottom + Offset(0f, 2f), width * 0.55f, 7f)
    if (glow > 0f) {
        drawRoundRect(color, Offset(left - 10f, top - 26f), Size(width + 20f, height + 34f), CornerRadius(26f), alpha = 0.4f * glow)
    }
    drawRoundRect(Color.White, Offset(left, top), Size(width, height), CornerRadius(20f), alpha = 0.65f)
    val level = height * 0.24f
    drawRoundRect(color, Offset(left + 5f, bottom.y - level), Size(width - 10f, level - 5f), CornerRadius(14f), alpha = 0.35f)
    drawRoundRect(color, Offset(left, top), Size(width, height), CornerRadius(20f), style = Stroke(4f))
    drawRoundRect(color, Offset(left + width * 0.08f, top - 16f), Size(width * 0.84f, 20f), CornerRadius(8f))
    drawRoundRect(Color.White, Offset(left + width * 0.16f, top - 12f), Size(width * 0.46f, 4f), CornerRadius(2f), alpha = 0.55f)
    line(Color.White, Offset(left + 12f, top + 16f), Offset(left + 12f, top + height * 0.5f), 5f, alpha = 0.8f)
    drawFeelingFace(feeling, Offset(bottom.x, top + height * 0.4f), width * 0.27f)
}

/** The wooden bench the jars stand on, its top at [top] from [left] to [right]. */
internal fun DrawScope.drawBench(left: Float, right: Float, top: Float) {
    for (x in listOf(left + 22f, right - 34f)) {
        drawRoundRect(Color(0xFF8A5A3C), Offset(x, top + 8f), Size(12f, 48f), CornerRadius(4f))
    }
    drawRoundRect(Shadow, Offset(left, top + 4f), Size(right - left, 16f), CornerRadius(6f))
    drawRoundRect(Color(0xFFB5743F), Offset(left, top), Size(right - left, 14f), CornerRadius(6f))
    line(Color(0xFFD08F55), Offset(left + 8f, top + 4f), Offset(right - 8f, top + 4f), 2f)
}

/**
 * Her token for the feeling of the day: her [photo] in a white card [size] units a side or, if the grown-ups did not
 * choose one, a drawn face.
 */
internal fun DrawScope.drawPortrait(photo: ImageBitmap?, center: Offset, size: Float) {
    val half = size / 2
    val corner = center - Offset(half, half)
    drawRoundRect(Shadow, corner + Offset(0f, 4f), Size(size, size), CornerRadius(size * 0.14f))
    drawRoundRect(Color.White, corner, Size(size, size), CornerRadius(size * 0.14f))
    val inner = size * 0.86f
    val window = Rect(center - Offset(inner / 2, inner / 2), Size(inner, inner))
    if (photo != null) {
        clipPath(Path().apply { addRoundRect(RoundRect(window, CornerRadius(size * 0.09f))) }) {
            drawImage(
                photo,
                srcSize = IntSize(photo.width, photo.height),
                dstOffset = IntOffset(window.left.toInt(), window.top.toInt()),
                dstSize = IntSize(inner.toInt(), inner.toInt()),
                filterQuality = FilterQuality.Medium,
            )
        }
    } else {
        drawRoundRect(Color(0xFFDDF1FB), window.topLeft, window.size, CornerRadius(size * 0.09f))
        drawChildFace(center + Offset(0f, size * 0.04f), inner * 0.82f)
    }
}

/**
 * Her [photo] round, [radius] units, in a white ring with its shadow: her, sitting in Bruno's boat or pinned on the
 * mission card (ADR 0034).
 */
internal fun DrawScope.drawRoundPhoto(photo: ImageBitmap, center: Offset, radius: Float) {
    circle(Shadow, center + Offset(0f, radius * 0.14f), radius)
    circle(Color.White, center, radius)
    val inner = radius * 0.84f
    clipPath(Path().apply { addOval(Rect(center, inner)) }) {
        drawImage(
            photo,
            srcSize = IntSize(photo.width, photo.height),
            dstOffset = IntOffset((center.x - inner).toInt(), (center.y - inner).toInt()),
            dstSize = IntSize((inner * 2).toInt(), (inner * 2).toInt()),
            filterQuality = FilterQuality.Medium,
        )
    }
}

/** A girl's face, [size] units across, smiling: her token when there is no photo of her. */
internal fun DrawScope.drawChildFace(center: Offset, size: Float) {
    val r = size / 2
    val hair = Color(0xFF6B3E26)
    circle(hair, center + Offset(0f, -r * 0.1f), r)
    for (side in listOf(-1f, 1f)) circle(hair, center + Offset(side * r * 0.86f, r * 0.2f), r * 0.3f)
    circle(Color(0xFFF5C7A1), center + Offset(0f, r * 0.08f), r * 0.76f)
    fill(
        path {
            moveTo(center.x - r * 0.8f, center.y - r * 0.12f)
            quadraticTo(center.x - r * 0.5f, center.y - r * 0.95f, center.x + r * 0.15f, center.y - r * 0.82f)
            quadraticTo(center.x + r * 0.7f, center.y - r * 0.72f, center.x + r * 0.8f, center.y - r * 0.12f)
            quadraticTo(center.x + r * 0.2f, center.y - r * 0.5f, center.x - r * 0.8f, center.y - r * 0.12f)
            close()
        },
        hair,
    )
    for (side in listOf(-1f, 1f)) {
        circle(Color(0xFF2C2340), center + Offset(side * r * 0.3f, r * 0.05f), r * 0.09f)
        circle(Color(0xFFF29AA5), center + Offset(side * r * 0.47f, r * 0.3f), r * 0.12f, alpha = 0.7f)
    }
    stroke(
        path {
            moveTo(center.x - r * 0.24f, center.y + r * 0.36f)
            quadraticTo(center.x, center.y + r * 0.58f, center.x + r * 0.24f, center.y + r * 0.36f)
        },
        Color(0xFF2C2340),
        r * 0.07f,
    )
}

/** Bruno's own token for the jar: a little Bruno, smiling, in a white circle [size] units across. */
internal fun DrawScope.drawBrunoToken(
    look: BrunoLook,
    center: Offset,
    size: Float,
    time: Float,
) {
    circle(Shadow, center + Offset(0f, 3f), size / 2)
    circle(Color.White, center, size / 2)
    drawBruno(look, center + Offset(0f, size * 0.06f), size * 0.78f / look.size, Pose(Mouth.GRIN, Eyes.HAPPY, Arms.DOWN), time)
}

/** A grown-up's face, [size] units across, smiling, with short hair: the one who plays with her (ADR 0036). */
internal fun DrawScope.drawGrownUpFace(center: Offset, size: Float) {
    val r = size / 2
    val hair = Color(0xFF3A2A22)
    circle(hair, center + Offset(0f, -r * 0.14f), r * 0.86f)
    circle(Color(0xFFE9B78F), center + Offset(0f, r * 0.1f), r * 0.78f)
    fill(
        path {
            moveTo(center.x - r * 0.82f, center.y)
            quadraticTo(center.x - r * 0.72f, center.y - r * 0.92f, center.x, center.y - r * 0.9f)
            quadraticTo(center.x + r * 0.72f, center.y - r * 0.92f, center.x + r * 0.82f, center.y)
            quadraticTo(center.x + r * 0.55f, center.y - r * 0.5f, center.x - r * 0.05f, center.y - r * 0.46f)
            quadraticTo(center.x - r * 0.6f, center.y - r * 0.42f, center.x - r * 0.82f, center.y)
            close()
        },
        hair,
    )
    for (side in listOf(-1f, 1f)) {
        circle(Color(0xFF2C2340), center + Offset(side * r * 0.3f, r * 0.08f), r * 0.08f)
        circle(Color(0xFFF29AA5), center + Offset(side * r * 0.48f, r * 0.34f), r * 0.1f, alpha = 0.5f)
    }
    stroke(
        path {
            moveTo(center.x - r * 0.26f, center.y + r * 0.4f)
            quadraticTo(center.x, center.y + r * 0.62f, center.x + r * 0.26f, center.y + r * 0.4f)
        },
        Color(0xFF2C2340),
        r * 0.07f,
    )
}
