package com.example.data.model

enum class PetAccessory(
    val id: String,
    val title: String,
    val emoji: String,
    val unlockLevel: Int
) {
    NONE("none", "Natural", "✨", 1),
    PARTY_HAT("party_hat", "Party Hat", "🥳", 2),
    SUPER_CAPE("super_cape", "Hero Cape", "🦸", 3),
    FLOWER_CROWN("flower_crown", "Flower Crown", "🌸", 4),
    ROYAL_CROWN("royal_crown", "Royal Crown", "👑", 5),
    SUNGLASSES("sunglasses", "Cool Shades", "😎", 6);

    companion object {
        fun fromId(id: String): PetAccessory = entries.firstOrNull { it.id == id } ?: NONE
    }
}

enum class PetMood {
    HAPPY,
    EATING,
    GIGGLING,
    DANCING
}

data class PetState(
    val name: String = "Pip",
    val level: Int = 1,
    val starsFedThisLevel: Int = 0,
    val totalStarsFed: Int = 0,
    val happiness: Int = 100,
    val equippedAccessory: PetAccessory = PetAccessory.NONE,
    val unlockedAccessories: Set<PetAccessory> = setOf(PetAccessory.NONE)
) {
    val starsNeededForNextLevel: Int
        get() = (level * 2) + 1

    val progressFraction: Float
        get() = (starsFedThisLevel.toFloat() / starsNeededForNextLevel.toFloat()).coerceIn(0f, 1f)
}
