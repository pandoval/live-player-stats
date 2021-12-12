package com.example.liveplayerstats.boxscore

data class Video(
    val canPurchase: Boolean,
    val deepLink: List<Any>,
    val isLeaguePass: Boolean,
    val isMagicLeap: Boolean,
    val isNBAOnTNTVR: Boolean,
    val isNationalBlackout: Boolean,
    val isNextVR: Boolean,
    val isOculusVenues: Boolean,
    val isTNTOT: Boolean,
    val isVR: Boolean,
    val regionalBlackoutCodes: String,
    val streams: List<StreamXXX>,
    val tntotIsOnAir: Boolean
)