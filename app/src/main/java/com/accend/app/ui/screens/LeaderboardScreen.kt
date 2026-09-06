package com.accend.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.accend.app.model.LeaderboardUser
import com.accend.app.ui.components.AccendAvatar
import com.accend.app.ui.components.bouncyClickable
import com.accend.app.ui.components.pressScale
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
import com.accend.app.viewmodel.LeaderboardFilterTab

@Composable
fun LeaderboardScreen(
    leaderboardUsers: List<LeaderboardUser>,
    selectedTab: LeaderboardFilterTab,
    friendsOnly: Boolean,
    isSyncing: Boolean = false,
    onTabSelected: (LeaderboardFilterTab) -> Unit,
    onToggleFriendsOnly: () -> Unit,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUserItem = leaderboardUsers.firstOrNull { it.isCurrentUser }
    val topUsers = leaderboardUsers.take(20)
    val hasOtherRealUsers = leaderboardUsers.any { !it.isCurrentUser }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("leaderboard_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE CLOUD COHORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = GoldPrimary
                    )
                }
                Text(
                    text = "Sovereign Standings",
                    fontFamily = AccendSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            }

            // Controls: Refresh & Friends toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cloud refresh button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCard)
                        .border(1.dp, GoldHairline, RoundedCornerShape(10.dp))
                        .bouncyClickable(enabled = !isSyncing) { onRefresh() }
                        .padding(8.dp)
                        .testTag("leaderboard_refresh_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = GoldPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Standings",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Friends filter toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (friendsOnly) GoldPrimary.copy(alpha = 0.2f) else ObsidianCard)
                        .border(
                            1.dp,
                            if (friendsOnly) GoldPrimary else GoldHairline,
                            RoundedCornerShape(10.dp)
                        )
                        .bouncyClickable { onToggleFriendsOnly() }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("friends_filter_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Friends",
                            tint = if (friendsOnly) GoldPrimary else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (friendsOnly) "Friends" else "All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (friendsOnly) GoldPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: All-Time vs Weekly
        TabRow(
            selectedTabIndex = if (selectedTab == LeaderboardFilterTab.ALL_TIME) 0 else 1,
            containerColor = ObsidianCard,
            contentColor = GoldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (selectedTab == LeaderboardFilterTab.ALL_TIME) 0 else 1]
                    ),
                    color = GoldPrimary
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedTab == LeaderboardFilterTab.ALL_TIME,
                onClick = {
                    onTabSelected(LeaderboardFilterTab.ALL_TIME)
                },
                text = {
                    Text(
                        text = "ALL-TIME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == LeaderboardFilterTab.WEEKLY,
                onClick = {
                    onTabSelected(LeaderboardFilterTab.WEEKLY)
                },
                text = {
                    Text(
                        text = "WEEKLY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pinned Current User Row if present
        currentUserItem?.let { current ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(14.dp))
                    .testTag("current_user_leaderboard_row"),
                color = ObsidianSurface
            ) {
                LeaderboardRowContent(
                    user = current,
                    isPinned = true
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VERIFIED REAL ASCENDANTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
            )

            Text(
                text = if (hasOtherRealUsers) "${topUsers.size} active" else "1 sovereign registered",
                fontSize = 11.sp,
                color = GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!hasOtherRealUsers) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
                color = ObsidianCard
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.12f))
                            .border(1.5.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Sovereign Rank 1",
                            tint = GoldPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Vanguard of the Cohort",
                        fontFamily = AccendSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    val syncInteractionSource = remember { MutableInteractionSource() }
                    Button(
                        onClick = {
                            onRefresh()
                        },
                        interactionSource = syncInteractionSource,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color(0xFF0A0B0E)
                        ),
                        modifier = Modifier
                            .pressScale(syncInteractionSource)
                            .testTag("sync_cloud_standings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF0A0B0E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSyncing) "SYNCING CLOUD..." else "SYNC & REFRESH STANDINGS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = Color(0xFF0A0B0E)
                        )
                    }
                }
            }
        } else {
            // Top 20 Users List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(topUsers, key = { _, user -> user.userId }) { index, user ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { height -> height / 2 },
                            animationSpec = tween(
                                durationMillis = 360,
                                delayMillis = (index.coerceAtMost(10)) * 40,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 360,
                                delayMillis = (index.coerceAtMost(10)) * 40,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        label = "leaderboard_row_enter"
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    if (user.isCurrentUser) GoldPrimary else GoldHairline,
                                    RoundedCornerShape(12.dp)
                                ),
                            color = if (user.isCurrentUser) ObsidianSurface else ObsidianCard
                        ) {
                            LeaderboardRowContent(
                                user = user,
                                isPinned = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRowContent(
    user: LeaderboardUser,
    isPinned: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Rank Number or Medal
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                when (user.rank) {
                    1 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Rank 1",
                        tint = GoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                    2 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Rank 2",
                        tint = Color(0xFFC0C0C0),
                        modifier = Modifier.size(20.dp)
                    )
                    3 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Rank 3",
                        tint = Color(0xFFCD7F32),
                        modifier = Modifier.size(18.dp)
                    )
                    else -> Text(
                        text = "#${user.rank}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (user.isCurrentUser) GoldLight else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            AccendAvatar(
                avatarId = user.avatarId,
                displayName = user.displayName,
                customAvatarUri = user.customAvatarUri,
                size = 38.dp,
                showRing = user.isCurrentUser
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (user.isCurrentUser) GoldLight else TextPrimary
                    )
                    if (user.isCurrentUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "YOU",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ObsidianBg
                            )
                        }
                    }
                }
                Text(
                    text = "LVL ${user.level} • ${user.title}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // EXP & Streak
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "EXP",
                    tint = GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${user.exp} EXP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GoldLight
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = "Streak",
                    tint = Color(0xFFFF7A45),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${user.streak}d streak",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
