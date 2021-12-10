package com.example.liveplayerstats.pbp

data class Play(
    val clock: String,
    val description: String,
    val eventMsgType: String,
    val formatted: Formatted,
    val hTeamScore: String,
    val isScoreChange: Boolean,
    val isVideoAvailable: Boolean,
    val personId: String,
    val teamId: String,
    val vTeamScore: String
)