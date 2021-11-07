package com.example.liveplayerstats.playercomponents

import androidx.lifecycle.*
import com.example.liveplayerstats.boxscore.Boxscore
import com.example.liveplayerstats.newplayercomponents.NewPlayerStateEvent
import com.example.liveplayerstats.playerlist.Standard
import com.example.liveplayerstats.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val playerRepository: PlayerRepository,
    private val playerStatsRepository: PlayerStatsRepository): ViewModel() {

    val allPlayers: LiveData<List<Player>> = playerRepository.allPlayers.asLiveData()


    //MVI, ABOVE IS MVVM
    private val _dataState: MutableLiveData<DataState<Boxscore>> =
        MutableLiveData()

    val dataState: LiveData<DataState<Boxscore>>
        get() = _dataState

    fun setStateEvent(playerStatsStateEvent: PlayerStatsStateEvent, teamId: String) {
        viewModelScope.launch {
            when (playerStatsStateEvent) {

                is PlayerStatsStateEvent.GetPlayerStatsEvent -> {
                    playerStatsRepository.getStats(teamId)
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

    fun insert(player: Player) = viewModelScope.launch {
        playerRepository.insert(player)
    }

    fun deleteAll() = viewModelScope.launch {
        playerRepository.deleteAll()
    }

    class PlayerViewModelFactory(private val playerRepository: PlayerRepository,
                                 private val playerStatsRepository: PlayerStatsRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(playerRepository,playerStatsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    sealed class PlayerStatsStateEvent {

        object  GetPlayerStatsEvent: PlayerStatsStateEvent()

        object None: PlayerStatsStateEvent()
    }
}

