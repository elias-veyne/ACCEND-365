package com.accend.app.model

data class LeaderboardUser(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val avatarId: String,
    val level: Int,
    val title: String,
    val exp: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false,
    val isFriend: Boolean = false,
    val reachedAtTimestamp: Long = 0L,
    val customAvatarUri: String? = null
)
