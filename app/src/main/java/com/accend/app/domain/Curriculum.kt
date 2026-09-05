package com.accend.app.domain

data class CurriculumTask(
    val id: String,
    val pillarId: String,
    val name: String,
    val description: String,
    val xp: Int,
    val tier: Int
)

enum class SkillTrack(val label: String, private val stages: List<String>) {
    CODING("Coding", listOf("Fundamentals", "Web foundations", "Python", "Databases", "APIs", "Full-stack projects")),
    PUBLIC_SPEAKING("Public Speaking", listOf("Presence", "Storytelling", "Persuasion", "Leadership communication")),
    WRITING("Writing", listOf("Journaling", "Blogging", "Fiction", "Copywriting", "Published work")),
    DESIGN("Design", listOf("Visual foundations", "Typography", "UI/UX", "Branding", "Portfolio")),
    PERSONAL_FINANCE("Personal Finance", listOf("Budgeting", "Saving", "Investing", "Income streams", "Wealth building")),
    SALES("Sales", listOf("Mindset", "Prospecting", "Pitching", "Negotiation", "Closing", "Scaling")),
    DATA_SCIENCE("Data Science", listOf("Statistics", "Python and Pandas", "Visualization", "ML basics", "Portfolio")),
    PHOTOGRAPHY_VIDEO("Photography / Video", listOf("Composition", "Lighting", "Editing", "Storytelling", "Portfolio"));

    fun stageForTier(tier: Int): String = stages[(10 - tier).coerceIn(0, stages.lastIndex)]
}

object Curriculum {
    fun tasksForDay(day: Int, skillTrack: SkillTrack = SkillTrack.CODING): List<CurriculumTask> {
        val safeDay = day.coerceIn(1, 365)
        val tier = Progression.tierForDay(safeDay)
        val week = ((safeDay - 1) / 7) + 1
        val taskXp = Progression.taskXpForWeek(week)
        return listOf(
            CurriculumTask("physical-mobility-$safeDay", "physical", "Mobility primer", "Prepare your body with controlled movement.", taskXp, tier),
            CurriculumTask("physical-strength-$safeDay", "physical", "Strength block", "Complete the day's focused movement practice.", taskXp, tier),
            CurriculumTask("mental-meditation-$safeDay", "mental", "Meditation", "Return attention to the breath for ${3 + (10 - tier) * 2} minutes.", taskXp, tier),
            CurriculumTask("mental-reflection-$safeDay", "mental", "Reflection", "Write a clear reflection on today's choices.", taskXp, tier),
            CurriculumTask("skills-learn-$safeDay", "skills", "Learn: ${skillTrack.stageForTier(tier)}", "Take a focused lesson for 10 to 15 minutes.", taskXp, tier),
            CurriculumTask("skills-practice-$safeDay", "skills", "Practice", "Turn the lesson into one concrete repetition.", taskXp, tier),
            CurriculumTask("social-micro-$safeDay", "social", "Micro-interaction", "Make eye contact, smile, and greet one person.", taskXp, tier),
            CurriculumTask("social-confidence-$safeDay", "social", "Confidence challenge", "Ask one open question and stay curious.", taskXp, tier)
        ) + if (safeDay % 7 == 0) listOf(
            CurriculumTask("skills-build-$safeDay", "skills", "Build day", "Ship a small artifact from this week's practice.", taskXp * 2, tier),
            CurriculumTask("social-capstone-$safeDay", "social", "Social capstone", "Create a meaningful moment of connection.", taskXp * 2, tier)
        ) else emptyList()
    }
}