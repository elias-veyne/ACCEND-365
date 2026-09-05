package com.accend.app

import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.progression.CurriculumEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionTest {

    @Test
    fun testLevelProgressionMath() {
        val lvl1Req = ProgressionConfig.requiredExpForLevel(1)
        assertTrue(lvl1Req > 0)

        val progress0 = ProgressionConfig.calculateProgress(0)
        assertEquals(1, progress0.currentLevel)
        assertEquals("AWAKENED", progress0.title)

        // Test level progression
        val progress40Xp = ProgressionConfig.calculateProgress(40)
        assertTrue(progress40Xp.currentLevel >= 4)
    }

    @Test
    fun testCurriculumGenerationAcross365Days() {
        // Test first day, mid day, and final day 365
        val day1Tasks = CurriculumEngine.generateTasksForDay(1, "coding")
        assertEquals(4, day1Tasks.keys.size)
        assertTrue(day1Tasks[Pillar.PHYSICAL]!!.isNotEmpty())
        assertTrue(day1Tasks[Pillar.MENTAL]!!.isNotEmpty())
        assertTrue(day1Tasks[Pillar.SKILLS]!!.isNotEmpty())
        assertTrue(day1Tasks[Pillar.SOCIAL]!!.isNotEmpty())

        val day180Tasks = CurriculumEngine.generateTasksForDay(180, "public_speaking")
        assertEquals(4, day180Tasks.keys.size)
        assertTrue(day180Tasks[Pillar.SKILLS]!!.any { it.title.contains("Speech") || it.title.contains("Speaking") || it.title.contains("Delivery") || it.title.contains("Storytelling") || it.title.contains("Debate") })

        val day365Tasks = CurriculumEngine.generateTasksForDay(365, "coding")
        assertEquals(4, day365Tasks.keys.size)
        val physicalDay365 = day365Tasks[Pillar.PHYSICAL]!!
        assertNotNull(physicalDay365)
    }

    @Test
    fun testTitlesBands() {
        assertEquals("AWAKENED", ProgressionConfig.getTitleForLevel(1))
        assertEquals("INITIATE", ProgressionConfig.getTitleForLevel(15))
        assertEquals("ASCENDANT", ProgressionConfig.getTitleForLevel(85))
        assertEquals("BEYOND", ProgressionConfig.getTitleForLevel(100))
    }
}
