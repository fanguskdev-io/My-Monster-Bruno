package com.fanguskdev.mymonsterbruno.audio

import com.fanguskdev.mymonsterbruno.game.COUNTDOWN_FROM
import com.fanguskdev.mymonsterbruno.game.Feeling
import com.fanguskdev.mymonsterbruno.game.Happening
import com.fanguskdev.mymonsterbruno.game.Hop
import com.fanguskdev.mymonsterbruno.game.HousePart
import com.fanguskdev.mymonsterbruno.game.Hue
import com.fanguskdev.mymonsterbruno.game.LETTERS
import com.fanguskdev.mymonsterbruno.game.LINE_LAST
import com.fanguskdev.mymonsterbruno.game.MAX_COUNT
import com.fanguskdev.mymonsterbruno.game.NAME_ALPHABET
import com.fanguskdev.mymonsterbruno.game.Place
import com.fanguskdev.mymonsterbruno.game.Placing
import com.fanguskdev.mymonsterbruno.game.RhymeWord
import com.fanguskdev.mymonsterbruno.game.STORY_STEPS
import com.fanguskdev.mymonsterbruno.game.SUMS
import com.fanguskdev.mymonsterbruno.game.Shape
import com.fanguskdev.mymonsterbruno.game.Side
import com.fanguskdev.mymonsterbruno.game.StartWord
import com.fanguskdev.mymonsterbruno.game.Story
import com.fanguskdev.mymonsterbruno.game.Sum
import com.fanguskdev.mymonsterbruno.game.TalkWord
import com.fanguskdev.mymonsterbruno.game.Thing
import com.fanguskdev.mymonsterbruno.game.Weather
import com.fanguskdev.mymonsterbruno.game.Weekday
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

    /** «Ordena la historia», on the island of feelings (ADR 0025): what the voice asks, and Bruno's thanks. */
    const val STORY_INTRO = "n_historia_intro"
    const val WHAT_FIRST = "n_que_primero"
    const val AND_NEXT = "n_y_despues"
    const val AND_AT_THE_END = "n_y_al_final"
    const val LATER = "n_mas_tarde"
    const val THANKS_STORIES = "b_gracias_historias"

    /** The stickers of the room (ADR 0024): Bruno gives her the one of each island she finishes, and she sticks it. */
    const val STICKER_LETTERS = "b_pegatina_letras"
    const val STICKER_NUMBERS = "b_pegatina_numeros"
    const val STICKER_COLORS = "b_pegatina_colores"
    const val STICKER_SHAPES = "b_pegatina_formas"
    const val STICKER_FEELINGS = "b_pegatina_emociones"
    const val STICKER_WORDS = "b_pegatina_palabras"
    const val STICK_IT = "n_pega_pegatina"
    const val SO_PRETTY = "b_que_bonita"

    /** The countdown of Bruno's rocket, on the island of numbers (ADR 0023). */
    const val ROCKET_INTRO = "n_cohete_intro"
    const val PRESS_ZERO = "n_boton_cero"
    const val LIFT_OFF = "n_despegue"
    const val OFF_I_GO = "b_alla_voy"
    const val THANKS_ROCKET = "b_gracias_cohete"

    /** The assembly, after the feeling of the day (ADR 0030): how it starts, the weather, and how the day starts. */
    const val ASSEMBLY_INTRO = "n_asamblea_intro"
    const val WHAT_WEATHER = "n_que_tiempo"
    const val DAY_STARTS = "b_empieza_el_dia"

    /** The sums of strawberries, on the island of numbers (ADR 0029): how they start, the question, and Bruno's thanks. */
    const val SUMS_INTRO = "n_sumas_intro"
    const val HOW_MANY_NOW = "n_cuantas_ahora"
    const val COUNT_THEM_ALL = "n_contamos"
    const val THANKS_SUMS = "b_gracias_sumas"

    /** «Dentro y fuera», on the island of shapes (ADR 0028): how it starts, Bruno hidden, and his thanks. */
    const val PLACES_INTRO = "n_sitios_intro"
    const val HIDDEN = "b_escondido"
    const val THANKS_PLACES = "b_gracias_sitios"

    /** «El anterior y el siguiente», on the number line of the island of numbers (ADR 0026). */
    const val NUMBER_LINE_INTRO = "n_recta_intro"
    const val BESIDE_BRUNO = "n_al_lado"
    const val THANKS_NUMBER_LINE = "b_gracias_recta"

    /** The rhymes, on the island of words (ADR 0021): how they start and how Bruno thanks her. */
    const val RHYMES_INTRO = "n_rimas_intro"
    const val THANKS_RHYMES = "b_gracias_rimas"
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

    /** «Primero, la semilla cae en la tierra.», the picture [step] (from 0) of [story], as the voice tells it. */
    fun storyStep(story: Story, step: Int) = "n_${story.key}_${step + 1}"

    /** What the voice asks for the picture [step] (from 0): what happens first, next, or at the end. */
    fun askStep(step: Int) = when (step) {
        0 -> WHAT_FIRST
        STORY_STEPS - 1 -> AND_AT_THE_END
        else -> AND_NEXT
    }

    /** «¿Dónde está el nueve?», the number of the countdown that goes now. */
    fun whereIs(number: Int) = "n_donde_$number"

    /** «¡Nueve!», counting down: not «Una» as the strawberries, «¡Uno!». */
    fun down(number: Int) = "n_atras_$number"

    /** «¡Hoy es martes!», Bruno in the assembly. */
    fun todayIs(day: Weekday) = "b_hoy_es_${day.word}"

    /** «Ayer fue lunes y mañana será miércoles.», when today is [day]. */
    fun aroundToday(day: Weekday) = "n_ayer_${day.word}"

    /** «Ese es el jueves.», the card she touched when it is not today. */
    fun thatDay(day: Weekday) = "n_ese_dia_${day.word}"

    /** «¡Llueve! ¡A por el paraguas y las botas!», Bruno about the weather she says. */
    fun weatherIs(weather: Weather) = "b_tiempo_${weather.word}"

    /** «Bruno tiene una fresa.», the strawberries he has before she gives him more. */
    fun brunoHas(count: Int) = "n_tiene_$count"

    /** «Dale dos más.» */
    fun giveMore(count: Int) = "n_dale_$count"

    /** «¡Sí! Una y dos, tres.» */
    fun sumIs(sum: Sum) = "n_suma_${sum.key}"

    /** «Esconde a Bruno dentro de la caja grande.», where the voice asks her to put Bruno or his ball. */
    fun putIt(placing: Placing) = "n_pon_${placing.thing.word}_${placing.place.word}"

    /** «¡Sí! Bruno está dentro de la caja grande.» */
    fun itIs(placing: Placing) = "n_ya_${placing.thing.word}_${placing.place.word}"

    /** «Huum... Así está encima de la mesa.», where she left it ([place]; null, the floor) when it was not there. */
    fun leftAt(place: Place?) = "n_asi_${place?.word ?: "suelo"}"

    /** «¿Qué número va antes del cinco?», Bruno on the number line. */
    fun askSide(hop: Hop) = "b_${hop.side.word}_del_${hop.square}"

    /** «¡Sí! Antes del cinco va el cuatro.» */
    fun answerSide(hop: Hop) = "n_${hop.side.word}_del_${hop.square}"

    /** «Ese es el siete.», the square of the number line she touched, when it is not the one. */
    fun thatSquare(number: Int) = "n_ese_es_$number"

    /** «¡Gato! ¿Qué rima con gato?» */
    fun whatRhymes(word: RhymeWord) = "b_rima_${word.key}"

    /** «Gato.», the voice naming a card of the rhymes, so that she plays with the words she hears. */
    fun card(word: RhymeWord) = "n_carta_${word.key}"

    /** «¿Rana? Huum... No termina igual.», when she chooses [word] and it does not rhyme. */
    fun noRhyme(word: RhymeWord) = "b_no_rima_${word.key}"

    /** «¡Sí! Gato y pato riman.», for the pair of [word]. */
    fun rhyme(word: RhymeWord) = "n_riman_${word.pair}"

    /** «¡Un gato con zapato!», Bruno's silly rhyme with the pair of [word]. */
    fun sillyRhyme(word: RhymeWord) = "b_tonta_${word.pair}"

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
        HOW_DO_YOU_FEEL, TAKE_YOUR_PHOTO, TAKE_THE_FACE, ME_TOO, RHYMES_INTRO, THANKS_RHYMES, ROCKET_INTRO, PRESS_ZERO,
        LIFT_OFF, OFF_I_GO, THANKS_ROCKET, STICKER_LETTERS, STICKER_NUMBERS, STICKER_COLORS, STICKER_SHAPES, STICKER_FEELINGS,
        STICKER_WORDS, STICK_IT, SO_PRETTY, STORY_INTRO, WHAT_FIRST, AND_NEXT, AND_AT_THE_END, LATER, THANKS_STORIES,
        NUMBER_LINE_INTRO, BESIDE_BRUNO, THANKS_NUMBER_LINE, PLACES_INTRO, HIDDEN, THANKS_PLACES, SUMS_INTRO, HOW_MANY_NOW,
        COUNT_THEM_ALL, THANKS_SUMS, ASSEMBLY_INTRO, WHAT_WEATHER, DAY_STARTS,
    ) +
        Weekday.entries.flatMap { listOf(todayIs(it), aroundToday(it), thatDay(it)) } +
        Weather.entries.map { weatherIs(it) } +
        SUMS.flatMap { listOf(brunoHas(it.has), giveMore(it.more), sumIs(it)) }.distinct() +
        Thing.entries.flatMap { thing -> Place.entries.flatMap { listOf(putIt(Placing(thing, it)), itIs(Placing(thing, it))) } } +
        (Place.entries - Place.OUT_OF_BOX).map { leftAt(it) } + leftAt(null) +
        Side.entries.flatMap { side -> side.squares.flatMap { listOf(askSide(Hop(it, side)), answerSide(Hop(it, side))) } } +
        (0..LINE_LAST).map { thatSquare(it) } +
        Story.entries.flatMap { story -> (0 until STORY_STEPS).map { storyStep(story, it) } } +
        (1..COUNTDOWN_FROM).flatMap { listOf(whereIs(it), down(it)) } +
        RhymeWord.entries.flatMap { listOf(whatRhymes(it), card(it), noRhyme(it)) } +
        RhymeWord.entries.filter { it.key == it.pair }.flatMap { listOf(rhyme(it), sillyRhyme(it)) } +
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
    ROCKET("cohete"),
    CLAP("palmada"),
    HOP("salto"),
}
