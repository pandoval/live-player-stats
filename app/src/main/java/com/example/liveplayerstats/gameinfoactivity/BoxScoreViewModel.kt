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
class BoxScoreViewModel @Inject constructor(private val playerStatsRepository: PlayerStatsRepository): ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<BoxScore>>> =
        MutableLiveData()

    val dataState: LiveData<DataState<List<BoxScore>>>
        get() = _dataState

    fun setStateEvent(boxScoreStateEvent: BoxScoreStateEvent, teamIds: List<String>) {
        viewModelScope.launch {
            when (boxScoreStateEvent) {

                is BoxScoreStateEvent.GetBoxScoreEvent -> {
                    playerStatsRepository.getStats(teamIds)
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is BoxScoreStateEvent.None -> {

                }
            }
        }
    }

    sealed class BoxScoreStateEvent {

        object  GetBoxScoreEvent: BoxScoreStateEvent()

        object None: BoxScoreStateEvent()
    }
}
