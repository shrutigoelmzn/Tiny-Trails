package com.example.data.repository

import android.content.Context
import com.example.data.model.CharacterProgress
import com.example.data.model.GardenState
import com.example.data.model.RewardResult
import com.example.data.model.StickerCategory
import com.example.data.model.StickerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProgressRepository private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("tiny_trails_progress", Context.MODE_PRIVATE)

    private val defaultStickers = listOf(
        StickerItem("sticker_cow", StickerCategory.ANIMALS, "Cow", "🐄", "Cow", "Friendly farm cow says Moo!", false),
        StickerItem("sticker_cat", StickerCategory.ANIMALS, "Cat", "🐱", "Cat", "Playful kitten says Meow!", false),
        StickerItem("sticker_dog", StickerCategory.ANIMALS, "Dog", "🐶", "Dog", "Happy puppy wags its tail!", false),
        StickerItem("sticker_butterfly", StickerCategory.ANIMALS, "Butterfly", "🦋", "Butterfly", "Fluttering colorful wings!", false),
        StickerItem("sticker_dino", StickerCategory.DINOSAURS, "Dinosaur", "🦕", "Dinosaur", "Gentle long-neck dinosaur!", false),
        StickerItem("sticker_trex", StickerCategory.DINOSAURS, "T-Rex", "🦖", "T-Rex", "Mighty friendly roar!", false),
        StickerItem("sticker_car", StickerCategory.VEHICLES, "Car", "🚗", "Car", "Beep beep vroom!", false),
        StickerItem("sticker_train", StickerCategory.VEHICLES, "Train", "🚂", "Train", "Choo choo down the tracks!", false),
        StickerItem("sticker_plane", StickerCategory.VEHICLES, "Airplane", "✈️", "Airplane", "Soaring high in the sky!", false),
        StickerItem("sticker_apple", StickerCategory.FRUITS, "Apple", "🍎", "Apple", "Crisp, sweet, and yummy!", false),
        StickerItem("sticker_banana", StickerCategory.FRUITS, "Banana", "🍌", "Banana", "Sweet yellow treat!", false),
        StickerItem("sticker_strawberry", StickerCategory.FRUITS, "Strawberry", "🍓", "Strawberry", "Juicy red berry!", false),
        StickerItem("sticker_rocket", StickerCategory.SPACE, "Rocket", "🚀", "Rocket", "3, 2, 1 Blast off!", false),
        StickerItem("sticker_star", StickerCategory.SPACE, "Star", "⭐", "Star", "Twinkling shining light!", false),
        StickerItem("sticker_dolphin", StickerCategory.SEA_ANIMALS, "Dolphin", "🐬", "Dolphin", "Jumping over ocean waves!", false),
        StickerItem("sticker_turtle", StickerCategory.SEA_ANIMALS, "Turtle", "🐢", "Turtle", "Swimming gently in the sea!", false)
    )

    private val _progressFlow = MutableStateFlow<Map<String, CharacterProgress>>(loadProgress())
    val progressFlow: StateFlow<Map<String, CharacterProgress>> = _progressFlow.asStateFlow()

    private val _stickersFlow = MutableStateFlow<List<StickerItem>>(loadStickers())
    val stickersFlow: StateFlow<List<StickerItem>> = _stickersFlow.asStateFlow()

    private val _gardenFlow = MutableStateFlow<GardenState>(loadGardenState())
    val gardenFlow: StateFlow<GardenState> = _gardenFlow.asStateFlow()

    fun getProgress(characterId: String): CharacterProgress {
        return _progressFlow.value[characterId] ?: CharacterProgress(characterId = characterId)
    }

    fun recordCompletion(characterId: String, stars: Int): RewardResult {
        val current = getProgress(characterId)
        val updated = current.copy(
            completed = true,
            stars = maxOf(current.stars, stars),
            attempts = current.attempts + 1,
            lastPracticedAt = System.currentTimeMillis()
        )

        val newProgressMap = _progressFlow.value.toMutableMap()
        newProgressMap[characterId] = updated
        _progressFlow.value = newProgressMap
        saveProgress(characterId, updated)

        val totalCompleted = newProgressMap.values.count { it.completed }

        // Major rewards approximately every 3-4 levels completed
        var newlyUnlockedSticker: StickerItem? = null
        var newlyUnlockedGardenItem: String? = null

        val currentStickers = _stickersFlow.value.toMutableList()

        // Check if a milestone was reached (e.g. 1st lesson, then every 3 lessons: 3, 6, 9, 12, 15...)
        val shouldReward = totalCompleted == 1 || (totalCompleted % 3 == 0)
        val stickerIndexToUnlock = if (totalCompleted == 1) 0 else (totalCompleted / 3).coerceAtMost(currentStickers.size - 1)

        if (shouldReward && stickerIndexToUnlock < currentStickers.size && !currentStickers[stickerIndexToUnlock].isUnlocked) {
            currentStickers[stickerIndexToUnlock] = currentStickers[stickerIndexToUnlock].copy(isUnlocked = true)
            newlyUnlockedSticker = currentStickers[stickerIndexToUnlock]

            val gardenSeeds = 1 + (totalCompleted / 3)
            val gardenPlants = (totalCompleted / 3).coerceAtLeast(1)
            val gardenFlowers = (totalCompleted / 4)
            val gardenButterflies = (totalCompleted / 6)

            newlyUnlockedGardenItem = when {
                totalCompleted <= 1 -> "🌱 Sprouted Tiny Seed"
                totalCompleted <= 4 -> "🌸 Blooming Flower"
                totalCompleted <= 8 -> "🦋 Fluttering Butterfly"
                else -> "✨ Garden Sunshine Gem"
            }
            advanceGarden(seeds = gardenSeeds, plants = gardenPlants, flowers = gardenFlowers, butterflies = gardenButterflies)
        }

        _stickersFlow.value = currentStickers
        saveStickers(currentStickers)

        return RewardResult(
            starsEarned = stars,
            newlyUnlockedSticker = newlyUnlockedSticker,
            newlyUnlockedGardenItem = newlyUnlockedGardenItem,
            totalLevelsCompleted = totalCompleted
        )
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        _progressFlow.value = emptyMap()
        _stickersFlow.value = defaultStickers
        _gardenFlow.value = GardenState()
    }

    private fun advanceGarden(seeds: Int, plants: Int, flowers: Int, butterflies: Int) {
        val newState = GardenState(
            seedsCount = seeds,
            plantsGrown = plants,
            flowersBloomed = flowers,
            butterflies = butterflies
        )
        _gardenFlow.value = newState
        prefs.edit()
            .putInt("garden_seeds", seeds)
            .putInt("garden_plants", plants)
            .putInt("garden_flowers", flowers)
            .putInt("garden_butterflies", butterflies)
            .apply()
    }

    private fun loadGardenState(): GardenState {
        return GardenState(
            seedsCount = prefs.getInt("garden_seeds", 1),
            plantsGrown = prefs.getInt("garden_plants", 0),
            flowersBloomed = prefs.getInt("garden_flowers", 0),
            butterflies = prefs.getInt("garden_butterflies", 0)
        )
    }

    private fun loadProgress(): Map<String, CharacterProgress> {
        val map = mutableMapOf<String, CharacterProgress>()
        com.example.data.content.CharacterRepository.allCharacters.forEach { char ->
            val id = char.id
            if (prefs.getBoolean("prog_${id}_done", false)) {
                map[id] = CharacterProgress(
                    characterId = id,
                    completed = true,
                    stars = prefs.getInt("prog_${id}_stars", 3),
                    attempts = prefs.getInt("prog_${id}_attempts", 1),
                    lastPracticedAt = prefs.getLong("prog_${id}_time", 0L)
                )
            }
        }
        return map
    }

    private fun saveProgress(id: String, progress: CharacterProgress) {
        prefs.edit()
            .putBoolean("prog_${id}_done", progress.completed)
            .putInt("prog_${id}_stars", progress.stars)
            .putInt("prog_${id}_attempts", progress.attempts)
            .putLong("prog_${id}_time", progress.lastPracticedAt)
            .apply()
    }

    private fun loadStickers(): List<StickerItem> {
        return defaultStickers.map { sticker ->
            val unlocked = prefs.getBoolean("sticker_${sticker.id}_unlocked", false)
            sticker.copy(isUnlocked = unlocked)
        }
    }

    private fun saveStickers(stickers: List<StickerItem>) {
        val editor = prefs.edit()
        stickers.forEach { sticker ->
            editor.putBoolean("sticker_${sticker.id}_unlocked", sticker.isUnlocked)
        }
        editor.apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: ProgressRepository? = null

        fun getInstance(context: Context): ProgressRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProgressRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
