package com.example.data.content

import com.example.data.model.CategoryType
import com.example.data.model.CharacterContent
import com.example.data.model.LearningLanguage
import com.example.data.model.MiniActivityType
import com.example.domain.tracing.NormalizedPoint
import com.example.domain.tracing.TraceCharacterDef
import com.example.domain.tracing.TraceStroke

object ShapesAndStrokesContent {

    // 1. Horizontal Straight Line
    val STROKE_H_LINE = CharacterContent(
        id = "stroke_h_line",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "—",
        pronunciationText = "Line",
        exampleWord = "Straight Line",
        exampleEmoji = "📏",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_h_line",
            displayChar = "—",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.15f, 0.50f),
                        NormalizedPoint(0.35f, 0.50f),
                        NormalizedPoint(0.55f, 0.50f),
                        NormalizedPoint(0.75f, 0.50f),
                        NormalizedPoint(0.85f, 0.50f)
                    )
                )
            )
        )
    )

    // 2. Vertical Tall Line
    val STROKE_V_LINE = CharacterContent(
        id = "stroke_v_line",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "|",
        pronunciationText = "Tall Line",
        exampleWord = "Tall Tree",
        exampleEmoji = "🌲",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_v_line",
            displayChar = "|",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.15f),
                        NormalizedPoint(0.50f, 0.35f),
                        NormalizedPoint(0.50f, 0.55f),
                        NormalizedPoint(0.50f, 0.75f),
                        NormalizedPoint(0.50f, 0.85f)
                    )
                )
            )
        )
    )

    // 3. Zigzag Mountain Path
    val STROKE_ZIGZAG = CharacterContent(
        id = "stroke_zigzag",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "⚡",
        pronunciationText = "Zigzag",
        exampleWord = "Zigzag Trail",
        exampleEmoji = "⚡",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_zigzag",
            displayChar = "⚡",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.18f, 0.75f),
                        NormalizedPoint(0.28f, 0.50f),
                        NormalizedPoint(0.38f, 0.25f),
                        NormalizedPoint(0.48f, 0.50f),
                        NormalizedPoint(0.58f, 0.75f),
                        NormalizedPoint(0.68f, 0.50f),
                        NormalizedPoint(0.82f, 0.25f)
                    )
                )
            )
        )
    )

    // 4. Ocean Wave
    val STROKE_WAVE = CharacterContent(
        id = "stroke_wave",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "〰️",
        pronunciationText = "Wave",
        exampleWord = "Ocean Wave",
        exampleEmoji = "🌊",
        exampleCount = 1,
        miniActivityType = MiniActivityType.COUNT_OBJECTS,
        traceDefinition = TraceCharacterDef(
            id = "trace_wave",
            displayChar = "〰️",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.15f, 0.60f),
                        NormalizedPoint(0.25f, 0.35f),
                        NormalizedPoint(0.38f, 0.35f),
                        NormalizedPoint(0.50f, 0.60f),
                        NormalizedPoint(0.62f, 0.65f),
                        NormalizedPoint(0.75f, 0.35f),
                        NormalizedPoint(0.85f, 0.55f)
                    )
                )
            )
        )
    )

    // 5. Sunny Circle
    val SHAPE_CIRCLE = CharacterContent(
        id = "shape_circle",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "◯",
        pronunciationText = "Circle",
        exampleWord = "Round Circle",
        exampleEmoji = "🟡",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_circle",
            displayChar = "◯",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.32f, 0.28f),
                        NormalizedPoint(0.24f, 0.48f),
                        NormalizedPoint(0.28f, 0.68f),
                        NormalizedPoint(0.50f, 0.80f),
                        NormalizedPoint(0.72f, 0.68f),
                        NormalizedPoint(0.76f, 0.48f),
                        NormalizedPoint(0.68f, 0.28f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                )
            )
        )
    )

    // 6. Friendly Square
    val SHAPE_SQUARE = CharacterContent(
        id = "shape_square",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "□",
        pronunciationText = "Square",
        exampleWord = "Toy Block",
        exampleEmoji = "🟦",
        exampleCount = 1,
        miniActivityType = MiniActivityType.SIMPLE_MATCHING,
        traceDefinition = TraceCharacterDef(
            id = "trace_square",
            displayChar = "□",
            strokes = listOf(
                // Top edge
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.24f),
                        NormalizedPoint(0.50f, 0.24f),
                        NormalizedPoint(0.76f, 0.24f)
                    )
                ),
                // Right edge
                TraceStroke(
                    id = 2,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.76f, 0.24f),
                        NormalizedPoint(0.76f, 0.50f),
                        NormalizedPoint(0.76f, 0.76f)
                    )
                ),
                // Bottom edge
                TraceStroke(
                    id = 3,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.76f, 0.76f),
                        NormalizedPoint(0.50f, 0.76f),
                        NormalizedPoint(0.24f, 0.76f)
                    )
                ),
                // Left edge
                TraceStroke(
                    id = 4,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.24f, 0.76f),
                        NormalizedPoint(0.24f, 0.50f),
                        NormalizedPoint(0.24f, 0.24f)
                    )
                )
            )
        )
    )

    // 7. Roof Triangle
    val SHAPE_TRIANGLE = CharacterContent(
        id = "shape_triangle",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "△",
        pronunciationText = "Triangle",
        exampleWord = "Party Hat",
        exampleEmoji = "🔺",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FIND_OBJECT,
        traceDefinition = TraceCharacterDef(
            id = "trace_triangle",
            displayChar = "△",
            strokes = listOf(
                // Down-left diagonal
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.20f),
                        NormalizedPoint(0.36f, 0.50f),
                        NormalizedPoint(0.22f, 0.78f)
                    )
                ),
                // Bottom horizontal
                TraceStroke(
                    id = 2,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.22f, 0.78f),
                        NormalizedPoint(0.50f, 0.78f),
                        NormalizedPoint(0.78f, 0.78f)
                    )
                ),
                // Up-left diagonal
                TraceStroke(
                    id = 3,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.78f, 0.78f),
                        NormalizedPoint(0.64f, 0.50f),
                        NormalizedPoint(0.50f, 0.20f)
                    )
                )
            )
        )
    )

    // 8. Twinkle Star
    val SHAPE_STAR = CharacterContent(
        id = "shape_star",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "⭐",
        pronunciationText = "Star",
        exampleWord = "Twinkle Star",
        exampleEmoji = "⭐",
        exampleCount = 1,
        miniActivityType = MiniActivityType.BALLOON_POP,
        traceDefinition = TraceCharacterDef(
            id = "trace_star",
            displayChar = "⭐",
            strokes = listOf(
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.16f),
                        NormalizedPoint(0.58f, 0.36f),
                        NormalizedPoint(0.82f, 0.38f),
                        NormalizedPoint(0.64f, 0.54f),
                        NormalizedPoint(0.70f, 0.78f),
                        NormalizedPoint(0.50f, 0.65f),
                        NormalizedPoint(0.30f, 0.78f),
                        NormalizedPoint(0.36f, 0.54f),
                        NormalizedPoint(0.18f, 0.38f),
                        NormalizedPoint(0.42f, 0.36f),
                        NormalizedPoint(0.50f, 0.16f)
                    )
                )
            )
        )
    )

    // 9. Sweet Heart
    val SHAPE_HEART = CharacterContent(
        id = "shape_heart",
        category = CategoryType.SHAPES,
        language = LearningLanguage.ENGLISH,
        displayValue = "♥",
        pronunciationText = "Heart",
        exampleWord = "Sweet Heart",
        exampleEmoji = "❤️",
        exampleCount = 1,
        miniActivityType = MiniActivityType.FEED_ANIMAL,
        traceDefinition = TraceCharacterDef(
            id = "trace_heart",
            displayChar = "♥",
            strokes = listOf(
                // Left lobe
                TraceStroke(
                    id = 1,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.35f),
                        NormalizedPoint(0.38f, 0.22f),
                        NormalizedPoint(0.24f, 0.30f),
                        NormalizedPoint(0.24f, 0.50f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                ),
                // Right lobe
                TraceStroke(
                    id = 2,
                    tolerance = 0.22f,
                    startRadius = 0.22f,
                    pathPoints = listOf(
                        NormalizedPoint(0.50f, 0.35f),
                        NormalizedPoint(0.62f, 0.22f),
                        NormalizedPoint(0.76f, 0.30f),
                        NormalizedPoint(0.76f, 0.50f),
                        NormalizedPoint(0.50f, 0.82f)
                    )
                )
            )
        )
    )

    val ALL_SHAPES = listOf(
        STROKE_H_LINE,
        STROKE_V_LINE,
        STROKE_ZIGZAG,
        STROKE_WAVE,
        SHAPE_CIRCLE,
        SHAPE_SQUARE,
        SHAPE_TRIANGLE,
        SHAPE_STAR,
        SHAPE_HEART
    )
}
