package com.fanguskdev.mymonsterbruno.game

import kotlinx.serialization.Serializable

/** What she did one day, for her week in the grown-ups' corner (ADR 0040). */
@Serializable
data class DayLog(
    /** Seconds she played. */
    val seconds: Int = 0,
    /** The islands she finished, by name, and how many times. */
    val islands: Map<String, Int> = emptyMap(),
    /** What Bruno asked for when she touched something else, by what it was (as «letra:m»), and how many times. */
    val misses: Map<String, Int> = emptyMap(),
)

/** How many days her week looks back: today and the six before. */
internal const val WEEK_DAYS = 7

/** [day], as yyyymmdd, as a number of days since 1970: to go back and forth across months and years. */
internal fun dayNumber(day: Int): Long {
    val month = day / 100 % 100
    val year = day / 10000 - if (month <= 2) 1 else 0
    val era = (if (year >= 0) year else year - 399) / 400
    val ofEra = year - era * 400
    val ofYear = (153 * (if (month > 2) month - 3 else month + 9) + 2) / 5 + day % 100 - 1
    return era * 146_097L + ofEra * 365 + ofEra / 4 - ofEra / 100 + ofYear - 719_468L
}

/** The day, as yyyymmdd, that is [number] days since 1970. */
internal fun dayOf(number: Long): Int {
    val shifted = number + 719_468L
    val era = (if (shifted >= 0) shifted else shifted - 146_096L) / 146_097L
    val ofEra = shifted - era * 146_097L
    val yearOfEra = (ofEra - ofEra / 1460 + ofEra / 36_524 - ofEra / 146_096) / 365
    val ofYear = ofEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val shiftedMonth = (5 * ofYear + 2) / 153
    val dayOfMonth = ofYear - (153 * shiftedMonth + 2) / 5 + 1
    val month = if (shiftedMonth < 10) shiftedMonth + 3 else shiftedMonth - 9
    val year = yearOfEra + era * 400 + if (month <= 2) 1 else 0
    return (year * 10000 + month * 100 + dayOfMonth).toInt()
}

/** The [WEEK_DAYS] days up to [today], as yyyymmdd, the oldest first. */
internal fun weekUpTo(today: Int): List<Int> {
    val number = dayNumber(today)
    return (WEEK_DAYS - 1 downTo 0).map { dayOf(number - it) }
}

/** The save with [change] done to what she did on [day], forgetting what is older than a week. */
internal fun SaveData.logged(day: Int, change: (DayLog) -> DayLog): SaveData {
    val oldest = dayNumber(day) - (WEEK_DAYS - 1)
    val kept = days.filterKeys { key -> key.toIntOrNull()?.let { dayNumber(it) >= oldest } ?: false }
    val key = day.toString()
    return copy(days = kept + (key to change(kept[key] ?: DayLog())))
}

/** What she did each day of the week up to [today], the oldest first, with nothing on the days she did not play. */
internal fun SaveData.week(today: Int): List<Pair<Int, DayLog>> = weekUpTo(today).map { it to (days[it.toString()] ?: DayLog()) }

/** What she missed most in the week up to [today], the most first, as many as [count]: what it was and how many times. */
internal fun SaveData.hardest(today: Int, count: Int = HARDEST): List<Pair<String, Int>> {
    val total = HashMap<String, Int>()
    for ((_, log) in week(today)) for ((what, times) in log.misses) total[what] = (total[what] ?: 0) + times
    return total.entries.sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .take(count)
        .map { it.key to it.value }
}

private const val HARDEST = 3

/**
 * The names of what she can miss, for her week (ADR 0040): what Bruno asked for when she touched something else, as a
 * kind and what of it.
 */
internal object Miss {
    const val LETTER = "letra"
    const val COLOR = "color"
    const val SERIES = "serie"
    const val SHAPE = "forma"
    const val FEELING = "emocion"
    const val STORY = "historia"
    const val START = "empieza"
    const val RHYME = "rima"
    const val COUNTDOWN = "cuenta_atras"
    const val SIDE = "recta"
    const val SUM = "suma"
    const val PLACE = "sitio"
    const val ORDINAL = "puesto"

    fun letter(letter: Char) = "$LETTER:$letter"

    fun color(hue: Hue) = "$COLOR:${hue.word}"

    fun shape(shape: Shape) = "$SHAPE:${shape.word}"

    fun feeling(feeling: Feeling) = "$FEELING:${feeling.word}"

    fun start(syllable: String) = "$START:$syllable"

    fun rhyme(pair: String) = "$RHYME:$pair"

    fun side(side: Side) = "$SIDE:${side.word}"

    fun sum(sum: Sum) = "$SUM:${sum.key}"

    fun place(place: Place) = "$PLACE:${place.word}"

    fun ordinal(place: Ordinal) = "$ORDINAL:${place.word}"
}
