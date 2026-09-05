package com.accend.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.accend.app.ui.theme.GoldDark
import com.accend.app.ui.theme.GoldHairline
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.ObsidianSurface
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextSecondary

@Composable
fun HeatmapView(
    currentDay: Int,
    completedTasks: List<TaskProgressEntity>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Map dayNumber -> count of completed tasks
    val dayCompletionMap = remember(completedTasks) {
        completedTasks.groupBy { it.dayNumber }.mapValues { it.value.size }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianSurface)
            .border(1.dp, GoldHairline, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("activity_heatmap")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "365-DAY ASCENSION HEATMAP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = GoldPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Scroll to view all 52 weeks",
                fontSize = 10.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Heatmap Grid: 7 rows (days of week) x 53 columns (weeks of year)
        Row(
            modifier = Modifier.horizontalScroll(scrollState)
        ) {
            val totalWeeks = 53
            for (week in 0 until totalWeeks) {
                Column(
                    modifier = Modifier.padding(horizontal = 1.5.dp)
                ) {
                    for (dayInWeek in 1..7) {
                        val dayNumber = (week * 7) + dayInWeek
                        if (dayNumber <= 365) {
                            val count = dayCompletionMap[dayNumber] ?: 0
                            val isCurrentDay = dayNumber == currentDay

                            val cellColor = when {
                                count >= 4 -> GoldLight
                                count in 2..3 -> GoldPrimary
                                count == 1 -> GoldDark
                                dayNumber < currentDay -> Color(0xFF1E222D)
                                else -> Color(0xFF13151D) // Future
                            }

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 1.5.dp)
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(cellColor)
                                    .then(
                                        if (isCurrentDay) {
                                            Modifier.border(1.dp, GoldLight, RoundedCornerShape(2.dp))
                                        } else {
                                            Modifier
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Less", fontSize = 10.sp, color = TextMuted)
            Spacer(modifier = Modifier.width(6.dp))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF1E222D)))
            Spacer(modifier = Modifier.width(3.dp))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(GoldDark))
            Spacer(modifier = Modifier.width(3.dp))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(GoldPrimary))
            Spacer(modifier = Modifier.width(3.dp))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(GoldLight))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Ascended (4+)", fontSize = 10.sp, color = TextMuted)
        }
    }
}
