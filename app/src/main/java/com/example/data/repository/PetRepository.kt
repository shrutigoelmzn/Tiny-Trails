package com.example.data.repository

import android.content.Context
import com.example.data.model.PetAccessory
import com.example.data.model.PetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PetRepository private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("tiny_trails_pet", Context.MODE_PRIVATE)

    private val _petStateFlow = MutableStateFlow(loadPetState())
    val petStateFlow: StateFlow<PetState> = _petStateFlow.asStateFlow()

    private fun loadPetState(): PetState {
        val level = prefs.getInt("level", 1)
        val starsFedThisLevel = prefs.getInt("stars_fed_this_level", 0)
        val totalStarsFed = prefs.getInt("total_stars_fed", 0)
        val happiness = prefs.getInt("happiness", 100)
        val equippedId = prefs.getString("equipped_accessory", PetAccessory.NONE.id) ?: PetAccessory.NONE.id
        val equipped = PetAccessory.fromId(equippedId)

        val unlocked = PetAccessory.entries.filter { it.unlockLevel <= level }.toSet()

        return PetState(
            name = "Pip",
            level = level,
            starsFedThisLevel = starsFedThisLevel,
            totalStarsFed = totalStarsFed,
            happiness = happiness,
            equippedAccessory = equipped,
            unlockedAccessories = unlocked
        )
    }

    private fun savePetState(state: PetState) {
        prefs.edit()
            .putInt("level", state.level)
            .putInt("stars_fed_this_level", state.starsFedThisLevel)
            .putInt("total_stars_fed", state.totalStarsFed)
            .putInt("happiness", state.happiness)
            .putString("equipped_accessory", state.equippedAccessory.id)
            .apply()
    }

    /**
     * Feeds 1 star to Pip.
     * Returns true if this feeding resulted in a Level Up!
     */
    fun feedStar(): Boolean {
        val current = _petStateFlow.value
        val newFedThisLevel = current.starsFedThisLevel + 1
        val newTotalFed = current.totalStarsFed + 1
        val needed = current.starsNeededForNextLevel

        val leveledUp = newFedThisLevel >= needed
        val newLevel = if (leveledUp) current.level + 1 else current.level
        val finalFedThisLevel = if (leveledUp) 0 else newFedThisLevel

        val newUnlocked = PetAccessory.entries.filter { it.unlockLevel <= newLevel }.toSet()

        val updated = current.copy(
            level = newLevel,
            starsFedThisLevel = finalFedThisLevel,
            totalStarsFed = newTotalFed,
            happiness = (current.happiness + 15).coerceAtMost(100),
            unlockedAccessories = newUnlocked
        )

        _petStateFlow.value = updated
        savePetState(updated)
        return leveledUp
    }

    fun ticklePet() {
        val current = _petStateFlow.value
        val updated = current.copy(
            happiness = (current.happiness + 10).coerceAtMost(100)
        )
        _petStateFlow.value = updated
        savePetState(updated)
    }

    fun equipAccessory(accessory: PetAccessory) {
        val current = _petStateFlow.value
        if (current.unlockedAccessories.contains(accessory)) {
            val updated = current.copy(equippedAccessory = accessory)
            _petStateFlow.value = updated
            savePetState(updated)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PetRepository? = null

        fun getInstance(context: Context): PetRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PetRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
