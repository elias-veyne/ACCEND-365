package com.accend.app.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.accend.app.R
import com.accend.app.ui.theme.MentalCyan
import com.accend.app.ui.theme.PhysicalOrange
import com.accend.app.ui.theme.SkillsViolet
import com.accend.app.ui.theme.SocialEmerald

enum class Pillar(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val themeColor: Color,
    @DrawableRes val imageRes: Int
) {
    PHYSICAL(
        id = "physical",
        title = "Physical",
        subtitle = "Strength & Movement",
        description = "Progressive physical training, muscular endurance, and recovery.",
        themeColor = PhysicalOrange,
        imageRes = R.drawable.img_pillar_physical
    ),
    MENTAL(
        id = "mental",
        title = "Mental",
        subtitle = "Focus & Discipline",
        description = "Meditation, deep work, daily journaling, and willpower challenges.",
        themeColor = MentalCyan,
        imageRes = R.drawable.img_pillar_mental
    ),
    SKILLS(
        id = "skills",
        title = "Skills",
        subtitle = "Deliberate Mastery",
        description = "Daily learning, deliberate practice, and weekly capstone builds.",
        themeColor = SkillsViolet,
        imageRes = R.drawable.img_pillar_skills
    ),
    SOCIAL(
        id = "social",
        title = "Social",
        subtitle = "Confidence & Impact",
        description = "Micro-interactions, real-world confidence trials, and social capstones.",
        themeColor = SocialEmerald,
        imageRes = R.drawable.img_pillar_social
    );

    companion object {
        fun fromId(id: String): Pillar = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: PHYSICAL
    }
}
