package com.accend.app

import com.accend.app.domain.Progression
import com.accend.app.domain.Curriculum
import com.accend.app.domain.SkillTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionTest {
    @Test
    fun zeroXpStartsAwakenedAtLevelOne() {
        val status = Progression.levelFor(0)

        assertEquals(1, status.level)
        assertEquals("AWAKENED", status.title)
        assertEquals(0f, status.progress)
    }

    @Test
    fun xpCrossingHundredAdvancesLevel() {
        val status = Progression.levelFor(245)

        assertEquals(3, status.level)
        assertEquals(45, status.xpIntoLevel)
        assertEquals(55, status.xpToNext)
    }

    @Test
    fun tierMappingMovesFromBeginnerToAdvanced() {
        assertEquals(10, Progression.tierForDay(1))
        assertEquals(6, Progression.tierForDay(180))
        assertEquals(1, Progression.tierForDay(365))
    }

    @Test
    fun weeklyTaskXpIncreasesByTwo() {
        assertEquals(10, Progression.taskXpForWeek(1))
        assertEquals(20, Progression.taskXpForWeek(6))
        assertTrue(Progression.taskXpForWeek(52) > Progression.taskXpForWeek(1))
    }

    @Test
    fun curriculumCoversFourPillarsEveryDay() {
        val tasks = Curriculum.tasksForDay(1, SkillTrack.CODING)

        assertEquals(setOf("physical", "mental", "skills", "social"), tasks.map { it.pillarId }.toSet())
        assertEquals(8, tasks.size)
        assertTrue(tasks.all { it.tier == 10 })
    }

    @Test
    fun everySeventhDayAddsBuildAndCapstoneTasks() {
        val tasks = Curriculum.tasksForDay(7, SkillTrack.DESIGN)

        assertTrue(tasks.any { it.id == "skills-build-7" })
        assertTrue(tasks.any { it.id == "social-capstone-7" })
        assertTrue(tasks.any { it.name.contains("Visual foundations") })
    }
}