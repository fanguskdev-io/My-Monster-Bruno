package com.fanguskdev.mymonsterbruno.game

import kotlin.random.Random

/** The weathers of dressing Bruno (ADR 0044): sun, rain and snow, each with its clothes. */
internal val DRESS_WEATHERS = listOf(Weather.SUN, Weather.RAIN, Weather.SNOW)

/** What Bruno can put on, and for which weather: two things for each (ADR 0044). [word] names its lines. */
internal enum class Clothes(val word: String, val weather: Weather) {
    SUNGLASSES("gafas_de_sol", Weather.SUN),
    CAP("gorra", Weather.SUN),
    UMBRELLA("paraguas", Weather.RAIN),
    BOOTS("botas_de_agua", Weather.RAIN),
    WOOLLY_HAT("gorro_de_lana", Weather.SNOW),
    SCARF("bufanda", Weather.SNOW),
}

/** A round of dressing Bruno: the [weather] in the window, and three things to choose from, one for each weather. */
internal data class DressRound(val weather: Weather, val choices: List<Clothes>) {
    val right: Clothes get() = choices.first { it.weather == weather }
}

/** The three rounds of dressing Bruno: one for each weather, in any order, each with one thing of every weather. */
internal fun dressRounds(random: Random): List<DressRound> = DRESS_WEATHERS.shuffled(random).map { weather ->
    DressRound(weather, DRESS_WEATHERS.map { other -> Clothes.entries.filter { it.weather == other }.random(random) }.shuffled(random))
}

/** The bins of the street, as in Spain (ADR 0044): yellow for packaging, blue for paper and green for glass. */
internal enum class Bin(val word: String) {
    YELLOW("amarillo"),
    BLUE("azul"),
    GREEN("verde"),
}

/** What goes in the bins, two things for each. [word] names its lines. */
internal enum class Rubbish(val word: String, val bin: Bin) {
    BOTTLE("botella_de_plastico", Bin.YELLOW),
    CAN("lata", Bin.YELLOW),
    NEWSPAPER("periodico", Bin.BLUE),
    BOX("caja_de_carton", Bin.BLUE),
    JAR("tarro", Bin.GREEN),
    GLASS_BOTTLE("botella_de_cristal", Bin.GREEN),
}

/** The things of a visit to the bins: the six, one after the other, never two of the same bin in a row. */
internal fun rubbishRounds(random: Random): List<Rubbish> {
    repeat(TRIES) {
        val order = Rubbish.entries.shuffled(random)
        if (order.zipWithNext().none { (a, b) -> a.bin == b.bin }) return order
    }
    return listOf(Rubbish.BOTTLE, Rubbish.NEWSPAPER, Rubbish.JAR, Rubbish.CAN, Rubbish.BOX, Rubbish.GLASS_BOTTLE)
}

private const val TRIES = 50

/** How many teeth Bruno shows to brush: four above and four below. */
internal const val TEETH = 8

/** The second game of the island of the house, after the teeth: they take turns by the days she goes (ADR 0044). */
internal enum class HomeGame { DRESS, RECYCLE }

/** The second game of the island of the house today: dressing Bruno one day she goes, the bins the next, and again. */
internal val SaveData.homeGameToday: HomeGame
    get() = HomeGame.entries[(homeDays - 1).coerceAtLeast(0) % HomeGame.entries.size]

/** The save on a [day] she goes to the island of the house: one more day if it is another one. */
internal fun SaveData.homeOn(day: Int): SaveData = if (homeDay == day) this else copy(homeDay = day, homeDays = homeDays + 1)
