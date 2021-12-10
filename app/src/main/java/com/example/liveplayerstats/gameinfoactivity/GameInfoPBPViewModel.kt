package com.example.liveplayerstats.gameinfoactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveplayerstats.pbp.Play
import com.example.liveplayerstats.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameInfoPBPViewModel @Inject constructor(private val gameInfoRepository: GameInfoRepository): ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<Play>>> =
        MutableLiveData()

    val dataState: LiveData<DataState<List<Play>>>
        get() = _dataState

    fun setStateEvent(GameInfoPBPStateEvent: GameInfoPBPStateEvent, gameId: String) {
        viewModelScope.launch {
            when (GameInfoPBPStateEvent) {

                is GameInfoPBPStateEvent.GetGameInfoPBPEvent -> {
                    gameInfoRepository.getPlays(gameId)
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is GameInfoPBPStateEvent.None -> {

                }
            }
        }
    }

    sealed class GameInfoPBPStateEvent {

        object  GetGameInfoPBPEvent: GameInfoPBPStateEvent()

        object None: GameInfoPBPStateEvent()
    }
}
