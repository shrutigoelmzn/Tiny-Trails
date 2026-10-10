package com.example.data.content

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent
import com.example.data.model.LearningLanguage
import com.example.data.model.MiniActivityType
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceCharacterDef
import com.example.domain.tracing.TraceStroke

object NumbersContent {

    val NUMBER_0 = CharacterContent(
        id = "num_0",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "0",
        pronunciationText = "Zero",
        exampleWord = "Zero Stars",
        exampleEmoji = "✨",
        exampleCount = 0,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_0",
            displayChar = "0",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.35f, 0.26f),
                        NormalizedPoint(0.30f, 0.45f),
                        NormalizedPoint(0.30f, 0.60f),
                        NormalizedPoint(0.36f, 0.76f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.64f, 0.76f),
                        NormalizedPoint(0.70f, 0.60f),
                        NormalizedPoint(0.70f, 0.45f),
                        NormalizedPoint(0.65f, 0.26f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                )
            )
        )
    )

    val NUMBER_1 = CharacterContent(
        id = "num_1",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "1",
        pronunciationText = "One",
        exampleWord = "One Sun",
        exampleEmoji = "☀️",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_1",
            displayChar = "1",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.38f, 0.32f),
                        NormalizedPoint(0.44f, 0.25f),
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.50f, 0.35f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.50f, 0.65f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                )
            )
        )
    )

    val NUMBER_2 = CharacterContent(
        id = "num_2",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "2",
        pronunciationText = "Two",
        exampleWord = "Two Ducks",
        exampleEmoji = "🦆",
        exampleCount = 2,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_2",
            displayChar = "2",
            strokes = listOf(
                // Stroke 1: Arch to diagonal base
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.30f),
                        NormalizedPoint(0.42f, 0.20f),
                        NormalizedPoint(0.58f, 0.20f),
                        NormalizedPoint(0.68f, 0.30f),
                        NormalizedPoint(0.66f, 0.44f),
                        NormalizedPoint(0.52f, 0.58f),
                        NormalizedPoint(0.32f, 0.80f)
                    )
                ),
                // Stroke 2: Horizontal bottom base
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.80f),
                        NormalizedPoint(0.46f, 0.80f),
                        NormalizedPoint(0.60f, 0.80f),
                        NormalizedPoint(0.72f, 0.80f)
                    )
                )
            )
        )
    )

    val NUMBER_3 = CharacterContent(
        id = "num_3",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "3",
        pronunciationText = "Three",
        exampleWord = "Three Apples",
        exampleEmoji = "🍎",
        exampleCount = 3,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_3",
            displayChar = "3",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.34f, 0.24f),
                        NormalizedPoint(0.48f, 0.18f),
                        NormalizedPoint(0.66f, 0.22f),
                        NormalizedPoint(0.68f, 0.36f),
                        NormalizedPoint(0.52f, 0.48f)
                    )
                ),
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.52f, 0.48f),
                        NormalizedPoint(0.70f, 0.58f),
                        NormalizedPoint(0.70f, 0.74f),
                        NormalizedPoint(0.54f, 0.84f),
                        NormalizedPoint(0.34f, 0.80f)
                    )
                )
            )
        )
    )

    val NUMBER_4 = CharacterContent(
        id = "num_4",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "4",
        pronunciationText = "Four",
        exampleWord = "Four Birds",
        exampleEmoji = "🐦",
        exampleCount = 4,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_4",
            displayChar = "4",
            strokes = listOf(
                // Stroke 1: Diagonal slant down and across
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.58f, 0.20f),
                        NormalizedPoint(0.44f, 0.40f),
                        NormalizedPoint(0.30f, 0.60f),
                        NormalizedPoint(0.46f, 0.60f),
                        NormalizedPoint(0.74f, 0.60f)
                    )
                ),
                // Stroke 2: Vertical straight line down
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.60f, 0.36f),
                        NormalizedPoint(0.60f, 0.58f),
                        NormalizedPoint(0.60f, 0.82f)
                    )
                )
            )
        )
    )

    val NUMBER_5 = CharacterContent(
        id = "num_5",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "5",
        pronunciationText = "Five",
        exampleWord = "Five Fish",
        exampleEmoji = "🐟",
        exampleCount = 5,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_5",
            displayChar = "5",
            strokes = listOf(
                // Stroke 1: Downward neck and circular belly
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.42f, 0.20f),
                        NormalizedPoint(0.38f, 0.45f),
                        NormalizedPoint(0.54f, 0.42f),
                        NormalizedPoint(0.68f, 0.52f),
                        NormalizedPoint(0.68f, 0.70f),
                        NormalizedPoint(0.54f, 0.82f),
                        NormalizedPoint(0.36f, 0.78f)
                    )
                ),
                // Stroke 2: Top horizontal bar
                TraceStroke(
                    id = 2,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.42f, 0.20f),
                        NormalizedPoint(0.56f, 0.20f),
                        NormalizedPoint(0.70f, 0.20f)
                    )
                )
            )
        )
    )

    val NUMBER_6 = CharacterContent(
        id = "num_6",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "6",
        pronunciationText = "Six",
        exampleWord = "Six Balls",
        exampleEmoji = "⚽",
        exampleCount = 6,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_6",
            displayChar = "6",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.22f),
                        NormalizedPoint(0.48f, 0.24f),
                        NormalizedPoint(0.34f, 0.42f),
                        NormalizedPoint(0.30f, 0.64f),
                        NormalizedPoint(0.40f, 0.82f),
                        NormalizedPoint(0.60f, 0.82f),
                        NormalizedPoint(0.68f, 0.68f),
                        NormalizedPoint(0.64f, 0.54f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.32f, 0.62f)
                    )
                )
            )
        )
    )

    val NUMBER_7 = CharacterContent(
        id = "num_7",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "7",
        pronunciationText = "Seven",
        exampleWord = "Seven Butterflies",
        exampleEmoji = "🦋",
        exampleCount = 7,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_7",
            displayChar = "7",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.32f, 0.20f),
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.68f, 0.20f),
                        NormalizedPoint(0.58f, 0.40f),
                        NormalizedPoint(0.48f, 0.62f),
                        NormalizedPoint(0.42f, 0.82f)
                    )
                )
            )
        )
    )

    val NUMBER_8 = CharacterContent(
        id = "num_8",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "8",
        pronunciationText = "Eight",
        exampleWord = "Eight Flowers",
        exampleEmoji = "🌸",
        exampleCount = 8,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_8",
            displayChar = "8",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.36f, 0.26f),
                        NormalizedPoint(0.38f, 0.42f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.66f, 0.60f),
                        NormalizedPoint(0.66f, 0.74f),
                        NormalizedPoint(0.50f, 0.82f),
                        NormalizedPoint(0.34f, 0.74f),
                        NormalizedPoint(0.34f, 0.60f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.64f, 0.42f),
                        NormalizedPoint(0.64f, 0.26f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                )
            )
        )
    )

    val NUMBER_9 = CharacterContent(
        id = "num_9",
        category = CategoryType.NUMBERS,
        language = LearningLanguage.NUMBER,
        displayValue = "9",
        pronunciationText = "Nine",
        exampleWord = "Nine Balloons",
        exampleEmoji = "🎈",
        exampleCount = 9,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_9",
            displayChar = "9",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.18f,
                    startRadius = 0.20f,
                    pathPoints = listOf(
                        NormalizedPoint(0.64f, 0.48f),
                        NormalizedPoint(0.50f, 0.50f),
                        NormalizedPoint(0.34f, 0.42f),
                        NormalizedPoint(0.34f, 0.28f),
                        NormalizedPoint(0.46f, 0.20f),
                        NormalizedPoint(0.64f, 0.20f),
                        NormalizedPoint(0.66f, 0.40f),
                        NormalizedPoint(0.66f, 0.62f),
                        NormalizedPoint(0.56f, 0.82f)
                    )
                )
            )
        )
    )

    val ALL_NUMBERS = listOf(
        NUMBER_0, NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4,
        NUMBER_5, NUMBER_6, NUMBER_7, NUMBER_8, NUMBER_9
    )
}
