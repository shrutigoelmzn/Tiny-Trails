package com.example.feature.certificate

data class MilestoneCertificate(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val badgeEmoji: String,
    val categoryTag: String,
    val requirementDescription: String,
    val checkUnlocked: (totalCompleted: Int, numbersCompleted: Int, lettersCompleted: Int, shapesCompleted: Int, totalStars: Int) -> Boolean
)

object CertificateDefinitions {

    val ALL_CERTIFICATES = listOf(
        MilestoneCertificate(
            id = "cert_first_hero",
            title = "First Tracing Hero",
            subtitle = "Official Early Learner Diploma",
            description = "For showing joyful curiosity and taking the brave first steps in tracing lines and letters!",
            badgeEmoji = "🌟",
            categoryTag = "Starter Award",
            requirementDescription = "Complete your first tracing lesson",
            checkUnlocked = { total, _, _, _, _ -> total >= 1 }
        ),
        MilestoneCertificate(
            id = "cert_number_master",
            title = "Number Master Award",
            subtitle = "Math & Counting Achievement",
            description = "For mastering numbers and demonstrating excellent precision and counting skills!",
            badgeEmoji = "🔢",
            categoryTag = "Numbers 0–9",
            requirementDescription = "Complete 5 or more numbers",
            checkUnlocked = { _, nums, _, _, _ -> nums >= 5 }
        ),
        MilestoneCertificate(
            id = "cert_alphabet_explorer",
            title = "Alphabet Explorer Diploma",
            subtitle = "Letters & Phonics Achievement",
            description = "For incredible perseverance and talent in exploring alphabet letters and words!",
            badgeEmoji = "🔤",
            categoryTag = "Alphabet Letters",
            requirementDescription = "Complete 8 or more letters",
            checkUnlocked = { _, _, letters, _, _ -> letters >= 8 }
        ),
        MilestoneCertificate(
            id = "cert_shape_artist",
            title = "Shape & Stroke Master",
            subtitle = "Fine Motor Skills & Geometry",
            description = "For steady hand-eye coordination and artistic mastery of straight lines, waves, and shapes!",
            badgeEmoji = "🔺",
            categoryTag = "Shapes & Strokes",
            requirementDescription = "Complete 3 or more shapes & lines",
            checkUnlocked = { _, _, _, shapes, _ -> shapes >= 3 }
        ),
        MilestoneCertificate(
            id = "cert_superstar_graduate",
            title = "Superstar Graduate",
            subtitle = "Highest Honor of Tiny Trails",
            description = "For outstanding dedication, joyful learning, and collecting a shining constellation of stars!",
            badgeEmoji = "🏆",
            categoryTag = "Grand Mastery",
            requirementDescription = "Earn 25 or more stars",
            checkUnlocked = { _, _, _, _, stars -> stars >= 25 }
        )
    )
}
