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

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun insertList(playerList: List<Player>) = viewModelScope.launch {
        repository.insertList(playerList)
    }

    fun deleteById(id: String) = viewModelScope.launch {
        repository.deleteById(id)
    }

    fun deleteList(idList: List<String>) = viewModelScope.launch {
        repository.deleteList(idList)
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


