package com.accend.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.accend.app.model.Pillar
import com.accend.app.ui.components.AccendBottomBar
import com.accend.app.ui.components.LevelUpCelebrationDialog
import com.accend.app.ui.screens.AnalysisScreen
import com.accend.app.ui.screens.CinematicSplashScreen
import com.accend.app.ui.screens.HomeScreen
import com.accend.app.ui.screens.IdentitySetupScreen
import com.accend.app.ui.screens.LeaderboardScreen
import com.accend.app.ui.screens.PillarTaskScreen
import com.accend.app.ui.screens.SettingsScreen
import com.accend.app.ui.theme.ObsidianBg
import com.accend.app.viewmodel.AccendViewModel
import com.accend.app.viewmodel.AccendViewModelFactory
import com.accend.app.viewmodel.NavigationTab

@Composable
fun AccendApp(
    viewModelFactory: AccendViewModelFactory,
    modifier: Modifier = Modifier
) {
    var showSplash by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        AccendAppContent(viewModelFactory = viewModelFactory)

        AnimatedVisibility(
            visible = showSplash,
            exit = fadeOut(animationSpec = tween(900, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 1.08f, animationSpec = tween(900, easing = FastOutSlowInEasing)),
            label = "splash_overlay"
        ) {
            CinematicSplashScreen(
                onDismiss = { showSplash = false }
            )
        }
    }
}

@Composable
private fun AccendAppContent(
    viewModelFactory: AccendViewModelFactory,
    modifier: Modifier = Modifier
) {
    val viewModel: AccendViewModel = viewModel(factory = viewModelFactory)

    val userProfile by viewModel.userProfile.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedPillar by viewModel.selectedPillar.collectAsState()
    val celebrationEvent by viewModel.celebrationEvent.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val dayTasks by viewModel.currentDayTasks.collectAsState()
    val allCompletedTasks by viewModel.allCompletedTasks.collectAsState()
    val isSyncing by viewModel.isSyncingCloud.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val leaderboardFilterTab by viewModel.leaderboardFilterTab.collectAsState()
    val filterFriendsOnly by viewModel.filterFriendsOnly.collectAsState()

    val leaderboardUsers = viewModel.getLeaderboard(leaderboardFilterTab, filterFriendsOnly)

    // Check if onboarding is complete
    if (!userProfile.onboardingComplete) {
        IdentitySetupScreen(
            onComplete = { name, avatar, skillTrack, customAvatarUri ->
                viewModel.completeOnboarding(name, avatar, skillTrack, customAvatarUri)
            },
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = ObsidianBg,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                if (selectedPillar == null) {
                    AccendBottomBar(
                        currentTab = activeTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ObsidianBg)
            ) {
                // Smooth slide transitions for feature opening / back navigation
                AnimatedContent(
                    targetState = selectedPillar,
                    transitionSpec = {
                        if (targetState != null) {
                            // Opening feature: Silky smooth slide in from right with subtle scaling
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> (fullWidth * 0.35f).toInt() },
                                animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(360, easing = FastOutSlowInEasing)) +
                             scaleIn(initialScale = 0.96f, animationSpec = tween(360, easing = FastOutSlowInEasing))
                            ).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() },
                                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing))
                            )
                        } else {
                            // Returning from feature: Silky smooth slide out to right
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> -(fullWidth * 0.15f).toInt() },
                                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing))
                            ).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> (fullWidth * 0.35f).toInt() },
                                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                                scaleOut(targetScale = 0.96f, animationSpec = tween(280, easing = FastOutSlowInEasing))
                            )
                        }
                    },
                    label = "pillar_feature_slide_transition"
                ) { currentPillar ->
                    if (currentPillar != null) {
                        val pillarTasks = dayTasks.filter { it.pillar == currentPillar }
                        PillarTaskScreen(
                            pillar = currentPillar,
                            currentDay = currentDay,
                            tasks = pillarTasks,
                            onBack = { viewModel.closePillar() },
                            onToggleTask = { task, notes -> viewModel.toggleTask(task, notes) },
                            onSaveNotes = { taskId, notes -> viewModel.saveNotes(taskId, notes) }
                        )
                    } else {
                        AnimatedContent(
                            targetState = activeTab,
                            transitionSpec = {
                                val forward = targetState.ordinal > initialState.ordinal
                                val initialOffset = if (forward) { width: Int -> (width * 0.25f).toInt() } else { width: Int -> -(width * 0.25f).toInt() }
                                val targetOffset = if (forward) { width: Int -> -(width * 0.25f).toInt() } else { width: Int -> (width * 0.25f).toInt() }

                                (slideInHorizontally(
                                    initialOffsetX = initialOffset,
                                    animationSpec = tween(340, easing = FastOutSlowInEasing)
                                ) + fadeIn(animationSpec = tween(340, easing = FastOutSlowInEasing))
                                ).togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = targetOffset,
                                        animationSpec = tween(280, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(220, easing = FastOutSlowInEasing))
                                )
                            },
                            label = "tab_smooth_slide_transition"
                        ) { tab ->
                            when (tab) {
                                NavigationTab.HOME -> {
                                    HomeScreen(
                                        userProfile = userProfile,
                                        currentDay = currentDay,
                                        quote = currentQuote,
                                        dayTasks = dayTasks,
                                        isSyncing = isSyncing,
                                        onOpenPillar = { pillar -> viewModel.openPillar(pillar) },
                                        onSyncClick = { viewModel.syncToCloud() },
                                        onAvatarClick = { viewModel.selectTab(NavigationTab.SETTINGS) }
                                    )
                                }
                                NavigationTab.ANALYSIS -> {
                                    AnalysisScreen(
                                        userProfile = userProfile,
                                        currentDay = currentDay,
                                        completedTasks = allCompletedTasks
                                    )
                                }
                                NavigationTab.LEADERBOARD -> {
                                    LeaderboardScreen(
                                        leaderboardUsers = leaderboardUsers,
                                        selectedTab = leaderboardFilterTab,
                                        friendsOnly = filterFriendsOnly,
                                        isSyncing = isSyncing,
                                        onTabSelected = { filter -> viewModel.setLeaderboardFilterTab(filter) },
                                        onToggleFriendsOnly = { viewModel.setFriendsOnly(!filterFriendsOnly) },
                                        onRefresh = { viewModel.refreshCloudLeaderboard() }
                                    )
                                }
                                NavigationTab.SETTINGS -> {
                                    SettingsScreen(
                                        userProfile = userProfile,
                                        isSyncing = isSyncing,
                                        syncMessage = syncMessage,
                                        onUpdateProfile = { name, avatar, skillTrack, customAvatarUri ->
                                            viewModel.updateProfile(name, avatar, skillTrack, customAvatarUri)
                                        },
                                        onTogglePause = { viewModel.togglePause() },
                                        onTogglePillarMute = { pillar -> viewModel.togglePillarMute(pillar) },
                                        onUpdateReminders = { mEnabled, mTime, eEnabled, eTime ->
                                            viewModel.updateReminders(mEnabled, mTime, eEnabled, eTime)
                                        },
                                        onSyncWithCloud = { viewModel.syncToCloud() }
                                    )
                                }
                            }
                        }
                    }
                }

                // Level-Up / Title celebration overlay
                celebrationEvent?.let { event ->
                    LevelUpCelebrationDialog(
                        event = event,
                        onDismiss = { viewModel.dismissCelebration() }
                    )
                }
            }
        }
    }
}
