package com.accend.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material3.Icon
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
import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.Quote
import com.accend.app.model.TaskItem
import com.accend.app.model.UserProfile
import com.accend.app.ui.components.AccendAvatar
import com.accend.app.ui.components.bouncyClickable
import com.accend.app.ui.components.DailyQuoteCard
import com.accend.app.ui.components.PillarCard
import com.accend.app.ui.components.ProgressionCard
import com.accend.app.ui.theme.AccendSerif
import com.accend.app.ui.theme.GoldBorder
import com.accend.app.ui.theme.GoldHairline
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.ObsidianBg
import com.accend.app.ui.theme.ObsidianCard
import com.accend.app.ui.theme.ObsidianSurface
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextPrimary
import com.accend.app.ui.theme.TextSecondary
import com.accend.app.audio.SoundManager

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    currentDay: Int,
    quote: Quote,
    dayTasks: List<TaskItem>,
    isSyncing: Boolean,
    onOpenPillar: (Pillar) -> Unit,
    onSyncClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onDayChange: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val progress = remember(userProfile.totalExp) {
        ProgressionConfig.calculateProgress(userProfile.totalExp)
    }

    val completedCount = dayTasks.count { it.isCompleted }
    val totalTasksCount = dayTasks.size

    val tasksByPillar = remember(dayTasks) {
        dayTasks.groupBy { it.pillar }
    }

    val isDayRestDay = ((currentDay - 1) % 7) == 6 // 7th day is recovery

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("home_screen")
    ) {
        // Top Bar: Avatar, Name, Program Day Badge, Cloud Sync status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    SoundManager.playClick()
                    onAvatarClick()
                }
            ) {
                AccendAvatar(
                    avatarId = userProfile.avatarId,
                    displayName = userProfile.displayName,
                    customAvatarUri = userProfile.customAvatarUri,
                    size = 46.dp,
                    showRing = true,
                    onClick = onAvatarClick
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = userProfile.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${userProfile.title} • COHORT 365",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Sync or Paused pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (userProfile.isPaused) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4A3816))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PauseCircle,
                                contentDescription = "Paused",
                                tint = GoldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PAUSED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianCard)
                        .border(1.dp, GoldHairline, CircleShape)
                        .bouncyClickable { onSyncClick() }
                        .testTag("home_sync_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                        contentDescription = "Sync",
                        tint = if (isSyncing) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Locked Protocol Day Badge (Non-interactive: reflects real current day)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(12.dp))
                .testTag("protocol_day_badge"),
            color = ObsidianSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAY $currentDay OF 365",
                        fontFamily = AccendSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Week ${((currentDay - 1) / 7) + 1} • Tier ${((currentDay - 1) / 36.5).toInt() + 1}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianCard)
                        .border(1.dp, GoldHairline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isDayRestDay) "RECOVERY DAY" else "ACTIVE PROTOCOL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = if (isDayRestDay) GoldPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: Progression Card
        ProgressionCard(
            progress = progress,
            completedTasksToday = completedCount,
            totalTasksToday = totalTasksCount
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Daily Quote Card
        DailyQuoteCard(quote = quote)

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Four Full-Width Pillar Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DAILY ASCENSION PILLARS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = GoldPrimary
            )
            Text(
                text = "$completedCount/$totalTasksCount Done",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Pillar.entries.forEach { pillar ->
            val tasks = tasksByPillar[pillar] ?: emptyList()
            val pCompleted = tasks.count { it.isCompleted }
            val pTotal = tasks.size
            val pExp = tasks.sumOf { it.expValue }

            PillarCard(
                pillar = pillar,
                completedTasksCount = pCompleted,
                totalTasksCount = pTotal,
                totalExpAvailable = pExp,
                isRestDay = isDayRestDay && pillar == Pillar.PHYSICAL,
                onClick = { onOpenPillar(pillar) },
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
