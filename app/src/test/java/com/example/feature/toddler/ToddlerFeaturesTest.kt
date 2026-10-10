package com.example.feature.toddler

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.content.CharacterRepository
import com.example.data.model.CategoryType
import com.example.data.model.PetAccessory
import com.example.data.repository.PetRepository
import com.example.feature.certificate.CertificateDefinitions
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
@Config(manifest = Config.NONE)
class ToddlerFeaturesTest {

    private lateinit var application: Application
    private lateinit var petRepo: PetRepository

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        petRepo = PetRepository.getInstance(application)
    }

    @Test
    fun testShapesAndStrokesRegisteredInRepository() {
        val shapes = CharacterRepository.shapes
        assertTrue("Shapes list should not be empty", shapes.isNotEmpty())
        assertTrue("Should contain at least 8 shapes & pre-writing lines", shapes.size >= 8)

        val byCategory = CharacterRepository.getByCategory(CategoryType.SHAPES)
        assertEquals(shapes.size, byCategory.size)

        // Check specific key shapes
        val circle = CharacterRepository.findById("shape_circle")
        assertNotNull("Circle should exist", circle)
        assertEquals("◯", circle?.displayValue)

        val zigzag = CharacterRepository.findById("stroke_zigzag")
        assertNotNull("Zigzag should exist", zigzag)

        val star = CharacterRepository.findById("shape_star")
        assertNotNull("Star should exist", star)

        // Verify strokes have valid points
        shapes.forEach { shape ->
            assertTrue("Shape ${shape.id} must have at least one stroke", shape.traceDefinition.strokes.isNotEmpty())
            shape.traceDefinition.strokes.forEach { stroke ->
                assertTrue("Stroke in ${shape.id} must have path points", stroke.pathPoints.isNotEmpty())
                assertTrue("Stroke start point should exist", stroke.startPoint.x in 0f..1f)
            }
        }
    }

    @Test
    fun testVirtualPetFeedingAndLevelUp() {
        val initialState = petRepo.petStateFlow.value
        assertEquals("Pip", initialState.name)
        assertTrue("Happiness should start high", initialState.happiness >= 90)

        // Feed stars until level up
        val needed = initialState.starsNeededForNextLevel
        var leveledUp = false
        for (i in 1..needed) {
            leveledUp = petRepo.feedStar()
        }

        assertTrue("Pet should have leveled up after feeding needed stars", leveledUp)
        val updatedState = petRepo.petStateFlow.value
        assertEquals(initialState.level + 1, updatedState.level)
        assertTrue("Total fed count should increase", updatedState.totalStarsFed >= needed)

        // Check unlocked accessory at level 2 (Party Hat)
        assertTrue(
            "Party Hat should unlock at level 2",
            updatedState.unlockedAccessories.contains(PetAccessory.PARTY_HAT)
        )
    }

    @Test
    fun testMilestoneCertificatesUnlockLogic() {
        val certs = CertificateDefinitions.ALL_CERTIFICATES
        assertTrue("Should have certificates defined", certs.isNotEmpty())

        val firstHeroCert = certs.first { it.id == "cert_first_hero" }
        // 0 completed -> locked
        assertFalse(firstHeroCert.checkUnlocked(0, 0, 0, 0, 0))
        // 1 completed -> unlocked
        assertTrue(firstHeroCert.checkUnlocked(1, 0, 0, 0, 3))

        val numberMasterCert = certs.first { it.id == "cert_number_master" }
        assertFalse(numberMasterCert.checkUnlocked(4, 4, 0, 0, 12))
        assertTrue(numberMasterCert.checkUnlocked(5, 5, 0, 0, 15))

        val shapeMasterCert = certs.first { it.id == "cert_shape_artist" }
        assertFalse(shapeMasterCert.checkUnlocked(2, 0, 0, 2, 6))
        assertTrue(shapeMasterCert.checkUnlocked(3, 0, 0, 3, 9))
    }
}
