package com.accend.app.model

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedAtDay: Int? = null
)

object BadgesProvider {
    fun getBadges(totalExp: Int, level: Int, completedTasksCount: Int, streakDays: Int): List<Badge> {
        return listOf(
            Badge(
                id = "first_step",
                title = "The Awakening",
                description = "Complete your first task on the Ascension journey.",
                iconName = "flag",
                isUnlocked = completedTasksCount >= 1
            ),
            Badge(
                id = "iron_routine",
                title = "Iron Discipline",
                description = "Maintain a 7-day uninterrupted ascension streak.",
                iconName = "fire",
                isUnlocked = streakDays >= 7
            ),
            Badge(
                id = "pillar_equilibrium",
                title = "Pillar Equilibrium",
                description = "Complete all four pillars in a single calendar day.",
                iconName = "balance",
                isUnlocked = completedTasksCount >= 4
            ),
            Badge(
                id = "initiate_tier",
                title = "Ascended Initiate",
                description = "Attain Level 11 and unlock the Initiate title band.",
                iconName = "shield",
                isUnlocked = level >= 11
            ),
            Badge(
                id = "thousand_exp",
                title = "Reservoir of Power",
                description = "Accumulate over 1,000 Total Experience Points.",
                iconName = "bolt",
                isUnlocked = totalExp >= 1000
            ),
            Badge(
                id = "deep_focus",
                title = "The Citadel of Mind",
                description = "Complete 15 mental discipline sessions.",
                iconName = "spa",
                isUnlocked = completedTasksCount >= 15
            ),
            Badge(
                id = "striver_rank",
                title = "Striver Milestone",
                description = "Attain Level 21 through relentless continuous effort.",
                iconName = "military_tech",
                isUnlocked = level >= 21
            ),
            Badge(
                id = "mastery_craft",
                title = "Craftsman's Hands",
                description = "Complete your first weekly Skills Capstone build.",
                iconName = "build",
                isUnlocked = completedTasksCount >= 7
            ),
            Badge(
                id = "social_influence",
                title = "Sovereign Poise",
                description = "Complete 10 social confidence stretch challenges.",
                iconName = "group",
                isUnlocked = completedTasksCount >= 10
            ),
            Badge(
                id = "month_one",
                title = "Cohort of Thirty",
                description = "Maintain focus across 30 days of character progression.",
                iconName = "calendar_month",
                isUnlocked = streakDays >= 30 || level >= 15
            ),
            Badge(
                id = "ironmind_breakthrough",
                title = "Ironmind Sovereign",
                description = "Attain Level 41 and forge an impenetrable will.",
                iconName = "diamond",
                isUnlocked = level >= 41
            ),
            Badge(
                id = "the_ascendant",
                title = "The Apex Cohort",
                description = "Reach Level 61 and conquer the upper echelon.",
                iconName = "star",
                isUnlocked = level >= 61
            )
        )
    }
}
