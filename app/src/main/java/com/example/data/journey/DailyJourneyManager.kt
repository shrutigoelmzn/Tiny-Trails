package com.example.data.journey

import com.example.data.content.CharacterRepository
import com.example.data.model.CharacterContent
import com.example.data.model.CharacterProgress

enum class JourneyStepType {
    LEARN_AND_TRACE,
    MINI_ACTIVITY,
    REVISION_TRACE,
    REWARD_MILESTONE
}

data class JourneyStep(
    val id: String,
    val character: CharacterContent,
    val stepType: JourneyStepType,
    val title: String,
    val isCompleted: Boolean = false
)

object DailyJourneyManager {

    /**
     * Generates a balanced 3–7 minute daily learning journey mixing new lessons,
     * weak/retried lessons for gentle revision, and mini activities.
     * Priority:
     * 1. Unfinished lessons
     * 2. Weak / frequently retried lessons (needs practice)
     * 3. New lessons
     * 4. Revision
     */
    fun generateDailySession(progressMap: Map<String, CharacterProgress>): List<JourneyStep> {
        val allChars = CharacterRepository.allCharacters

        // 1. Identify weak or retried lessons (stars < 3 or attempts >= 2)
        val needsPractice = allChars.filter { char ->
            val prog = progressMap[char.id]
            prog != null && (!prog.completed || prog.stars < 3 || prog.attempts > 1)
        }

        // 2. Identify unattempted new lessons
        val unattempted = allChars.filter { char ->
            val prog = progressMap[char.id]
            prog == null || !prog.completed
        }

        // 3. Completed lessons for revision
        val completed = allChars.filter { char ->
            progressMap[char.id]?.completed == true
        }

        val primaryChar = unattempted.firstOrNull() ?: needsPractice.firstOrNull() ?: allChars.first()
        val secondaryChar = unattempted.getOrNull(1) ?: needsPractice.getOrNull(1) ?: completed.firstOrNull() ?: allChars[1]
        val revisionChar = needsPractice.firstOrNull { it.id != primaryChar.id } ?: completed.shuffled().firstOrNull() ?: allChars[2]

        return listOf(
            JourneyStep(
                id = "step_1",
                character = primaryChar,
                stepType = JourneyStepType.LEARN_AND_TRACE,
                title = "Learn & Trace '${primaryChar.displayValue}'",
                isCompleted = progressMap[primaryChar.id]?.completed == true
            ),
            JourneyStep(
                id = "step_2",
                character = revisionChar,
                stepType = JourneyStepType.MINI_ACTIVITY,
                title = "Play: ${revisionChar.exampleWord} Mini Activity",
                isCompleted = progressMap[revisionChar.id]?.completed == true
            ),
            JourneyStep(
                id = "step_3",
                character = secondaryChar,
                stepType = JourneyStepType.LEARN_AND_TRACE,
                title = "Trace '${secondaryChar.displayValue}'",
                isCompleted = progressMap[secondaryChar.id]?.completed == true
            ),
            JourneyStep(
                id = "step_4",
                character = primaryChar,
                stepType = JourneyStepType.REWARD_MILESTONE,
                title = "Unlock Today's Star Reward! ⭐",
                isCompleted = progressMap[primaryChar.id]?.completed == true && progressMap[secondaryChar.id]?.completed == true
            )
        )
    }

    /**
     * Returns characters that need practice (retried often or stars < 3) for Parent Area.
     */
    fun getNeedsPracticeList(progressMap: Map<String, CharacterProgress>): List<CharacterContent> {
        return CharacterRepository.allCharacters.filter { char ->
            val prog = progressMap[char.id]
            prog != null && (prog.attempts >= 2 || (prog.completed && prog.stars < 3))
        }.take(5)
    }
}
