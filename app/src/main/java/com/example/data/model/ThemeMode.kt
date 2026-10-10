package com.example.data.model

enum class ThemeMode(val title: String, val emoji: String) {
    HAPPY_GARDEN("Happy Garden", "🌸"),
    DINO_WORLD("Dino World", "🦕"),
    SPACE_ADVENTURE("Space Adventure", "🚀")
}

enum class LearningLanguage {
    NUMBER,
    ENGLISH,
    HINDI
}

enum class CategoryType {
    NUMBERS,
    ALPHABET_UPPER,
    ALPHABET_LOWER,
    SHAPES
}

enum class MiniActivityType {
    COUNT_OBJECTS,
    BALLOON_POP,
    FIND_OBJECT,
    SIMPLE_MATCHING,
    FEED_ANIMAL,
    TAP_CORRECT
}
