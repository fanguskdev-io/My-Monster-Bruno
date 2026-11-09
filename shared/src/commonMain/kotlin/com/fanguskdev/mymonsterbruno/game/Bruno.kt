package com.fanguskdev.mymonsterbruno.game

import kotlinx.serialization.Serializable

/** The colours she can choose for Bruno when he hatches (docs/diseno.md). */
@Serializable
enum class BrunoColor { MINT, PINK, ORANGE, PURPLE }

/** What Bruno has on his head: she chooses it when he hatches. */
@Serializable
enum class Topping { HORNS, EARS, ANTENNAE }

/** Where an accessory goes: Bruno wears the newest of each place. */
enum class Slot { HEAD, FACE, NECK }

/** The presents Bruno gets each time she finishes an island, in this order. */
@Serializable
enum class Accessory(val slot: Slot) {
    BEANIE(Slot.HEAD),
    GLASSES(Slot.FACE),
    BOW_TIE(Slot.NECK),
    FLOWERS(Slot.HEAD),
    SCARF(Slot.NECK),
    CROWN(Slot.HEAD),
}

/**
 * How Bruno looks: what she chose when he hatched, the presents he has and how much he has grown; and, once she has
 * opened his wardrobe (ADR 0032), which of his presents he wears ([dressed]).
 */
@Serializable
data class BrunoLook(
    val color: BrunoColor = BrunoColor.MINT,
    val top: Topping = Topping.HORNS,
    val accessories: List<Accessory> = emptyList(),
    val growth: Int = 0,
    val dressed: List<Accessory>? = null,
) {
    /** What he wears: what she chose in his wardrobe or, if she has not, the newest accessory of each place. */
    val wearing: List<Accessory>
        get() {
            val worn = dressed?.filter { it in accessories } ?: accessories
            return Slot.entries.mapNotNull { slot -> worn.lastOrNull { it.slot == slot } }
        }

    /** [accessory] put on, instead of what he wore in its place, or taken off if he wore it (ADR 0032). */
    fun toggled(accessory: Accessory): BrunoLook =
        copy(dressed = if (accessory in wearing) wearing - accessory else wearing.filter { it.slot != accessory.slot } + accessory)

    /** With the present [gift], which he puts on at once in its place. */
    fun withPresent(gift: Accessory): BrunoLook =
        copy(accessories = accessories + gift, dressed = dressed?.let { worn -> worn.filter { it.slot != gift.slot } + gift })

    /** How big he is next to the newborn: a little more every two islands, up to [MAX_GROWTH] steps. */
    val size: Float get() = 1f + GROWTH_STEP * growth.coerceIn(0, MAX_GROWTH)

    /** The next present, or null if he has them all. */
    val nextPresent: Accessory? get() = Accessory.entries.firstOrNull { it !in accessories }

    companion object {
        const val MAX_GROWTH = 3
        const val GROWTH_STEP = 0.07f
    }
}
