package com.example.data.model

import com.example.domain.tracing.TraceCharacterDef

data class CharacterContent(
    val id: String,
    val category: CategoryType,
    val language: LearningLanguage,
    val displayValue: String,
    val pronunciationText: String,
    val exampleWord: String,
    val exampleEmoji: String,
    val exampleCount: Int = 1,
    val traceDefinition: TraceCharacterDef,
    val miniActivityType: MiniActivityType
)
