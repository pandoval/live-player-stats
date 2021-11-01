package com.example.liveplayerstats.boxscore

data class Period(
    val current: Int,
    val isEndOfPeriod: Boolean,
    val isHalftime: Boolean
)