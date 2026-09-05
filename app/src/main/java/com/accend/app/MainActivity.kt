package com.accend.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.accend.app.data.AccendRepository
import com.accend.app.data.AppDatabase
import com.accend.app.data.FirebaseSyncManager
import com.accend.app.ui.AccendApp
import com.accend.app.ui.theme.MyApplicationTheme
import com.accend.app.viewmodel.AccendViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var firebaseSyncManager: FirebaseSyncManager
    private lateinit var repository: AccendRepository
    private lateinit var viewModelFactory: AccendViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local persistence & cloud sync infrastructure
        database = AppDatabase.getInstance(applicationContext)
        firebaseSyncManager = FirebaseSyncManager(applicationContext)
        repository = AccendRepository(database.accendDao(), firebaseSyncManager)
        viewModelFactory = AccendViewModelFactory(repository)

        // Attempt silent authentication / cloud connection in background
        lifecycleScope.launch {
            firebaseSyncManager.autoSignInIfAvailable()
        }

        setContent {
            MyApplicationTheme {
                AccendApp(viewModelFactory = viewModelFactory)
            }
        }
    }
}
