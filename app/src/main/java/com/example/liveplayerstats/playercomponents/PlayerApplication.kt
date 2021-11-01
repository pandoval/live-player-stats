package com.example.liveplayerstats.playercomponents

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class PlayerApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { PlayerDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { PlayerRepository(database.playerDao()) }
}