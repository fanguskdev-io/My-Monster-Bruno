package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * An iPhone on its side, in points, and what of its screen cannot be seen: the Dynamic Island, in the middle of one side,
 * and the rounded corners. Measured on the simulator, iOS 26.3, with `xcrun simctl io <udid> screenshot --mask=black`,
 * and a pixel more on every edge: what is hidden here is never less than what the phone hides.
 */
internal class Iphone(
    val name: String,
    val width: Float,
    val height: Float,
    /** The island, with it on the left. */
    private val islandOnLeft: Rect,
    /** How far each corner hides the screen from the side, every [CORNER_STEP] points from the top or the bottom. */
    private val corner: List<Float>,
) {
    /** For the names of the files. */
    val id = name.lowercase().replace(" ", "")

    /** The island on the left or on the right: the phone can be held either way. */
    fun island(onLeft: Boolean) = if (onLeft) {
        islandOnLeft
    } else {
        Rect(width - islandOnLeft.right, islandOnLeft.top, width - islandOnLeft.left, islandOnLeft.bottom)
    }

    /** The edge of the top left corner, from the top down to the side; the other three corners are its mirror images. */
    val cornerEdge = corner.mapIndexed { i, depth -> Offset(depth, i * CORNER_STEP) }

    /** The room between a circle and what cannot be seen: less than zero if part of it is hidden. */
    fun room(center: Offset, radius: Float, islandOnLeft: Boolean): Float {
        val island = island(islandOnLeft)
        // Seen from its nearest corner, as if that were the top left one.
        val inCorner = Offset(min(center.x, width - center.x), min(center.y, height - center.y))
        if (island.contains(center) || inCorner.x < depth(inCorner.y)) return -radius
        val toCorner = cornerEdge.zipWithNext { a, b -> distance(inCorner, a, b) }.min()
        return min(toCorner, distance(center, island)) - radius
    }

    private fun depth(y: Float): Float {
        val i = (y / CORNER_STEP).toInt()
        if (i >= corner.size - 1) return 0f
        return lerp(corner[i], corner[i + 1], y / CORNER_STEP - i)
    }

    companion object {
        const val CORNER_STEP = 4f

        /** The one she plays on. */
        val PRO_15 = Iphone(
            "iPhone 15 Pro",
            852f,
            393f,
            Rect(11f, 133.6f, 48.4f, 259.4f),
            listOf(58.4f, 35.4f, 26.7f, 20.7f, 16f, 12.7f, 9.7f, 7.4f, 5.7f, 4f, 3f, 2f, 1.4f, 1f, 0.7f, 0f),
        )

        /** The simulator's that is most like it (docs/pruebas.md). */
        val PRO_17 = Iphone(
            "iPhone 17 Pro",
            874f,
            402f,
            Rect(13.6f, 138f, 51f, 264f),
            listOf(66.4f, 41.4f, 32f, 25.4f, 20.4f, 16.4f, 13f, 10.4f, 8.4f, 6.4f, 4.7f, 3.7f, 2.7f, 2f, 1.4f, 1f, 0.7f, 0f),
        )
    }
}

private fun distance(point: Offset, a: Offset, b: Offset): Float {
    val ab = b - a
    val t = (((point.x - a.x) * ab.x + (point.y - a.y) * ab.y) / (ab.x * ab.x + ab.y * ab.y)).coerceIn(0f, 1f)
    return distance(point, a + ab * t)
}

private fun distance(point: Offset, rect: Rect) =
    hypot(max(max(rect.left - point.x, 0f), point.x - rect.right), max(max(rect.top - point.y, 0f), point.y - rect.bottom))
