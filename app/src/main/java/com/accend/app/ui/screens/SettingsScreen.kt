package com.accend.app.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.SkillTrack
import com.accend.app.model.UserProfile
import com.accend.app.ui.components.AccendAvatar
import com.accend.app.ui.theme.AccendSerif
import com.accend.app.ui.theme.GoldBorder
import com.accend.app.ui.theme.GoldHairline
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.MentalCyan
import com.accend.app.ui.theme.ObsidianBg
import com.accend.app.ui.theme.ObsidianCard
import com.accend.app.ui.theme.ObsidianSurface
import com.accend.app.ui.theme.PhysicalOrange
import com.accend.app.ui.theme.SkillsViolet
import com.accend.app.ui.theme.SocialEmerald
import com.accend.app.ui.theme.SuccessGreen
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextPrimary
import com.accend.app.ui.theme.TextSecondary
import java.io.File

@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    isSyncing: Boolean,
    syncMessage: String?,
    completedTaskCount: Int,
    onUpdateProfile: (displayName: String, avatarId: String, skillTrackId: String, customAvatarUri: String?) -> Unit,
    onTogglePause: () -> Unit,
    onTogglePillarMute: (Pillar) -> Unit,
    onUpdateReminders: (morning: Boolean, morningTime: String, evening: Boolean, eveningTime: String) -> Unit,
    onSyncWithCloud: () -> Unit,
    onUseStreakFreeze: () -> Unit,
    onClearCache: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var editName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var selectedAvatar by remember(userProfile.avatarId) { mutableStateOf(userProfile.avatarId) }
    var customAvatarUri by remember(userProfile.customAvatarUri) { mutableStateOf(userProfile.customAvatarUri) }
    var selectedSkillTrack by remember(userProfile.chosenSkillTrack) { mutableStateOf(userProfile.chosenSkillTrack) }
    var morningReminder by remember { mutableStateOf(userProfile.morningReminderEnabled) }
    var eveningReminder by remember { mutableStateOf(userProfile.eveningReminderEnabled) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val file = File(context.filesDir, "avatar_user_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                customAvatarUri = file.absolutePath
                selectedAvatar = "custom_gallery"
                onUpdateProfile(editName, "custom_gallery", selectedSkillTrack, file.absolutePath)
            } catch (e: Exception) {
                Log.e("SettingsScreen", "Failed to copy gallery avatar: ${e.message}")
            }
        }
    }

    val availableAvatars = listOf(
        "avatar_gold_1", "avatar_gold_2", "avatar_gold_3",
        "avatar_gold_4", "avatar_gold_5", "avatar_gold_6"
    )

    val completionPct = if (userProfile.currentDay > 1) {
        ((userProfile.totalDaysCompleted.toFloat() / userProfile.currentDay.toFloat()) * 100f).coerceIn(0f, 100f)
    } else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "SETTINGS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = GoldPrimary
        )
        Text(
            text = "Preferences",
            fontFamily = AccendSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ══════════════════════════════════════
        //  PROFILE
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Profile")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AccendAvatar(
                            avatarId = selectedAvatar,
                            displayName = editName,
                            customAvatarUri = customAvatarUri,
                            size = 56.dp,
                            showRing = true,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                                .border(1.5.dp, ObsidianBg, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Change photo",
                                tint = ObsidianBg,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = editName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, GoldPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = userProfile.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val progress = ProgressionConfig.calculateProgress(userProfile.totalExp)
                Text(text = "Level ${progress.currentLevel}  ·  ${progress.totalExp} EXP", fontSize = 12.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableAvatars.forEach { avatarId ->
                        val isSelected = avatarId == selectedAvatar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ObsidianSurface)
                                .border(2.dp, if (isSelected) GoldPrimary else Color.Transparent, CircleShape)
                                .clickable {
                                    selectedAvatar = avatarId
                                    onUpdateProfile(editName, avatarId, selectedSkillTrack, customAvatarUri)
                                }
                                .padding(2.dp)
                        ) {
                            AccendAvatar(avatarId = avatarId, displayName = editName, size = 30.dp, showRing = isSelected)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it; onUpdateProfile(it, selectedAvatar, selectedSkillTrack, customAvatarUri) },
                    label = { Text("Display Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary, unfocusedBorderColor = GoldHairline,
                        focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Skill Track", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                SkillTrack.ALL.forEach { track ->
                    val isSelected = track.id == selectedSkillTrack
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ObsidianSurface else Color.Transparent)
                            .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedSkillTrack = track.id; onUpdateProfile(editName, selectedAvatar, track.id, customAvatarUri) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = track.name, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) GoldLight else TextPrimary)
                        if (isSelected) Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ══════════════════════════════════════
        //  STATISTICS
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Statistics")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Day ${userProfile.currentDay}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "${completionPct.toInt()}% completed", fontSize = 12.sp, color = TextSecondary)
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ObsidianSurface
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "${userProfile.totalTasksCompleted}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Text(text = "Tasks Done", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                // Per-pillar stats
                val pillars = listOf(
                    Triple("Physical", userProfile.physicalCompleted, PhysicalOrange),
                    Triple("Mental", userProfile.mentalCompleted, MentalCyan),
                    Triple("Skills", userProfile.skillsCompleted, SkillsViolet),
                    Triple("Social", userProfile.socialCompleted, SocialEmerald)
                )
                val totalCompleted = (userProfile.physicalCompleted + userProfile.mentalCompleted + userProfile.skillsCompleted + userProfile.socialCompleted).coerceAtLeast(1)

                pillars.forEach { (name, count, color) ->
                    val pct = (count.toFloat() / totalCompleted.toFloat()).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color, modifier = Modifier.width(70.dp))
                        LinearProgressIndicator(
                            progress = { pct },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = color,
                            trackColor = color.copy(alpha = 0.15f)
                        )
                        Text(text = "$count", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(30.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Best Streak: ${userProfile.bestStreak} days", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "Best Level: ${userProfile.level}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ══════════════════════════════════════
        //  NOTIFICATIONS
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Notifications")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggleRow(
                    title = "Morning Review",
                    subtitle = userProfile.morningReminderTime,
                    icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    checked = morningReminder,
                    onCheckedChange = {
                        morningReminder = it
                        onUpdateReminders(morningReminder, "08:00", eveningReminder, "20:00")
                    }
                )
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsToggleRow(
                    title = "Evening Review",
                    subtitle = userProfile.eveningReminderTime,
                    icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    checked = eveningReminder,
                    onCheckedChange = {
                        eveningReminder = it
                        onUpdateReminders(morningReminder, "08:00", eveningReminder, "20:00")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Pillar Alerts", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                Pillar.entries.forEach { pillar ->
                    val isMuted = when (pillar.id) { "physical" -> userProfile.physicalMuted; "mental" -> userProfile.mentalMuted; "skills" -> userProfile.skillsMuted; "social" -> userProfile.socialMuted; else -> false }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${pillar.title} Alerts", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = !isMuted,
                            onCheckedChange = { onTogglePillarMute(pillar) },
                            colors = SwitchDefaults.colors(checkedThumbColor = pillar.themeColor, checkedTrackColor = pillar.themeColor.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ══════════════════════════════════════
        //  PROGRAM
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Program")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).testTag("program_pause_controls"),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Streak Freeze
                SettingsToggleRow(
                    title = "Streak Freeze",
                    subtitle = "${userProfile.streakFreezeTokens} tokens remaining",
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(20.dp)) },
                    checked = false,
                    onCheckedChange = { onUseStreakFreeze() }
                )
                Text(text = "Use a token to preserve your streak for today", fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(start = 30.dp))

                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))

                // Pause Program
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (userProfile.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Pause status",
                            tint = if (userProfile.isPaused) Color(0xFFFFC53D) else GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Pause Program", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(
                                text = if (userProfile.isPaused) "Curriculum frozen" else "${userProfile.maxPauseDays - userProfile.pauseDaysUsed} pause days left",
                                fontSize = 11.sp, color = if (userProfile.isPaused) Color(0xFFFFC53D) else TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onTogglePause,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userProfile.isPaused) SuccessGreen else GoldPrimary,
                        contentColor = ObsidianBg
                    )
                ) {
                    Text(
                        text = if (userProfile.isPaused) "RESUME PROGRAM" else "PAUSE PROGRAM",
                        fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp
                    )
                }

                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))

                // Clear Cache
                SettingsRow(
                    title = "Clear Cache",
                    subtitle = "Free up local storage",
                    icon = { Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = onClearCache
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ══════════════════════════════════════
        //  ACCOUNT SETTINGS
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Account Settings")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsRow(
                    title = "Edit Profile",
                    subtitle = "Name, avatar & skill track",
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = { /* scroll to profile section */ }
                )
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    title = "Privacy Settings",
                    subtitle = when (userProfile.privacyLevel) {
                        "everyone" -> "Everyone"
                        "friends_only" -> "Friends Only"
                        "private" -> "Private"
                        else -> "Friends Only"
                    },
                    icon = { Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = { /* toggle privacy */ }
                )
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    title = "Connected Accounts",
                    subtitle = "Manage linked services",
                    icon = { Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = { /* connected accounts */ }
                )
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    title = "Data & Privacy",
                    subtitle = "Export, backup & privacy controls",
                    icon = { Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = { /* data & privacy */ }
                )
                HorizontalDivider(color = GoldHairline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                SettingsRow(
                    title = "Dark Mode",
                    subtitle = if (userProfile.isDarkMode) "Enabled" else "Disabled",
                    icon = { Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                    onClick = { onToggleDarkMode() }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ══════════════════════════════════════
        //  CLOUD SYNC
        // ══════════════════════════════════════
        SettingsSectionHeader(title = "Cloud Sync")
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "ACCEND Cloud", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(text = "Sync profile, XP & completions", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                if (syncMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = syncMessage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSyncWithCloud,
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("sync_cloud_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                            contentDescription = "Sync", modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSyncing) "SYNCHRONIZING..." else "MANUAL CLOUD SYNC",
                            fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── About ──
        Surface(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            color = ObsidianSurface
        ) {
            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "ACCEND v2.0.0", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Character Progression for Real Life", fontSize = 10.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldPrimary.copy(alpha = 0.4f))
        )
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}
