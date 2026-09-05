package com.accend.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.accend.app.data.AchievementEntity
import com.accend.app.data.DayProgressEntity
import com.accend.app.data.QuoteEntity
import com.accend.app.data.UserEntity
import com.accend.app.domain.Curriculum
import com.accend.app.domain.CurriculumTask
import com.accend.app.domain.Progression
import com.accend.app.domain.SkillTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val Ink = Color(0xFF0A0A0A)
private val SurfaceDark = Color(0xFF101010)
private val Gold = Color(0xFF9C7C24)
private val GoldMuted = Color(0xFF8A6D1E)
private val TextMuted = Color(0xFF9E9A8D)

class AccendViewModel(application: AccendApplication) : AndroidViewModel(application) {
    private val dao = application.database.accendDao()
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user = _user.asStateFlow()
    private val _completed = MutableStateFlow<Set<String>>(emptySet())
    val completed = _completed.asStateFlow()
    private val _allProgress = MutableStateFlow<List<DayProgressEntity>>(emptyList())
    val allProgress = _allProgress.asStateFlow()
    private val _levelUp = MutableStateFlow<Int?>(null)
    val levelUp = _levelUp.asStateFlow()

    init {
        viewModelScope.launch { dao.observeUser().collect { _user.value = it } }
        viewModelScope.launch { dao.observeAllProgress().collect { progress -> _allProgress.value = progress; _completed.value = progress.filter { it.completed }.map { it.subTaskId }.toSet() } }
        viewModelScope.launch {
            dao.upsertQuotes(listOf(QuoteEntity(1, "The secret of getting ahead is getting started.", "Mark Twain", "beginning"), QuoteEntity(2, "We are what we repeatedly do.", "Will Durant", "practice"), QuoteEntity(3, "Small disciplines repeated with consistency lead to great achievements.", "John Maxwell", "consistency")))
            dao.upsertAchievements(listOf(AchievementEntity("first-task", "First step", "Complete your first task.", "01"), AchievementEntity("week-one", "Seven days", "Complete a full week.", "07"), AchievementEntity("level-ten", "Awakened", "Reach level 10.", "10")))
        }
    }

    fun saveProfile(name: String, track: String) {
        viewModelScope.launch {
            dao.upsertUser(UserEntity(displayName = name.trim().uppercase(), chosenSkillTrack = track, onboardingComplete = true))
        }
    }

    fun completeTask(pillar: String, task: CurriculumTask) {
        if (task.id in _completed.value) return
        _completed.value += task.id
        viewModelScope.launch {
            val current = _user.value ?: return@launch
            val previousLevel = Progression.levelFor(current.totalXp).level
            val updatedXp = current.totalXp + task.xp
            val completedAfterTask = _completed.value
            val dayTasks = Curriculum.tasksForDay(current.currentDay, SkillTrack.entries.firstOrNull { it.label == current.chosenSkillTrack } ?: SkillTrack.CODING)
            val dayFinished = dayTasks.all { it.id in completedAfterTask }
            dao.upsertUser(current.copy(totalXp = updatedXp, currentDay = if (dayFinished) (current.currentDay + 1).coerceAtMost(365) else current.currentDay))
            dao.upsertProgress(DayProgressEntity("local-user", current.currentDay, pillar, task.id, true, System.currentTimeMillis()))
            if (Progression.levelFor(updatedXp).level > previousLevel) _levelUp.value = Progression.levelFor(updatedXp).level
        }
    }

    fun dismissLevelUp() { _levelUp.value = null }
}

private val pillars = listOf(
    "physical" to "Physical",
    "mental" to "Mental",
    "skills" to "Skills",
    "social" to "Social"
)

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AccendViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AccendTheme { AccendApp(viewModel) } }
    }
}

@Composable
private fun AccendApp(viewModel: AccendViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val levelUp by viewModel.levelUp.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val start = if (user?.onboardingComplete == true) "home" else "onboarding"
    Box(Modifier.fillMaxSize()) {
        NavHost(navController, startDestination = start) {
            composable("onboarding") { OnboardingScreen { name, track -> viewModel.saveProfile(name, track); navController.navigate("home") { popUpTo("onboarding") { inclusive = true } } } }
            composable("home") { HomeScreen(user ?: UserEntity(onboardingComplete = true), navController) }
            composable("pillar/{id}") { entry -> PillarScreen(entry.arguments?.getString("id") ?: "physical", viewModel, navController) }
            composable("analysis") { AnalysisScreen(user ?: UserEntity(onboardingComplete = true), viewModel, navController) }
            composable("leaderboard") { LeaderboardScreen(user ?: UserEntity(onboardingComplete = true), navController) }
            composable("settings") { SettingsScreen(user ?: UserEntity(onboardingComplete = true), navController) }
        }
        if (levelUp != null) LevelUpOverlay(levelUp ?: 1) { viewModel.dismissLevelUp() }
    }
}

@Composable
private fun OnboardingScreen(onContinue: (String, String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var track by rememberSaveable { mutableStateOf(SkillTrack.CODING.label) }
    Column(Modifier.fillMaxSize().background(Ink).padding(horizontal = 28.dp), verticalArrangement = Arrangement.Center) {
        Text("ACCEND", color = Gold, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        Spacer(Modifier.height(18.dp))
        Text("Become the person\nyou keep promising.", color = Color.White, fontSize = 38.sp, lineHeight = 43.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(14.dp))
        Text("A quiet daily system for strength, clarity, skill, and connection.", color = TextMuted, fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(58.dp))
        Text("YOUR NAME", color = GoldMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        TextField(value = name, onValueChange = { name = it }, singleLine = true, placeholder = { Text("Enter your name", color = TextMuted) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Gold, unfocusedIndicatorColor = GoldMuted))
        Spacer(Modifier.height(20.dp)); Text("CHOOSE YOUR SKILL TRACK", color = GoldMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp)); LazyColumn(Modifier.height(110.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { items(SkillTrack.entries.toList()) { option -> TrackChip(option.label, option.label == track) { track = option.label } } }
        Spacer(Modifier.height(20.dp)); GoldButton("Begin day one", enabled = name.isNotBlank()) { onContinue(name, track) }
    }
}

@Composable
private fun HomeScreen(user: UserEntity, navController: NavHostController) {
    val level = Progression.levelFor(user.totalXp)
    Scaffold(containerColor = Ink, bottomBar = { BottomBar(navController, "home") }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item { Spacer(Modifier.height(12.dp)); Header(user.displayName, "DAY ${user.currentDay}") }
            item { ProgressCard(level.level, level.title, user.totalXp, level.progress) }
            item { QuoteCard() }
            item { Text("TODAY'S PILLARS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) }
            items(pillars) { (id, name) -> PillarRow(id, name, navController) }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun PillarScreen(id: String, viewModel: AccendViewModel, navController: NavHostController) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val completed by viewModel.completed.collectAsStateWithLifecycle()
    val tasks = Curriculum.tasksForDay(user?.currentDay ?: 1, SkillTrack.entries.firstOrNull { it.label == user?.chosenSkillTrack } ?: SkillTrack.CODING).filter { it.pillarId == id }
    val name = pillars.firstOrNull { it.first == id }?.second ?: "Pillar"
    Scaffold(containerColor = Ink, bottomBar = { BottomBar(navController, "home") }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(18.dp)); BackHeader(name, navController)
            Spacer(Modifier.height(22.dp))
            val done = tasks.count { it.id in completed }
            Text("${done}/${tasks.size} COMPLETE", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp)); ProgressBar(done.toFloat() / tasks.size)
            Spacer(Modifier.height(28.dp))
            tasks.forEach { task -> TaskRow(task, task.id in completed) { viewModel.completeTask(id, task) } }
        }
    }
}

@Composable
private fun AnalysisScreen(user: UserEntity, viewModel: AccendViewModel, navController: NavHostController) {
    val progress by viewModel.allProgress.collectAsStateWithLifecycle()
    val level = Progression.levelFor(user.totalXp)
    Scaffold(containerColor = Ink, bottomBar = { BottomBar(navController, "analysis") }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Header("ANALYSIS", "DAY ${user.currentDay}"); Spacer(Modifier.height(22.dp))
            Text("${user.totalXp} XP", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("Level ${level.level} ${level.title}", color = Gold, fontFamily = FontFamily.Serif, fontSize = 18.sp)
            Spacer(Modifier.height(30.dp)); Text("THE LAST 7 DAYS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { (1..7).forEach { day -> val count = progress.count { it.dayNumber == day && it.completed }; Box(Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(4.dp)).background(if (count >= 6) Gold else if (count > 0) Color(0xFF66521B) else Color(0xFF332A13))) } }
            Spacer(Modifier.height(30.dp)); Text("WEAK LINK", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(10.dp)); Text("Your consistency is the next level.", color = Color.White, fontSize = 20.sp, fontFamily = FontFamily.Serif)
        }
    }
}

@Composable
private fun LeaderboardScreen(user: UserEntity, navController: NavHostController) {
    Scaffold(containerColor = Ink, bottomBar = { BottomBar(navController, "leaderboard") }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Header("LEADERBOARD", "WEEKLY"); Spacer(Modifier.height(22.dp))
            listOf("Maya Chen" to "2,480 XP", "Jordan Ellis" to "2,210 XP", user.displayName to "${user.totalXp} XP").forEachIndexed { index, row ->
                Row(Modifier.fillMaxWidth().padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) { Text("${index + 1}", color = Gold, modifier = Modifier.width(32.dp)); Box(Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF2A2415)), contentAlignment = Alignment.Center) { Text(row.first.take(1), color = Gold, fontWeight = FontWeight.Bold) }; Spacer(Modifier.width(14.dp)); Text(row.first, color = Color.White, modifier = Modifier.weight(1f)); Text(row.second, color = TextMuted) }
                HorizontalDivider(color = Color(0xFF29251B))
            }
        }
    }
}

@Composable
private fun SettingsScreen(user: UserEntity, navController: NavHostController) {
    Scaffold(containerColor = Ink, bottomBar = { BottomBar(navController, "settings") }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) { Header("SETTINGS", "ACCOUNT"); Spacer(Modifier.height(24.dp)); Text(user.displayName, color = Color.White, fontSize = 27.sp, fontFamily = FontFamily.Serif); Text("Joined ${user.joinDate}", color = TextMuted); Spacer(Modifier.height(32.dp)); listOf("Notifications", "Skill track: ${user.chosenSkillTrack}", "Pause program", "Support / About").forEach { SettingRow(it) } }
    }
}

@Composable private fun Header(title: String, eyebrow: String) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(eyebrow, color = GoldMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp); Text(title, color = Color.White, fontSize = 25.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold) }; Text("365", color = Gold, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) } }
@Composable private fun BackHeader(title: String, nav: NavHostController) { Row(verticalAlignment = Alignment.CenterVertically) { Text("‹", color = Gold, fontSize = 36.sp, modifier = Modifier.clickable { nav.popBackStack() }); Spacer(Modifier.width(10.dp)); Text(title.uppercase(), color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Serif) } }
@Composable private fun ProgressCard(level: Int, title: String, xp: Int, progress: Float) { Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF211B0B), SurfaceDark))).padding(20.dp)) { Row(verticalAlignment = Alignment.Bottom) { Text("LV.$level", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.width(10.dp)); Text(title, color = Gold, fontFamily = FontFamily.Serif, fontSize = 18.sp); Spacer(Modifier.weight(1f)); Text("$xp XP", color = TextMuted, fontSize = 12.sp) }; Spacer(Modifier.height(16.dp)); ProgressBar(progress); Spacer(Modifier.height(8.dp)); Text("${(progress * 100).toInt()} XP to next level", color = TextMuted, fontSize = 12.sp) } }
@Composable private fun ProgressBar(progress: Float) { Box(Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(Color(0xFF2B2516))) { Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(7.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Color(0xFF4A3908), Gold)))) } }
@Composable private fun QuoteCard() { Column(Modifier.fillMaxWidth().border(1.dp, Color(0x555F4A17), RoundedCornerShape(8.dp)).padding(18.dp)) { Text("“The secret of getting ahead is getting started.”", color = Color.White, fontFamily = FontFamily.Serif, fontSize = 19.sp, lineHeight = 27.sp); Spacer(Modifier.height(8.dp)); Text("MARK TWAIN  /  DAY ONE", color = GoldMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) } }
@Composable private fun PillarRow(id: String, name: String, nav: NavHostController) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(SurfaceDark).clickable { nav.navigate("pillar/$id") }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(38.dp).clip(CircleShape).background(Color(0xFF2B2516)), contentAlignment = Alignment.Center) { Text(name.take(1), color = Gold, fontWeight = FontWeight.Bold) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(name, color = Color.White, fontWeight = FontWeight.SemiBold); Text("Ready when you are", color = TextMuted, fontSize = 12.sp) }; Text("›", color = Gold, fontSize = 26.sp) } }
@Composable private fun TaskRow(task: CurriculumTask, done: Boolean, onToggle: () -> Unit) { Row(Modifier.fillMaxWidth().padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = done, onCheckedChange = { onToggle() }); Spacer(Modifier.width(8.dp)); Column(Modifier.weight(1f)) { Text(task.name, color = if (done) TextMuted else Color.White, fontWeight = FontWeight.SemiBold); Text(task.description, color = TextMuted, fontSize = 13.sp, lineHeight = 19.sp) }; Text("+${task.xp}", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
@Composable private fun TrackChip(label: String, selected: Boolean, onClick: () -> Unit) { Text(label, color = if (selected) Ink else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (selected) Gold else SurfaceDark).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 7.dp)) }
@Composable private fun LevelUpOverlay(level: Int, onDismiss: () -> Unit) { Box(Modifier.fillMaxSize().background(Color(0xEE0A0A0A)).clickable { onDismiss() }, contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("LEVEL UP", color = GoldMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp); Spacer(Modifier.height(12.dp)); Text("$level", color = Gold, fontSize = 88.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold); Text(Progression.levelFor(level * 100).title, color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Serif); Spacer(Modifier.height(22.dp)); Text("TAP TO CONTINUE", color = TextMuted, fontSize = 11.sp, letterSpacing = 2.sp) } } }
@Composable private fun SettingRow(label: String) { Row(Modifier.fillMaxWidth().padding(vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) { Text(label, color = Color.White, modifier = Modifier.weight(1f)); Text("›", color = Gold, fontSize = 25.sp) }; HorizontalDivider(color = Color(0xFF29251B)) }
@Composable private fun GoldButton(label: String, enabled: Boolean, onClick: () -> Unit) { Button(onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(5.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink, disabledContainerColor = Color(0xFF2B2516), disabledContentColor = TextMuted)) { Text(label.uppercase(), fontWeight = FontWeight.Bold, letterSpacing = 1.sp) } }
@Composable private fun BottomBar(nav: NavHostController, current: String) { val items = listOf("home" to "Home", "analysis" to "Analysis", "leaderboard" to "Ranks", "settings" to "Settings"); NavigationBar(containerColor = Ink) { items.forEach { (route, label) -> NavigationBarItem(selected = route == current, onClick = { nav.navigate(route) { launchSingleTop = true } }, icon = { Text(label.take(1), fontWeight = FontWeight.Bold) }, label = { Text(label, fontSize = 10.sp) }) } } }
@Composable private fun AccendTheme(content: @Composable () -> Unit) { MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme(primary = Gold, background = Ink, surface = SurfaceDark, onPrimary = Ink, onBackground = Color.White, onSurface = Color.White), content = content) }