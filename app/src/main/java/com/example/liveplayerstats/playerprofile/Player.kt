package com.example.liveplayerstats.playerprofile

import com.google.gson.annotations.SerializedName

data class Player (
    @SerializedName("league")
    var league: League
)
