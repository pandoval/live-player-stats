package com.example.liveplayerstats.gameinfoactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.pbp.Play

class GameInfoSharedViewModel: ViewModel() {

    private val mutableBoxScore = MutableLiveData<BoxScore>()
    val boxScore: LiveData<BoxScore> get() = mutableBoxScore

    private val mutablePlays = MutableLiveData<List<Play>>()
    val plays: LiveData<List<Play>> get() = mutablePlays

    fun setBoxScore(boxScore: BoxScore) {
        mutableBoxScore.value = boxScore
    }

    fun setPlays(plays: List<Play>) {
        mutablePlays.value = plays
    }
}