package com.accend.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accend.app.data.AccendRepository
import com.accend.app.data.TaskProgressEntity
import com.accend.app.model.LeaderboardUser
import com.accend.app.model.Pillar
import com.accend.app.model.ProgressionConfig
import com.accend.app.model.Quote
import com.accend.app.model.QuotesProvider
import com.accend.app.model.TaskItem
import com.accend.app.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME,
    ANALYSIS,
    LEADERBOARD,
    SETTINGS
}

enum class LeaderboardFilterTab {
    ALL_TIME,
    WEEKLY
}

data class CelebrationEvent(
    val newLevel: Int,
    val newTitle: String,
    val isTitleUnlock: Boolean,
    val totalExp: Int
)

class AccendViewModel(
    private val repository: AccendRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = repository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserProfile()
        )

    private val _currentDay = MutableStateFlow(1)
    val currentDay: StateFlow<Int> = _currentDay.asStateFlow()

    private val _activeTab = MutableStateFlow(NavigationTab.HOME)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _selectedPillar = MutableStateFlow<Pillar?>(null)
    val selectedPillar: StateFlow<Pillar?> = _selectedPillar.asStateFlow()

    private val _celebrationEvent = MutableStateFlow<CelebrationEvent?>(null)
    val celebrationEvent: StateFlow<CelebrationEvent?> = _celebrationEvent.asStateFlow()

    private val _leaderboardFilterTab = MutableStateFlow(LeaderboardFilterTab.ALL_TIME)
    val leaderboardFilterTab: StateFlow<LeaderboardFilterTab> = _leaderboardFilterTab.asStateFlow()

    private val _filterFriendsOnly = MutableStateFlow(false)
    val filterFriendsOnly: StateFlow<Boolean> = _filterFriendsOnly.asStateFlow()

    private val _isSyncingCloud = MutableStateFlow(false)
    val isSyncingCloud: StateFlow<Boolean> = _isSyncingCloud.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Real cloud ascendant members fetched from Firestore
    private val _cloudLeaderboardUsers = MutableStateFlow<List<LeaderboardUser>>(emptyList())
    val cloudLeaderboardUsers: StateFlow<List<LeaderboardUser>> = _cloudLeaderboardUsers.asStateFlow()

    // Daily tasks for the currently inspected day and user skill track
    val currentDayTasks: StateFlow<List<TaskItem>> = combine(
        _currentDay,
        userProfile
    ) { day, profile ->
        Pair(day, profile.chosenSkillTrack)
    }.flatMapLatest { (day, skillTrack) ->
        repository.getDayTasksFlow(day, skillTrack)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All completed task entities across time
    val allCompletedTasks: StateFlow<List<TaskProgressEntity>> = repository.getAllCompletedTasksFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentQuote: StateFlow<Quote> = _currentDay.flatMapLatest { day ->
        MutableStateFlow(QuotesProvider.getQuoteForDay(day))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = QuotesProvider.getQuoteForDay(1)
    )

    init {
        // Auto-restore profile from cloud if database is fresh on new APK install
        viewModelScope.launch {
            repository.tryRestoreFromCloudIfEmpty()
        }
        // Current day is strictly locked to the user's sovereign protocol progress
        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile.currentDay in 1..365) {
                    _currentDay.value = profile.currentDay
                }
            }
        }
        refreshCloudLeaderboard()
    }

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
        _selectedPillar.value = null
    }

    fun openPillar(pillar: Pillar) {
        _selectedPillar.value = pillar
    }

    fun closePillar() {
        _selectedPillar.value = null
    }

    fun selectDay(day: Int) {
        // Day is locked to user's real protocol progression
    }

    fun toggleTask(task: TaskItem, notes: String? = null) {
        viewModelScope.launch {
            val result = repository.toggleTaskCompletion(task, notes)
            if (result.leveledUp || result.newTitleUnlocked) {
                _celebrationEvent.value = CelebrationEvent(
                    newLevel = result.newLevel,
                    newTitle = result.newTitle,
                    isTitleUnlock = result.newTitleUnlocked,
                    totalExp = result.newTotalExp
                )
            }
        }
    }

    fun saveNotes(taskId: String, notes: String) {
        viewModelScope.launch {
            repository.saveTaskNotes(taskId, notes)
        }
    }

    fun dismissCelebration() {
        _celebrationEvent.value = null
    }

    fun completeOnboarding(
        displayName: String,
        avatarId: String,
        skillTrackId: String,
        customAvatarUri: String? = null
    ) {
        viewModelScope.launch {
            val profile = userProfile.value.copy(
                userId = "local_user",
                displayName = displayName.ifBlank { "Ascendant" },
                avatarId = avatarId,
                customAvatarUri = customAvatarUri,
                chosenSkillTrack = skillTrackId,
                onboardingComplete = true,
                email = "imthesmith786@gmail.com"
            )
            repository.saveProfile(profile)
        }
    }

    fun togglePause() {
        viewModelScope.launch {
            repository.toggleProgramPause()
        }
    }

    fun updateProfile(
        displayName: String,
        avatarId: String,
        chosenSkillTrack: String,
        customAvatarUri: String? = null
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                userId = "local_user",
                displayName = displayName,
                avatarId = avatarId,
                customAvatarUri = customAvatarUri ?: userProfile.value.customAvatarUri,
                chosenSkillTrack = chosenSkillTrack
            )
            repository.saveProfile(updated)
        }
    }

    fun updateReminders(
        morningEnabled: Boolean,
        morningTime: String,
        eveningEnabled: Boolean,
        eveningTime: String
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                morningReminderEnabled = morningEnabled,
                morningReminderTime = morningTime,
                eveningReminderEnabled = eveningEnabled,
                eveningReminderTime = eveningTime
            )
            repository.saveProfile(updated)
        }
    }

    fun togglePillarMute(pillar: Pillar) {
        viewModelScope.launch {
            val p = userProfile.value
            val updated = when (pillar) {
                Pillar.PHYSICAL -> p.copy(physicalMuted = !p.physicalMuted)
                Pillar.MENTAL -> p.copy(mentalMuted = !p.mentalMuted)
                Pillar.SKILLS -> p.copy(skillsMuted = !p.skillsMuted)
                Pillar.SOCIAL -> p.copy(socialMuted = !p.socialMuted)
            }
            repository.saveProfile(updated)
        }
    }

    fun setLeaderboardFilterTab(tab: LeaderboardFilterTab) {
        _leaderboardFilterTab.value = tab
    }

    fun setFriendsOnly(friendsOnly: Boolean) {
        _filterFriendsOnly.value = friendsOnly
    }

    fun syncToCloud() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            _syncMessage.value = "Synchronizing with ACCEND Cloud..."
            val success = repository.tryCloudSync()
            if (success) {
                val realUsers = repository.fetchCloudLeaderboard(userProfile.value.userId)
                _cloudLeaderboardUsers.value = realUsers
                _syncMessage.value = "Ascension progress secured to cloud."
            } else {
                _syncMessage.value = "Saved locally (offline mode active)."
            }
            _isSyncingCloud.value = false
        }
    }

    fun refreshCloudLeaderboard() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            try {
                repository.tryCloudSync()
                val realUsers = repository.fetchCloudLeaderboard(userProfile.value.userId)
                _cloudLeaderboardUsers.value = realUsers
            } catch (e: Exception) {
                // Non-blocking offline fallback
            } finally {
                _isSyncingCloud.value = false
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    /**
     * Constructs the real leaderboard ranking containing ONLY verified live users from the database.
     * No mock bots or simulated users are used.
     */
    fun getLeaderboard(
        filter: LeaderboardFilterTab,
        friendsOnly: Boolean
    ): List<LeaderboardUser> {
        val currentUser = userProfile.value
        val userXp = currentUser.totalExp
        val userLevel = currentUser.level
        val userTitle = currentUser.title

        val currentObj = LeaderboardUser(
            rank = 1,
            userId = currentUser.userId,
            displayName = currentUser.displayName.ifBlank { "Sovereign" },
            avatarId = currentUser.avatarId,
            customAvatarUri = currentUser.customAvatarUri,
            level = userLevel,
            title = userTitle,
            exp = userXp,
            streak = (currentUser.currentDay.coerceAtLeast(1)),
            isCurrentUser = true,
            isFriend = false
        )

        // Filter out any cloud entry that matches the current user to avoid duplicate entries
        val otherRealUsers = _cloudLeaderboardUsers.value
            .filter { it.userId != currentUser.userId && !it.isCurrentUser && it.displayName.isNotBlank() }

        val combined = (otherRealUsers + currentObj)
            .let { list ->
                if (friendsOnly) list.filter { it.isFriend || it.isCurrentUser } else list
            }
            .sortedWith(
                compareByDescending<LeaderboardUser> { it.exp }
                    .thenBy { if (it.isCurrentUser) 0 else 1 }
            )

        return combined.mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }
    }
}
