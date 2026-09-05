package com.accend.app.progression

import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.SkillTrack
import com.accend.app.model.TaskItem

object CurriculumEngine {

    fun getTierForDay(dayNumber: Int): Int {
        val clamped = dayNumber.coerceIn(1, 365)
        return ((clamped - 1) / 36.5).toInt() + 1
    }

    fun getWeekNumber(dayNumber: Int): Int {
        return ((dayNumber - 1).coerceAtLeast(0) / 7) + 1
    }

    fun generateTasksForDay(dayNumber: Int, skillTrackId: String): Map<Pillar, List<TaskItem>> {
        val week = getWeekNumber(dayNumber)
        val tier = getTierForDay(dayNumber)
        val baseTaskExp = ProgressionConfig.calculateTaskExp(week)
        val dayOfWeek = ((dayNumber - 1) % 7) + 1 // 1..7

        val physicalTasks = generatePhysicalTasks(dayNumber, dayOfWeek, tier, baseTaskExp)
        val mentalTasks = generateMentalTasks(dayNumber, dayOfWeek, tier, baseTaskExp)
        val skillsTasks = generateSkillsTasks(dayNumber, dayOfWeek, tier, baseTaskExp, skillTrackId)
        val socialTasks = generateSocialTasks(dayNumber, dayOfWeek, tier, baseTaskExp)

        return mapOf(
            Pillar.PHYSICAL to physicalTasks,
            Pillar.MENTAL to mentalTasks,
            Pillar.SKILLS to skillsTasks,
            Pillar.SOCIAL to socialTasks
        )
    }

    private fun generatePhysicalTasks(dayNumber: Int, dayOfWeek: Int, tier: Int, baseExp: Int): List<TaskItem> {
        val tasks = mutableListOf<TaskItem>()
        val exp = baseExp

        when (dayOfWeek) {
            1, 4 -> {
                // Chest, Biceps, Triceps, Neck, Shoulders, Back
                val pushups = 15 + (tier * 4)
                val pullupsOrRows = 8 + (tier * 2)
                val dipsOrPike = 10 + (tier * 3)

                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_1",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Upper Body Compound: Push-Ups",
                        description = "Strict tempo with chest touching ground, full lockout, engaged core.",
                        target = "4 sets of $pushups reps",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_2",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Upper Body Pull: Rows / Pull-Ups",
                        description = "Explosive pull, 1-second squeeze at the apex, controlled eccentric descent.",
                        target = "4 sets of $pullupsOrRows reps",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_3",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Shoulder & Triceps: Pike Push-Ups / Dips",
                        description = "Elevated hips, elbows tracked back, intense overhead focus.",
                        target = "3 sets of $dipsOrPike reps",
                        expValue = exp
                    )
                )
            }
            2, 5 -> {
                // Abs and Forearms
                val plankSecs = 45 + (tier * 15)
                val legRaises = 12 + (tier * 3)
                val deadHangSecs = 30 + (tier * 10)

                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_1",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Core Tension: Hollow Body Plank",
                        description = "Posterior pelvic tilt, glutes clenched, pressing ground away.",
                        target = "3 rounds of ${plankSecs}s hold",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_2",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Abdominal Power: Hanging Leg Raises",
                        description = "Strict leg lift without swinging momentum, controlled descent.",
                        target = "3 sets of $legRaises reps",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_3",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Grip & Forearms: Heavy Dead Hang",
                        description = "Active shoulder engagement on a pull-up bar, maximum grip crush.",
                        target = "3 sets of ${deadHangSecs}s hang",
                        expValue = exp
                    )
                )
            }
            3, 6 -> {
                // Quads, Hamstrings, Calves, and Glutes
                val squats = 20 + (tier * 5)
                val lunges = 12 + (tier * 3)
                val calfRaises = 25 + (tier * 5)

                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_1",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Lower Body Pillar: Deep Bodyweight Squats",
                        description = "Hip crease below knee, neutral spine, explosive hip drive.",
                        target = "4 sets of $squats reps",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_2",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Unilateral Power: Walking Lunges",
                        description = "Full knee bend, 90-degree angles, stabilizing each stride.",
                        target = "3 sets of $lunges reps per leg",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_3",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "SETS & REPS",
                        title = "Lower Leg Resilience: Standing Calf Raises",
                        description = "Maximum plantar flexion at the top, slow 2-second stretch at bottom.",
                        target = "3 sets of $calfRaises reps",
                        expValue = exp
                    )
                )
            }
            else -> {
                // Day 7: Rest & Mobility
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_1",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "MOBILITY",
                        title = "Full Body Hip & Thoracic Mobility",
                        description = "Deep hip openers, 90/90 mobility, thoracic rotations, and child's pose.",
                        target = "20 minutes focused flow",
                        expValue = exp
                    )
                )
                tasks.add(
                    TaskItem(
                        id = "phys_d${dayNumber}_2",
                        pillar = Pillar.PHYSICAL,
                        dayNumber = dayNumber,
                        subType = "RECOVERY",
                        title = "Low-Intensity Aerobic Flush: Brisk Walk",
                        description = "Zone 1 heart rate outdoor walk in nature or sunlight for recovery.",
                        target = "30 minutes continuous walk",
                        expValue = exp
                    )
                )
            }
        }
        return tasks
    }

    private fun generateMentalTasks(dayNumber: Int, dayOfWeek: Int, tier: Int, baseExp: Int): List<TaskItem> {
        val exp = baseExp
        val medMinutes = 10 + (tier * 2)
        val deepWorkMinutes = 45 + (tier * 5)

        val reflections = listOf(
            "What gave you the most resistance today, and how did you conquer it?",
            "Identify one unconscious habit that drained your energy this week.",
            "Write three principles you refuse to compromise on under pressure.",
            "Audit your relationship with attention: Where did your focus wander?",
            "Reflect on a past failure and re-frame the permanent lesson gained."
        )
        val reflectionPrompt = reflections[(dayNumber - 1) % reflections.size]

        val challenges = listOf(
            "3-minute cold shower finish upon waking.",
            "Zero refined sugar or processed snacks for 24 hours.",
            "Complete digital fast for the first 60 minutes after waking.",
            "No phone usage during any meal today.",
            "Eliminate all complaint or criticism from your speech today.",
            "Wake up 45 minutes earlier than your baseline schedule.",
            "Read 15 pages of philosophy or non-fiction in silence."
        )
        val disciplinePrompt = challenges[(dayNumber - 1) % challenges.size]

        return listOf(
            TaskItem(
                id = "mental_d${dayNumber}_1",
                pillar = Pillar.MENTAL,
                dayNumber = dayNumber,
                subType = "MEDITATION",
                title = "Vipassana Mindfulness & Breath Anchor",
                description = "Sit upright with unmoving posture. Observe the breath without interference.",
                target = "$medMinutes minutes unguided stillness",
                expValue = exp
            ),
            TaskItem(
                id = "mental_d${dayNumber}_2",
                pillar = Pillar.MENTAL,
                dayNumber = dayNumber,
                subType = "REFLECTION",
                title = "Stoic Daily Journal & Audit",
                description = reflectionPrompt,
                target = "Written reflection in notes",
                expValue = exp
            ),
            TaskItem(
                id = "mental_d${dayNumber}_3",
                pillar = Pillar.MENTAL,
                dayNumber = dayNumber,
                subType = "DEEP WORK",
                title = "Monastic Focus Block",
                description = "Single-task focus on your highest-leverage intellectual endeavor. Zero tabs, zero notifications.",
                target = "$deepWorkMinutes minutes deep work",
                expValue = exp
            ),
            TaskItem(
                id = "mental_d${dayNumber}_4",
                pillar = Pillar.MENTAL,
                dayNumber = dayNumber,
                subType = "DISCIPLINE",
                title = "Ascetic Willpower Challenge",
                description = disciplinePrompt,
                target = "Completed without compromise",
                expValue = exp
            )
        )
    }

    private fun generateSkillsTasks(
        dayNumber: Int,
        dayOfWeek: Int,
        tier: Int,
        baseExp: Int,
        trackId: String
    ): List<TaskItem> {
        val exp = baseExp
        val isBuildDay = (dayOfWeek == 7) // Every seventh day replaces practice with a larger applied challenge

        val track = SkillTrack.findById(trackId)
        val tasks = mutableListOf<TaskItem>()

        val learnTopics = when (track.id) {
            "coding" -> listOf(
                "Memory layout, CPU cache lines, and stack vs. heap allocation",
                "Asynchronous concurrency models: Goroutines, coroutines, and event loops",
                "Relational schema indexing: B-trees vs. LSM trees under high write load",
                "Distributed consensus primitives: Paxos, Raft, and leader election",
                "Clean Architecture and unidirectional state flow in mobile engineering",
                "API contract design: Idempotency keys, rate limit headers, and backpressure",
                "Profilers and memory leak identification using heap dumps"
            )
            "public_speaking" -> listOf(
                "Diaphragmatic resonance and chest voice projection",
                "The Power of the Strategic Pause: Creating dramatic tension",
                "The Aristotelian Triad: Ethos, Pathos, and Logos in speech structure",
                "Body language anchoring and eliminating pacifying micro-gestures",
                "Vocal variety: Pacing, pitch modulation, and dynamic cadence",
                "Handling hostile Q&A questions with grace and authority",
                "Story arc structure: Inciting incident, turning point, and moral call"
            )
            "design" -> listOf(
                "Typography scales, baseline grid alignment, and optical hierarchy",
                "Color psychology and contrast ratios for accessibility (WCAG 2.2)",
                "Spatial composition: Negative space as a premier luxury design tool",
                "Micro-interactions: Easing curves, spring physics, and tactile response",
                "Design systems architecture: Design tokens from primitives to components",
                "Gestalt principles: Proximity, similarity, continuation, and closure",
                "Information density vs. cognitive friction on mobile surfaces"
            )
            "finance" -> listOf(
                "Asset allocation models: Equity risk premium vs. sovereign yields",
                "Tax efficiency: Sheltered accounts, capital gains strategies, and harvesting",
                "Analyzing balance sheets: Working capital, debt covenants, and free cash flow",
                "Compound interest economics and early-stage capital velocity",
                "Risk parity and asymmetric upside-to-downside risk profiles",
                "Behavioral economics: Overcoming loss aversion in market downturns",
                "Real estate syndications and depreciation accounting"
            )
            else -> listOf( // sales
                "The SPIN Selling Framework: Situation, Problem, Implication, Need-Payoff",
                "Uncovering hidden objections through empathetic discovery questioning",
                "Framing price vs. ROI value: Anchoring perceived value",
                "Mirroring, labeling, and Chris Voss' tactical empathy techniques",
                "Structuring multi-stakeholder enterprise deals and champion building",
                "The Assumptive Close and closing confidence without pressure",
                "Post-sale client retention: Turning buyers into vocal advocates"
            )
        }

        val learnTopic = learnTopics[(dayNumber - 1) % learnTopics.size]

        tasks.add(
            TaskItem(
                id = "skills_d${dayNumber}_learn",
                pillar = Pillar.SKILLS,
                dayNumber = dayNumber,
                subType = "LEARN",
                title = "${track.name}: Concept Mastery",
                description = "Study: $learnTopic. Take concise bulleted notes.",
                target = "25 minutes structured study",
                expValue = exp
            )
        )

        if (isBuildDay) {
            tasks.add(
                TaskItem(
                    id = "skills_d${dayNumber}_build",
                    pillar = Pillar.SKILLS,
                    dayNumber = dayNumber,
                    subType = "BUILD",
                    title = "Weekly ${track.name} Capstone Build",
                    description = "Apply everything learned over the last 6 days into a tangible, shareable, production-ready deliverable.",
                    target = "60 minutes applied creation",
                    expValue = exp * 2
                )
            )
        } else {
            tasks.add(
                TaskItem(
                    id = "skills_d${dayNumber}_practice",
                    pillar = Pillar.SKILLS,
                    dayNumber = dayNumber,
                    subType = "PRACTICE",
                    title = "Deliberate Hands-on Drills",
                    description = "Execute practical reps on today's theory without shortcuts.",
                    target = "30 minutes active drill",
                    expValue = exp
                )
            )
        }

        return tasks
    }

    private fun generateSocialTasks(dayNumber: Int, dayOfWeek: Int, tier: Int, baseExp: Int): List<TaskItem> {
        val exp = baseExp
        val isCapstoneDay = (dayOfWeek == 7)

        val microInteractions = listOf(
            "Give a genuine, specific compliment to a barista, cashier, or colleague.",
            "Hold eye contact with everyone you speak to until they finish their sentence.",
            "Reach out to an old friend or mentor with a warm note of gratitude.",
            "Initiate a casual conversation with a complete stranger in a queue.",
            "Call a family member and listen intently without interrupting for 10 minutes.",
            "Introduce two acquaintances who could mutually benefit from knowing each other.",
            "Smile genuinely and greet three people you pass during your commute."
        )

        val confidenceChallenges = listOf(
            "Express a dissenting opinion politely and firmly in a group discussion.",
            "Ask for a 10% discount at a local coffee shop or store with calm confidence.",
            "Practice comfortable silence: Resist the urge to fill conversational pauses.",
            "Approach someone whose work you admire and introduce yourself succinctly.",
            "Order food or drink with direct posture, vocal projection, and zero hesitation.",
            "Set a healthy interpersonal boundary firmly without over-explaining yourself.",
            "Share a personal story of vulnerability or failure with a trusted peer."
        )

        val micro = microInteractions[(dayNumber - 1) % microInteractions.size]
        val challenge = confidenceChallenges[(dayNumber - 1) % confidenceChallenges.size]

        val tasks = mutableListOf<TaskItem>()

        tasks.add(
            TaskItem(
                id = "social_d${dayNumber}_micro",
                pillar = Pillar.SOCIAL,
                dayNumber = dayNumber,
                subType = "MICRO-INTERACTION",
                title = "Low-Friction Social Warmup",
                description = micro,
                target = "Execute 1 mindful interaction",
                expValue = exp
            )
        )

        if (isCapstoneDay) {
            tasks.add(
                TaskItem(
                    id = "social_d${dayNumber}_capstone",
                    pillar = Pillar.SOCIAL,
                    dayNumber = dayNumber,
                    subType = "CAPSTONE",
                    title = "Weekly Social Capstone Challenge",
                    description = "Host, speak, or take lead in an interpersonal setting (organize a dinner, speak in a meeting, or attend a networking event).",
                    target = "Completed leadership moment",
                    expValue = exp * 2
                )
            )
        } else {
            tasks.add(
                TaskItem(
                    id = "social_d${dayNumber}_confidence",
                    pillar = Pillar.SOCIAL,
                    dayNumber = dayNumber,
                    subType = "CONFIDENCE",
                    title = "Confidence Stretch Trial",
                    description = challenge,
                    target = "Completed with poise",
                    expValue = exp
                )
            )
        }

        return tasks
    }
}
