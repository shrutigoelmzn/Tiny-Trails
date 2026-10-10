package com.example.data.content

import com.example.data.model.CharacterContent

object PrototypeCharacters {
    val NUMBER_0: CharacterContent get() = NumbersContent.NUMBER_0
    val NUMBER_1: CharacterContent get() = NumbersContent.NUMBER_1
    val NUMBER_2: CharacterContent get() = NumbersContent.NUMBER_2
    val NUMBER_3: CharacterContent get() = NumbersContent.NUMBER_3
    val NUMBER_4: CharacterContent get() = NumbersContent.NUMBER_4
    val NUMBER_5: CharacterContent get() = NumbersContent.NUMBER_5
    val NUMBER_6: CharacterContent get() = NumbersContent.NUMBER_6
    val NUMBER_7: CharacterContent get() = NumbersContent.NUMBER_7
    val NUMBER_8: CharacterContent get() = NumbersContent.NUMBER_8
    val NUMBER_9: CharacterContent get() = NumbersContent.NUMBER_9

    val LETTER_A: CharacterContent get() = AlphabetUpperContent.LETTER_A
    val LETTER_B: CharacterContent get() = AlphabetUpperContent.LETTER_B
    val LETTER_C: CharacterContent get() = AlphabetUpperContent.LETTER_C
    val LETTER_LOWER_A: CharacterContent get() = AlphabetLowerContent.LETTER_LOWER_A

    val ALL_ITEMS: List<CharacterContent> get() = CharacterRepository.allCharacters

    fun findById(id: String): CharacterContent? {
        return CharacterRepository.findById(id)
    }
}
