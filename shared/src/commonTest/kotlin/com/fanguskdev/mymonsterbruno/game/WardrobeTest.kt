package com.fanguskdev.mymonsterbruno.game

import com.fanguskdev.mymonsterbruno.storage.MemoryStore
import kotlin.test.Test
import kotlin.test.assertEquals

/** Bruno's wardrobe (ADR 0032): what he wears, what she chooses, and the presents he gets after. */
class WardrobeTest {
    private val all = BrunoLook(accessories = listOf(Accessory.BEANIE, Accessory.GLASSES, Accessory.BOW_TIE, Accessory.FLOWERS))

    @Test
    fun withoutChoosingHeWearsTheNewestOfEachPlace() {
        assertEquals(listOf(Accessory.FLOWERS, Accessory.GLASSES, Accessory.BOW_TIE), all.wearing)
    }

    @Test
    fun sheChoosesOneOfEachPlaceOrNone() {
        val beanie = all.toggled(Accessory.BEANIE)
        assertEquals(listOf(Accessory.BEANIE, Accessory.GLASSES, Accessory.BOW_TIE), beanie.wearing, "el gorro, en vez de las flores")
        val noGlasses = beanie.toggled(Accessory.GLASSES)
        assertEquals(listOf(Accessory.BEANIE, Accessory.BOW_TIE), noGlasses.wearing, "sin gafas")
        assertEquals(listOf(Accessory.BEANIE, Accessory.GLASSES, Accessory.BOW_TIE), noGlasses.toggled(Accessory.GLASSES).wearing)
        val nothing = noGlasses.toggled(Accessory.BEANIE).toggled(Accessory.BOW_TIE)
        assertEquals(emptyList(), nothing.wearing, "sin nada")
    }

    @Test
    fun aNewPresentGoesOnAtOnceInItsPlace() {
        val chosen = all.toggled(Accessory.BEANIE).toggled(Accessory.GLASSES)
        val scarf = chosen.withPresent(Accessory.SCARF)
        assertEquals(listOf(Accessory.BEANIE, Accessory.SCARF), scarf.wearing, "la bufanda, en vez de la pajarita")
        assertEquals(
            listOf(Accessory.CROWN, Accessory.GLASSES),
            BrunoLook(accessories = listOf(Accessory.GLASSES)).withPresent(Accessory.CROWN).wearing.sortedBy {
                it.slot
            },
        )
    }

    @Test
    fun whatSheChoseIsKept() {
        val store = MemoryStore()
        val look = all.toggled(Accessory.BEANIE).toggled(Accessory.BOW_TIE).copy(color = BrunoColor.PURPLE, top = Topping.EARS)
        SaveStore.save(store, SaveData(hatched = true, look = look))
        assertEquals(look, SaveStore.load(store).look)
        assertEquals(listOf(Accessory.BEANIE, Accessory.GLASSES), SaveStore.load(store).look.wearing)
    }
}
