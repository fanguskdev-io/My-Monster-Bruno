package com.fanguskdev.mymonsterbruno.storage

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Her photo is kept in pieces small enough for every platform (ADR 0016), and comes back whole. */
class PhotoStoreTest {
    @Test
    fun aPhotoComesBackWholeInPiecesThatFitTheDesktop() {
        val store = MemoryStore()
        val photo = Random(1).nextBytes(60_000)
        PhotoStore.save(store, photo)
        assertContentEquals(photo, PhotoStore.load(store))
        val count = store.getString("foto.partes")?.toInt() ?: 0
        assertTrue(count > 1, "un JPEG así va en varias partes")
        assertTrue((0 until count).all { (store.getString("foto.$it")?.length ?: 0) <= 8192 }, "una parte no cabe en el escritorio")
    }

    @Test
    fun anotherPhotoReplacesItAndRemovingItLeavesNothing() {
        val store = MemoryStore()
        PhotoStore.save(store, Random(1).nextBytes(60_000))
        val small = Random(2).nextBytes(100)
        PhotoStore.save(store, small)
        assertContentEquals(small, PhotoStore.load(store))
        assertNull(store.getString("foto.1"), "quedan partes de la foto de antes")
        PhotoStore.remove(store)
        assertNull(PhotoStore.load(store))
        assertNull(store.getString("foto.0"))
    }

    @Test
    fun aBrokenPhotoIsNoPhoto() {
        val store = MemoryStore()
        PhotoStore.save(store, Random(1).nextBytes(10_000))
        store.remove("foto.1")
        assertNull(PhotoStore.load(store))
    }
}
