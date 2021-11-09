package com.example.liveplayerstats.teamschedule

data class Vega(
    val extendedStatusNum: Int,
    val gameId: String,
    val gameUrlCode: String,
    val hTeam: HTeamXXX,
    val homeStartDate: String,
    val homeStartTime: String,
    val isHomeTeam: Boolean,
    val isStartTimeTBD: Boolean,
    val nugget: NuggetX,
    val seasonId: String,
    val seasonStageId: Int,
    val startDateEastern: String,
    val startTimeEastern: String,
    val startTimeUTC: String,
    val statusNum: Int,
    val tags: List<String>,
    val vTeam: VTeamXXX,
    val visitorStartDate: String,
    val visitorStartTime: String,
    val watch: WatchX
)