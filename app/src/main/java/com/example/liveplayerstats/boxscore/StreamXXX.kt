package com.example.liveplayerstats.boxscore

data class StreamXXX(
    val doesArchiveExist: Boolean,
    val duration: Int,
    val isArchiveAvailToWatch: Boolean,
    val isOnAir: Boolean,
    val streamId: String,
    val streamType: String
)