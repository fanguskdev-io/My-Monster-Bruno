package com.fanguskdev.mymonsterbruno.game

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * The paths of «Trazos antes de las letras» (ADR 0031), along which she takes Bruno home with her finger, in the order of
 * the strokes of her class: straight lines first, across, down and slanting, and then waves, loops and a spiral. [word]
 * names their lines and their count in the save.
 */
internal enum class PathShape(val word: String) {
    ACROSS("recta"),
    DOWN("abajo"),
    SLANT("inclinada"),
    WAVES("olas"),
    LOOPS("bucles"),
    SPIRAL("caracol"),
}

/** How many paths she takes Bruno home along in a visit. */
internal const val PATH_ROUNDS = 3

/** The paths of a visit: the ones she has done least and, of those, the first in the order of her class. */
internal fun pathRounds(done: Map<String, Int>): List<PathShape> = PathShape.entries.sortedBy { done[it.word] ?: 0 }.take(PATH_ROUNDS)

/** The path on the stage, from where Bruno starts to his house: points [PATH_STEP] units apart, as the letters have. */
internal fun PathShape.points(): List<Offset> = evenly((0..CURVE_POINTS).map { at(it / CURVE_POINTS.toFloat()) })

/** The point of the path at [u], from 0 at its start to 1 at the house. */
private fun PathShape.at(u: Float): Offset = when (this) {
    PathShape.ACROSS -> Offset(150f + 500f * u, 228f)
    PathShape.DOWN -> Offset(400f, 110f + 216f * u)
    PathShape.SLANT -> Offset(180f + 430f * u, 104f + 222f * u)
    PathShape.WAVES -> Offset(140f + 520f * u, 228f + 56f * sin(2.0 * PI * 2.5 * u).toFloat())
    PathShape.LOOPS -> {
        // A stretched cycloid, which goes back on itself at the top: three big loops like a row of joined «e».
        val t = (-PI + 2.0 * PI * 3.0 * u).toFloat()
        Offset(150f + LOOP_ADVANCE * (t + PI.toFloat()) - LOOP_RADIUS * sin(t), 228f - LOOP_RADIUS * cos(t))
    }
    PathShape.SPIRAL -> {
        // A snail: two turns from the outside in, to the house in the middle, wide enough apart to tell them.
        val angle = 2.0 * PI * 2.0 * u
        val radius = 122f * (1f - 0.74f * u)
        Offset(400f + radius * cos(angle).toFloat(), 222f + radius * sin(angle).toFloat())
    }
}

/** The points of [curve] again, [PATH_STEP] apart along it. */
private fun evenly(curve: List<Offset>): List<Offset> {
    val points = mutableListOf(curve.first())
    var walked = 0f
    for (i in 1 until curve.size) {
        var from = curve[i - 1]
        val to = curve[i]
        var left = hypot(to.x - from.x, to.y - from.y)
        while (walked + left >= PATH_STEP) {
            val k = (PATH_STEP - walked) / left
            from = Offset(from.x + (to.x - from.x) * k, from.y + (to.y - from.y) * k)
            points += from
            left = hypot(to.x - from.x, to.y - from.y)
            walked = 0f
        }
        walked += left
    }
    if (points.last() != curve.last()) points += curve.last()
    return points
}

/**
 * [this] on a [day] she goes to the island of letters: one more day there if it is another day than the last. Its second
 * game takes turns by those days (ADR 0031): tracing letters the first day, Bruno's paths the next, and so on; by the
 * days she goes, as on the island of numbers, so that they take turns even if she does not play every day.
 */
internal fun SaveData.lettersOn(day: Int): SaveData = if (lettersDay == day) this else copy(lettersDay = day, lettersDays = lettersDays + 1)

/** Whether today the second game of the island of letters is Bruno's paths, rather than tracing letters. */
internal val SaveData.pathsToday: Boolean get() = lettersDays > 0 && lettersDays % 2 == 0

/** The distance between the points of a path, as between those of a letter. */
internal const val PATH_STEP = 4f

private const val CURVE_POINTS = 1200
private const val LOOP_ADVANCE = 26f
private const val LOOP_RADIUS = 70f
