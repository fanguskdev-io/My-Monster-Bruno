package com.fanguskdev.mymonsterbruno.game

/**
 * The days of the week of «La asamblea de Bruno» (ADR 0030), Monday first as on the calendar of her class. [word] names
 * their lines, without accents, and [letter] is the one on its card, as in class: the X for Wednesday.
 */
internal enum class Weekday(val word: String, val letter: String) {
    MONDAY("lunes", "L"),
    TUESDAY("martes", "M"),
    WEDNESDAY("miercoles", "X"),
    THURSDAY("jueves", "J"),
    FRIDAY("viernes", "V"),
    SATURDAY("sabado", "S"),
    SUNDAY("domingo", "D"),
    ;

    val yesterday: Weekday get() = entries[(ordinal + entries.size - 1) % entries.size]
    val tomorrow: Weekday get() = entries[(ordinal + 1) % entries.size]
}

/** The day of the week of [day], as yyyymmdd, by the days since 1 January 1970, a Thursday (Howard Hinnant's way). */
internal fun weekdayOf(day: Int): Weekday {
    val month = day / 100 % 100
    val year = day / 10000 - if (month <= 2) 1 else 0
    val era = (if (year >= 0) year else year - 399) / 400
    val ofEra = year - era * 400
    val ofYear = (153 * (if (month > 2) month - 3 else month + 9) + 2) / 5 + day % 100 - 1
    val days = era * 146_097L + ofEra * 365 + ofEra / 4 - ofEra / 100 + ofYear - 719_468L
    return Weekday.entries[((days % 7 + 7 + THURSDAY) % 7).toInt()]
}

/** What she sees out of her window, in the assembly (ADR 0030). [word] names its lines and its count in the save. */
internal enum class Weather(val word: String) {
    SUN("sol"),
    CLOUDS("nubes"),
    RAIN("lluvia"),
    SNOW("nieve"),
}

private const val THURSDAY = 3
