package com.example.data.content

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent
import com.example.data.model.LearningLanguage
import com.example.data.model.MiniActivityType
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceCharacterDef
import com.example.domain.tracing.TraceStroke

object AlphabetLowerContent {

    val LETTER_LOWER_A = CharacterContent(
        id = "alpha_a",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "a",
        pronunciationText = "Little a",
        exampleWord = "Ant",
        exampleEmoji = "🐜",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_a",
            displayChar = "a",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.66f, 0.48f),
                        NormalizedPoint(0.50f, 0.42f),
                        NormalizedPoint(0.32f, 0.54f),
                        NormalizedPoint(0.32f, 0.72f),
                        NormalizedPoint(0.48f, 0.84f),
                        NormalizedPoint(0.66f, 0.78f),
                        NormalizedPoint(0.66f, 0.52f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.66f, 0.44f),
                        NormalizedPoint(0.66f, 0.64f),
                        NormalizedPoint(0.66f, 0.84f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_B = CharacterContent(
        id = "alpha_b",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "b",
        pronunciationText = "Little b",
        exampleWord = "Bear",
        exampleEmoji = "🐻",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_b",
            displayChar = "b",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.18f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.52f),
                        NormalizedPoint(0.52f, 0.44f),
                        NormalizedPoint(0.68f, 0.54f),
                        NormalizedPoint(0.68f, 0.72f),
                        NormalizedPoint(0.52f, 0.82f),
                        NormalizedPoint(0.34f, 0.78f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_C = CharacterContent(
        id = "alpha_c",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "c",
        pronunciationText = "Little c",
        exampleWord = "Cookie",
        exampleEmoji = "🍪",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_c",
            displayChar = "c",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.66f, 0.52f),
                        NormalizedPoint(0.52f, 0.44f),
                        NormalizedPoint(0.36f, 0.52f),
                        NormalizedPoint(0.34f, 0.68f),
                        NormalizedPoint(0.48f, 0.82f),
                        NormalizedPoint(0.66f, 0.76f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_D = CharacterContent(
        id = "alpha_d",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "d",
        pronunciationText = "Little d",
        exampleWord = "Dog",
        exampleEmoji = "🐶",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_d",
            displayChar = "d",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.52f),
                        NormalizedPoint(0.48f, 0.44f),
                        NormalizedPoint(0.34f, 0.54f),
                        NormalizedPoint(0.34f, 0.72f),
                        NormalizedPoint(0.48f, 0.82f),
                        NormalizedPoint(0.64f, 0.76f),
                        NormalizedPoint(0.64f, 0.52f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.18f),
                        NormalizedPoint(0.64f, 0.50f),
                        NormalizedPoint(0.64f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_E = CharacterContent(
        id = "alpha_e",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "e",
        pronunciationText = "Little e",
        exampleWord = "Egg",
        exampleEmoji = "🥚",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_e",
            displayChar = "e",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.62f),
                        NormalizedPoint(0.66f, 0.62f),
                        NormalizedPoint(0.62f, 0.48f),
                        NormalizedPoint(0.48f, 0.44f),
                        NormalizedPoint(0.34f, 0.54f),
                        NormalizedPoint(0.34f, 0.72f),
                        NormalizedPoint(0.48f, 0.82f),
                        NormalizedPoint(0.66f, 0.78f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_F = CharacterContent(
        id = "alpha_f",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "f",
        pronunciationText = "Little f",
        exampleWord = "Flower",
        exampleEmoji = "🌸",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_f",
            displayChar = "f",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.22f),
                        NormalizedPoint(0.50f, 0.18f),
                        NormalizedPoint(0.42f, 0.32f),
                        NormalizedPoint(0.42f, 0.56f),
                        NormalizedPoint(0.42f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.44f),
                        NormalizedPoint(0.56f, 0.44f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_G = CharacterContent(
        id = "alpha_g",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "g",
        pronunciationText = "Little g",
        exampleWord = "Giraffe",
        exampleEmoji = "🦒",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_g",
            displayChar = "g",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.48f),
                        NormalizedPoint(0.48f, 0.42f),
                        NormalizedPoint(0.34f, 0.52f),
                        NormalizedPoint(0.34f, 0.66f),
                        NormalizedPoint(0.48f, 0.74f),
                        NormalizedPoint(0.64f, 0.70f),
                        NormalizedPoint(0.64f, 0.48f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.44f),
                        NormalizedPoint(0.64f, 0.74f),
                        NormalizedPoint(0.56f, 0.88f),
                        NormalizedPoint(0.38f, 0.88f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_H = CharacterContent(
        id = "alpha_h",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "h",
        pronunciationText = "Little h",
        exampleWord = "Hat",
        exampleEmoji = "👒",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_h",
            displayChar = "h",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.18f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.54f),
                        NormalizedPoint(0.50f, 0.44f),
                        NormalizedPoint(0.64f, 0.54f),
                        NormalizedPoint(0.64f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_I = CharacterContent(
        id = "alpha_i",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "i",
        pronunciationText = "Little i",
        exampleWord = "Igloo",
        exampleEmoji = "🧊",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_i",
            displayChar = "i",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.46f),
                        NormalizedPoint(0.50f, 0.64f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.28f),
                        NormalizedPoint(0.50f, 0.32f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_J = CharacterContent(
        id = "alpha_j",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "j",
        pronunciationText = "Little j",
        exampleWord = "Jellyfish",
        exampleEmoji = "🪼",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_j",
            displayChar = "j",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.56f, 0.44f),
                        NormalizedPoint(0.56f, 0.74f),
                        NormalizedPoint(0.48f, 0.88f),
                        NormalizedPoint(0.34f, 0.84f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.56f, 0.28f),
                        NormalizedPoint(0.56f, 0.32f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_K = CharacterContent(
        id = "alpha_k",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "k",
        pronunciationText = "Little k",
        exampleWord = "Koala",
        exampleEmoji = "🐨",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_k",
            displayChar = "k",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.18f),
                        NormalizedPoint(0.34f, 0.50f),
                        NormalizedPoint(0.34f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.48f),
                        NormalizedPoint(0.48f, 0.62f),
                        NormalizedPoint(0.34f, 0.68f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.46f, 0.64f),
                        NormalizedPoint(0.58f, 0.74f),
                        NormalizedPoint(0.68f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_L = CharacterContent(
        id = "alpha_l",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "l",
        pronunciationText = "Little l",
        exampleWord = "Leaf",
        exampleEmoji = "🍃",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_l",
            displayChar = "l",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.48f, 0.18f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.48f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_M = CharacterContent(
        id = "alpha_m",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "m",
        pronunciationText = "Little m",
        exampleWord = "Mouse",
        exampleEmoji = "🐭",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_m",
            displayChar = "m",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.44f),
                        NormalizedPoint(0.24f, 0.64f),
                        NormalizedPoint(0.24f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.52f),
                        NormalizedPoint(0.36f, 0.44f),
                        NormalizedPoint(0.50f, 0.54f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.52f),
                        NormalizedPoint(0.64f, 0.44f),
                        NormalizedPoint(0.76f, 0.54f),
                        NormalizedPoint(0.76f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_N = CharacterContent(
        id = "alpha_n",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "n",
        pronunciationText = "Little n",
        exampleWord = "Nut",
        exampleEmoji = "🥜",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_n",
            displayChar = "n",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.44f),
                        NormalizedPoint(0.30f, 0.64f),
                        NormalizedPoint(0.30f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.30f, 0.54f),
                        NormalizedPoint(0.48f, 0.44f),
                        NormalizedPoint(0.68f, 0.54f),
                        NormalizedPoint(0.68f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_O = CharacterContent(
        id = "alpha_o",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "o",
        pronunciationText = "Little o",
        exampleWord = "Owl",
        exampleEmoji = "🦉",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_o",
            displayChar = "o",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.44f),
                        NormalizedPoint(0.34f, 0.52f),
                        NormalizedPoint(0.34f, 0.72f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.66f, 0.72f),
                        NormalizedPoint(0.66f, 0.52f),
                        NormalizedPoint(0.50f, 0.44f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_P = CharacterContent(
        id = "alpha_p",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "p",
        pronunciationText = "Little p",
        exampleWord = "Pig",
        exampleEmoji = "🐷",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_p",
            displayChar = "p",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.44f),
                        NormalizedPoint(0.34f, 0.68f),
                        NormalizedPoint(0.34f, 0.90f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.52f),
                        NormalizedPoint(0.52f, 0.44f),
                        NormalizedPoint(0.66f, 0.54f),
                        NormalizedPoint(0.66f, 0.68f),
                        NormalizedPoint(0.52f, 0.76f),
                        NormalizedPoint(0.34f, 0.72f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_Q = CharacterContent(
        id = "alpha_q",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "q",
        pronunciationText = "Little q",
        exampleWord = "Quail",
        exampleEmoji = "🐦",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_q",
            displayChar = "q",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.52f),
                        NormalizedPoint(0.48f, 0.44f),
                        NormalizedPoint(0.34f, 0.54f),
                        NormalizedPoint(0.34f, 0.68f),
                        NormalizedPoint(0.48f, 0.76f),
                        NormalizedPoint(0.64f, 0.70f),
                        NormalizedPoint(0.64f, 0.52f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.44f),
                        NormalizedPoint(0.64f, 0.70f),
                        NormalizedPoint(0.64f, 0.90f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_R = CharacterContent(
        id = "alpha_r",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "r",
        pronunciationText = "Little r",
        exampleWord = "Rabbit",
        exampleEmoji = "🐰",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_r",
            displayChar = "r",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.36f, 0.44f),
                        NormalizedPoint(0.36f, 0.64f),
                        NormalizedPoint(0.36f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.36f, 0.58f),
                        NormalizedPoint(0.50f, 0.46f),
                        NormalizedPoint(0.66f, 0.52f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_S = CharacterContent(
        id = "alpha_s",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "s",
        pronunciationText = "Little s",
        exampleWord = "Star",
        exampleEmoji = "⭐",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_s",
            displayChar = "s",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.50f),
                        NormalizedPoint(0.50f, 0.44f),
                        NormalizedPoint(0.36f, 0.50f),
                        NormalizedPoint(0.40f, 0.62f),
                        NormalizedPoint(0.54f, 0.66f),
                        NormalizedPoint(0.66f, 0.72f),
                        NormalizedPoint(0.60f, 0.82f),
                        NormalizedPoint(0.36f, 0.80f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_T = CharacterContent(
        id = "alpha_t",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "t",
        pronunciationText = "Little t",
        exampleWord = "Turtle",
        exampleEmoji = "🐢",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_t",
            displayChar = "t",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.48f, 0.24f),
                        NormalizedPoint(0.48f, 0.68f),
                        NormalizedPoint(0.58f, 0.82f),
                        NormalizedPoint(0.68f, 0.80f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.44f),
                        NormalizedPoint(0.64f, 0.44f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_U = CharacterContent(
        id = "alpha_u",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "u",
        pronunciationText = "Little u",
        exampleWord = "Unicorn",
        exampleEmoji = "🦄",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_u",
            displayChar = "u",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.44f),
                        NormalizedPoint(0.34f, 0.68f),
                        NormalizedPoint(0.46f, 0.82f),
                        NormalizedPoint(0.64f, 0.82f),
                        NormalizedPoint(0.64f, 0.44f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.54f),
                        NormalizedPoint(0.64f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_V = CharacterContent(
        id = "alpha_v",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "v",
        pronunciationText = "Little v",
        exampleWord = "Violin",
        exampleEmoji = "🎻",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_v",
            displayChar = "v",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.44f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.68f, 0.44f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_W = CharacterContent(
        id = "alpha_w",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "w",
        pronunciationText = "Little w",
        exampleWord = "Whale",
        exampleEmoji = "🐳",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_w",
            displayChar = "w",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.44f),
                        NormalizedPoint(0.36f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.36f, 0.82f),
                        NormalizedPoint(0.50f, 0.54f)
                    )
                ),
                TraceStroke(
                    id = 3,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.54f),
                        NormalizedPoint(0.64f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 4,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.82f),
                        NormalizedPoint(0.76f, 0.44f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_X = CharacterContent(
        id = "alpha_x",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "x",
        pronunciationText = "Little x",
        exampleWord = "Fox",
        exampleEmoji = "🦊",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_x",
            displayChar = "x",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.44f),
                        NormalizedPoint(0.50f, 0.62f),
                        NormalizedPoint(0.68f, 0.82f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.68f, 0.44f),
                        NormalizedPoint(0.50f, 0.62f),
                        NormalizedPoint(0.32f, 0.82f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_Y = CharacterContent(
        id = "alpha_y",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "y",
        pronunciationText = "Little y",
        exampleWord = "Yak",
        exampleEmoji = "🐂",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_y",
            displayChar = "y",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.44f),
                        NormalizedPoint(0.50f, 0.66f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.68f, 0.44f),
                        NormalizedPoint(0.50f, 0.66f),
                        NormalizedPoint(0.36f, 0.88f)
                    )
                )
            )
        )
    )

    val LETTER_LOWER_Z = CharacterContent(
        id = "alpha_z",
        category = CategoryType.ALPHABET_LOWER,
        language = LearningLanguage.ENGLISH,
        displayValue = "z",
        pronunciationText = "Little z",
        exampleWord = "Zoo",
        exampleEmoji = "🦁",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_lower_z",
            displayChar = "z",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.44f),
                        NormalizedPoint(0.66f, 0.44f),
                        NormalizedPoint(0.34f, 0.82f),
                        NormalizedPoint(0.66f, 0.82f)
                    )
                )
            )
        )
    )

    val ALL_LOWERCASE = listOf(
        LETTER_LOWER_A, LETTER_LOWER_B, LETTER_LOWER_C, LETTER_LOWER_D, LETTER_LOWER_E,
        LETTER_LOWER_F, LETTER_LOWER_G, LETTER_LOWER_H, LETTER_LOWER_I, LETTER_LOWER_J,
        LETTER_LOWER_K, LETTER_LOWER_L, LETTER_LOWER_M, LETTER_LOWER_N, LETTER_LOWER_O,
        LETTER_LOWER_P, LETTER_LOWER_Q, LETTER_LOWER_R, LETTER_LOWER_S, LETTER_LOWER_T,
        LETTER_LOWER_U, LETTER_LOWER_V, LETTER_LOWER_W, LETTER_LOWER_X, LETTER_LOWER_Y,
        LETTER_LOWER_Z
    )
}
