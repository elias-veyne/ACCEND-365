package com.accend.app.model

data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val pillarContext: String
)

object QuotesProvider {
    private val QUOTES = listOf(
        Quote("q1", "We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle", "Pillar of Discipline"),
        Quote("q2", "No man is free who is not master of himself.", "Epictetus", "Mental Mastery"),
        Quote("q3", "Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius", "Character Ascension"),
        Quote("q4", "Today is victory over yourself of yesterday; tomorrow is your victory over lesser men.", "Miyamoto Musashi", "Physical & Martial Grit"),
        Quote("q5", "He who has a why to live can bear almost any how.", "Friedrich Nietzsche", "Purpose & Focus"),
        Quote("q6", "First say to yourself what you would be; and then do what you have to do.", "Epictetus", "Identity & Action"),
        Quote("q7", "It is not the mountain we conquer, but ourselves.", "Sir Edmund Hillary", "Physical Endurance"),
        Quote("q8", "The impediment to action advances action. What stands in the way becomes the way.", "Marcus Aurelius", "Mental Fortitude"),
        Quote("q9", "To know oneself is true insight. To conquer oneself is true strength.", "Lao Tzu", "Inner Sovereignty"),
        Quote("q10", "Diligence is the mother of good luck.", "Benjamin Franklin", "Skills Mastery"),
        Quote("q11", "He who knows when he can fight and when he cannot will be victorious.", "Sun Tzu", "Strategic Discernment"),
        Quote("q12", "Seek not that the things which happen should happen as you wish; but wish the things which happen to be as they are.", "Epictetus", "Equanimity"),
        Quote("q13", "The soul becomes dyed with the color of its thoughts.", "Marcus Aurelius", "Mental Clarity"),
        Quote("q14", "Difficulty shows what men are.", "Epictetus", "Resilience"),
        Quote("q15", "Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.", "John C. Maxwell", "Ascension Path")
    )

    fun getQuoteForDay(dayNumber: Int): Quote {
        val index = (dayNumber - 1).coerceAtLeast(0) % QUOTES.size
        return QUOTES[index]
    }
}
