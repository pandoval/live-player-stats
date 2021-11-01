package com.example.liveplayerstats.todayscoreboard

import com.google.gson.annotations.SerializedName

data class Game (
    @SerializedName("gameId")
    var gameId: String,

    @SerializedName("vTeam")
    var vTeam: VTeam,

    @SerializedName("hTeam")
    var hTeam: HTeam
)