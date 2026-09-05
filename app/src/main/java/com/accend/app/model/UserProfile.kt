package com.accend.app.model

data class UserProfile(
    val userId: String = "local_user",
    val displayName: String = "Ascendant",
    val avatarId: String = "avatar_gold_1",
    val customAvatarUri: String? = null,
    val chosenSkillTrack: String = "coding",
    val currentDay: Int = 1,
    val totalExp: Int = 0,
    val level: Int = 1,
    val title: String = "AWAKENED",
    val onboardingComplete: Boolean = false,
    val isPaused: Boolean = false,
    val pauseDaysUsed: Int = 0,
    val maxPauseDays: Int = 14,
    val morningReminderEnabled: Boolean = true,
    val morningReminderTime: String = "07:30 AM",
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "08:30 PM",
    val physicalMuted: Boolean = false,
    val mentalMuted: Boolean = false,
    val skillsMuted: Boolean = false,
    val socialMuted: Boolean = false,
    val lastSyncedAt: Long? = null,
    val email: String? = null
)
