package com.example.liveplayerstats.playercomponents

import androidx.lifecycle.*
import com.example.liveplayerstats.playercomponents.Player
import com.example.liveplayerstats.playercomponents.PlayerRepository
import kotlinx.coroutines.launch

class PlayerViewModel(private val repository: PlayerRepository): ViewModel() {

    val allPlayers: LiveData<List<Player>> = repository.allPlayers.asLiveData()

    fun insert(player: Player) = viewModelScope.launch {
        repository.insert(player)
    }

    class PlayerViewModelFactory(private val repository: PlayerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlayerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

