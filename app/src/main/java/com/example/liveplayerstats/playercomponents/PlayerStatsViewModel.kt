package com.example.liveplayerstats.playercomponents

import androidx.lifecycle.*
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerStatsViewModel @Inject constructor(private val playerStatsRepository: PlayerStatsRepository): ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<BoxScore>>> =
        MutableLiveData()

    val dataState: LiveData<DataState<List<BoxScore>>>
        get() = _dataState

    fun setStateEvent(playerStatsStateEvent: PlayerStatsStateEvent, teamIds: List<String>) {
        viewModelScope.launch {
            when (playerStatsStateEvent) {

                is PlayerStatsStateEvent.GetPlayerStatsEvent -> {
                    playerStatsRepository.getStats(teamIds)
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is PlayerStatsStateEvent.None -> {

                }
            }
        }
    }

    sealed class PlayerStatsStateEvent {

        object  GetPlayerStatsEvent: PlayerStatsStateEvent()

        object None: PlayerStatsStateEvent()
    }
}

