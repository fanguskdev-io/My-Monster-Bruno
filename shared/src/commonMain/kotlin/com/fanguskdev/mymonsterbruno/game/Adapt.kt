package com.fanguskdev.mymonsterbruno.game

/**
 * The games where she chooses among a few, which adapt to what she knows (ADR 0020): Comeletras, the balloons, the faces
 * of the feelings and the cards of the island of words. [word] names each in the save and in the grown-ups' corner.
 */
internal enum class Choosing(val word: String) {
    COOKIES("galletas"),
    BALLOONS("globos"),
    FACES("caritas"),
    CARDS("cartas"),
}

/** How many to choose from: two if it is hard for her, three to start with, four if it is easy. */
internal const val MIN_CHOICES = 2
internal const val CHOICES = 3
internal const val MAX_CHOICES = 4

/** How many of the last rounds of each game count. */
internal const val LAST_ROUNDS = 5

/**
 * How many to choose from in [game] now: four after [LAST_ROUNDS] rounds in a row right at the first try, two if more
 * than half of the last ones went wrong at first, and three otherwise or until she has played that many. She is never
 * told: there are no levels, points or stars.
 */
internal fun SaveData.choices(game: Choosing): Int {
    val last = firstTries[game.word].orEmpty().takeLast(LAST_ROUNDS)
    if (last.size < LAST_ROUNDS) return CHOICES
    val right = last.count { it }
    return when {
        right == LAST_ROUNDS -> MAX_CHOICES
        right * 2 < LAST_ROUNDS -> MIN_CHOICES
        else -> CHOICES
    }
}

/** The save with one more round of [game], right at the first try or not: only the last [LAST_ROUNDS] are kept. */
internal fun SaveData.played(game: Choosing, firstTry: Boolean): SaveData =
    copy(firstTries = firstTries + (game.word to (firstTries[game.word].orEmpty() + firstTry).takeLast(LAST_ROUNDS)))

/**
 * The options of a round cut to [count]: [answer] always stays, and so does the order, so the answer is still anywhere;
 * the ones left out are the last of the others.
 */
internal fun <T> List<T>.narrowed(answer: T, count: Int): List<T> {
    val kept = filter { it != answer }.take(count - 1).toSet()
    return filter { it == answer || it in kept }
}
