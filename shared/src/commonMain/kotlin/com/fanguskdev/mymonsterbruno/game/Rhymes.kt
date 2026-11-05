package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/**
 * The words of the rhymes (ADR 0021), in pairs that end the same, «gato» and «pato», each with its picture. No two pairs
 * end the same, so among the cards only one rhymes with Bruno's word. [word] is how it is said and written, with its
 * accent; [key] names its files, without it.
 */
internal enum class RhymeWord(val word: String, val ending: String) {
    GATO("gato", "ato"),
    PATO("pato", "ato"),
    LUNA("luna", "una"),
    CUNA("cuna", "una"),
    RATON("ratón", "ón"),
    BOTON("botón", "ón"),
    RANA("rana", "ana"),
    LANA("lana", "ana"),
    SOL("sol", "ol"),
    COL("col", "ol"),
    PEZ("pez", "ez"),
    NUEZ("nuez", "ez"),
    MESA("mesa", "esa"),
    FRESA("fresa", "esa"),
    FOCA("foca", "oca"),
    BOCA("boca", "oca"),
    LLAVE("llave", "ave"),
    NAVE("nave", "ave"),
    SETA("seta", "eta"),
    MALETA("maleta", "eta"),
    ;

    /** The word without its accent, as the save and the files name it: «raton». */
    val key: String get() = name.lowercase()

    /** The pair it belongs to, by its first word: «gato» for both «gato» and «pato». */
    val pair: String get() = entries.first { it.ending == ending }.key
}

/** A round of the rhymes: Bruno says [said], and she finds among [choices] the one that ends the same. */
internal data class RhymeRound(val said: RhymeWord, val choices: List<RhymeWord>) {
    val answer: RhymeWord get() = choices.first { it != said && it.ending == said.ending }
}

/**
 * The five rounds of the rhymes: the pairs she has found least ([found], by pair), ties at random. Bruno says one word
 * of the pair, and the other is among the pictures with up to three of other pairs, as many as she chooses from now.
 */
internal fun rhymeRounds(random: Random, found: Map<String, Int> = emptyMap()): List<RhymeRound> {
    val pairs = RhymeWord.entries.groupBy { it.ending }.values.toList()
    return pairs.shuffled(random).sortedBy { found[it.first().pair] ?: 0 }.take(RHYME_ROUNDS).map { pair ->
        val (said, match) = pair.shuffled(random)
        val others = (pairs - setOf(pair)).shuffled(random).take(MAX_CHOICES - 1).map { it.random(random) }
        RhymeRound(said, (others + match).shuffled(random))
    }
}

internal const val RHYME_ROUNDS = 5
