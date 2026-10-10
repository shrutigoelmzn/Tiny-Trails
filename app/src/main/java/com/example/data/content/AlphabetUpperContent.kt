package com.example.data.content

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent
import com.example.data.model.LearningLanguage
import com.example.data.model.MiniActivityType
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceCharacterDef
import com.example.domain.tracing.TraceStroke

object AlphabetUpperContent {

    val LETTER_A = CharacterContent(
        id = "alpha_A",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "A",
        pronunciationText = "A",
        exampleWord = "Apple",
        exampleEmoji = "🍎",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_A",
            displayChar = "A",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.18f),
                        NormalizedPoint(0.43f, 0.35f),
                        NormalizedPoint(0.35f, 0.55f),
                        NormalizedPoint(0.24f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.18f),
                        NormalizedPoint(0.57f, 0.35f),
                        NormalizedPoint(0.65f, 0.55f),
                        NormalizedPoint(0.76f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.58f),
                        NormalizedPoint(0.42f, 0.58f),
                        NormalizedPoint(0.50f, 0.58f),
                        NormalizedPoint(0.58f, 0.58f),
                        NormalizedPoint(0.66f, 0.58f)
                    )
                )
            )
        )
    )

    val LETTER_B = CharacterContent(
        id = "alpha_B",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "B",
        pronunciationText = "B",
        exampleWord = "Ball",
        exampleEmoji = "⚽",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_B",
            displayChar = "B",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.32f, 0.40f),
                        NormalizedPoint(0.32f, 0.60f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.54f, 0.20f),
                        NormalizedPoint(0.68f, 0.28f),
                        NormalizedPoint(0.68f, 0.44f),
                        NormalizedPoint(0.52f, 0.50f),
                        NormalizedPoint(0.32f, 0.50f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.50f),
                        NormalizedPoint(0.56f, 0.50f),
                        NormalizedPoint(0.72f, 0.58f),
                        NormalizedPoint(0.72f, 0.74f),
                        NormalizedPoint(0.56f, 0.82f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_C = CharacterContent(
        id = "alpha_C",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "C",
        pronunciationText = "C",
        exampleWord = "Cat",
        exampleEmoji = "🐱",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_C",
            displayChar = "C",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.70f, 0.30f),
                        NormalizedPoint(0.54f, 0.20f),
                        NormalizedPoint(0.36f, 0.26f),
                        NormalizedPoint(0.30f, 0.50f),
                        NormalizedPoint(0.36f, 0.74f),
                        NormalizedPoint(0.54f, 0.82f),
                        NormalizedPoint(0.70f, 0.72f)
                    )
                )
            )
        )
    )

    val LETTER_D = CharacterContent(
        id = "alpha_D",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "D",
        pronunciationText = "D",
        exampleWord = "Duck",
        exampleEmoji = "🦆",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_D",
            displayChar = "D",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.56f, 0.20f),
                        NormalizedPoint(0.72f, 0.36f),
                        NormalizedPoint(0.72f, 0.66f),
                        NormalizedPoint(0.56f, 0.82f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_E = CharacterContent(
        id = "alpha_E",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "E",
        pronunciationText = "E",
        exampleWord = "Elephant",
        exampleEmoji = "🐘",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_E",
            displayChar = "E",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.52f, 0.20f),
                        NormalizedPoint(0.70f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.62f, 0.50f)
                    )
                ),
                TraceStroke(
                    id = 4,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.82f),
                        NormalizedPoint(0.52f, 0.82f),
                        NormalizedPoint(0.70f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_F = CharacterContent(
        id = "alpha_F",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "F",
        pronunciationText = "F",
        exampleWord = "Frog",
        exampleEmoji = "🐸",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_F",
            displayChar = "F",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.52f, 0.20f),
                        NormalizedPoint(0.70f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.62f, 0.50f)
                    )
                )
            )
        )
    )

    val LETTER_G = CharacterContent(
        id = "alpha_G",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "G",
        pronunciationText = "G",
        exampleWord = "Grapes",
        exampleEmoji = "🍇",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_G",
            displayChar = "G",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.70f, 0.30f),
                        NormalizedPoint(0.54f, 0.20f),
                        NormalizedPoint(0.36f, 0.26f),
                        NormalizedPoint(0.30f, 0.50f),
                        NormalizedPoint(0.36f, 0.74f),
                        NormalizedPoint(0.54f, 0.82f),
                        NormalizedPoint(0.70f, 0.74f),
                        NormalizedPoint(0.70f, 0.54f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.70f, 0.54f),
                        NormalizedPoint(0.56f, 0.54f)
                    )
                )
            )
        )
    )

    val LETTER_H = CharacterContent(
        id = "alpha_H",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "H",
        pronunciationText = "H",
        exampleWord = "House",
        exampleEmoji = "🏠",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_H",
            displayChar = "H",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.32f, 0.50f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.68f, 0.20f),
                        NormalizedPoint(0.68f, 0.50f),
                        NormalizedPoint(0.68f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.50f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.68f, 0.50f)
                    )
                )
            )
        )
    )

    val LETTER_I = CharacterContent(
        id = "alpha_I",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "I",
        pronunciationText = "I",
        exampleWord = "Ice Cream",
        exampleEmoji = "🍦",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_I",
            displayChar = "I",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_J = CharacterContent(
        id = "alpha_J",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "J",
        pronunciationText = "J",
        exampleWord = "Juice",
        exampleEmoji = "🧃",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_J",
            displayChar = "J",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.62f, 0.20f),
                        NormalizedPoint(0.62f, 0.60f),
                        NormalizedPoint(0.56f, 0.78f),
                        NormalizedPoint(0.44f, 0.82f),
                        NormalizedPoint(0.32f, 0.72f)
                    )
                )
            )
        )
    )

    val LETTER_K = CharacterContent(
        id = "alpha_K",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "K",
        pronunciationText = "K",
        exampleWord = "Kite",
        exampleEmoji = "🪁",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_K",
            displayChar = "K",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.68f, 0.22f),
                        NormalizedPoint(0.50f, 0.40f),
                        NormalizedPoint(0.34f, 0.52f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.42f, 0.46f),
                        NormalizedPoint(0.56f, 0.64f),
                        NormalizedPoint(0.70f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_L = CharacterContent(
        id = "alpha_L",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "L",
        pronunciationText = "L",
        exampleWord = "Lion",
        exampleEmoji = "🦁",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_L",
            displayChar = "L",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.20f),
                        NormalizedPoint(0.34f, 0.52f),
                        NormalizedPoint(0.34f, 0.82f),
                        NormalizedPoint(0.52f, 0.82f),
                        NormalizedPoint(0.70f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_M = CharacterContent(
        id = "alpha_M",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "M",
        pronunciationText = "M",
        exampleWord = "Moon",
        exampleEmoji = "🌙",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_M",
            displayChar = "M",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.82f),
                        NormalizedPoint(0.24f, 0.50f),
                        NormalizedPoint(0.24f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.20f),
                        NormalizedPoint(0.36f, 0.38f),
                        NormalizedPoint(0.50f, 0.56f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.56f),
                        NormalizedPoint(0.64f, 0.38f),
                        NormalizedPoint(0.76f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 4,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.76f, 0.20f),
                        NormalizedPoint(0.76f, 0.50f),
                        NormalizedPoint(0.76f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_N = CharacterContent(
        id = "alpha_N",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "N",
        pronunciationText = "N",
        exampleWord = "Nest",
        exampleEmoji = "🪺",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_N",
            displayChar = "N",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.28f, 0.82f),
                        NormalizedPoint(0.28f, 0.50f),
                        NormalizedPoint(0.28f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.28f, 0.20f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.72f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.72f, 0.82f),
                        NormalizedPoint(0.72f, 0.50f),
                        NormalizedPoint(0.72f, 0.20f)
                    )
                )
            )
        )
    )

    val LETTER_O = CharacterContent(
        id = "alpha_O",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "O",
        pronunciationText = "O",
        exampleWord = "Orange",
        exampleEmoji = "🍊",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_O",
            displayChar = "O",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.34f, 0.28f),
                        NormalizedPoint(0.28f, 0.50f),
                        NormalizedPoint(0.34f, 0.74f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.66f, 0.74f),
                        NormalizedPoint(0.72f, 0.50f),
                        NormalizedPoint(0.66f, 0.28f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                )
            )
        )
    )

    val LETTER_P = CharacterContent(
        id = "alpha_P",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "P",
        pronunciationText = "P",
        exampleWord = "Penguin",
        exampleEmoji = "🐧",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_P",
            displayChar = "P",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.32f, 0.50f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.56f, 0.20f),
                        NormalizedPoint(0.70f, 0.30f),
                        NormalizedPoint(0.70f, 0.44f),
                        NormalizedPoint(0.56f, 0.54f),
                        NormalizedPoint(0.32f, 0.54f)
                    )
                )
            )
        )
    )

    val LETTER_Q = CharacterContent(
        id = "alpha_Q",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "Q",
        pronunciationText = "Q",
        exampleWord = "Queen",
        exampleEmoji = "👑",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_Q",
            displayChar = "Q",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.34f, 0.28f),
                        NormalizedPoint(0.28f, 0.50f),
                        NormalizedPoint(0.34f, 0.74f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.66f, 0.74f),
                        NormalizedPoint(0.72f, 0.50f),
                        NormalizedPoint(0.66f, 0.28f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.54f, 0.66f),
                        NormalizedPoint(0.74f, 0.86f)
                    )
                )
            )
        )
    )

    val LETTER_R = CharacterContent(
        id = "alpha_R",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "R",
        pronunciationText = "R",
        exampleWord = "Rainbow",
        exampleEmoji = "🌈",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_R",
            displayChar = "R",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.32f, 0.50f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.56f, 0.20f),
                        NormalizedPoint(0.70f, 0.30f),
                        NormalizedPoint(0.70f, 0.44f),
                        NormalizedPoint(0.56f, 0.54f),
                        NormalizedPoint(0.32f, 0.54f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.48f, 0.54f),
                        NormalizedPoint(0.60f, 0.68f),
                        NormalizedPoint(0.72f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_S = CharacterContent(
        id = "alpha_S",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "S",
        pronunciationText = "S",
        exampleWord = "Sun",
        exampleEmoji = "☀️",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_S",
            displayChar = "S",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.68f, 0.30f),
                        NormalizedPoint(0.52f, 0.20f),
                        NormalizedPoint(0.36f, 0.28f),
                        NormalizedPoint(0.36f, 0.42f),
                        NormalizedPoint(0.52f, 0.50f),
                        NormalizedPoint(0.68f, 0.58f),
                        NormalizedPoint(0.68f, 0.72f),
                        NormalizedPoint(0.52f, 0.82f),
                        NormalizedPoint(0.32f, 0.76f)
                    )
                )
            )
        )
    )

    val LETTER_T = CharacterContent(
        id = "alpha_T",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "T",
        pronunciationText = "T",
        exampleWord = "Tiger",
        exampleEmoji = "🐯",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_T",
            displayChar = "T",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.28f, 0.20f),
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.72f, 0.20f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_U = CharacterContent(
        id = "alpha_U",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "U",
        pronunciationText = "U",
        exampleWord = "Umbrella",
        exampleEmoji = "☂️",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_U",
            displayChar = "U",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.32f, 0.56f),
                        NormalizedPoint(0.36f, 0.74f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.64f, 0.74f),
                        NormalizedPoint(0.68f, 0.56f),
                        NormalizedPoint(0.68f, 0.20f)
                    )
                )
            )
        )
    )

    val LETTER_V = CharacterContent(
        id = "alpha_V",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "V",
        pronunciationText = "V",
        exampleWord = "Van",
        exampleEmoji = "🚐",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_V",
            displayChar = "V",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.28f, 0.20f),
                        NormalizedPoint(0.38f, 0.50f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.62f, 0.50f),
                        NormalizedPoint(0.72f, 0.20f)
                    )
                )
            )
        )
    )

    val LETTER_W = CharacterContent(
        id = "alpha_W",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "W",
        pronunciationText = "W",
        exampleWord = "Watermelon",
        exampleEmoji = "🍉",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_W",
            displayChar = "W",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.22f, 0.20f),
                        NormalizedPoint(0.30f, 0.50f),
                        NormalizedPoint(0.38f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.38f, 0.82f),
                        NormalizedPoint(0.44f, 0.55f),
                        NormalizedPoint(0.50f, 0.35f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.35f),
                        NormalizedPoint(0.56f, 0.55f),
                        NormalizedPoint(0.62f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 4,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.62f, 0.82f),
                        NormalizedPoint(0.70f, 0.50f),
                        NormalizedPoint(0.78f, 0.20f)
                    )
                )
            )
        )
    )

    val LETTER_X = CharacterContent(
        id = "alpha_X",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "X",
        pronunciationText = "X",
        exampleWord = "Xylophone",
        exampleEmoji = "🎵",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_X",
            displayChar = "X",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.20f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.70f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.70f, 0.20f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.30f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_Y = CharacterContent(
        id = "alpha_Y",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "Y",
        pronunciationText = "Y",
        exampleWord = "Yo-yo",
        exampleEmoji = "🪀",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_Y",
            displayChar = "Y",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.20f),
                        NormalizedPoint(0.40f, 0.35f),
                        NormalizedPoint(0.50f, 0.50f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.70f, 0.20f),
                        NormalizedPoint(0.60f, 0.35f),
                        NormalizedPoint(0.50f, 0.50f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.50f, 0.66f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_Z = CharacterContent(
        id = "alpha_Z",
        category = CategoryType.ALPHABET_UPPER,
        language = LearningLanguage.ENGLISH,
        displayValue = "Z",
        pronunciationText = "Z",
        exampleWord = "Zebra",
        exampleEmoji = "🦓",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_Z",
            displayChar = "Z",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.22f),
                        NormalizedPoint(0.50f, 0.22f),
                        NormalizedPoint(0.70f, 0.22f),
                        NormalizedPoint(0.50f, 0.52f),
                        NormalizedPoint(0.30f, 0.80f),
                        NormalizedPoint(0.50f, 0.80f),
                        NormalizedPoint(0.70f, 0.80f)
                    )
                )
            )
        )
    )

    val ALL_UPPERCASE = listOf(
        LETTER_A, LETTER_B, LETTER_C, LETTER_D, LETTER_E,
        LETTER_F, LETTER_G, LETTER_H, LETTER_I, LETTER_J,
        LETTER_K, LETTER_L, LETTER_M, LETTER_N, LETTER_O,
        LETTER_P, LETTER_Q, LETTER_R, LETTER_S, LETTER_T,
        LETTER_U, LETTER_V, LETTER_W, LETTER_X, LETTER_Y,
        LETTER_Z
    )
}
