package com.example.liveplayerstats

import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.playerlist.PlayerList
import com.example.liveplayerstats.playerprofile.Player
import com.example.liveplayerstats.teamschedule.TeamSchedule
import com.example.liveplayerstats.todayscoreboard.Scoreboard
import retrofit2.http.GET
import retrofit2.http.Path

interface NBAApi {

    companion object {
        const val SEASON_YEAR = "2021"
        const val BASE_URL = "https://data.nba.net/prod/"
    }

    @GET("v1/$SEASON_YEAR/players/{playerID}_profile.json")
    suspend fun getPlayerProfile(@Path("playerID") playerID: String): Player

    @GET("v1/{date}/scoreboard.json")
    suspend fun getScoreboard(@Path("date") date: String): Scoreboard

    @GET("v1/{date}/{gameId}_boxscore.json")
    suspend fun getBoxscore(@Path("date") date: String, @Path("gameId") gameId: String): BoxScore

    //CHANGE 2020 TO PLAYING YEAR
    @GET("v1/$SEASON_YEAR/players.json")
    suspend fun getPlayerList(): PlayerList

    @GET("v1/${SEASON_YEAR}/teams/{teamId}/schedule.json")
    suspend fun getTeamSchedule(@Path("teamId") teamId: String): TeamSchedule
}