package com.accend.app.data

import android.content.Context
import android.provider.Settings
import com.accend.app.model.LeaderboardUser
import com.accend.app.model.UserProfile
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Cloud sync for ACCEND.
 *
 * Transport: CrudCrud free REST API (https://crudcrud.com) - no account,
 * captcha or credentials required, HTTPS + CORS enabled. Every real user
 * profile and task completion is persisted server-side under a per-install
 * device id, so the leaderboard reflects actual synced users and a fresh
 * install can restore its profile from the cloud.
 *
 * Swap note: method signatures mirror the original Firestore plan, so a real
 * Firebase project can replace the transport later without touching call sites.
 */
class FirebaseSyncManager(private val context: Context) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    fun getCurrentFirebaseUser(): Nothing? = null

    suspend fun autoSignInIfAvailable(userEmailHint: String = ""): String? = deviceId()

    suspend fun syncProfileToFirestore(profile: UserProfile): Boolean = runCatching {
        val id = deviceId()
        val existing = findUserDoc(id)
        val doc = profileDoc(profile, id)
        val (code, _) = if (existing != null) {
            request("PUT", url("users", "_id" to existing.getString("_id")), doc.toString())
        } else {
            request("POST", url("users"), doc.toString())
        }
        code in 200..299
    }.getOrDefault(false)

    suspend fun fetchRealLeaderboardUsers(currentUserId: String): List<LeaderboardUser> = runCatching {
        fetchUsers().mapNotNull { row ->
            val userId = row.optString("userId").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val displayName = row.optString("displayName").ifBlank { return@mapNotNull null }
            LeaderboardUser(
                rank = 0,
                userId = userId,
                displayName = displayName,
                avatarId = row.optString("avatarId").ifBlank { "avatar_gold_1" },
                customAvatarUri = row.optString("customAvatarUri").takeIf { it.isNotBlank() },
                level = row.optInt("level", 1),
                title = row.optString("title").ifBlank { "AWAKENED" },
                exp = row.optInt("totalExp", 0),
                streak = row.optInt("streak", 1),
                isCurrentUser = false,
                isFriend = false,
                reachedAtTimestamp = row.optLong("updatedAt", 0L)
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
    ): Boolean = runCatching {
        val id = deviceId()
        val existing = findFirstDoc("completions", "userId" to id, "taskId" to taskId)
        val doc = JSONObject().apply {
            put("userId", id)
            put("dayNumber", dayNumber)
            put("taskId", taskId)
            put("isCompleted", isCompleted)
            put("xpAwarded", xpAwarded)
            put("notes", notes ?: "")
            put("completedAt", if (isCompleted) System.currentTimeMillis() else JSONObject.NULL)
            put("updatedAt", System.currentTimeMillis())
        }
        val (code, _) = if (existing != null) {
            request("PUT", url("completions", "_id" to existing.getString("_id")), doc.toString())
        } else {
            request("POST", url("completions"), doc.toString())
        }
        code in 200..299
    }.getOrDefault(false)

    suspend fun fetchCloudProfile(userId: String): Map<String, Any>? = runCatching {
        findUserDoc(userId)?.toCloudMap()
    }.getOrNull()

    suspend fun restoreUserProfileFromFirestore(fallbackEmail: String = ""): UserProfile? = runCatching {
        findUserDoc(deviceId())?.let { toUserProfile(it) }
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

    private suspend fun findUserDoc(userId: String): JSONObject? =
        findFirstDoc("users", "userId" to userId)

    private suspend fun findFirstDoc(
        resource: String,
        vararg query: Pair<String, String>
    ): JSONObject? {
        val (code, body) = request("GET", url(resource, *query))
        if (code !in 200..299) return null
        return runCatching {
            val arr = JSONArray(body)
            if (arr.length() > 0) arr.getJSONObject(0) else null
        }.getOrNull()
    }

    private suspend fun fetchUsers(): List<JSONObject> {
        val (code, body) = request("GET", url("users"))
        if (code !in 200..299) return emptyList()
        return runCatching {
            val arr = JSONArray(body)
            (0 until arr.length()).map { arr.getJSONObject(it) }
        }.getOrDefault(emptyList())
    }

    private fun profileDoc(profile: UserProfile, id: String): JSONObject = JSONObject().apply {
        put("userId", id)
        put("displayName", profile.displayName.ifBlank { "Ascendant" })
        put("avatarId", profile.avatarId)
        put("customAvatarUri", profile.customAvatarUri)
        put("chosenSkillTrack", profile.chosenSkillTrack)
        put("currentDay", profile.currentDay)
        put("totalExp", profile.totalExp)
        put("level", profile.level)
        put("title", profile.title)
        put("streak", profile.currentDay.coerceAtLeast(1))
        put("email", profile.email ?: "")
        put("updatedAt", System.currentTimeMillis())
    }

    private fun toUserProfile(row: JSONObject): UserProfile? {
        val displayName = row.optString("displayName").ifBlank { return null }
        return UserProfile(
            userId = "local_user",
            displayName = displayName,
            avatarId = row.optString("avatarId").ifBlank { "avatar_gold_1" },
            customAvatarUri = row.optString("customAvatarUri").takeIf { it.isNotBlank() },
            chosenSkillTrack = row.optString("chosenSkillTrack").ifBlank { "coding" },
            currentDay = row.optInt("currentDay", 1),
            totalExp = row.optInt("totalExp", 0),
            level = row.optInt("level", 1),
            title = row.optString("title").ifBlank { "AWAKENED" },
            onboardingComplete = true,
            email = row.optString("email").takeIf { it.isNotBlank() }
        )
    }

    private fun JSONObject.toCloudMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        keys().forEach { key ->
            val value = opt(key)
            if (value != null && value != JSONObject.NULL) {
                map[key] = value
            }
        }
        return map
    }

    private suspend fun request(
        method: String,
        url: String,
        body: String? = null
    ): Pair<Int, String> = withContext(Dispatchers.IO) {
        val requestBody = body?.toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .method(method, requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            response.code to (response.body?.string() ?: "")
        }
    }

    private fun url(resource: String, vararg query: Pair<String, String>): String {
        val base = "https://crudcrud.com/api/$CLOUD_API_KEY/$resource"
        if (query.isEmpty()) return base
        val params = query.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }
        return "$base?$params"
    }

    private companion object {
        const val CLOUD_API_KEY = "3d6f2eddc0694ad4bf43e5e86b41c028"
    }
}
