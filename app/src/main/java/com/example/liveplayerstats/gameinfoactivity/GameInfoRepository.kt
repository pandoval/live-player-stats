package com.example.liveplayerstats.gameinfoactivity

import android.util.Log
import com.example.liveplayerstats.NBAApi
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.pbp.Play
import com.example.liveplayerstats.playercomponents.PlayerStatsRepository
import com.example.liveplayerstats.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameInfoRepository @Inject constructor(private val nbaApi: NBAApi){

    suspend fun getPlays(gameId: String): Flow<DataState<List<Play>>> = flow {

        emit(DataState.Loading)
        try {
            val date = PlayerStatsRepository.getDate()
            val plays = mutableListOf<Play>()
            for (i in 1..11) {
                Log.e("gameinforepository", "$date, $gameId, ${i.toString()}")
                val quarterPlays = nbaApi.getPBP(date, gameId = gameId, i.toString()).plays
                if (quarterPlays.isNotEmpty()) {
                    for (p in quarterPlays) {
                        plays.add(0, p)
                    }
                } else {
                    break
                }
            }
            emit(DataState.Success(plays))
        } catch (e: Exception) {
            if (e is HttpException) {
                emit(DataState.Success(listOf()))
            }
            Log.e("gameinforepository", e.toString())
            emit(DataState.Error(e))
        }
    }
}
