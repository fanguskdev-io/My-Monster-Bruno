package com.fanguskdev.mymonsterbruno.audio

import com.fanguskdev.mymonsterbruno.game.HousePart
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.MAX_COUNT
import com.fanguskdev.mymonsterbruno.game.Shape

/**
 * The voice lines, by the names of their files in `files/voces/`. What each one says is in voces/guion.tsv, where
 * tools/voces.py makes them from (ADR 0004); the lines that start with `n_` are the narrator's, with `b_`, Bruno's.
 */
internal object Voice {
    const val EGG = "n_huevo"
    const val EGG_AGAIN = "n_huevo_otra"
    const val HELLO_I_AM_BRUNO = "b_hola_soy_bruno"
    const val CHOOSE_COLOR = "n_elige_color"
    const val CHOOSE_TOP = "n_elige_cabeza"
    const val GREEN_BUTTON = "n_boton_verde"
    const val LOVE_IT = "b_me_encanta"
    const val HANDSOME = "b_que_guapo"
    const val HELLO = "b_hola"
    const val GO_TO_ISLAND = "b_vamos_isla"
    const val TICKLES = "b_jiji"
    const val GIGGLE = "b_jiji_corto"
    const val WHICH_ISLAND = "n_que_isla"
    const val TO_LETTERS = "n_isla_letras"
    const val TO_NUMBERS = "n_isla_numeros"
    const val TOO_FAR = "n_isla_lejos"
    const val HUNGRY_FOR_LETTERS = "b_hambre_letras"
    const val DRAG_HINT = "n_arrastra"
    const val THANKS_LETTERS = "b_gracias_letras"
    const val WELL_DONE = "n_trazo_bien"
    const val THANKS_STRAWBERRIES = "b_gracias_fresas"
    const val PRESENT = "n_regalo"
    const val THANKS_PRESENT = "b_gracias_regalo"
    const val GROWING = "b_crezco"
    const val YAWN = "b_bostezo"
    const val MISSION_LETTER = "n_mision_letra"
    const val MISSION_COUNT = "n_mision_contar"
    const val MISSION_COLOR = "n_mision_color"
    const val SEE_YOU_TOMORROW = "b_hasta_manana"
    const val ASLEEP = "b_dormido"
    const val CALM_INTRO = "n_calma_intro"
    const val CALM_LET_GO = "n_calma_suelta"
    const val CALM_AGAIN = "n_calma_otra"
    const val CALM_END = "n_calma_fin"
    const val TO_COLORS = "n_isla_colores"
    const val LOVE_BALLOONS = "b_globos"
    const val THANKS_BALLOONS = "b_gracias_globos"
    const val LOOK_AT_THE_ROW = "n_mira_fila"
    const val WHICH_NEXT = "n_cual_va_ahora"
    const val LOOK_AGAIN = "n_mira_otra_vez"
    const val THANKS_SERIES = "b_gracias_series"
    const val TO_SHAPES = "n_isla_formas"
    const val BUILD_HOUSE = "n_casa"
    const val THANKS_HOUSE = "b_gracias_casa"
    const val MISSION_SHAPE = "n_mision_forma"

    /** «¡Quiero la a! La a de avión.» */
    fun want(vowel: Char) = "b_quiero_$vowel"

    /** «¡Ñam! ¡La a de avión!» */
    fun yum(vowel: Char) = "b_nam_$vowel"

    /** «Huum... Esa es la e.» */
    fun thatIs(vowel: Char) = "b_esa_es_$vowel"

    /** «¡Yo quiero la a!» */
    fun iWant(vowel: Char) = "b_yo_quiero_$vowel"

    /** «Esta es la a. Sigue el camino con el dedo.» */
    fun trace(vowel: Char) = "n_trazar_$vowel"

    /** «¡La a! ¡Ya sé hacer la a!» */
    fun traced(vowel: Char) = "b_letra_$vowel"

    /** «Bruno quiere tres fresas. ¿Se las das?» */
    fun askCount(count: Int) = "n_contar_$count"

    /** «Tres.» */
    fun number(count: Int) = "n_num_$count"

    /** «¡Ñam! ¡Tres fresas!» */
    fun strawberries(count: Int) = "b_fresas_$count"

    /** «¡Quiero el globo rojo!» */
    fun wantBalloon(hue: Hue) = "b_quiero_globo_${hue.word}"

    /** «¡Sí! ¡El rojo!» */
    fun yes(hue: Hue) = "b_si_${hue.word}"

    /** «Huum... Ese es el azul.» */
    fun thatColor(hue: Hue) = "b_ese_es_${hue.word}"

    /** «¡Yo quiero el rojo!» */
    fun iWantBalloon(hue: Hue) = "b_yo_quiero_globo_${hue.word}"

    /** «Rojo.», to read a series out. */
    fun color(hue: Hue) = "n_color_${hue.word}"

    /** «¡Ahora, el tejado! Dame el triángulo.» */
    fun askPart(part: HousePart) = "b_pide_${part.word}"

    /** «¡Sí! ¡El triángulo!» */
    fun yes(shape: Shape) = "b_si_${shape.word}"

    /** «Huum... Ese es el círculo.» */
    fun thatShape(shape: Shape) = "b_ese_es_${shape.word}"

    /** «¡Yo quiero el triángulo!» */
    fun iWantShape(shape: Shape) = "b_yo_quiero_${shape.word}"

    /** Every line, to load them all when the game opens. */
    val ALL: List<String> = listOf(
        EGG, EGG_AGAIN, HELLO_I_AM_BRUNO, CHOOSE_COLOR, CHOOSE_TOP, GREEN_BUTTON, LOVE_IT, HANDSOME, HELLO, GO_TO_ISLAND,
        TICKLES, GIGGLE, WHICH_ISLAND, TO_LETTERS, TO_NUMBERS, TOO_FAR, HUNGRY_FOR_LETTERS, DRAG_HINT, THANKS_LETTERS,
        WELL_DONE, THANKS_STRAWBERRIES, PRESENT, THANKS_PRESENT, GROWING, YAWN, MISSION_LETTER, MISSION_COUNT,
        MISSION_COLOR, SEE_YOU_TOMORROW, ASLEEP, CALM_INTRO, CALM_LET_GO, CALM_AGAIN, CALM_END, TO_COLORS, LOVE_BALLOONS,
        THANKS_BALLOONS, LOOK_AT_THE_ROW, WHICH_NEXT, LOOK_AGAIN, THANKS_SERIES, TO_SHAPES, BUILD_HOUSE, THANKS_HOUSE,
        MISSION_SHAPE,
    ) +
        LETTERS.flatMap { listOf(want(it), yum(it), thatIs(it), iWant(it), trace(it), traced(it)) } +
        (1..MAX_COUNT).flatMap { listOf(askCount(it), number(it), strawberries(it)) } +
        Hue.entries.flatMap { listOf(wantBalloon(it), yes(it), thatColor(it), iWantBalloon(it), color(it)) } +
        HousePart.entries.map { askPart(it) } +
        Shape.entries.flatMap { listOf(yes(it), thatShape(it), iWantShape(it)) }
}

/** The sound effects, by the names of their files in `files/sonidos/` (tools/sonidos.py makes them). */
internal enum class Sfx(val file: String) {
    TAP("toque"),
    CRACK("crac"),
    HATCH("nacer"),
    MUNCH("nam"),
    RIGHT("acierto"),
    BOING("boing"),
    WHOOSH("vuela"),
    SPARKLE("chispas"),
    STROKE("trazo"),
    LETTER("letra"),
    PRESENT("regalo"),
    PICK("fresa"),
    POP("pop"),
}
