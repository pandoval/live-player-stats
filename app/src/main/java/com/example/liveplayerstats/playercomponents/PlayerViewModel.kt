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
class PlayerViewModel @Inject constructor(private val playerRepository: PlayerRepository): ViewModel() {

    val allPlayers: LiveData<List<Player>> = playerRepository.allPlayers.asLiveData()

    fun insert(player: Player) = viewModelScope.launch {
        playerRepository.insert(player)
    }

    fun deleteAll() = viewModelScope.launch {
        playerRepository.deleteAll()
    }

    class PlayerViewModelFactory(private val playerRepository: PlayerRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(playerRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

