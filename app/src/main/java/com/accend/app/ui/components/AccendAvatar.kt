package com.accend.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.accend.app.ui.theme.GoldDark
import com.accend.app.ui.theme.GoldLight
import com.accend.app.ui.theme.GoldPrimary
import com.accend.app.ui.theme.ObsidianCard
import com.accend.app.ui.theme.TextPrimary

@Composable
fun AccendAvatar(
    avatarId: String,
    displayName: String,
    modifier: Modifier = Modifier,
    customAvatarUri: String? = null,
    size: Dp = 48.dp,
    showRing: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val goldRingBrush = Brush.sweepGradient(
        listOf(
            GoldPrimary,
            GoldLight,
            GoldDark,
            GoldPrimary
        )
    )

    val ringWidth = if (size > 64.dp) 3.dp else 2.dp

    val initials = if (displayName.isNotBlank()) {
        displayName.trim().take(2).uppercase()
    } else {
        "AC"
    }

    val hasCustomImage = !customAvatarUri.isNullOrBlank() ||
            avatarId.startsWith("/") ||
            avatarId.startsWith("file:") ||
            avatarId.startsWith("content:")

    val imageModel = customAvatarUri?.takeIf { it.isNotBlank() } ?: avatarId

    val avatarBg = when (avatarId) {
        "avatar_gold_1" -> Color(0xFF1F2430)
        "avatar_gold_2" -> Color(0xFF281E19)
        "avatar_gold_3" -> Color(0xFF16272E)
        "avatar_gold_4" -> Color(0xFF251C33)
        "avatar_gold_5" -> Color(0xFF1B2B23)
        "avatar_gold_6" -> Color(0xFF2E2A1C)
        else -> ObsidianCard
    }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showRing) {
                    Modifier.border(ringWidth, goldRingBrush, CircleShape)
                } else {
                    Modifier
                }
            )
            .padding(if (showRing) ringWidth + 2.dp else 0.dp)
            .clip(CircleShape)
            .background(avatarBg)
            .then(
                if (onClick != null) Modifier.bouncyClickable { onClick() } else Modifier
            )
            .testTag("accend_avatar"),
        contentAlignment = Alignment.Center
    ) {
        if (hasCustomImage) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Custom Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (size >= 40.dp) {
            Text(
                text = initials,
                color = GoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp,
                letterSpacing = 0.5.sp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = GoldLight,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}
