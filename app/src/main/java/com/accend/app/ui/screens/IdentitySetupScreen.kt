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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.accend.app.model.SkillTrack
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
import com.accend.app.ui.theme.TextMuted
import com.accend.app.ui.theme.TextPrimary
import com.accend.app.ui.theme.TextSecondary
import java.io.File

@Composable
fun IdentitySetupScreen(
    onComplete: (displayName: String, avatarId: String, skillTrackId: String, customAvatarUri: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var displayName by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("avatar_gold_1") }
    var customAvatarUri by remember { mutableStateOf<String?>(null) }
    var selectedSkillTrackId by remember { mutableStateOf("coding") }
    var isSubmitting by remember { mutableStateOf(false) }

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
                selectedAvatarId = "custom_gallery"
            } catch (e: Exception) {
                Log.e("IdentitySetup", "Failed to copy gallery avatar: ${e.message}")
            }
        }
    }

    val scrollState = rememberScrollState()

    val availableAvatars = listOf(
        "avatar_gold_1",
        "avatar_gold_2",
        "avatar_gold_3",
        "avatar_gold_4",
        "avatar_gold_5",
        "avatar_gold_6"
    )

    val isFormValid = displayName.trim().isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("identity_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "ACCEND",
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACCEND",
                    fontFamily = AccendSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = 4.sp,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CHARACTER IDENTITY SETUP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Initiate your 365-day sovereign character progression. Every action builds your permanent character.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Avatar Selector with Gold Frame and Gallery Picker Badge
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(4.dp)
            ) {
                AccendAvatar(
                    avatarId = selectedAvatarId,
                    displayName = displayName.ifBlank { "AC" },
                    customAvatarUri = customAvatarUri,
                    size = 96.dp,
                    showRing = true,
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                // Small circular gallery button overlay on avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .border(2.dp, ObsidianBg, CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("avatar_gallery_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload from gallery",
                        tint = ObsidianBg,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Choose from Gallery Button
            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ObsidianCard,
                    contentColor = GoldLight
                ),
                modifier = Modifier.testTag("choose_from_gallery_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (customAvatarUri != null) "PHOTO SELECTED • CHANGE" else "CHOOSE PFP FROM GALLERY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "OR CHOOSE ASCENSION CREST",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                availableAvatars.forEach { avatarId ->
                    val isSelected = avatarId == selectedAvatarId && customAvatarUri == null
                    Box(
                        modifier = Modifier
                            .clickable {
                                selectedAvatarId = avatarId
                                customAvatarUri = null
                            }
                            .padding(2.dp)
                    ) {
                        AccendAvatar(
                            avatarId = avatarId,
                            displayName = displayName.ifBlank { "AC" },
                            size = 38.dp,
                            showRing = isSelected
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Display Name Input with uppercase label
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SOVEREIGN NAME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = { Text("e.g. Marcus Thorne", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldHairline,
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("display_name_input")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Skill Track Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CHOOSE PRIMARY SKILL TRACK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GoldPrimary
                )
                Text(
                    text = "One track of deliberate mastery across 365 days.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                SkillTrack.ALL.forEach { track ->
                    val isSelected = track.id == selectedSkillTrackId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) GoldPrimary else GoldHairline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSkillTrackId = track.id }
                            .testTag("skill_track_${track.id}"),
                        color = if (isSelected) ObsidianSurface else ObsidianCard
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = track.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) GoldLight else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(GoldDark.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = track.tag.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = track.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(GoldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = ObsidianBg,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Action Button (disabled until name is entered)
            Button(
                onClick = {
                    if (isFormValid && !isSubmitting) {
                        isSubmitting = true
                        onComplete(displayName, selectedAvatarId, selectedSkillTrackId, customAvatarUri)
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("begin_ascension_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color(0xFF0A0B0E),
                    disabledContainerColor = ObsidianCard,
                    disabledContentColor = TextMuted
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color(0xFF0A0B0E),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BEGIN ASCENSION",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.5.sp,
                            color = Color(0xFF0A0B0E)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF0A0B0E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Backed by the ACCEND Cloud — your data syncs across devices",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
