package com.example.liveplayerstats.newplayercomponents

import androidx.lifecycle.*
import com.example.liveplayerstats.playerlist.Standard
import com.example.liveplayerstats.util.DataState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPlayerViewModel @Inject constructor(
    private val repository: NewPlayerRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<Standard>>> =
        MutableLiveData()

    val dataState: LiveData<DataState<List<Standard>>>
        get() = _dataState

    fun setStateEvent(newPlayerStateEvent: NewPlayerStateEvent) {
        viewModelScope.launch {
            when (newPlayerStateEvent) {

                is NewPlayerStateEvent.GetPlayersEvents -> {
                    repository.getPlayers()
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is NewPlayerStateEvent.None -> {

                }
            }
        }
    }

}

sealed class NewPlayerStateEvent {

    object  GetPlayersEvents: NewPlayerStateEvent()

    object None: NewPlayerStateEvent()
}