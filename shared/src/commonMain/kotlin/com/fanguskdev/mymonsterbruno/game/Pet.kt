package com.fanguskdev.mymonsterbruno.game

/**
 * Her favourite animal (ADR 0033): a grown-up chooses it in their corner, and it lives in Bruno's room, on a shelf beside
 * him. [word] names its line and is what the save keeps.
 */
internal enum class Pet(val word: String) {
    CAT("gato"),
    DOG("perro"),
    RABBIT("conejo"),
    FISH("pez"),
    BIRD("pajaro"),
    TURTLE("tortuga"),
}

/** Her animal, if a grown-up has chosen one. */
internal val SaveData.chosenPet: Pet? get() = Pet.entries.firstOrNull { it.word == pet }
