package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.audio.Voice
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** «Ordena la historia» (ADR 0025): which stories come, and how their pictures are dealt. */
class StoriesTest {
    @Test
    fun twoStoriesTheOnesSheHasOrderedLeast() {
        repeat(100) { seed ->
            val first = storyRounds(Random(seed))
            assertEquals(STORY_ROUNDS, first.toSet().size, "historias repetidas: $first")
            val then = storyRounds(Random(seed), first.associate { it.key to 1 })
            assertTrue(then.none { it in first }, "vuelve a una que ya ordenó: $then")
        }
    }

    @Test
    fun thePicturesAreNeverDealtInOrder() {
        val order = (0 until STORY_STEPS).toList()
        val seen = mutableSetOf<List<Int>>()
        repeat(200) { seed ->
            val cards = dealtSteps(Random(seed))
            assertEquals(order, cards.sorted())
            assertNotEquals(order, cards, "ya en orden")
            seen += cards
        }
        assertEquals(5, seen.size, "las cinco formas de desordenarlas")
    }

    @Test
    fun everyPictureHasItsLine() {
        for (story in Story.entries) {
            for (step in 0 until STORY_STEPS) assertTrue(Voice.storyStep(story, step) in Voice.ALL, "${story.key} $step")
        }
        assertEquals(listOf(Voice.WHAT_FIRST, Voice.AND_NEXT, Voice.AND_AT_THE_END), (0 until STORY_STEPS).map { Voice.askStep(it) })
    }
}
