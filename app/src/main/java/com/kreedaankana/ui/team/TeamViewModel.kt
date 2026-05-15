package com.kreedaankana.ui.team

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {

    private val repository = TeamRepository()

    private val _teams = MutableLiveData<List<TeamModel>>()
    val teams: LiveData<List<TeamModel>> = _teams

    private val _players = MutableLiveData<List<PlayerModel>>()
    val players: LiveData<List<PlayerModel>> = _players

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchTeams()
    }

    private fun fetchTeams() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getTeams().collect {
                _teams.value = it
                _isLoading.value = false
            }
        }
    }

    fun fetchPlayers(teamId: String) {
        viewModelScope.launch {
            repository.getPlayers(teamId).collect {
                _players.value = it
            }
        }
    }

    fun createTeam(team: TeamModel, onResult: (Boolean) -> Unit) {
        repository.addTeam(team, onResult)
    }

    fun addPlayer(teamId: String, player: PlayerModel, onResult: (Boolean) -> Unit) {
        repository.addPlayer(teamId, player, onResult)
    }

    fun deletePlayer(teamId: String, playerId: String, onResult: (Boolean) -> Unit) {
        repository.deletePlayer(teamId, playerId, onResult)
    }

    fun updatePlayer(teamId: String, player: PlayerModel, onResult: (Boolean) -> Unit) {
        repository.updatePlayer(teamId, player, onResult)
    }
}
