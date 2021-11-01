package com.example.liveplayerstats.todayscoreboard

import com.google.gson.annotations.SerializedName

data class Scoreboard (
    @SerializedName("games")
    var games: List<Game>
)

