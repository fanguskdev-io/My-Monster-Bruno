package com.fanguskdev.mymonsterbruno.game

/** The consonants the grown-ups can take out (ADR 0041): the vowels always stay, they are where it all starts. */
internal val HIDEABLE_LETTERS = CONSONANTS + NEXT_CONSONANTS + LAST_CONSONANTS

/** The consonants the grown-ups took out of Comeletras, the tracing and the rocks of the island (ADR 0041). */
internal val SaveData.hidden: Set<Char> get() = hiddenLetters.mapNotNull { it.singleOrNull() }.filter { it in HIDEABLE_LETTERS }.toSet()

/** The save with [letter] taken out, or back in if it was out. */
internal fun SaveData.toggledLetter(letter: Char): SaveData {
    val key = letter.toString()
    return copy(hiddenLetters = if (key in hiddenLetters) hiddenLetters - key else hiddenLetters + key)
}

/** The save with the island called [name] off the map, or back on it; the last one on the map always stays. */
internal fun SaveData.toggledIsland(name: String, all: List<String>): SaveData = when {
    name in hiddenIslands -> copy(hiddenIslands = hiddenIslands - name)
    all.count { it !in hiddenIslands } <= 1 -> this
    else -> copy(hiddenIslands = hiddenIslands + name)
}
