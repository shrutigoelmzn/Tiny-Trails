package com.example.data.content

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent

object CharacterRepository {

    val numbers: List<CharacterContent> = NumbersContent.ALL_NUMBERS
    val uppercase: List<CharacterContent> = AlphabetUpperContent.ALL_UPPERCASE
    val lowercase: List<CharacterContent> = AlphabetLowerContent.ALL_LOWERCASE
    val shapes: List<CharacterContent> = ShapesAndStrokesContent.ALL_SHAPES

    val allCharacters: List<CharacterContent> = numbers + uppercase + lowercase + shapes

    private val idMap: Map<String, CharacterContent> = allCharacters.associateBy { it.id }

    fun findById(id: String): CharacterContent? {
        return idMap[id]
    }

    fun getByCategory(category: CategoryType): List<CharacterContent> {
        return when (category) {
            CategoryType.NUMBERS -> numbers
            CategoryType.ALPHABET_UPPER -> uppercase
            CategoryType.ALPHABET_LOWER -> lowercase
            CategoryType.SHAPES -> shapes
        }
    }

    fun getNextCharacter(currentId: String): CharacterContent? {
        val index = allCharacters.indexOfFirst { it.id == currentId }
        return if (index != -1 && index + 1 < allCharacters.size) {
            allCharacters[index + 1]
        } else {
            allCharacters.firstOrNull()
        }
    }
}
