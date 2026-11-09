package com.fanguskdev.mymonsterbruno.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.fanguskdev.mymonsterbruno.ui.circle
import com.fanguskdev.mymonsterbruno.ui.path
import com.fanguskdev.mymonsterbruno.ui.stroke

/** A clothes hanger, [size] across and centred on [center]: the button of Bruno's wardrobe in the room (ADR 0032). */
internal fun DrawScope.drawHanger(center: Offset, size: Float) {
    val k = size / 40f
    stroke(
        path {
            moveTo(center.x, center.y - 1f * k)
            lineTo(center.x, center.y - 6f * k)
            cubicTo(center.x, center.y - 15f * k, center.x + 10f * k, center.y - 15f * k, center.x + 9f * k, center.y - 8f * k)
        },
        Color.White,
        3.2f * k,
    )
    stroke(
        path {
            moveTo(center.x, center.y - 1f * k)
            lineTo(center.x - 17f * k, center.y + 11f * k)
            lineTo(center.x + 17f * k, center.y + 11f * k)
            close()
        },
        Color.White,
        3.2f * k,
    )
}

/**
 * Bruno's wardrobe, open: its wooden frame round [box], the light inside and a shelf under each row of what is kept in it,
 * at [shelves], with two little knobs at the top.
 */
internal fun DrawScope.drawWardrobe(box: Rect, shelves: List<Float>) {
    drawRoundRect(Shadow, box.topLeft + Offset(0f, 6f), box.size, CornerRadius(18f))
    drawRoundRect(Color(0xFFB97A45), box.topLeft, box.size, CornerRadius(18f))
    val inside = Rect(box.left + 12f, box.top + 12f, box.right - 12f, box.bottom - 12f)
    drawRoundRect(Color(0xFFF6E7D2), inside.topLeft, inside.size, CornerRadius(10f))
    for (y in shelves) {
        drawRect(Color(0xFFD9AE7E), Offset(inside.left, y), Size(inside.width, 6f))
        drawRect(Color(0xFFC4955F), Offset(inside.left, y + 6f), Size(inside.width, 2f))
    }
    for (x in listOf(box.center.x - 26f, box.center.x + 26f)) circle(Color(0xFF8E5A2B), Offset(x, box.top + 6f), 5f)
}
