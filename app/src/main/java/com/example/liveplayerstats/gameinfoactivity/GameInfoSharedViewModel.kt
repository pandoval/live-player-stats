package com.example.liveplayerstats.gameinfoactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.liveplayerstats.boxscore.BoxScore

class GameInfoSharedViewModel: ViewModel() {

    private val mutableBoxScore = MutableLiveData<BoxScore>()
    val boxScore: LiveData<BoxScore> get() = mutableBoxScore

    fun setBoxScore(boxScore: BoxScore) {
        mutableBoxScore.value = boxScore
    }
}