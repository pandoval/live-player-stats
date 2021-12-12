package com.example.liveplayerstats.boxscore

data class BasicGameData(
    val arena: Arena,
    val attendance: String,
    val clock: String,
    val endTimeUTC: String,
    val extendedStatusNum: Int,
    val gameDuration: GameDuration,
    val gameId: String,
    val gameUrlCode: String,
    val hTeam: HTeam,
    val hasGameBookPdf: Boolean,
    val homeStartDate: String,
    val homeStartTime: String,
    val isBuzzerBeater: Boolean,
    val isGameActivated: Boolean,
    val isNeutralVenue: Boolean,
    val isPreviewArticleAvail: Boolean,
    val isRecapArticleAvail: Boolean,
    val isStartTimeTBD: Boolean,
    val leagueName: String,
    val nugget: Nugget,
    val officials: Officials,
    val period: Period,
    val seasonStageId: Int,
    val seasonYear: String,
    val startDateEastern: String,
    val startTimeEastern: String,
    val startTimeUTC: String,
    val statusNum: Int,
    val tickets: Tickets,
    val vTeam: VTeam,
    val visitorStartDate: String,
    val visitorStartTime: String,
    val watch: Watch
)