package com.accend.app.data

import android.content.Context
import android.provider.Settings
import com.accend.app.model.LeaderboardUser
import com.accend.app.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Cloud sync for ACCEND.
 *
 * Transport: Firebase Firestore (project accend-5c0f1). Every real user
 * profile and task completion is persisted server-side under a per-install
 * device id, so the leaderboard reflects actual synced users and a fresh
 * install can restore its profile from the cloud.
 */
class FirebaseSyncManager(private val context: Context) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? =
        FirebaseAuth.getInstance().currentUser

    suspend fun autoSignInIfAvailable(userEmailHint: String = ""): String? {
        // Prefer anonymous auth when enabled in the Firebase console; harmless if not.
        runCatching {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().addOnFailureListener { /* ignore */ }
            }
        }
        return deviceId()
    }

    suspend fun syncProfileToFirestore(profile: UserProfile): Boolean {
        val id = deviceId()
        return runCatching {
            firestore.collection(USERS)
                .document(id)
                .set(profileDoc(profile, id))
                .awaitResult()
        }.isSuccess
    }

    suspend fun fetchRealLeaderboardUsers(currentUserId: String): List<LeaderboardUser> =
        runCatching {
            firestore.collection(USERS).get().awaitResult().documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val userId = (data["userId"] as? String)?.takeIf { it.isNotBlank() }
                    ?: doc.id.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                val displayName = (data["displayName"] as? String)?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                LeaderboardUser(
                    rank = 0,
                    userId = userId,
                    displayName = displayName,
                    avatarId = (data["avatarId"] as? String)?.takeIf { it.isNotBlank() }
                        ?: "avatar_gold_1",
                    customAvatarUri = (data["customAvatarUri"] as? String)
                        ?.takeIf { it.isNotBlank() },
                    level = (data["level"] as? Number)?.toInt() ?: 1,
                    title = (data["title"] as? String)?.takeIf { it.isNotBlank() } ?: "AWAKENED",
                    exp = (data["totalExp"] as? Number)?.toInt() ?: 0,
                    streak = (data["streak"] as? Number)?.toInt() ?: 1,
                    isCurrentUser = userId == currentUserId,
                    isFriend = false,
                    reachedAtTimestamp = (data["updatedAt"] as? Number)?.toLong() ?: 0L
                )
            }
        }.getOrDefault(emptyList())

    suspend fun syncTaskCompletionToFirestore(
        userId: String,
        dayNumber: Int,
        taskId: String,
        isCompleted: Boolean,
        xpAwarded: Int,
        notes: String?
    ): Boolean {
        val id = deviceId()
        return runCatching {
            val data = mutableMapOf<String, Any>(
                "userId" to id,
                "dayNumber" to dayNumber,
                "taskId" to taskId,
                "isCompleted" to isCompleted,
                "xpAwarded" to xpAwarded,
                "notes" to (notes ?: ""),
                "updatedAt" to System.currentTimeMillis()
            )
            if (isCompleted) {
                data["completedAt"] = System.currentTimeMillis()
            }
            firestore.collection(COMPLETIONS)
                .document("${id}_$taskId")
                .set(data)
                .awaitResult()
        }.isSuccess
    }

    suspend fun fetchCloudProfile(userId: String): Map<String, Any>? = runCatching {
        val doc = firestore.collection(USERS).document(userId).get().awaitResult()
        if (doc.exists()) doc.data else null
    }.getOrNull()

    suspend fun restoreUserProfileFromFirestore(fallbackEmail: String = ""): UserProfile? =
        runCatching {
            val doc = firestore.collection(USERS).document(deviceId()).get().awaitResult()
            if (!doc.exists()) null else toUserProfile(doc.data ?: emptyMap())
        }.getOrNull()

    private fun deviceId(): String {
        val prefs = context.getSharedPreferences("accend_cloud", Context.MODE_PRIVATE)
        prefs.getString("device_id", null)?.let { return it }
        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }
        val id = androidId?.takeIf { it.isNotBlank() && it != "9774d56d682e549c" }
            ?: "accend-${UUID.randomUUID()}"
        prefs.edit().putString("device_id", id).apply()
        return id
    }

    private fun profileDoc(profile: UserProfile, id: String): Map<String, Any> = mapOf(
        "userId" to id,
        "displayName" to profile.displayName.ifBlank { "Ascendant" },
        "avatarId" to profile.avatarId,
        "customAvatarUri" to (profile.customAvatarUri ?: ""),
        "chosenSkillTrack" to profile.chosenSkillTrack,
        "currentDay" to profile.currentDay,
        "totalExp" to profile.totalExp,
        "level" to profile.level,
        "title" to profile.title,
        "streak" to profile.currentDay.coerceAtLeast(1),
        "email" to (profile.email ?: ""),
        "updatedAt" to System.currentTimeMillis()
    )

    private fun toUserProfile(data: Map<String, Any>): UserProfile? {
        val displayName = (data["displayName"] as? String)?.takeIf { it.isNotBlank() }
            ?: return null
        return UserProfile(
            userId = "local_user",
            displayName = displayName,
            avatarId = (data["avatarId"] as? String)?.takeIf { it.isNotBlank() }
                ?: "avatar_gold_1",
            customAvatarUri = (data["customAvatarUri"] as? String)?.takeIf { it.isNotBlank() },
            chosenSkillTrack = (data["chosenSkillTrack"] as? String)
                ?.takeIf { it.isNotBlank() } ?: "coding",
            currentDay = (data["currentDay"] as? Number)?.toInt() ?: 1,
            totalExp = (data["totalExp"] as? Number)?.toInt() ?: 0,
            level = (data["level"] as? Number)?.toInt() ?: 1,
            title = (data["title"] as? String)?.takeIf { it.isNotBlank() } ?: "AWAKENED",
            onboardingComplete = true,
            email = (data["email"] as? String)?.takeIf { it.isNotBlank() }
        )
    }

    private suspend fun <T> Task<T>.awaitResult(): T =
        suspendCancellableCoroutine { cont ->
            addOnSuccessListener { cont.resume(it) }
            addOnFailureListener { cont.resumeWithException(it) }
        }

    private companion object {
        const val USERS = "users"
        const val COMPLETIONS = "completions"
    }
}
