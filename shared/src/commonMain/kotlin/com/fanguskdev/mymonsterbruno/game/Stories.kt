package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/**
 * The stories of «Ordena la historia» (ADR 0025): something of every day in three pictures, in order: what happens first,
 * what happens next and what happens at the end. [key] names its lines and its count in the save.
 */
internal enum class Story(val key: String) {
    FLOWER("flor"),
    TEETH("dientes"),
    CAKE("bizcocho"),
    SNOWMAN("nieve"),
}

/** How many pictures each story has: first, next and at the end. */
internal const val STORY_STEPS = 3

/** The stories of a visit: the ones she has ordered least ([ordered], by key), ties at random. */
internal fun storyRounds(random: Random, ordered: Map<String, Int> = emptyMap()): List<Story> =
    Story.entries.shuffled(random).sortedBy { ordered[it.key] ?: 0 }.take(STORY_ROUNDS)

/** The pictures of a story as they are dealt, by step: never already in order, so there is always something to do. */
internal fun dealtSteps(random: Random): List<Int> {
    val order = (0 until STORY_STEPS).toList()
    var cards = order.shuffled(random)
    while (cards == order) cards = order.shuffled(random)
    return cards
}

internal const val STORY_ROUNDS = 2
