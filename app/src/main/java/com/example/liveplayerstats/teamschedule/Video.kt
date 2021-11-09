package com.example.liveplayerstats.teamschedule

data class Video(
    val canPurchase: Boolean,
    val deepLink: List<DeepLink>,
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