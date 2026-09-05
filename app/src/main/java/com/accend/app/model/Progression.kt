package com.accend.app.model

import kotlin.math.pow
import kotlin.math.roundToInt

data class TitleBand(
    val title: String,
    val minLevel: Int,
    val maxLevel: Int,
    val description: String
)

object ProgressionConfig {
    val TITLE_BANDS = listOf(
        TitleBand("AWAKENED", 1, 10, "The first spark of self-awareness and conscious purpose."),
        TitleBand("INITIATE", 11, 20, "Early consistency begins to forge daily rituals."),
        TitleBand("STRIVER", 21, 30, "Relentless effort under physical and mental resistance."),
        TitleBand("DISCIPLINED", 31, 40, "Action independent of fleeting motivation or comfort."),
        TitleBand("IRONMIND", 41, 50, "Unshakeable composure, focus, and resilience."),
        TitleBand("VANGUARD", 51, 60, "Operating ahead of convention with decisive mastery."),
        TitleBand("APEX", 61, 70, "Exceptional execution across all four pillars simultaneously."),
        TitleBand("ELITE", 71, 80, "Rare command of craft, body, mind, and interpersonal poise."),
        TitleBand("ASCENDANT", 81, 90, "Transcendence of limits into enduring personal sovereignty."),
        TitleBand("BEYOND", 91, 100, "The ultimate realization of 365 days of relentless growth.")
    )

    /**
     * Blueprint: Task EXP for week w = 10 + 2 * (w - 1)
     */
    fun calculateTaskExp(weekNumber: Int): Int {
        val w = weekNumber.coerceAtLeast(1)
        return 10 + 2 * (w - 1)
    }

    /**
     * Blueprint: Required EXP from level L to level L + 1 = round(2.2 * L^1.5)
     */
    fun requiredExpForLevel(level: Int): Int {
        val l = level.coerceAtLeast(1)
        return (2.2 * l.toDouble().pow(1.5)).roundToInt().coerceAtLeast(1)
    }

    fun getTitleForLevel(level: Int): String {
        val clampedLevel = level.coerceIn(1, 100)
        return TITLE_BANDS.firstOrNull { clampedLevel in it.minLevel..it.maxLevel }?.title ?: "BEYOND"
    }

    data class LevelProgress(
        val currentLevel: Int,
        val title: String,
        val totalExp: Int,
        val currentLevelExp: Int,
        val requiredExpForNextLevel: Int,
        val progressFraction: Float
    )

    fun calculateProgress(totalExp: Int): LevelProgress {
        var remainingXp = totalExp.coerceAtLeast(0)
        var level = 1

        while (level < 100) {
            val req = requiredExpForLevel(level)
            if (remainingXp >= req) {
                remainingXp -= req
                level++
            } else {
                break
            }
        }

        val neededNext = if (level >= 100) 1 else requiredExpForLevel(level)
        val progress = if (level >= 100) 1.0f else (remainingXp.toFloat() / neededNext.toFloat()).coerceIn(0f, 1f)

        return LevelProgress(
            currentLevel = level,
            title = getTitleForLevel(level),
            totalExp = totalExp,
            currentLevelExp = remainingXp,
            requiredExpForNextLevel = neededNext,
            progressFraction = progress
        )
    }
}
