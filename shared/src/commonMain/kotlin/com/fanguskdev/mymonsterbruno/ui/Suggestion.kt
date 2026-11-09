package com.fanguskdev.mymonsterbruno.ui

import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.SaveData

/**
 * The island Bruno suggests when she gets to the map (ADR 0035): the one she has finished fewest times and, if there are
 * several, a different one of those each [day] (yyyymmdd); never one the grown-ups took off the map (ADR 0041).
 */
internal fun SaveData.suggestedIsland(day: Int): Island {
    val shown = Island.entries.filter { it.name !in hiddenIslands }.ifEmpty { Island.entries }
    val fewest = shown.minOf { islands[it.name] ?: 0 }
    val least = shown.filter { (islands[it.name] ?: 0) == fewest }
    return least[day.mod(least.size)]
}

/** What Bruno says to suggest this island: «¡A mí me apetece ir a la isla de las formas! ¿Y a ti?». */
internal val Island.suggestionLine: String get() = when (this) {
    Island.VOWELS -> Voice.SUGGEST_LETTERS
    Island.NUMBERS -> Voice.SUGGEST_NUMBERS
    Island.COLORS -> Voice.SUGGEST_COLORS
    Island.SHAPES -> Voice.SUGGEST_SHAPES
    Island.FEELINGS -> Voice.SUGGEST_FEELINGS
    Island.WORDS -> Voice.SUGGEST_WORDS
    Island.HOME -> Voice.SUGGEST_HOME
}
