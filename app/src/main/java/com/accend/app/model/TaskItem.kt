package com.accend.app.model

data class TaskItem(
    val id: String,
    val pillar: Pillar,
    val dayNumber: Int,
    val subType: String, // e.g. "SETS & REPS", "MEDITATION", "REFLECTION", "DEEP WORK", "DISCIPLINE", "LEARN", "PRACTICE", "BUILD", "MICRO-INTERACTION", "CONFIDENCE"
    val title: String,
    val description: String,
    val target: String,
    val expValue: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val notes: String? = null
)
