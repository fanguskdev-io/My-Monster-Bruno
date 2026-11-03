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

/** How Bruno looks: what she chose when he hatched, the presents he has and how much he has grown. */
@Serializable
data class BrunoLook(
    val color: BrunoColor = BrunoColor.MINT,
    val top: Topping = Topping.HORNS,
    val accessories: List<Accessory> = emptyList(),
    val growth: Int = 0,
) {
    /** What he wears: the newest accessory of each place. */
    val wearing: List<Accessory> get() = Slot.entries.mapNotNull { slot -> accessories.lastOrNull { it.slot == slot } }

    /** How big he is next to the newborn: a little more every two islands, up to [MAX_GROWTH] steps. */
    val size: Float get() = 1f + GROWTH_STEP * growth.coerceIn(0, MAX_GROWTH)

    /** The next present, or null if he has them all. */
    val nextPresent: Accessory? get() = Accessory.entries.firstOrNull { it !in accessories }

    companion object {
        const val MAX_GROWTH = 3
        const val GROWTH_STEP = 0.07f
    }
}
