package com.accend.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_progress")
data class TaskProgressEntity(
    @PrimaryKey val id: String, // e.g. "task_d42_phys_1"
    val dayNumber: Int,
    val taskId: String,
    val pillarId: String,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val notes: String?,
    val xpAwarded: Int
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "local_user",
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
    val email: String? = null,
    // v2.0 — Feature additions
    val streakFreezeTokens: Int = 3,
    val restDaysUsedThisWeek: Int = 0,
    val weeklyReflection: String = "",
    val dailyGoalsPillarIds: String = "",
    val isDarkMode: Boolean = true,
    val privacyLevel: String = "friends_only",
    val totalDaysCompleted: Int = 0,
    val totalTasksCompleted: Int = 0,
    val bestStreak: Int = 0,
    val physicalCompleted: Int = 0,
    val mentalCompleted: Int = 0,
    val skillsCompleted: Int = 0,
    val socialCompleted: Int = 0
)
