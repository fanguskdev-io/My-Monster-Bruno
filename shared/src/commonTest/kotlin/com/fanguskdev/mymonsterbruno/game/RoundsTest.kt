package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RoundsTest {
    @Test
    fun comeletrasStartsWithTheVowelsAndThenAsksForWhatSheKnowsLeast() {
        repeat(200) { seed ->
            val first = letterRounds(Random(seed))
            assertEquals(VOWELS.toSet(), first.map { it.wanted }.toSet(), "el primer día, las vocales")
            for (round in first) {
                assertEquals(MAX_CHOICES, round.cookies.toSet().size, "galletas repetidas: $round")
                assertEquals(1, round.cookies.count { it == round.wanted })
                assertTrue(round.cookies.all { it in LETTERS })
            }
            // With each vowel fed once, the consonants come in: the four of them and one vowel.
            val then = letterRounds(Random(seed), VOWELS.associate { it.toString() to 1 })
            assertEquals(CONSONANTS.toSet(), then.map { it.wanted }.filter { it in CONSONANTS }.toSet())
            assertEquals(LETTER_ROUNDS, then.map { it.wanted }.toSet().size)
        }
    }

    @Test
    fun theTTheNAndTheDComeInOnceSheKnowsTheFirstConsonants() {
        repeat(200) { seed ->
            // With the vowels known, the M, P, L and S, and not yet the T, the N or the D.
            val vowels = letterRounds(Random(seed), VOWELS.associate { it.toString() to 1 })
            assertTrue(vowels.none { it.wanted in NEXT_CONSONANTS }, "la T, la N o la D antes de tiempo: $vowels")
            // With the four known too, the three come in.
            val four = letterRounds(Random(seed), (VOWELS + CONSONANTS).associate { it.toString() to 1 })
            assertEquals(NEXT_CONSONANTS.toSet(), four.map { it.wanted }.filter { it in NEXT_CONSONANTS }.toSet())
            // And the same in the tracing.
            assertTrue(tracePicks((VOWELS + CONSONANTS).associate { it.toString() to 1 }, Random(seed)).all { it in NEXT_CONSONANTS })
        }
    }

    @Test
    fun theFTheBAndTheRComeInOnceSheKnowsTheTTheNAndTheD() {
        repeat(200) { seed ->
            // With the M, P, L and S known, and not yet the T, the N or the D: no F, B or R.
            val before = letterRounds(Random(seed), (VOWELS + CONSONANTS).associate { it.toString() to 1 })
            assertTrue(before.none { it.wanted in LAST_CONSONANTS }, "la F, la B o la R antes de tiempo: $before")
            // With the T, the N and the D known too, the three come in.
            val known = (VOWELS + CONSONANTS + NEXT_CONSONANTS).associate { it.toString() to 1 }
            assertEquals(
                LAST_CONSONANTS.toSet(),
                letterRounds(Random(seed), known).map {
                    it.wanted
                }.filter { it in LAST_CONSONANTS }.toSet(),
            )
            // And the same in the tracing, where they already have their strokes (ADR 0017).
            assertTrue(tracePicks(known, Random(seed)).all { it in LAST_CONSONANTS })
            for (letter in LAST_CONSONANTS) assertEquals(letter, Word.of(letter).letter)
        }
    }

    @Test
    fun theStrawberriesGoFromOneToFiveAndThenUpToTen() {
        repeat(200) { seed ->
            val first = countRounds(Random(seed))
            assertEquals((1..SMALL_COUNT).toList(), first.sorted())
            assertNotEquals(SMALL_COUNT, first.first())
            // Once she has counted one to five, two of them and three of six to ten.
            val then = countRounds(Random(seed), (1..SMALL_COUNT).associate { it.toString() to 1 })
            assertEquals(5, then.toSet().size)
            assertEquals(2, then.count { it <= SMALL_COUNT })
            assertEquals(3, then.count { it in SMALL_COUNT + 1..MAX_COUNT })
            assertNotEquals(then.max(), then.first())
        }
    }

    @Test
    fun theLettersToTraceAreTheLeastTracedTheVowelsFirst() {
        val traced = mapOf("a" to 4, "e" to 3, "i" to 0, "o" to 2, "u" to 0)
        repeat(50) { seed ->
            assertEquals(setOf('i', 'u'), tracePicks(traced, Random(seed)).toSet())
        }
        assertEquals(TRACED_LETTERS, tracePicks(emptyMap(), Random(1)).size)
        val vowelsDone = VOWELS.associate { it.toString() to 1 }
        repeat(50) { seed -> assertTrue(tracePicks(vowelsDone, Random(seed)).all { it in CONSONANTS }) }
    }

    @Test
    fun theBalloonsAreFiveColoursEachWithTwoOthers() {
        repeat(200) { seed ->
            val rounds = balloonRounds(Random(seed))
            assertEquals(BALLOON_ROUNDS, rounds.size)
            assertEquals(BALLOON_ROUNDS, rounds.map { it.wanted }.toSet().size, "un color pedido dos veces: $rounds")
            for (round in rounds) {
                assertEquals(MAX_CHOICES, round.balloons.toSet().size, "globos repetidos: $round")
                assertEquals(1, round.balloons.count { it == round.wanted })
            }
        }
    }

    @Test
    fun theSeriesAreTwoOfTwoColoursAndTwoOfThreeAndTheAnswerIsAmongTheChoices() {
        repeat(200) { seed ->
            val rounds = seriesRounds(Random(seed))
            assertEquals(SERIES_SIZES, rounds.map { it.pattern.size })
            for (round in rounds) {
                assertEquals(round.pattern.size, round.pattern.toSet().size, "un color repetido en el patrón: $round")
                assertEquals(SERIES_CHOICES, round.choices.toSet().size, "opciones repetidas: $round")
                assertTrue(round.pattern.all { it in round.choices })
                assertTrue(round.answer in round.choices)
                // The row shows the whole pattern at least once, and something more.
                assertTrue(round.shown in round.pattern.size + 1..round.pattern.size + 2, "$round")
                assertEquals(round.pattern[round.shown % round.pattern.size], round.answer)
            }
        }
        val reds = SeriesRound(listOf(Hue.RED, Hue.BLUE), shown = 3, choices = listOf(Hue.RED, Hue.BLUE, Hue.GREEN))
        assertEquals(listOf(Hue.RED, Hue.BLUE, Hue.RED), (0 until 3).map { reds.at(it) })
        assertEquals(Hue.BLUE, reds.answer)
    }

    @Test
    fun theHouseIsBuiltInOrderAndItsPieceIsAlwaysAmongTheThree() {
        repeat(200) { seed ->
            val rounds = houseRounds(Random(seed))
            assertEquals(HousePart.entries, rounds.map { it.part })
            for (round in rounds) {
                assertEquals(HOUSE_PIECES, round.pieces.toSet().size, "piezas repetidas: $round")
                assertEquals(1, round.pieces.count { it == round.part.shape })
            }
        }
        assertEquals(
            listOf(Shape.SQUARE, Shape.TRIANGLE, Shape.RECTANGLE, Shape.CIRCLE, Shape.SQUARE, Shape.RECTANGLE),
            HousePart.entries.map { it.shape },
        )
    }

    @Test
    fun theFeelingsComeEachOnceTheLullabyLastAndTheirFaceAmongTheThree() {
        repeat(200) { seed ->
            val rounds = feelingRounds(Random(seed))
            assertEquals(Happening.entries.toSet(), rounds.map { it.happening }.toSet())
            assertEquals(Happening.LULLABY, rounds.last().happening)
            for (round in rounds) {
                assertEquals(MAX_CHOICES, round.faces.toSet().size, "caritas repetidas: $round")
                assertEquals(1, round.faces.count { it == round.happening.feeling })
            }
        }
        assertEquals(Feeling.entries.toSet(), Happening.entries.map { it.feeling }.toSet())
    }

    @Test
    fun talkingAsksForFourWordsSheHasSaidLeastTheFewestClapsFirst() {
        repeat(200) { seed ->
            val first = talkRounds(Random(seed))
            assertEquals(TALK_ROUNDS, first.toSet().size)
            assertEquals(first.sortedBy { it.syllables.size }, first, "de menos palmadas a más")
            // With six words said once, the other four come first.
            val said = TalkWord.entries.shuffled(Random(seed)).take(6)
            val then = talkRounds(Random(seed), said.associate { it.word to 1 })
            assertEquals((TalkWord.entries - said.toSet()).toSet(), then.toSet())
        }
    }

    @Test
    fun eachWordToTalkIsItsSyllablesFromOneClapToFour() {
        assertEquals("mariposa", TalkWord.MARIPOSA.word)
        assertEquals(listOf("ma", "ri", "po", "sa"), TalkWord.MARIPOSA.syllables)
        assertEquals(setOf(1, 2, 3, 4), TalkWord.entries.map { it.syllables.size }.toSet())
        for (word in TalkWord.entries) {
            assertEquals(word.name.lowercase(), word.word, "su palabra son sus sílabas")
            assertTrue(word.syllables.all { syllable -> syllable.any { it in "aeiou" } }, "una sílaba sin vocal en $word")
        }
    }

    @Test
    fun theIslandOfWordsAsksForFivePairsAndOnlyOneCardStartsTheSame() {
        repeat(200) { seed ->
            val rounds = startRounds(Random(seed))
            assertEquals(START_ROUNDS, rounds.map { it.said.start }.toSet().size, "cada ronda, un par distinto")
            for (round in rounds) {
                assertEquals(MAX_CHOICES, round.choices.map { it.start }.toSet().size, "dos cartas empiezan igual: $round")
                assertTrue(round.said !in round.choices, "la palabra de Bruno está entre las cartas: $round")
                assertEquals(round.said.start, round.answer.start)
            }
            // With five pairs found once, the other five come first.
            val found = StartWord.entries.map { it.start }.distinct().shuffled(Random(seed)).take(5)
            val then = startRounds(Random(seed), found.associateWith { 1 })
            assertTrue(then.none { it.said.start in found }, "repite lo que ya ha encontrado")
        }
    }

    @Test
    fun eachWordOfTheIslandHasItsPartnerAndItsPlainName() {
        val pairs = StartWord.entries.groupBy { it.start }
        assertTrue(pairs.values.all { it.size == 2 }, "cada palabra, con otra que empieza igual")
        assertEquals("corazon", StartWord.CORAZON.word)
        assertEquals(listOf("co", "ra", "zón"), StartWord.CORAZON.syllables)
        for (word in StartWord.entries) {
            assertEquals(word.name.lowercase(), word.word, "su palabra son sus sílabas")
            assertTrue(word.syllables.all { syllable -> syllable.any { it in "aeiouáéíóú" } }, "una sílaba sin vocal en $word")
        }
    }

    @Test
    fun everyLetterHasItsWord() {
        assertEquals(LETTERS, Word.entries.map { it.letter })
        assertEquals(Word.OSO, Word.of('o'))
        assertEquals(Word.SOL, Word.of('s'))
    }
}
