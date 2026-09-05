package com.accend.app.data

import android.content.Context
import com.accend.app.model.LeaderboardUser
import com.accend.app.model.UserProfile

/**
 * No-op stub for Firebase sync. Cloud features are disabled until a
 * google-services.json is added and Firebase is properly configured.
 * All methods return gracefully without network calls.
 */
class FirebaseSyncManager(private val context: Context) {

    fun getCurrentFirebaseUser(): Nothing? = null

    suspend fun autoSignInIfAvailable(userEmailHint: String = ""): String? = null

    suspend fun syncProfileToFirestore(profile: UserProfile): Boolean = false

    suspend fun fetchRealLeaderboardUsers(currentUserId: String): List<LeaderboardUser> = emptyList()

    suspend fun syncTaskCompletionToFirestore(
        userId: String,
        dayNumber: Int,
        taskId: String,
        isCompleted: Boolean,
        xpAwarded: Int,
        notes: String?
    ): Boolean = false

    suspend fun fetchCloudProfile(userId: String): Map<String, Any>? = null

    suspend fun restoreUserProfileFromFirestore(fallbackEmail: String = ""): UserProfile? = null
}
