package com.accend.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "local-user",
    val displayName: String = "",
    val joinDate: Long = System.currentTimeMillis(),
    val currentDay: Int = 1,
    val totalXp: Int = 0,
    val chosenSkillTrack: String = "Coding",
    val onboardingComplete: Boolean = false
)

@Entity(primaryKeys = ["userId", "dayNumber", "pillarId", "subTaskId"])
data class DayProgressEntity(
    val userId: String,
    val dayNumber: Int,
    val pillarId: String,
    val subTaskId: String,
    val completed: Boolean = false,
    val completedAt: Long? = null,
    val notes: String = ""
)

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val id: Int,
    val text: String,
    val author: String,
    val category: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "weekly_xp", primaryKeys = ["userId", "weekNumber"])
data class WeeklyXpEntity(
    val userId: String,
    val weekNumber: Int,
    val xp: Int = 0
)