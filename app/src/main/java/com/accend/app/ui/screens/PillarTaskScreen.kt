package com.accend.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accend.app.model.Pillar
import com.accend.app.model.TaskItem
import com.accend.app.ui.theme.AccendSerif
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
import com.accend.app.ui.components.bouncyClickable

@Composable
fun PillarTaskScreen(
    pillar: Pillar,
    currentDay: Int,
    tasks: List<TaskItem>,
    onBack: () -> Unit,
    onToggleTask: (TaskItem, notes: String?) -> Unit,
    onSaveNotes: (taskId: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val completedCount = tasks.count { it.isCompleted }
    val totalExpEarned = tasks.filter { it.isCompleted }.sumOf { it.expValue }
    val totalExpAvailable = tasks.sumOf { it.expValue }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("pillar_task_screen_${pillar.id}")
    ) {
        // Back Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ObsidianCard)
                    .border(1.dp, GoldHairline, CircleShape)
                    .bouncyClickable { onBack() }
                    .testTag("pillar_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "${pillar.title.uppercase()} • DAY $currentDay",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = pillar.themeColor
                )
                Text(
                    text = pillar.subtitle,
                    fontFamily = AccendSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pillar Header Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, pillar.themeColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            color = ObsidianSurface
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, pillar.themeColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = pillar.imageRes),
                            contentDescription = pillar.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = pillar.description,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed: $completedCount of ${tasks.size} tasks",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (completedCount == tasks.size && tasks.isNotEmpty()) SuccessGreen else TextPrimary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "EXP",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalExpEarned / $totalExpAvailable EXP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GoldLight
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "TODAY'S CURRICULUM TASKS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = GoldPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtask items
        tasks.forEach { task ->
            TaskItemCard(
                task = task,
                themeColor = pillar.themeColor,
                onToggle = { notes -> onToggleTask(task, notes) },
                onSaveNotes = { notes -> onSaveNotes(task.id, notes) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TaskItemCard(
    task: TaskItem,
    themeColor: Color,
    onToggle: (notes: String?) -> Unit,
    onSaveNotes: (notes: String) -> Unit
) {
    var expandedNotes by remember { mutableStateOf(false) }
    var notesText by remember(task.notes) { mutableStateOf(task.notes ?: "") }

    val checkColor by animateColorAsState(
        targetValue = if (task.isCompleted) SuccessGreen else Color.Transparent,
        animationSpec = tween(300),
        label = "check_color"
    )

    val checkPop by animateFloatAsState(
        targetValue = if (task.isCompleted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "check_pop_spring"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (task.isCompleted) SuccessGreen.copy(alpha = 0.5f) else GoldHairline,
                RoundedCornerShape(14.dp)
            )
            .testTag("task_item_${task.id}"),
        color = ObsidianCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Interactive Checkbox with instant tactile feedback
                Box(
                    modifier = Modifier
                        .bouncyClickable(pressedScale = 0.88f) {
                            onToggle(if (notesText.isNotBlank()) notesText else null)
                        }
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(checkColor)
                        .border(
                            1.5.dp,
                            if (task.isCompleted) SuccessGreen else GoldHairline,
                            CircleShape
                        )
                        .testTag("task_checkbox_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = ObsidianBg,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer {
                                    scaleX = checkPop
                                    scaleY = checkPop
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(themeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.subType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColor,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // EXP value badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "EXP",
                                tint = GoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "+${task.expValue} EXP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (task.isCompleted) TextSecondary else TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Target Metric Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ObsidianSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldHairline)
                    ) {
                        Text(
                            text = "TARGET: ${task.target}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Notes affordance
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .bouncyClickable(pressedScale = 0.98f) { expandedNotes = !expandedNotes },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "Notes",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (notesText.isNotBlank()) "View Logged Notes" else "Add Reflection Note",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            AnimatedVisibility(visible = expandedNotes) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            onSaveNotes(it)
                        },
                        placeholder = { Text("Log sets completed, thoughts, or reflections...", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = GoldHairline,
                            focusedContainerColor = ObsidianSurface,
                            unfocusedContainerColor = ObsidianSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .testTag("task_notes_field_${task.id}")
                    )
                }
            }
        }
    }
}
