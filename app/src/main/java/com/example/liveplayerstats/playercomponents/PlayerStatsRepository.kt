package com.example.liveplayerstats.playercomponents

import android.util.Log
import com.example.liveplayerstats.NBAApi
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerStatsRepository @Inject constructor(private val nbaApi: NBAApi){

    suspend fun getStats(teamIds: List<String>): Flow<DataState<List<BoxScore>>> = channelFlow {

        send(DataState.Loading)
        try {
            val date = getDate()
            val scoreboard = nbaApi.getScoreboard(date)
            val games = scoreboard.games
            val boxscoreList = ArrayList<BoxScore>(teamIds.size)
            if (teamIds.isEmpty()) {
                send(DataState.Success(boxscoreList.toList()))
            }
            for (i in teamIds.indices) {
                val teamId = teamIds[i]
                var gameId = ""
                for (game in games) {
                    if (game.hTeam.teamId == teamId || game.vTeam.teamId == teamId) {
                        gameId = game.gameId
                        break
                    }
                }

                try {
                    val boxscore = nbaApi.getBoxscore(date, gameId)
                    boxscoreList.add(i, boxscore)
                } catch (e: Exception) {
                    val teamSchedule = nbaApi.getTeamSchedule(teamId)
                    val latestIndex = teamSchedule.league.lastStandardGamePlayedIndex
                    val gameList = teamSchedule.league.standard
                    //+1 because trying to return next game
                    val nextGameId = gameList[latestIndex+1].gameId
                    val nextDate = gameList[latestIndex+1].startDateEastern
                    val nextBoxscore = nbaApi.getBoxscore(nextDate, nextGameId)
                    boxscoreList.add(i, nextBoxscore)
                }

            }
            send(DataState.Success(boxscoreList.toList()))
        } catch (e: Exception) {
            Log.e("error", e.toString())
            send(DataState.Error(e))
        }
    }

    companion object {
        fun getDate(): String {
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
}