package com.fanguskdev.mymonsterbruno.game

/** How many days of «¿Cómo te sientes hoy?» the game keeps, for the grown-ups' corner (ADR 0019). */
internal const val MOOD_DAYS = 14

/** The save, with her feeling on [day] (yyyymmdd): one a day, the last she chose, and only the last [MOOD_DAYS] days. */
internal fun SaveData.feltOn(day: Int, feeling: Feeling): SaveData {
    val days = (moods + (day.toString() to feeling.word)).toList()
    return copy(moods = days.sortedByDescending { it.first.toIntOrNull() ?: 0 }.take(MOOD_DAYS).toMap())
}

/** The days she said how she felt, the latest first: the day, as yyyymmdd, and the feeling. */
internal fun SaveData.feltDays(): List<Pair<Int, Feeling>> = moods.mapNotNull { (day, word) ->
    val feeling = Feeling.entries.firstOrNull { it.word == word }
    val date = day.toIntOrNull()
    if (feeling == null || date == null) null else date to feeling
}.sortedByDescending { it.first }
