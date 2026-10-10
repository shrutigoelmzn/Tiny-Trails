package com.example.data.model

data class CharacterProgress(
    val characterId: String,
    val completed: Boolean = false,
    val stars: Int = 0,
    val attempts: Int = 0,
    val lastPracticedAt: Long = 0L
)

enum class StickerCategory(val title: String) {
    ANIMALS("Animals"),
    DINOSAURS("Dinosaurs"),
    VEHICLES("Vehicles"),
    FRUITS("Fruits"),
    SPACE("Space"),
    SEA_ANIMALS("Sea Animals")
}

data class StickerItem(
    val id: String,
    val category: StickerCategory,
    val displayName: String,
    val iconEmoji: String,
    val pronunciationText: String,
    val description: String,
    val isUnlocked: Boolean = false
)

data class GardenState(
    val seedsCount: Int = 1,
    val plantsGrown: Int = 0,
    val flowersBloomed: Int = 0,
    val butterflies: Int = 0
)

data class RewardResult(
    val starsEarned: Int,
    val newlyUnlockedSticker: StickerItem? = null,
    val newlyUnlockedGardenItem: String? = null,
    val totalLevelsCompleted: Int = 0
)
