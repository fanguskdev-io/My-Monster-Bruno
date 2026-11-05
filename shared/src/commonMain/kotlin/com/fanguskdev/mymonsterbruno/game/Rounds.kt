package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The vowels, the first letters of the classroom of four-year-olds (docs/diseno.md). */
internal val VOWELS = listOf('a', 'e', 'i', 'o', 'u')

/** The first consonants of the classroom of four-year-olds (ADR 0009). */
internal val CONSONANTS = listOf('m', 'p', 'l', 's')

/** The next consonants of the classroom (ADR 0014): they come in once she knows the first ones. */
internal val NEXT_CONSONANTS = listOf('t', 'n', 'd')

/** Every letter of the island of letters: the vowels first, then the consonants. */
internal val LETTERS = VOWELS + CONSONANTS + NEXT_CONSONANTS

/** The letters in steps, as they come in: each step once she has practised every letter of the one before. */
private val LETTER_STEPS = listOf(VOWELS, CONSONANTS, NEXT_CONSONANTS)

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
    TOMATE('t'),
    NUBE('n'),
    DADO('d'),
    ;

    companion object {
        fun of(letter: Char): Word = entries.first { it.letter == letter }
    }
}

/**
 * The letters she has practised least in [practised] (by letter), [count] of them, ties at random. They come in steps:
 * the first day, the vowels; the M, the P, the L and the S once she has practised every vowel; the T, the N and the D
 * once she has practised those four too.
 */
internal fun leastPractised(practised: Map<String, Int>, count: Int, random: Random): List<Char> {
    fun knows(step: List<Char>) = step.all { (practised[it.toString()] ?: 0) > 0 }
    val open = LETTER_STEPS.filterIndexed { i, _ -> LETTER_STEPS.take(i).all(::knows) }.flatten()
    return open.shuffled(random)
        .sortedWith(compareBy<Char>({ practised[it.toString()] ?: 0 }, { letter -> LETTER_STEPS.indexOfFirst { letter in it } }))
        .take(count)
}

/**
 * A round of Comeletras: Bruno wants [wanted] and [cookies] are the ones that can go on the plate, his among them; the
 * plate has as many as she chooses from now (ADR 0020).
 */
internal data class LetterRound(val wanted: Char, val cookies: List<Char>)

/** The five rounds of Comeletras: the letters she has fed Bruno least ([fed]), each with up to three other letters. */
internal fun letterRounds(random: Random, fed: Map<String, Int> = emptyMap()): List<LetterRound> =
    leastPractised(fed, LETTER_ROUNDS, random).map { wanted ->
        val others = (LETTERS - wanted).shuffled(random).take(MAX_CHOICES - 1)
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

/** A round of the balloons: Bruno wants [wanted], and [balloons], his colour among them, are the ones that can be out. */
internal data class BalloonRound(val wanted: Hue, val balloons: List<Hue>)

/** The five rounds of the balloons: five different colours, each with up to three others to choose from. */
internal fun balloonRounds(random: Random): List<BalloonRound> = Hue.entries.shuffled(random).take(BALLOON_ROUNDS).map { wanted ->
    val others = (Hue.entries - wanted).shuffled(random).take(MAX_CHOICES - 1)
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

/** A round of the feelings: what happens to Bruno, and the faces she can choose from to say how he feels, one right. */
internal data class FeelingRound(val happening: Happening, val faces: List<Feeling>)

/** The five things that happen to Bruno, in any order but the lullaby last, so the island ends calm. */
internal fun feelingRounds(random: Random): List<FeelingRound> =
    ((Happening.entries - Happening.LULLABY).shuffled(random) + Happening.LULLABY).map { happening ->
        val others = (Feeling.entries - happening.feeling).shuffled(random).take(MAX_CHOICES - 1)
        FeelingRound(happening, (others + happening.feeling).shuffled(random))
    }

/**
 * The words of «Habla con Bruno» (ADR 0011), cut in syllables the way they clap them in the classroom: from one clap to
 * four. Each has its picture, and none has an accent, so its [word] names its files as it is.
 */
internal enum class TalkWord(val syllables: List<String>) {
    SOL(listOf("sol")),
    LUNA(listOf("lu", "na")),
    OSO(listOf("o", "so")),
    FRESA(listOf("fre", "sa")),
    GLOBO(listOf("glo", "bo")),
    PELOTA(listOf("pe", "lo", "ta")),
    MANZANA(listOf("man", "za", "na")),
    REGALO(listOf("re", "ga", "lo")),
    MARIPOSA(listOf("ma", "ri", "po", "sa")),
    ELEFANTE(listOf("e", "le", "fan", "te")),
    ;

    /** The word, as Bruno asks for it and the save counts it: «mariposa». */
    val word: String get() = syllables.joinToString("")
}

/** The four words of a visit: the ones she has said least ([talked], by word), ties at random, the fewest claps first. */
internal fun talkRounds(random: Random, talked: Map<String, Int> = emptyMap()): List<TalkWord> = TalkWord.entries.shuffled(random)
    .sortedBy { talked[it.word] ?: 0 }
    .take(TALK_ROUNDS)
    .sortedBy { it.syllables.size }

internal const val TALK_ROUNDS = 4

/**
 * The words of the island of words (ADR 0013), cut in syllables as they clap them in the classroom, in pairs that start
 * the same: «PA-to» and «PA-ja-ri-ta». Each has its picture. The syllables are written as they are said, with their
 * accent; [word] and the names of the files go without it.
 */
internal enum class StartWord(val syllables: List<String>) {
    GAFAS(listOf("ga", "fas")),
    GALLETA(listOf("ga", "lle", "ta")),
    CORONA(listOf("co", "ro", "na")),
    CORAZON(listOf("co", "ra", "zón")),
    MANO(listOf("ma", "no")),
    MARIPOSA(listOf("ma", "ri", "po", "sa")),
    CASA(listOf("ca", "sa")),
    CAMA(listOf("ca", "ma")),
    PATO(listOf("pa", "to")),
    PAJARITA(listOf("pa", "ja", "ri", "ta")),
    LUNA(listOf("lu", "na")),
    LUPA(listOf("lu", "pa")),
    PELOTA(listOf("pe", "lo", "ta")),
    PERA(listOf("pe", "ra")),
    GORRO(listOf("go", "rro")),
    GOTA(listOf("go", "ta")),
    OSO(listOf("o", "so")),
    OJO(listOf("o", "jo")),
    REGALO(listOf("re", "ga", "lo")),
    RELOJ(listOf("re", "loj")),
    ;

    /** The word without its accent, as the save and the files name it: «corazon». */
    val word: String get() = plain(syllables.joinToString(""))

    /** The syllable it starts with: «pa». */
    val start: String get() = syllables.first()
}

/** A round of the island of words: Bruno says [said], and she finds among [choices] the one that starts the same. */
internal data class StartRound(val said: StartWord, val choices: List<StartWord>) {
    val answer: StartWord get() = choices.first { it.start == said.start }
}

/**
 * The five rounds of the island of words: the pairs whose start she has found least ([found], by syllable), ties at
 * random. Bruno says one word of the pair, and the other is among the pictures, with up to three of other pairs.
 */
internal fun startRounds(random: Random, found: Map<String, Int> = emptyMap()): List<StartRound> {
    val pairs = StartWord.entries.groupBy { it.start }.values.toList()
    return pairs.shuffled(random).sortedBy { found[plain(it.first().start)] ?: 0 }.take(START_ROUNDS).map { pair ->
        val (said, match) = pair.shuffled(random)
        val others = (pairs - setOf(pair)).shuffled(random).take(MAX_CHOICES - 1).map { it.random(random) }
        StartRound(said, (others + match).shuffled(random))
    }
}

/** [text] without the accents of Spanish, for the names of files: «zón» is «zon». */
internal fun plain(text: String): String = text.map {
    when (it) {
        'á' -> 'a'
        'é' -> 'e'
        'í' -> 'i'
        'ó' -> 'o'
        'ú' -> 'u'
        else -> it
    }
}.joinToString("")

internal const val START_ROUNDS = 5
