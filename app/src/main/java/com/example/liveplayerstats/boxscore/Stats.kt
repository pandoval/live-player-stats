package com.example.liveplayerstats.boxscore

data class Stats(
    val activePlayers: List<ActivePlayer>,
    val hTeam: HTeamXXX,
    val leadChanges: String,
    val timesTied: String,
    val vTeam: VTeamXXX
)