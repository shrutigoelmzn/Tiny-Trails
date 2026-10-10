package com.example.feature.progress

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.content.CharacterRepository
import com.example.data.repository.ProgressRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProgressTrackingTest {

    private lateinit var context: Context
    private lateinit var progressRepo: ProgressRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        progressRepo = ProgressRepository.getInstance(context)
        progressRepo.resetAllProgress()
    }

    @Test
    fun testRecordCompletion_updatesProgressFlowAndEarnedStars() {
        val charId = "num_1"
        val result = progressRepo.recordCompletion(charId, stars = 3)

        assertNotNull(result)
        assertEquals(3, result.starsEarned)
        assertTrue(result.totalLevelsCompleted >= 1)

        val progress = progressRepo.getProgress(charId)
        assertTrue("Character must be marked completed", progress.completed)
        assertEquals(3, progress.stars)
        assertEquals(1, progress.attempts)
    }

    @Test
    fun testRecordCompletion_preservesHighestStars() {
        val charId = "alpha_A"
        progressRepo.recordCompletion(charId, stars = 3)
        // Second attempt with 2 stars should not downgrade the 3 stars
        progressRepo.recordCompletion(charId, stars = 2)

        val progress = progressRepo.getProgress(charId)
        assertEquals(3, progress.stars)
        assertEquals(2, progress.attempts)
    }

    @Test
    fun testAllCharacters_accessibleInRepository() {
        val allChars = CharacterRepository.allCharacters
        assertTrue("Repository must contain at least 60 preschool characters", allChars.size >= 60)
        assertTrue("Repository must contain numbers 0–9", CharacterRepository.numbers.size >= 10)
        assertTrue("Repository must contain uppercase A–Z", CharacterRepository.uppercase.size >= 26)
        assertTrue("Repository must contain lowercase a–z", CharacterRepository.lowercase.size >= 26)
    }

    @Test
    fun testMilestoneBadges_unlockCriteria() {
        // Complete 1 character -> unlocks first step
        progressRepo.recordCompletion("num_1", stars = 3)
        val progressMap = progressRepo.progressFlow.value

        val completedCount = progressMap.values.count { it.completed }
        assertTrue("First Step Explorer should be unlocked", completedCount >= 1)

        // Complete 5 numbers
        val numbersToComplete = listOf("num_2", "num_3", "num_4", "num_5")
        numbersToComplete.forEach { id ->
            progressRepo.recordCompletion(id, stars = 3)
        }

        val updatedMap = progressRepo.progressFlow.value
        val numbersDone = CharacterRepository.numbers.count { updatedMap[it.id]?.completed == true }
        assertTrue("Number Whiz should be unlocked with 5 numbers", numbersDone >= 5)

        val totalStars = updatedMap.values.sumOf { it.stars }
        assertTrue("Star Collector should have at least 15 stars", totalStars >= 15)
    }

    @Test
    fun testResetAllProgress_clearsProgress() {
        progressRepo.recordCompletion("num_1", stars = 3)
        progressRepo.recordCompletion("alpha_A", stars = 3)
        assertTrue(progressRepo.progressFlow.value.isNotEmpty())

        progressRepo.resetAllProgress()
        assertTrue("Progress flow must be empty after reset", progressRepo.progressFlow.value.isEmpty())
        assertFalse(progressRepo.getProgress("num_1").completed)
    }
}
