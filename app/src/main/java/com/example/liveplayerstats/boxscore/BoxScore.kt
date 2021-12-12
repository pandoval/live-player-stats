package com.example.liveplayerstats.boxscore

data class BoxScore(
    val _internal: Internal,
    val basicGameData: BasicGameData,
    val previousMatchup: PreviousMatchup,
    val stats: Stats
)