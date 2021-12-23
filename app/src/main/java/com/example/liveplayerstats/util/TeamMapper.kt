package com.example.liveplayerstats.util

import android.content.Context
import com.example.liveplayerstats.R

object TeamMapper {

    fun tricodeNameMap(context: Context): Map<String, String> {
        val tricodeArray = context.resources.getStringArray(R.array.nba_tricode)
        val teamNameArray = context.resources.getStringArray(R.array.nba_team_names)
        return tricodeArray.zip(teamNameArray).toMap()
    }
}