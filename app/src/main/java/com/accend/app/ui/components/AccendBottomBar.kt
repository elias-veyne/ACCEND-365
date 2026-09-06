package com.accend.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accend.app.ui.theme.GoldDark
import com.accend.app.ui.theme.GoldHairline
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.ObsidianCard
import com.accend.app.ui.theme.ObsidianSurface
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextSecondary
import com.accend.app.viewmodel.NavigationTab

@Composable
fun AccendBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = ObsidianSurface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldHairline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = "Home",
                selected = currentTab == NavigationTab.HOME,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(NavigationTab.HOME) },
                testTag = "tab_home"
            )
            BottomNavItem(
                title = "Analysis",
                selected = currentTab == NavigationTab.ANALYSIS,
                selectedIcon = Icons.Filled.Analytics,
                unselectedIcon = Icons.Outlined.Analytics,
                onClick = { onTabSelected(NavigationTab.ANALYSIS) },
                testTag = "tab_analysis"
            )
            BottomNavItem(
                title = "Cohort",
                selected = currentTab == NavigationTab.LEADERBOARD,
                selectedIcon = Icons.Filled.Leaderboard,
                unselectedIcon = Icons.Outlined.Leaderboard,
                onClick = { onTabSelected(NavigationTab.LEADERBOARD) },
                testTag = "tab_leaderboard"
            )
            BottomNavItem(
                title = "Settings",
                selected = currentTab == NavigationTab.SETTINGS,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                onClick = { onTabSelected(NavigationTab.SETTINGS) },
                testTag = "tab_settings"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (selected) {
                            Modifier
                                .background(GoldDark.copy(alpha = 0.25f))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        } else {
                            Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        }
                    )
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = title,
                    tint = if (selected) GoldPrimary else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) GoldLight else TextMuted,
                letterSpacing = 0.5.sp
            )
        }
    }
}
