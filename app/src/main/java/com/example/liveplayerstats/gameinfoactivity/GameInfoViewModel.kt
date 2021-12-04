package com.example.liveplayerstats.gameinfoactivity

import androidx.lifecycle.*
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.playercomponents.PlayerStatsRepository
import com.example.liveplayerstats.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameInfoViewModel @Inject constructor(private val playerStatsRepository: PlayerStatsRepository): ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<BoxScore>>> =
        MutableLiveData()

    val dataState: LiveData<DataState<List<BoxScore>>>
        get() = _dataState

    fun setStateEvent(GameInfoStateEvent: GameInfoStateEvent, teamIds: List<String>) {
        viewModelScope.launch {
            when (GameInfoStateEvent) {

                is GameInfoStateEvent.GetGameInfoEvent -> {
                    playerStatsRepository.getStats(teamIds)
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is GameInfoStateEvent.None -> {

                }
            }
        }
    }

    sealed class GameInfoStateEvent {

        object  GetGameInfoEvent: GameInfoStateEvent()

        object None: GameInfoStateEvent()
    }
}
