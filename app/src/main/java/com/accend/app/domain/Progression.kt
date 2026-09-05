package com.accend.app.domain

data class LevelStatus(val level: Int, val title: String, val progress: Float, val xpIntoLevel: Int, val xpToNext: Int)

object Progression {
    private val titles = listOf("AWAKENED", "INITIATE", "DISCIPLINED", "BUILDER", "FORGED", "ASCENDING", "MASTER", "TRANSCENDENT", "BEYOND", "LIMITLESS")

    fun levelFor(xp: Int): LevelStatus {
        val safeXp = xp.coerceAtLeast(0)
        val level = (safeXp / 100).coerceIn(0, 99) + 1
        val xpIntoLevel = safeXp % 100
        return LevelStatus(level, titles[(level - 1) / 10], xpIntoLevel / 100f, xpIntoLevel, 100 - xpIntoLevel)
    }

    fun tierForDay(day: Int): Int = (((360 - day.coerceIn(1, 365)) / 36) + 1).coerceIn(1, 10)

    fun taskXpForWeek(week: Int): Int = 10 + (week.coerceAtLeast(1) - 1) * 2
}