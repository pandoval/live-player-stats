package com.example.liveplayerstats.teamschedule

data class Standard(
    val extendedStatusNum: Int,
    val gameId: String,
    val gameUrlCode: String,
    val hTeam: HTeam,
    val homeStartDate: String,
    val homeStartTime: String,
    val isHomeTeam: Boolean,
    val isStartTimeTBD: Boolean,
    val nugget: Nugget,
    val seasonId: String,
    val seasonStageId: Int,
    val startDateEastern: String,
    val startTimeEastern: String,
    val startTimeUTC: String,
    val statusNum: Int,
    val vTeam: VTeam,
    val visitorStartDate: String,
    val visitorStartTime: String,
    val watch: Watch
)