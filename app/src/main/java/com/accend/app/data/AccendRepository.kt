package com.accend.app.data

import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.TaskItem
import com.accend.app.model.UserProfile
import com.accend.app.progression.CurriculumEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccendRepository(
    private val dao: AccendDao,
    private val firebaseSyncManager: FirebaseSyncManager
) {

    val userProfileFlow: Flow<UserProfile> = dao.getUserProfileFlow().map { entity ->
        if (entity == null) {
            UserProfile(userId = "local_user")
        } else {
            UserProfile(
                userId = entity.userId,
                displayName = entity.displayName,
                avatarId = entity.avatarId,
                customAvatarUri = entity.customAvatarUri,
                chosenSkillTrack = entity.chosenSkillTrack,
                currentDay = entity.currentDay,
                totalExp = entity.totalExp,
                level = entity.level,
                title = entity.title,
                onboardingComplete = entity.onboardingComplete,
                isPaused = entity.isPaused,
                pauseDaysUsed = entity.pauseDaysUsed,
                maxPauseDays = entity.maxPauseDays,
                morningReminderEnabled = entity.morningReminderEnabled,
                morningReminderTime = entity.morningReminderTime,
                eveningReminderEnabled = entity.eveningReminderEnabled,
                eveningReminderTime = entity.eveningReminderTime,
                physicalMuted = entity.physicalMuted,
                mentalMuted = entity.mentalMuted,
                skillsMuted = entity.skillsMuted,
                socialMuted = entity.socialMuted,
                lastSyncedAt = entity.lastSyncedAt,
                email = entity.email
            )
        }
    }

    suspend fun getProfileSync(): UserProfile {
        val entity = dao.getUserProfile() ?: return UserProfile(userId = "local_user")
        return UserProfile(
            userId = entity.userId,
            displayName = entity.displayName,
            avatarId = entity.avatarId,
            customAvatarUri = entity.customAvatarUri,
            chosenSkillTrack = entity.chosenSkillTrack,
            currentDay = entity.currentDay,
            totalExp = entity.totalExp,
            level = entity.level,
            title = entity.title,
            onboardingComplete = entity.onboardingComplete,
            isPaused = entity.isPaused,
            pauseDaysUsed = entity.pauseDaysUsed,
            maxPauseDays = entity.maxPauseDays,
            morningReminderEnabled = entity.morningReminderEnabled,
            morningReminderTime = entity.morningReminderTime,
            eveningReminderEnabled = entity.eveningReminderEnabled,
            eveningReminderTime = entity.eveningReminderTime,
            physicalMuted = entity.physicalMuted,
            mentalMuted = entity.mentalMuted,
            skillsMuted = entity.skillsMuted,
            socialMuted = entity.socialMuted,
            lastSyncedAt = entity.lastSyncedAt,
            email = entity.email
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        val entity = UserProfileEntity(
            userId = "local_user",
            displayName = profile.displayName,
            avatarId = profile.avatarId,
            customAvatarUri = profile.customAvatarUri,
            chosenSkillTrack = profile.chosenSkillTrack,
            currentDay = profile.currentDay,
            totalExp = profile.totalExp,
            level = profile.level,
            title = profile.title,
            onboardingComplete = profile.onboardingComplete,
            isPaused = profile.isPaused,
            pauseDaysUsed = profile.pauseDaysUsed,
            maxPauseDays = profile.maxPauseDays,
            morningReminderEnabled = profile.morningReminderEnabled,
            morningReminderTime = profile.morningReminderTime,
            eveningReminderEnabled = profile.eveningReminderEnabled,
            eveningReminderTime = profile.eveningReminderTime,
            physicalMuted = profile.physicalMuted,
            mentalMuted = profile.mentalMuted,
            skillsMuted = profile.skillsMuted,
            socialMuted = profile.socialMuted,
            lastSyncedAt = System.currentTimeMillis(),
            email = profile.email
        )
        dao.insertOrUpdateProfile(entity)
        try {
            firebaseSyncManager.syncProfileToFirestore(profile)
        } catch (ignored: Exception) {
            // Firestore sync is non-blocking
        }
    }

    fun getDayTasksFlow(dayNumber: Int, skillTrackId: String): Flow<List<TaskItem>> {
        val baseTasksByPillar = CurriculumEngine.generateTasksForDay(dayNumber, skillTrackId)
        val allBaseTasks = baseTasksByPillar.values.flatten()

        return dao.getProgressForDay(dayNumber).map { progressEntities ->
            val progressMap = progressEntities.associateBy { it.taskId }
            allBaseTasks.map { task ->
                val progress = progressMap[task.id]
                task.copy(
                    isCompleted = progress?.isCompleted == true,
                    completedAt = progress?.completedAt,
                    notes = progress?.notes
                )
            }
        }
    }

    data class ToggleResult(
        val newTotalExp: Int,
        val newLevel: Int,
        val newTitle: String,
        val leveledUp: Boolean,
        val newTitleUnlocked: Boolean
    )

    suspend fun toggleTaskCompletion(
        task: TaskItem,
        notes: String? = null
    ): ToggleResult {
        val currentProfile = getProfileSync()
        val currentProgress = dao.getTaskProgressById(task.id)
        val wasCompleted = currentProgress?.isCompleted == true
        val willBeCompleted = !wasCompleted

        val xpDelta = if (willBeCompleted) task.expValue else -task.expValue
        val updatedTotalExp = (currentProfile.totalExp + xpDelta).coerceAtLeast(0)

        val newProgressCalc = ProgressionConfig.calculateProgress(updatedTotalExp)
        val oldLevel = currentProfile.level
        val oldTitle = currentProfile.title

        val leveledUp = willBeCompleted && newProgressCalc.currentLevel > oldLevel
        val newTitleUnlocked = willBeCompleted && newProgressCalc.title != oldTitle

        // Persist task progress
        val progressEntity = TaskProgressEntity(
            id = task.id,
            dayNumber = task.dayNumber,
            taskId = task.id,
            pillarId = task.pillar.id,
            isCompleted = willBeCompleted,
            completedAt = if (willBeCompleted) System.currentTimeMillis() else null,
            notes = notes ?: currentProgress?.notes,
            xpAwarded = if (willBeCompleted) task.expValue else 0
        )
        dao.insertOrUpdateProgress(progressEntity)

        // Update profile
        val updatedProfile = currentProfile.copy(
            totalExp = updatedTotalExp,
            level = newProgressCalc.currentLevel,
            title = newProgressCalc.title
        )
        saveProfile(updatedProfile)

        // Sync to cloud Firestore
        firebaseSyncManager.syncTaskCompletionToFirestore(
            userId = currentProfile.userId,
            dayNumber = task.dayNumber,
            taskId = task.id,
            isCompleted = willBeCompleted,
            xpAwarded = if (willBeCompleted) task.expValue else 0,
            notes = notes ?: currentProgress?.notes
        )

        return ToggleResult(
            newTotalExp = updatedTotalExp,
            newLevel = newProgressCalc.currentLevel,
            newTitle = newProgressCalc.title,
            leveledUp = leveledUp,
            newTitleUnlocked = newTitleUnlocked
        )
    }

    suspend fun saveTaskNotes(taskId: String, notes: String) {
        val progress = dao.getTaskProgressById(taskId)
        if (progress != null) {
            dao.insertOrUpdateProgress(progress.copy(notes = notes))
        }
    }

    suspend fun toggleProgramPause(): UserProfile {
        val profile = getProfileSync()
        if (!profile.isPaused && profile.pauseDaysUsed >= profile.maxPauseDays) {
            return profile // Cannot pause if limit reached
        }
        val newPaused = !profile.isPaused
        val newUsed = if (newPaused) profile.pauseDaysUsed + 1 else profile.pauseDaysUsed
        val updated = profile.copy(isPaused = newPaused, pauseDaysUsed = newUsed)
        saveProfile(updated)
        return updated
    }

    fun getAllCompletedTasksFlow(): Flow<List<TaskProgressEntity>> {
        return dao.getAllCompletedProgressFlow()
    }

    suspend fun tryCloudSync(): Boolean {
        val profile = getProfileSync()
        return firebaseSyncManager.syncProfileToFirestore(profile)
    }

    suspend fun fetchCloudLeaderboard(currentUserId: String): List<com.accend.app.model.LeaderboardUser> {
        return firebaseSyncManager.fetchRealLeaderboardUsers(currentUserId)
    }

    suspend fun tryRestoreFromCloudIfEmpty(): Boolean {
        val localProfile = dao.getUserProfile()
        if (localProfile == null || !localProfile.onboardingComplete) {
            val restored = firebaseSyncManager.restoreUserProfileFromFirestore()
            if (restored != null && restored.onboardingComplete) {
                saveProfile(restored)
                return true
            }
        }
        return false
    }
}
