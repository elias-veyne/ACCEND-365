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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.accend.app.model.BadgesProvider
import com.accend.app.model.Pillar
import com.accend.app.model.SkillTrack
import com.accend.app.model.UserProfile
import com.accend.app.ui.components.AccendAvatar
import com.accend.app.ui.theme.AccendSerif
import com.accend.app.ui.theme.GoldBorder
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
import java.io.File

@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    isSyncing: Boolean,
    syncMessage: String?,
    onUpdateProfile: (displayName: String, avatarId: String, skillTrackId: String, customAvatarUri: String?) -> Unit,
    onTogglePause: () -> Unit,
    onTogglePillarMute: (Pillar) -> Unit,
    onUpdateReminders: (morning: Boolean, morningTime: String, evening: Boolean, eveningTime: String) -> Unit,
    onSyncWithCloud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var editName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var selectedAvatar by remember(userProfile.avatarId) { mutableStateOf(userProfile.avatarId) }
    var customAvatarUri by remember(userProfile.customAvatarUri) { mutableStateOf(userProfile.customAvatarUri) }
    var selectedSkillTrack by remember(userProfile.chosenSkillTrack) { mutableStateOf(userProfile.chosenSkillTrack) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val file = File(context.filesDir, "avatar_user_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                customAvatarUri = file.absolutePath
                selectedAvatar = "custom_gallery"
                onUpdateProfile(editName, "custom_gallery", selectedSkillTrack, file.absolutePath)
            } catch (e: Exception) {
                Log.e("SettingsScreen", "Failed to copy gallery avatar: ${e.message}")
            }
        }
    }

    val badges = remember(userProfile.totalExp, userProfile.level, userProfile.currentDay) {
        BadgesProvider.getBadges(
            totalExp = userProfile.totalExp,
            level = userProfile.level,
            completedTasksCount = userProfile.totalExp / 15,
            streakDays = userProfile.currentDay
        )
    }

    val availableAvatars = listOf(
        "avatar_gold_1",
        "avatar_gold_2",
        "avatar_gold_3",
        "avatar_gold_4",
        "avatar_gold_5",
        "avatar_gold_6"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen")
    ) {
        // Header
        Text(
            text = "SYSTEM & SOVEREIGNTY",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = GoldPrimary
        )
        Text(
            text = "Program Settings & Controls",
            fontFamily = AccendSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Profile Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "PROFILE & IDENTITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AccendAvatar(
                            avatarId = selectedAvatar,
                            displayName = editName,
                            customAvatarUri = customAvatarUri,
                            size = 64.dp,
                            showRing = true,
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
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
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = editName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Skill Track: ${SkillTrack.findById(selectedSkillTrack).name}",
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                        Text(
                            text = "Level ${userProfile.level} • ${userProfile.title}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ObsidianSurface,
                        contentColor = GoldLight
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_choose_gallery_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (customAvatarUri != null) "CHANGE PHOTO FROM GALLERY" else "CHOOSE PHOTO FROM GALLERY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar options
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    availableAvatars.forEach { avatarId ->
                        val isSelected = avatarId == selectedAvatar && customAvatarUri == null
                        Box(
                            modifier = Modifier
                                .clickable {
                                    selectedAvatar = avatarId
                                    customAvatarUri = null
                                    onUpdateProfile(editName, avatarId, selectedSkillTrack, null)
                                }
                                .padding(2.dp)
                        ) {
                            AccendAvatar(
                                avatarId = avatarId,
                                displayName = editName,
                                size = 32.dp,
                                showRing = isSelected
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = {
                        editName = it
                        onUpdateProfile(it, selectedAvatar, selectedSkillTrack, customAvatarUri)
                    },
                    label = { Text("Display Name", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldHairline,
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Skill Track:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                SkillTrack.ALL.forEach { track ->
                    val isSelected = track.id == selectedSkillTrack
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ObsidianSurface else Color.Transparent)
                            .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedSkillTrack = track.id
                                onUpdateProfile(editName, selectedAvatar, track.id, customAvatarUri)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = track.name,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldLight else TextPrimary
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Program Pause Controls
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp))
                .testTag("program_pause_controls"),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROGRAM PAUSE SYSTEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GoldPrimary
                        )
                        Text(
                            text = if (userProfile.isPaused) "Curriculum currently frozen" else "Active progression",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (userProfile.isPaused) Color(0xFFFFC53D) else SuccessGreen
                        )
                    }

                    Icon(
                        imageVector = if (userProfile.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Pause status",
                        tint = if (userProfile.isPaused) Color(0xFFFFC53D) else GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pausing freezes your curriculum day without punitive streak loss. Annual allowance: ${userProfile.maxPauseDays - userProfile.pauseDaysUsed} days remaining of ${userProfile.maxPauseDays} total.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onTogglePause()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("toggle_pause_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userProfile.isPaused) GoldPrimary else ObsidianSurface,
                        contentColor = if (userProfile.isPaused) ObsidianBg else GoldLight
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (userProfile.isPaused) GoldPrimary else GoldHairline
                    )
                ) {
                    Text(
                        text = if (userProfile.isPaused) "RESUME ASCENSION PROGRAM" else "FREEZE & PAUSE TODAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Achievements & Badges Collection
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACHIEVEMENTS & BADGES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = GoldPrimary
                    )
                    Text(
                        text = "${badges.count { it.isUnlocked }} / ${badges.size} Unlocked",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                badges.forEach { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (badge.isUnlocked) ObsidianSurface else Color.Transparent)
                            .border(1.dp, if (badge.isUnlocked) GoldHairline else Color.Transparent, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (badge.isUnlocked) GoldPrimary.copy(alpha = 0.2f) else ObsidianSurface)
                                .border(1.dp, if (badge.isUnlocked) GoldPrimary else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (badge.isUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                contentDescription = badge.title,
                                tint = if (badge.isUnlocked) GoldPrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (badge.isUnlocked) GoldLight else TextMuted
                            )
                            Text(
                                text = badge.description,
                                fontSize = 11.sp,
                                color = if (badge.isUnlocked) TextSecondary else TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Notifications & Mute Controls
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DAILY REMINDERS & PILLAR CONTROLS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Morning reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Morning Focus Reminder", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text(text = "Scheduled at ${userProfile.morningReminderTime}", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = userProfile.morningReminderEnabled,
                        onCheckedChange = {
                            onUpdateReminders(it, userProfile.morningReminderTime, userProfile.eveningReminderEnabled, userProfile.eveningReminderTime)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldDark)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Evening reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Evening Reflection Reminder", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text(text = "Triggers only if tasks remain at ${userProfile.eveningReminderTime}", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = userProfile.eveningReminderEnabled,
                        onCheckedChange = {
                            onUpdateReminders(userProfile.morningReminderEnabled, userProfile.morningReminderTime, it, userProfile.eveningReminderTime)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = GoldDark)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Per-Pillar Mute Controls:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Pillar.entries.forEach { pillar ->
                    val isMuted = when (pillar) {
                        Pillar.PHYSICAL -> userProfile.physicalMuted
                        Pillar.MENTAL -> userProfile.mentalMuted
                        Pillar.SKILLS -> userProfile.skillsMuted
                        Pillar.SOCIAL -> userProfile.socialMuted
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${pillar.title} Alerts", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = !isMuted,
                            onCheckedChange = {
                                onTogglePillarMute(pillar)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = pillar.themeColor, checkedTrackColor = pillar.themeColor.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Account & Cloud Sync Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GoldHairline, RoundedCornerShape(16.dp)),
            color = ObsidianCard
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCOUNT & CLOUD SYNC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = GoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Authenticated Sovereign: ${userProfile.email ?: "imthesmith786@gmail.com"}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Text(
                    text = "Local database synced with the ACCEND Cloud — real user data, live across devices.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                if (syncMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = syncMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSyncWithCloud()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("sync_cloud_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianBg
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                            contentDescription = "Sync",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSyncing) "SYNCHRONIZING..." else "MANUAL CLOUD SYNC",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // About & Version
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            color = ObsidianSurface
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ACCEND v1.4.0 • Production Build",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Character Progression for Real Life • 365 Days",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
