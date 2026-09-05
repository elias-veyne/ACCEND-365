package com.accend.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accend.app.data.TaskProgressEntity
import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.UserProfile
import com.accend.app.ui.components.HeatmapView
import com.accend.app.ui.theme.AccendSerif
import com.accend.app.ui.theme.GoldDark
import com.accend.app.ui.theme.GoldHairline
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.ObsidianBg
import com.accend.app.ui.theme.ObsidianCard
import com.accend.app.ui.theme.ObsidianSurface
import com.accend.app.ui.theme.SuccessGreen
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextPrimary
import com.accend.app.ui.theme.TextSecondary

@Composable
fun AnalysisScreen(
    userProfile: UserProfile,
    currentDay: Int,
    completedTasks: List<TaskProgressEntity>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Per-pillar stats
    val pillarStats = remember(completedTasks, currentDay) {
        val daysElapsed = currentDay.coerceAtLeast(1)
        Pillar.entries.map { pillar ->
            val completedForPillar = completedTasks.count { it.pillarId == pillar.id }
            // Each day has roughly 3-4 tasks per pillar
            val estimatedTotalExpected = when (pillar) {
                Pillar.PHYSICAL -> daysElapsed * 3
                Pillar.MENTAL -> daysElapsed * 4
                Pillar.SKILLS -> daysElapsed * 2
                Pillar.SOCIAL -> daysElapsed * 2
            }
            val rate = if (estimatedTotalExpected > 0) {
                (completedForPillar.toFloat() / estimatedTotalExpected.toFloat()).coerceIn(0f, 1f)
            } else 0f
            Triple(pillar, completedForPillar, rate)
        }
    }

    // Weakest-link calculation
    val weakestLink = remember(pillarStats) {
        pillarStats.minByOrNull { it.third }
    }

    // Weekly EXP trend
    val weeklyXp = remember(completedTasks) {
        val map = mutableMapOf<Int, Int>()
        completedTasks.forEach { task ->
            val week = ((task.dayNumber - 1) / 7) + 1
            map[week] = (map[week] ?: 0) + task.xpAwarded
        }
        (1..4).map { w -> Pair(w, map[w] ?: (w * 90)) } // Show last 4 weeks trend
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("analysis_screen")
    ) {
        // Screen Header
        Text(
            text = "SOVEREIGN ANALYSIS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = GoldPrimary
        )
        Text(
            text = "Character Progression & Trajectory",
            fontFamily = AccendSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // High-level summary card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
            color = ObsidianCard
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "DAY", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$currentDay",
                        fontFamily = AccendSerif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(text = "of 365", fontSize = 10.sp, color = TextSecondary)
                }

                Box(modifier = Modifier.height(40.dp).width(1.dp).background(GoldHairline))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "LEVEL", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${userProfile.level}",
                        fontFamily = AccendSerif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(text = userProfile.title, fontSize = 10.sp, color = TextSecondary)
                }

                Box(modifier = Modifier.height(40.dp).width(1.dp).background(GoldHairline))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "TOTAL EXP", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${userProfile.totalExp}",
                        fontFamily = AccendSerif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(text = "earned", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 365-Day Heatmap
        HeatmapView(
            currentDay = currentDay,
            completedTasks = completedTasks
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Weakest-Link Insight
        weakestLink?.let { (pillar, count, rate) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFD48806).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("weakest_link_insight"),
                color = ObsidianSurface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Insight",
                            tint = Color(0xFFFFC53D),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WEAKEST-LINK INSIGHT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFFFC53D)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your ${pillar.title} pillar exhibits your lowest relative completion consistency (${(rate * 100).toInt()}%).",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "A chain breaks at its weakest link. Prioritize ${pillar.title} tasks before noon tomorrow to restore ascension equilibrium.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Per-Pillar Completion Breakdown
        Text(
            text = "PER-PILLAR BALANCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = GoldPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        pillarStats.forEach { (pillar, completed, rate) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, GoldHairline, RoundedCornerShape(12.dp)),
                color = ObsidianCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(pillar.themeColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pillar.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = "$completed completed (${(rate * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { rate },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = pillar.themeColor,
                        trackColor = ObsidianSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly EXP Trend
        Text(
            text = "WEEKLY EXP TREND",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = GoldPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyXp.forEach { (week, xp) ->
                    val maxPossibleXp = 600
                    val barHeight = ((xp.toFloat() / maxPossibleXp.toFloat()) * 80).coerceIn(12f, 80f).dp

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$xp",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "W$week",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title Path Roadmap
        Text(
            text = "TITLE PATH MILESTONES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = GoldPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProgressionConfig.TITLE_BANDS.forEach { band ->
            val isCurrent = userProfile.level in band.minLevel..band.maxLevel
            val isUnlocked = userProfile.level >= band.minLevel

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (isCurrent) GoldPrimary else if (isUnlocked) GoldHairline else ObsidianSurface,
                        RoundedCornerShape(12.dp)
                    ),
                color = if (isCurrent) ObsidianSurface else ObsidianCard
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) GoldPrimary
                                else if (isUnlocked) SuccessGreen.copy(alpha = 0.2f)
                                else ObsidianSurface
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isCurrent -> Icons.Default.AutoAwesome
                                isUnlocked -> Icons.Default.CheckCircle
                                else -> Icons.Default.Lock
                            },
                            contentDescription = band.title,
                            tint = if (isCurrent) ObsidianBg else if (isUnlocked) SuccessGreen else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = band.title,
                                fontFamily = AccendSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isCurrent) GoldLight else if (isUnlocked) TextPrimary else TextMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LVL ${band.minLevel}–${band.maxLevel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) GoldPrimary else TextMuted
                            )
                        }
                        Text(
                            text = band.description,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
