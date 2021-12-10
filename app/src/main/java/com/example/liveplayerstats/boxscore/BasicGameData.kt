package com.example.liveplayerstats.boxscore

data class BasicGameData(
        val clock: String,
        val hTeam: HTeamBS,
        val period: Period,
        val statusNum: Int,
        val vTeam: VTeamBS,
        val startTimeEastern: String,
        val startDateEastern: String,
        val gameId: String
)