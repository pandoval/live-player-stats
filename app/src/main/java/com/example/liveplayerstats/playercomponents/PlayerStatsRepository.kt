package com.example.liveplayerstats.playercomponents

import android.util.Log
import com.example.liveplayerstats.NBAApi
import com.example.liveplayerstats.boxscore.Boxscore
import com.example.liveplayerstats.playerlist.Standard
import com.example.liveplayerstats.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStatsRepository @Inject constructor(private val nbaApi: NBAApi){

    suspend fun getStats(teamIds: List<String>): Flow<DataState<List<Boxscore>>> = flow {

        emit(DataState.Loading)
        try {
            val date = getDate()
            val scoreboard = nbaApi.getScoreboard(date)
            val games = scoreboard.games
            val boxscoreList = ArrayList<Boxscore>()
            for (teamId in teamIds) {
                var gameId = ""
                for (game in games) {
                    if (game.hTeam.teamId == teamId || game.vTeam.teamId == teamId) {
                        gameId = game.gameId
                        break
                    }
                }
                val boxscore = nbaApi.getBoxscore(date, gameId)
                boxscoreList.add(boxscore)
            }
            emit(DataState.Success(boxscoreList.toList()))
        } catch (e: Exception) {
            Log.e("error", e.toString() )
            emit(DataState.Error(e))
        }
    }

    private fun getDate(): String {
        val tz: TimeZone = TimeZone.getTimeZone("America/New_York")
        val calendar = Calendar.getInstance(tz)
        //-3 because some games can go to 2 am
        calendar.add(Calendar.HOUR,-3)
        val year = calendar.get(Calendar.YEAR)
        val month: String = "%02d".format(calendar.get(Calendar.MONTH)+1)
        val day: String = "%02d".format(calendar.get(Calendar.DAY_OF_MONTH))
        return "$year$month$day"
    }
}