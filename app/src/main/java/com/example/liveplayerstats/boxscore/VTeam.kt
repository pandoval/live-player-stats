package com.example.liveplayerstats.boxscore

data class VTeam(
    val linescore: List<LinescoreX>,
    val loss: String,
    val score: String,
    val seriesLoss: String,
    val seriesWin: String,
    val teamId: String,
    val triCode: String,
    val win: String
)