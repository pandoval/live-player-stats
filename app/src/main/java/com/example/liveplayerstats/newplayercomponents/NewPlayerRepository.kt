package com.example.liveplayerstats.newplayercomponents

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.liveplayerstats.NBAApi
import com.example.liveplayerstats.R
import com.example.liveplayerstats.playercomponents.PlayerApplication
import com.example.liveplayerstats.playerlist.Standard
import com.example.liveplayerstats.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewPlayerRepository @Inject constructor(
    private val nbaApi: NBAApi) {

    suspend fun getPlayers(): Flow<DataState<List<Standard>>> = flow {
        emit(DataState.Loading)
        try {
            val playerList = nbaApi.getPlayerList().league.standard
            emit(DataState.Success(playerList))
        } catch (e: Exception) {
            Log.e("error", e.toString() )
            emit(DataState.Error(e))
        }
    }

}