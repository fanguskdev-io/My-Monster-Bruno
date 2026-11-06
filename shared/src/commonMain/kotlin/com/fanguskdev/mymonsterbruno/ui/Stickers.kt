package com.fanguskdev.mymonsterbruno.ui

import androidx.compose.ui.geometry.Offset
import com.fanguskdev.mymonsterbruno.audio.Voice
import com.fanguskdev.mymonsterbruno.game.SaveData

/**
 * The stickers of the room (ADR 0024): one for each island, the first time she finishes it, which she sticks on the wall
 * of Bruno's room wherever she wants, and moves when she likes. Those of the islands she has finished and not stuck yet,
 * in the order of the map.
 */
internal fun SaveData.pendingStickers(): List<Island> = Island.entries.filter { (islands[it.name] ?: 0) > 0 && it.name !in stickers }

/** The stickers already on the wall, with where they are, in the order of the map. */
internal fun SaveData.stuckStickers(): List<Pair<Island, Offset>> = Island.entries.mapNotNull { island ->
    stickers[island.name]?.takeIf { it.size == 2 }?.let { island to Offset(it[0], it[1]) }
}

/** The save with the sticker of [island] on the wall at [at], wherever it was before. */
internal fun SaveData.stuck(island: Island, at: Offset): SaveData = copy(stickers = stickers + (island.name to listOf(at.x, at.y)))

/** What Bruno says when he gives her the sticker of this island: «¡Te he guardado una pegatina de la isla de las letras!». */
internal val Island.stickerLine: String get() = when (this) {
    Island.VOWELS -> Voice.STICKER_LETTERS
    Island.NUMBERS -> Voice.STICKER_NUMBERS
    Island.COLORS -> Voice.STICKER_COLORS
    Island.SHAPES -> Voice.STICKER_SHAPES
    Island.FEELINGS -> Voice.STICKER_FEELINGS
    Island.WORDS -> Voice.STICKER_WORDS
}
