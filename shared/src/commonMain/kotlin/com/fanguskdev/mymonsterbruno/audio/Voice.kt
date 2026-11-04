package com.fanguskdev.mymonsterbruno.audio

import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Happening
import com.fanguskdev.mymonsterbruno.game.HousePart
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.MAX_COUNT
import com.fanguskdev.mymonsterbruno.game.NAME_ALPHABET
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.plain

/**
 * The voice lines, by the names of their files in `files/voces/`. What each one says is in voces/guion.tsv, where
 * tools/voces.py makes them from (ADR 0004); the lines that start with `n_` are the narrator's, with `b_`, Bruno's.
 */
internal object Voice {
    const val TITLE = "n_titulo"
    const val TAP_TO_START = "n_toca_empezar"
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
    const val TO_FEELINGS = "n_isla_emociones"
    const val HOW_FEELS = "n_como_se_siente"
    const val THANKS_FEELINGS = "b_gracias_emociones"
    const val MISSION_FEELING = "n_mision_emocion"
    const val TALK = "n_habla"
    const val HOLD_MIC = "n_microfono"
    const val CANT_HEAR = "b_no_te_oigo"
    const val THANKS_WORDS = "b_gracias_palabras"
    const val TO_WORDS = "n_isla_palabras"
    const val ITS_YOU = "b_eres_tu"
    const val NAME_INTRO = "n_nombre_intro"
    const val TEACH_ME_YOUR_NAME = "b_ensename_nombre"
    const val KNOWS_YOUR_NAME = "b_ya_se_tu_nombre"

    /** «¡Hola! ¿Cómo te sientes hoy?», the feeling of the day (ADR 0019), and what she does with her photo or the face. */
    const val HOW_DO_YOU_FEEL = "b_como_te_sientes"
    const val TAKE_YOUR_PHOTO = "n_lleva_tu_foto"
    const val TAKE_THE_FACE = "n_lleva_la_carita"

    /** «Y yo estoy contento, ¡porque has venido a jugar conmigo!», when Bruno puts his own in the jar of joy. */
    const val ME_TOO = "b_yo_contento"
    const val WORDS_INTRO = "n_palabras"
    const val THANKS_STARTS = "b_gracias_sonidos"
    const val MISSION_NAME = "n_mision_nombre"

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

    /** «¡Oh, no! A Bruno se le ha caído el helado.» */
    fun happened(happening: Happening) = "n_pasa_${happening.word}"

    /** «¡Sí! Bruno está triste.» */
    fun feels(feeling: Feeling) = "n_esta_${feeling.word}"

    /** «Esa carita está contenta. Mira bien a Bruno.» */
    fun notThatFace(feeling: Feeling) = "n_cara_${feeling.word}"

    /** «¿Le das un abrazo? Toca a Bruno.» */
    fun help(feeling: Feeling) = "n_ayuda_${feeling.word}"

    /** «¡Gracias! Ya estoy mejor.» */
    fun helped(feeling: Feeling) = "b_gracias_${feeling.word}"

    /** «Alegría.», when she touches the jar of [feeling] on the morning of the feeling of the day. */
    fun jar(feeling: Feeling) = "n_bote_${feeling.word}"

    /** «¡Qué bien! Me gusta verte contenta.», when she has put herself in the jar of [feeling]. */
    fun today(feeling: Feeling) = "b_hoy_${feeling.word}"

    /** «¡Dime mariposa!» */
    fun tellMe(word: TalkWord) = "b_dime_${word.word}"

    /** «¡Mariposa!», when he says it himself: there is no microphone, or he could not hear her. */
    fun word(word: TalkWord) = "b_palabra_${word.word}"

    /** «Huum... Eso no es manzana. ¿Me dices manzana?», when the device made out another word (ADR 0015). */
    fun notThat(word: TalkWord) = "b_no_es_${word.word}"

    /** «La ele.», «La eñe.», «La a, con tilde.»: a letter of her name (ADR 0017). */
    fun letterName(letter: Char) = "n_letra_" + when (letter) {
        'ñ' -> "enye"
        'á' -> "a_tilde"
        'é' -> "e_tilde"
        'í' -> "i_tilde"
        'ó' -> "o_tilde"
        'ú' -> "u_tilde"
        else -> letter.toString()
    }

    /** «¡Ma!», with a clap. */
    fun syllable(syllable: String) = "b_silaba_${plain(syllable)}"

    /** «¡Cuatro palmadas!» */
    fun claps(count: Int) = "n_palmadas_$count"

    /** «¿Qué empieza como pato?» */
    fun startsLike(word: StartWord) = "n_como_${word.word}"

    /** «¡Sí! Pato y pajarita empiezan por pa.» */
    fun bothStart(said: StartWord, found: StartWord) = "b_si_${said.word}_${found.word}"

    /** «Huum... Luna empieza por lu.» */
    fun startsWith(word: StartWord) = "b_empieza_${word.word}"

    /** «Busca otra que empiece por pa.» */
    fun findAnother(start: String) = "n_busca_${plain(start)}"

    /** Every line, to load them all when the game opens. */
    val ALL: List<String> = listOf(
        // The start screen's first: they are said a moment after the game opens, while the rest load.
        TITLE, TAP_TO_START, EGG, EGG_AGAIN, HELLO_I_AM_BRUNO, CHOOSE_COLOR, CHOOSE_TOP, GREEN_BUTTON, LOVE_IT, HANDSOME, HELLO,
        GO_TO_ISLAND, TICKLES, GIGGLE, WHICH_ISLAND, TO_LETTERS, TO_NUMBERS, HUNGRY_FOR_LETTERS, DRAG_HINT, THANKS_LETTERS,
        WELL_DONE, THANKS_STRAWBERRIES, PRESENT, THANKS_PRESENT, GROWING, YAWN, MISSION_LETTER, MISSION_COUNT,
        MISSION_COLOR, SEE_YOU_TOMORROW, ASLEEP, CALM_INTRO, CALM_LET_GO, CALM_AGAIN, CALM_END, TO_COLORS, LOVE_BALLOONS,
        THANKS_BALLOONS, LOOK_AT_THE_ROW, WHICH_NEXT, LOOK_AGAIN, THANKS_SERIES, TO_SHAPES, BUILD_HOUSE, THANKS_HOUSE,
        MISSION_SHAPE, TO_FEELINGS, HOW_FEELS, THANKS_FEELINGS, MISSION_FEELING, TALK, HOLD_MIC, CANT_HEAR, THANKS_WORDS,
        TO_WORDS, WORDS_INTRO, THANKS_STARTS, MISSION_NAME, ITS_YOU, NAME_INTRO, TEACH_ME_YOUR_NAME, KNOWS_YOUR_NAME,
        HOW_DO_YOU_FEEL, TAKE_YOUR_PHOTO, TAKE_THE_FACE, ME_TOO,
    ) +
        LETTERS.flatMap { listOf(want(it), yum(it), thatIs(it), iWant(it), trace(it), traced(it)) } +
        (1..MAX_COUNT).flatMap { listOf(askCount(it), number(it), strawberries(it)) } +
        Hue.entries.flatMap { listOf(wantBalloon(it), yes(it), thatColor(it), iWantBalloon(it), color(it)) } +
        HousePart.entries.map { askPart(it) } +
        Shape.entries.flatMap { listOf(yes(it), thatShape(it), iWantShape(it)) } +
        Happening.entries.map { happened(it) } +
        Feeling.entries.flatMap { listOf(feels(it), notThatFace(it), help(it), helped(it), jar(it), today(it)) } +
        TalkWord.entries.flatMap { listOf(tellMe(it), word(it), notThat(it)) } +
        (TalkWord.entries.flatMap { it.syllables } + StartWord.entries.flatMap { it.syllables }).map { syllable(it) }.distinct() +
        (1..TalkWord.entries.maxOf { it.syllables.size }).map { claps(it) } +
        StartWord.entries.flatMap { listOf(startsLike(it), startsWith(it)) } +
        StartWord.entries.flatMap { said ->
            StartWord.entries.filter { it != said && it.start == said.start }.map { bothStart(said, it) }
        } +
        StartWord.entries.map { it.start }.distinct().map { findAnother(it) } +
        NAME_ALPHABET.map { letterName(it) }
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
    START("inicio"),
    CLAP("palmada"),
}
