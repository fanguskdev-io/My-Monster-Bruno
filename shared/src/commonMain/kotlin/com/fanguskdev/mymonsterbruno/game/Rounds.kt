package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The vowels, the first letters of the classroom of four-year-olds (docs/diseno.md). */
internal val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

/** The first consonants of the classroom of four-year-olds (ADR 0009). */
internal val CONSONANTS = listOf('m', 'p', 'l', 's')

/** Every letter of the island of letters: the vowels first, then the consonants. */
internal val LETTERS = VOWELS + CONSONANTS

/** The word of each letter, the one Bruno says and the bubble draws: «la a de avión», «la eme de manzana». */
internal enum class Word(val letter: Char) {
    AVION('a'),
    ELEFANTE('e'),
    IGLU('i'),
    OSO('o'),
    UVA('u'),
    MANZANA('m'),
    PELOTA('p'),
    LUNA('l'),
    SOL('s'),
    ;

    companion object {
        fun of(letter: Char): Word = entries.first { it.letter == letter }
    }
}

/**
 * The letters she has practised least in [practised] (by letter), [count] of them: ties at random, but the vowels before
 * the consonants, so the first day is the vowels and the consonants come in once she knows them.
 */
internal fun leastPractised(practised: Map<String, Int>, count: Int, random: Random): List<Char> = LETTERS.shuffled(random)
    .sortedWith(compareBy<Char>({ practised[it.toString()] ?: 0 }, { if (it in VOWELS) 0 else 1 }))
    .take(count)

/** A round of Comeletras: Bruno wants [wanted] and the plate has three cookies, [cookies], one of them his. */
internal data class LetterRound(val wanted: Char, val cookies: List<Char>)

/** The five rounds of Comeletras: the letters she has fed Bruno least ([fed]), each with two other letters on the plate. */
internal fun letterRounds(random: Random, fed: Map<String, Int> = emptyMap()): List<LetterRound> =
    leastPractised(fed, LETTER_ROUNDS, random).map { wanted ->
        val others = (LETTERS - wanted).shuffled(random).take(COOKIES - 1)
        LetterRound(wanted, (others + wanted).shuffled(random))
    }

/**
 * How many strawberries Bruno wants in each of the five rounds. Until she has counted each of one to five ([counted]),
 * those, each once, never five first. Then two of one to five and three of six to ten, the ones she has counted least,
 * never the biggest first.
 */
internal fun countRounds(random: Random, counted: Map<String, Int> = emptyMap()): List<Int> {
    fun least(numbers: IntRange, count: Int) = numbers.shuffled(random).sortedBy { counted[it.toString()] ?: 0 }.take(count)
    val small = 1..SMALL_COUNT
    val knowsSmall = small.all { (counted[it.toString()] ?: 0) > 0 }
    val rounds = (if (knowsSmall) least(small, 2) + least(SMALL_COUNT + 1..MAX_COUNT, 3) else small.toList())
        .shuffled(random)
        .toMutableList()
    if (rounds.first() == rounds.max()) rounds.add(rounds.removeAt(0))
    return rounds
}

/** The two letters to trace: the ones she has traced least, the vowels first, ties at random. */
internal fun tracePicks(traced: Map<String, Int>, random: Random): List<Char> = leastPractised(traced, TRACED_LETTERS, random)

internal const val COOKIES = 3
internal const val LETTER_ROUNDS = 5
internal const val SMALL_COUNT = 5
internal const val MAX_COUNT = 10
internal const val TRACED_LETTERS = 2

/** The colours of the island of colours (ADR 0007), with the word that names them: «el globo rojo». */
internal enum class Hue(val word: String) {
    RED("rojo"),
    YELLOW("amarillo"),
    BLUE("azul"),
    GREEN("verde"),
    ORANGE("naranja"),
    PURPLE("morado"),
}

/** A round of the balloons: Bruno wants [wanted] and there are three balloons, [balloons], one of them his colour. */
internal data class BalloonRound(val wanted: Hue, val balloons: List<Hue>)

/** The five rounds of the balloons: five different colours, each with two others to choose from. */
internal fun balloonRounds(random: Random): List<BalloonRound> = Hue.entries.shuffled(random).take(BALLOON_ROUNDS).map { wanted ->
    val others = (Hue.entries - wanted).shuffled(random).take(BALLOONS - 1)
    BalloonRound(wanted, (others + wanted).shuffled(random))
}

/**
 * A series of balloons: [pattern] over and over, [shown] of them in a row and then a gap, which takes [answer]. She picks
 * it among three [choices]: the colours of the pattern and, when it has two, another one.
 */
internal data class SeriesRound(val pattern: List<Hue>, val shown: Int, val choices: List<Hue>) {
    /** The colour of the [i]th balloon of the row. */
    fun at(i: Int): Hue = pattern[i % pattern.size]

    val answer: Hue get() = at(shown)
}

/**
 * The four series: two of two colours (red, blue, red…) and two of three, as they make them in the classroom. The row
 * shows the pattern once and a bit more, so the gap is not always its first colour.
 */
internal fun seriesRounds(random: Random): List<SeriesRound> = SERIES_SIZES.map { size ->
    val pattern = Hue.entries.shuffled(random).take(size)
    val extra = (Hue.entries - pattern.toSet()).shuffled(random).take(SERIES_CHOICES - size)
    SeriesRound(pattern, shown = size + 1 + random.nextInt(2), choices = (pattern + extra).shuffled(random))
}

internal const val BALLOONS = 3
internal const val BALLOON_ROUNDS = 5
internal val SERIES_SIZES = listOf(2, 2, 3, 3)
internal const val SERIES_CHOICES = 3

/** The shapes of the island of shapes (ADR 0008), with the word that names them (without accents: it is a file name). */
internal enum class Shape(val word: String) {
    SQUARE("cuadrado"),
    TRIANGLE("triangulo"),
    CIRCLE("circulo"),
    RECTANGLE("rectangulo"),
}

/** The parts of Bruno's house, in the order they are built, each with its shape. */
internal enum class HousePart(val shape: Shape, val word: String) {
    WALLS(Shape.SQUARE, "paredes"),
    ROOF(Shape.TRIANGLE, "tejado"),
    DOOR(Shape.RECTANGLE, "puerta"),
    ROUND_WINDOW(Shape.CIRCLE, "ventana_redonda"),
    WINDOW(Shape.SQUARE, "ventana"),
    CHIMNEY(Shape.RECTANGLE, "chimenea"),
}

/** A round of the house: Bruno wants [part], and there are three pieces, [pieces], one of them its shape. */
internal data class HouseRound(val part: HousePart, val pieces: List<Shape>)

/** The six rounds of the house, in the order it is built, each with two other shapes to choose from. */
internal fun houseRounds(random: Random): List<HouseRound> = HousePart.entries.map { part ->
    val others = (Shape.entries - part.shape).shuffled(random).take(HOUSE_PIECES - 1)
    HouseRound(part, (others + part.shape).shuffled(random))
}

internal const val HOUSE_PIECES = 3

/** How Bruno feels on the island of feelings (ADR 0010), with the word of its files. */
internal enum class Feeling(val word: String) {
    JOY("alegria"),
    SADNESS("tristeza"),
    ANGER("enfado"),
    FEAR("miedo"),
    CALM("calma"),
}

/** What happens to Bruno, and how it makes him feel. */
internal enum class Happening(val feeling: Feeling, val word: String) {
    BIRTHDAY(Feeling.JOY, "cumple"),
    ICE_CREAM(Feeling.SADNESS, "helado"),
    TOWER(Feeling.ANGER, "torre"),
    STORM(Feeling.FEAR, "tormenta"),
    LULLABY(Feeling.CALM, "nana"),
}

/** A round of the feelings: what happens to Bruno, and three faces to say how he feels, one of them right. */
internal data class FeelingRound(val happening: Happening, val faces: List<Feeling>)

/** The five things that happen to Bruno, in any order but the lullaby last, so the island ends calm. */
internal fun feelingRounds(random: Random): List<FeelingRound> =
    ((Happening.entries - Happening.LULLABY).shuffled(random) + Happening.LULLABY).map { happening ->
        val others = (Feeling.entries - happening.feeling).shuffled(random).take(FEELING_FACES - 1)
        FeelingRound(happening, (others + happening.feeling).shuffled(random))
    }

internal const val FEELING_FACES = 3
