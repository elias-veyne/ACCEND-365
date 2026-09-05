package com.accend.app.model

data class SkillTrack(
    val id: String,
    val name: String,
    val description: String,
    val tag: String
) {
    companion object {
        val ALL = listOf(
            SkillTrack(
                id = "coding",
                name = "Systems & Code",
                description = "Algorithmic thinking, software architecture, Kotlin, and engineering execution.",
                tag = "Engineering"
            ),
            SkillTrack(
                id = "public_speaking",
                name = "Public Speaking",
                description = "Vocal presence, rhetoric, persuasive storytelling, and stage command.",
                tag = "Communication"
            ),
            SkillTrack(
                id = "design",
                name = "Visual Design",
                description = "Spatial aesthetics, typography, interaction design, and visual polish.",
                tag = "Creative"
            ),
            SkillTrack(
                id = "finance",
                name = "Personal Finance",
                description = "Capital allocation, portfolio principles, cash flow, and economic literacy.",
                tag = "Wealth"
            ),
            SkillTrack(
                id = "sales",
                name = "Sales & Influence",
                description = "Discovery conversations, high-stakes negotiation, objection handling, and closing.",
                tag = "Commercial"
            )
        )

        fun findById(id: String): SkillTrack = ALL.firstOrNull { it.id == id } ?: ALL[0]
    }
}
